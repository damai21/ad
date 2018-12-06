package com.atg.openssp.core.exchange;

import com.atg.openssp.common.core.entry.AccessControlTool;
import com.atg.openssp.common.core.entry.BiddingServiceInfo;
import com.atg.openssp.common.core.entry.CoreSupplyServlet;
import com.atg.openssp.common.core.exchange.Auction;
import com.atg.openssp.common.core.exchange.Exchange;
import com.atg.openssp.common.core.exchange.ExchangeExecutorServiceFacade;
import com.atg.openssp.common.core.exchange.RequestSessionAgent;
import com.atg.openssp.common.exception.RequestException;
import com.atg.openssp.common.provider.AdProviderReader;
import com.atg.openssp.common.provider.AdProviderReaderForInstance;
import com.atg.openssp.common.provider.AdProviderReaderForMultipleInstance;
import openrtb.bidrequest.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.math.FloatComparator;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This is the server which is mainly responsible to start the bidprocess, collect the result and build a reponse for the client.
 *
 * @author André Schmer
 *
 */
public class ExchangeServer implements Exchange<RequestSessionAgent> {

	private static final Logger LOG = LoggerFactory.getLogger(ExchangeServer.class);
    public static final String SCHEME = "https";

	/**
	 * Starts the process to exchange and build a response if a {@code VideoResult} can be expected.
	 * <p>
	 * Principle of work is the following:
	 * <ul>
	 * <li>fetch a list of channels</li>
	 * <li>invoke the callables due to the {@link ExchangeExecutorServiceFacade}</li>
	 * <li>Evaluates a winner from the list of futures by making a simple price comparison where the highest price wins</li>
	 * <li>If a valid winner is evaluated, the response is build and post bid operations such as winningnotifying can be processed</li>
	 * </ul>
	 *
	 * @param {@link
	 *            RequestSessionAgent}
	 *
	 * @return true if a provider, {@link AdProviderReader}, exists and building a response is successful, false otherwise
	 */
	@Override
	public boolean processExchange(final RequestSessionAgent agent) throws ExecutionException, RequestException {
		final AdProviderReader winner = execute(agent);
//		LOG.error(String.valueOf("this is winner" +winner));
//		LOG.error(String.valueOf("this is " +agent));
		return evaluateResponse(agent, winner);
	}

	private AdProviderReader execute(final RequestSessionAgent agent) throws ExecutionException, RequestException {
		try {
			final List<Callable<AdProviderReader>> callables = ChannelFactory.createListOfChannels(agent);
//			LOG.error(String.valueOf("THese are callables" +callables));

			final List<Future<AdProviderReader>> futures = ExchangeExecutorServiceFacade.instance.invokeAll(callables);
//			LOG.error(String.valueOf(futures));

			final Future<AdProviderReader> winnerFuture = futures.stream().reduce(ExchangeServer::validate).orElse(null);
//			LOG.error(String.valueOf(winnerFuture));

			if (winnerFuture != null) {
				try {
					return winnerFuture.get();
				} catch (ArrayIndexOutOfBoundsException ex) {
					LOG.error("no winner detected (winnerFuture is empty)");
				} catch (final ExecutionException e) {
					if (e.getCause() instanceof RequestException) {
						throw (RequestException) e.getCause();
					} else {
                        LOG.error("Request exec error : " + e.getMessage(), e);
					}
					throw e;
				}
			} else {
                LOG.error("no winner detected");
			}
		} catch (final InterruptedException e) {
            LOG.error("Interrupted exec error : "+e.getMessage());
		}
		return null;
	}

