package com.atg.openssp.common.core.exchange.channel.rtb;

import com.atg.openssp.common.core.broker.AbstractBroker;
import com.atg.openssp.common.core.connector.JsonPostConnector;
import com.atg.openssp.common.core.entry.BiddingServiceInfo;
import com.atg.openssp.common.core.entry.SessionAgent;
import com.atg.openssp.common.core.exchange.cookiesync.CookieSyncDTO;
import com.atg.openssp.common.core.exchange.cookiesync.CookieSyncManager;
import com.atg.openssp.common.core.exchange.cookiesync.DspCookieDto;
import com.atg.openssp.common.core.exchange.cookiesync.CookieSyncImageBuilderStrategy;
import com.atg.openssp.common.demand.ResponseContainer;
import com.atg.openssp.common.demand.Supplier;
import com.atg.openssp.common.exception.BidProcessingException;
import com.atg.openssp.common.logadapter.AuctionBidResponseLogProcessor;
import com.atg.openssp.common.logadapter.DspCookieSyncLogProcessor;
import com.atg.openssp.common.logadapter.RtbRequestLogProcessor;
import com.atg.openssp.common.logadapter.RtbResponseLogProcessor;
import com.atg.openssp.common.logadapter.TimeInfoLogProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import io.freestar.datacollector.api.ssp.interfaces.HeaderBiddingLog;
import openrtb.bidrequest.model.BidRequest;
import openrtb.bidrequest.model.Impression;
import openrtb.bidrequest.model.User;
import openrtb.bidresponse.model.Bid;
import openrtb.bidresponse.model.BidResponse;
import openrtb.bidresponse.model.SeatBid;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * This class acts as Broker to a connector used in demand (OpenRTB) context. It represents one Demand side (DSP).
 * 
 * @author André Schmer
 *
 */
public final class DemandBroker extends AbstractBroker implements Callable<ResponseContainer> {

	private static final Logger LOG = LoggerFactory.getLogger(DemandBroker.class);

	private final BiddingServiceInfo info;

	private final Supplier supplier;

	private final OpenRtbConnector connector;

	private final Header[] headers;

	private Gson gson;

	private BidRequest bidrequest;

	public DemandBroker(BiddingServiceInfo info, final Supplier supplier, final OpenRtbConnector connector, final SessionAgent agent) {
		super(agent);
		this.info = info;
        this.supplier = supplier.clone();
        this.connector = connector;



		headers = new Header[2];
		headers[0] = new BasicHeader("x-openrtb-version", supplier.getOpenRtbVersion());
		headers[1] = new BasicHeader("ContentType", supplier.getContentType());




		try {
			gson = new GsonBuilder().setVersion(Double.valueOf(supplier.getOpenRtbVersion())).create();
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
		}
	}

