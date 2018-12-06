package com.atg.openssp.common.logadapter;

import com.atg.openssp.common.core.entry.SessionAgent;
import com.atg.openssp.common.core.exchange.Auction;
import com.atg.openssp.common.core.exchange.RtbAdProvider;
//import io.freestar.datacollector.api.ssp.interfaces.HeaderBiddingLog;
import openrtb.bidresponse.model.Bid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Threadsafe logging of rtb request data with a {@link BlockingQueue}
 * 
 * @author Brian Sorensen
 */
public class AuctionWinLogProcessor extends Thread {

	private static final Logger log = LoggerFactory.getLogger(AuctionWinLogProcessor.class);

	public static final AuctionWinLogProcessor instance = new AuctionWinLogProcessor();
    private static final int LOG_SIZE = 200;
    private final BlockingQueue<String> logQueue = new ArrayBlockingQueue<>(LOG_SIZE, true);
	private boolean shuttingDown, loggerTerminated;

	private AuctionWinLogProcessor() {
		super.start();
	}

	@Override
	public void run() {
		try {
			while (shuttingDown == false) {
				final String item = logQueue.take();
				if (item != null) {
//                    System.out.println(getClass().getSimpleName()+"-QQ:"+logQueue.size()+"::"+item.getWinner().getBidRequest().getId());
                   // LogFacade.logAuctionWin(item);
                }
                Thread.yield();
			}
		} catch (final InterruptedException e) {
			log.error(e.getMessage());
			loggerTerminated = true;
			Thread.currentThread().interrupt();
		}
	}

    /**
     * Writes data to file with request information.
     *            {@link SessionAgent}
     */
    public void setLogData(Auction.AuctionResult winner) {
//        System.out.println(getClass().getSimpleName()+"-Q:"+logQueue.size());
        if (logQueue.size() == LOG_SIZE-2) {
            logQueue.clear();
            System.out.println(getClass().getSimpleName()+"-KQ:"+logQueue.size());
        }
        if (shuttingDown || loggerTerminated) {
            return;
        }
//        try {
//            if (winner instanceof Auction.MultipleAuctionResult) {
//                Auction.MultipleAuctionResult working = (Auction.MultipleAuctionResult) winner;
//                List<RtbAdProvider> winners = working.getWinningProvider();
//                for (RtbAdProvider w : winners) {
//                    populateWinInfo(logQueue, winner.getBidRequest().getId(), w.getImpId(), w);
//                }
//            } else if (winner instanceof Auction.SingularAuctionResult) {
//                Auction.SingularAuctionResult working = (Auction.SingularAuctionResult) winner;
//                populateWinInfo(logQueue, winner.getBidRequest().getId(), working.getWinningProvider().getImpId(), working.getWinningProvider());
//            } else {
//                Auction.AuctionNoResult working = (Auction.AuctionNoResult) winner;
//            }
//            Thread.yield();
//        } catch (final Exception e) {
//            log.error(e.getMessage(), e);
//        }
    }

    private void populateWinInfo(BlockingQueue<String> logQueue, String requestId, String id, RtbAdProvider winner) {
        List<Bid> bids = winner.getWinningSeat().getBid();
        for (Bid bid : bids) {
            if (!bid.getImpid().equals(id)) {
                continue;
            }
//            HeaderBiddingLog.AuctionWinLogEntry.Builder ale = HeaderBiddingLog.AuctionWinLogEntry.newBuilder();
//            try {
//                ale.setPrebidRequestId(requestId);
//                ale.setSupplierId(Long.toString(winner.getSupplier().getSupplierId()));
//
//                ale.setImpressionId(bid.getImpid());
////                ale.setBidId(bid.getId());
//                ale.setRequestId(bid.getImpid());
//
//                ale.setPriceMicros(LogFacade.convertToMicroDollars(winner.getExchangedCurrencyPrice()));
//                if (bid.getAdid() != null) {
//                    ale.setAdId(bid.getAdid());
//                }
//                if (bid.getCid() != null) {
//                  ale.setCampaignId(bid.getCid());
//                }
//                if (bid.getCrid() != null) {
//                    ale.setCreativeId(bid.getCrid());
//                }
//                logQueue.put(ale.build());
//            } catch (InterruptedException e) {
//                Thread.yield();
//                try {
//                    logQueue.put(ale.build());
//                } catch (InterruptedException e1) {
//                    log.error(e1.getMessage(), e1);
//                }
//            }
        }
    }

    /**
	 * Sets an indicator to shutdown this thread.
	 */
	public void shutDown() {
		shuttingDown = true;
		log.info("shutDown request received");
	}

}