	private static Future<AdProviderReader> validate
			(final Future<AdProviderReader> a, final Future<AdProviderReader> b) {

		try {
			if (b.get() == null) {

				return a;

			}
			if (a.get() == null) {
				return b;
			}

			HashMap<String, Float> map = new HashMap();
			if (a.get() instanceof AdProviderReaderForInstance) {
				String key = ((AdProviderReaderForInstance) a.get()).getAdid();
				map.put(key, ((AdProviderReaderForInstance) a.get()).getExchangedCurrencyPrice());
			}else if (a.get() instanceof AdProviderReaderForMultipleInstance) {
				for (int i=0; i<((AdProviderReaderForMultipleInstance) a.get()).getItemCount(); i++) {
					String key = ((AdProviderReaderForMultipleInstance) a.get()).getAdid(i);
					map.put(key, ((AdProviderReaderForMultipleInstance) a.get()).getExchangedCurrencyPrice(i));
				}
			}
			boolean check = true;
			if (b.get() instanceof AdProviderReaderForInstance) {
				String key = ((AdProviderReaderForInstance) b.get()).getAdid();
				check = check && FloatComparator.greaterThanWithPrecision(map.get(key), ((AdProviderReaderForInstance) b.get()).getExchangedCurrencyPrice());
			}else if (a.get() instanceof AdProviderReaderForMultipleInstance) {
				for (int i=0; i<((AdProviderReaderForMultipleInstance) a.get()).getItemCount(); i++) {
					String key = ((AdProviderReaderForMultipleInstance) b.get()).getAdid(i);
					check = check && FloatComparator.greaterThanWithPrecision(map.get(key), ((AdProviderReaderForMultipleInstance) b.get()).getExchangedCurrencyPrice(i));
				}
			}
			if (check) {
				return a;
			}
		} catch (final InterruptedException e) {
            LOG.error(e.getMessage());
		} catch (final CancellationException e) {
            LOG.error(e.getMessage());
		} catch (final ExecutionException e) {
            LOG.error(e.getMessage(), e);
		}

		LOG.error("print "+ b);
		return b;
	}

	private boolean evaluateResponse(final RequestSessionAgent agent, final AdProviderReader winner) {
        LOG.debug("evaluateResponse");
		BiddingServiceInfo info = agent.getBiddingServiceInfo();

		agent.getHttpResponse().setCharacterEncoding(info.getCharacterEncoding());
		agent.getHttpResponse().setContentType("Content-Type: "+info.getContentType());

		String originString;
		if (info.isAccessAllowOriginActivated() && winner instanceof Auction.AuctionResult) {
            LOG.debug("is HeaderBid AuctionResult");
            //TODO:  BKS need app
			if (((Auction.AuctionResult)winner).getBidRequest() != null) {
                originString = CoreSupplyServlet.computeOriginString(agent, info.getSite(), false);
			} else {
                originString = CoreSupplyServlet.computeOriginString(agent, info.getSite(), false);
            }
		} else {
		    Site site = agent.getBiddingServiceInfo().getSite();
            originString = CoreSupplyServlet.computeOriginString(agent, site, false);
        }
        AccessControlTool.populateAccessControlHeaders(agent.getHttpResponse(), originString);
		String referrer = agent.getHttpRequest().getHeader("referer");
		Map<String, String> headers = info.getHeaders();
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			agent.getHttpResponse().addHeader(entry.getKey(), entry.getValue());
		}

		try (Writer out = agent.getHttpResponse().getWriter()) {
			if (winner != null && winner.isValid()) {

				final String responseData;
				if (winner instanceof Auction.AuctionResult) {
					if (((Auction.AuctionResult)winner).getBidRequest() != null) {
						responseData = ((Auction.AuctionResult) winner).buildHeaderBidResponse();
					} else {
						responseData = "";
					}
				} else {
					responseData = winner.buildResponse();
				}
				out.append(responseData);

                if (agent.getBiddingServiceInfo().sendNurlNotifications()) {
                    LOG.debug("send winning nurl notification");
                    winner.perform(agent);
                } else {
                    LOG.debug("skipping winning nurl notification on header bidding");
                }
				out.flush();
				return true;
			}
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

}