	@Override
	public ResponseContainer call() throws Exception {
		if (bidrequest == null) {
			return null;
		}
        BidRequest workingBidRequest = bidrequest.clone();
		long startTS = System.currentTimeMillis();


		try {
            User user = workingBidRequest.getUser();
            String userId = user.getId();
            if (userId != null) {
                try {
                    long csBegin = System.currentTimeMillis();
                    if (CookieSyncManager.getInstance().supportsCookieSync()) {
                        if (!"".equals(userId) && !"undefined".equals(userId) ) {
                            CookieSyncDTO cookieSyncDTO = CookieSyncManager.getInstance().get(userId);
                            if (cookieSyncDTO != null) {
                                DspCookieDto dspDto = cookieSyncDTO.lookup(supplier.getCookieSyncName());
                                if (dspDto != null) {
                                    String buyerId = dspDto.getUid();
                                    user.setBuyeruid(buyerId);
                                    DspCookieSyncLogProcessor.instance.setLogData("include-buyer-id", userId, Long.toString(supplier.getSupplierId()), supplier.getCookieSyncName(), buyerId);
                                }
                            }
                            long csEnd = System.currentTimeMillis();
                            LOG.info(supplier.getCookieSyncName()+" Cookie Sync Update time: "+(csEnd-csBegin));
                        }
                        // adjust cookie sync to have actual runtime contents
                        CookieSyncImageBuilderStrategy strat = new CookieSyncImageBuilderStrategy(getSessionAgent().getHttpRequest().getContextPath(), supplier, workingBidRequest);
                        if (strat.hasCookieSync()) {
                            supplier.setCookieSync(URLEncoder.encode(strat.getImageString(), "UTF-8"));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LOG.error("Error on cookie sync lookup", ex);
                }
            }

            if (supplier.isCookieSyncOnly() && workingBidRequest.getUser().getBuyeruid() == null) {
                LOG.info(supplier.getShortName()+" ("+supplier.getCookieSyncName()+") is cookie sync only and we don't have a valid buyeruid.  Cancelling bid request.");
                RtbResponseLogProcessor.instance.setLogData("no buyeruid for cookie sync only supplier", "bidresponse", supplier.getShortName()+" ("+supplier.getCookieSyncName()+")");
                AuctionBidResponseLogProcessor.instance.setLogData(
                        workingBidRequest.getId(),
                        supplier.getSupplierId(),
                        new ArrayList(), "STATUS");
                return null;
            }
			DemandBrokerFilter brokerFilter = info.getDemandBrokerFilter(supplier, gson, workingBidRequest);
            final String jsonBidrequest = brokerFilter.filterRequest(gson, workingBidRequest);

			LOG.info(supplier.getShortName()+" bidrequest: " + jsonBidrequest);
			RtbRequestLogProcessor.instance.setLogData(jsonBidrequest, "bidrequest", supplier.getShortName());

			final String result = connector.connect(jsonBidrequest, headers);
            ArrayList<Bid> logBidList = new ArrayList();
			if (!StringUtils.isEmpty(result)) {
				if (JsonPostConnector.NO_CONTENT.equals(result)) {
                    LOG.info(supplier.getShortName()+" bidresponse: no content");
                    String output = "{\"id\": \""+bidrequest.getId()+"\", \"results\": \"no content\"}";
                    RtbResponseLogProcessor.instance.setLogData(output, "bidresponse", supplier.getShortName());
                    AuctionBidResponseLogProcessor.instance.setLogData(
                            workingBidRequest.getId(),
                            supplier.getSupplierId(),
                            logBidList, "NORMAL");
				} else {
					LOG.info(supplier.getShortName()+" bidresponse: " + result);
                    final BidResponse bidResponse = brokerFilter.filterResponse(gson, result);
                    for (Impression imp : workingBidRequest.getAllImp()) {
                        for (final SeatBid seatBid : bidResponse.getSeatbid()) {
                            for (final Bid bid : seatBid.getBid()) {
                                if (imp.getId().equals(bid.getImpid())) {
                                    logBidList.add(bid);
                                }
                            }
                        }
                    }
                    RtbResponseLogProcessor.instance.setLogData(result, "bidresponse", supplier.getShortName());
                    AuctionBidResponseLogProcessor.instance.setLogData(
                            workingBidRequest.getId(),
                            supplier.getSupplierId(),
                            logBidList, "NORMAL");

					ResponseContainer container =  new ResponseContainer(supplier, bidResponse);
					return container;
				}
			} else {
                LOG.info(supplier.getShortName()+" bidresponse: is null");
                RtbResponseLogProcessor.instance.setLogData("is null", "bidresponse", supplier.getShortName());
                AuctionBidResponseLogProcessor.instance.setLogData(
                        workingBidRequest.getId(),
                        supplier.getSupplierId(),
                        logBidList,"NORMAL");
            }
		} catch (final BidProcessingException e) {
			LOG.error(getClass().getSimpleName() + " " + ""+e.getMessage(), e);
            TimeInfoLogProcessor.instance.setTimeInfoLogData(info.getLoggingId(), supplier.getSupplierId()+" fault ("+e.getMessage()+")");
            if ("Read timed out".equals(e.getMessage())) {
                AuctionBidResponseLogProcessor.instance.setLogData(
                        workingBidRequest.getId(),
                        supplier.getSupplierId(),
                        new ArrayList(), "TIMEOUT");
            } else {
                AuctionBidResponseLogProcessor.instance.setLogData(
                        workingBidRequest.getId(),
                        supplier.getSupplierId(),
                        new ArrayList(), "ERROR");
            }
			throw e;
		} catch (final Exception e) {
			LOG.error(getClass().getSimpleName() + " " + e.getMessage(), e);
            TimeInfoLogProcessor.instance.setTimeInfoLogData(info.getLoggingId(), supplier.getSupplierId()+" fault ("+e.getMessage()+")");
            AuctionBidResponseLogProcessor.instance.setLogData(
                    workingBidRequest.getId(),
                    supplier.getSupplierId(),
                    new ArrayList(), "ERROR");
			//throw e;
		} finally {
            long endTS = System.currentTimeMillis();
            TimeInfoLogProcessor.instance.setTimeInfoLogData(info.getLoggingId(), workingBidRequest.getId(), workingBidRequest.getUser().getId(), supplier.getSupplierId(), supplier.getShortName(), startTS, endTS, endTS-startTS);
		}
		return null;
	}

	public void setBidRequest(final BidRequest bidrequest) {
		this.bidrequest = bidrequest;
	}

}
