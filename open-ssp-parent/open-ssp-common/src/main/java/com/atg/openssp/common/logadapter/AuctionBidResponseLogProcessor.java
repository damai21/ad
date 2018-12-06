package com.atg.openssp.common.logadapter;

import com.atg.openssp.common.core.entry.SessionAgent;
//import io.freestar.datacollector.api.ssp.interfaces.HeaderBiddingLog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import openrtb.bidrequest.model.BidRequest;
import openrtb.bidrequest.model.Site;
import openrtb.bidrequest.model.User;
import openrtb.bidresponse.model.Bid;
import openrtb.bidresponse.model.BidResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Threadsafe logging of rtb request data with a {@link BlockingQueue}
 * 
 * @author Brian Sorensen
 */
public class AuctionBidResponseLogProcessor extends Thread {

	private static final Logger log = LoggerFactory.getLogger(AuctionBidResponseLogProcessor.class);

	public static final AuctionBidResponseLogProcessor instance = new AuctionBidResponseLogProcessor();
	private static final int LOG_SIZE = 200;
	private final BlockingQueue<String> logQueue = new ArrayBlockingQueue<>(LOG_SIZE, true);
	private boolean shuttingDown, loggerTerminated;

	private AuctionBidResponseLogProcessor() {
		super.start();
	}

	@Override
	public void run() {
		try {
			while (shuttingDown == false) {
				final String item = logQueue.take();
				if (item != null) {
                    //System.out.println(getClass().getSimpleName()+"-QQ:"+logQueue.size()+"::"+item.getRequestId());
//                    LogFacade.logAuction(item);
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
    public void setLogData(String requestId, Long supplierId, List<Bid> o, String status) {
        if (logQueue.size() == LOG_SIZE-2) {
            logQueue.clear();
            System.out.println(getClass().getSimpleName()+"-KQ:"+logQueue.size());
        }
        if (shuttingDown || loggerTerminated) {
            return;
        }
        for (Bid bid : o) {
//            HeaderBiddingLog.AuctionBidResponseLogEntry.Builder ale = HeaderBiddingLog.AuctionBidResponseLogEntry.newBuilder();
//            try {
//                ale.setPrebidRequestId(requestId);
//                ale.setSupplierId(Long.toString(supplierId));
//
//                ale.setImpressionId(bid.getImpid());
////                ale.setBidId(bid.getId());
//                ale.setRequestId(bid.getImpid());
//
//                ale.setPriceMicros(LogFacade.convertToMicroDollars(bid.getPrice()));
//                if (bid.getAdid() != null) {
//                    ale.setAdId(bid.getAdid());
//                }
//                if (bid.getCid() != null) {
//                    ale.setCampaignId(bid.getCid());
//                }
//                if (bid.getCrid() != null) {
//                    ale.setCreativeId(bid.getCrid());
//                }
//                ale.setStatus(status);
//                logQueue.put(ale.build());
//                Thread.yield();
//            } catch (final InterruptedException e) {
//                try {
//                    Thread.yield();
//                    // try again
//                    logQueue.put(ale.build());
//                } catch (final InterruptedException ignore) {
//                    log.error("interrupted again, giving up.");
//                    Thread.currentThread().interrupt();
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
