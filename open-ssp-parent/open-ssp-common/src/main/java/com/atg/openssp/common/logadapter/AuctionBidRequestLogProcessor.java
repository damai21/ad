package com.atg.openssp.common.logadapter;

import com.atg.openssp.common.core.entry.SessionAgent;
import com.atg.openssp.common.demand.Supplier;
//import io.freestar.datacollector.api.ssp.interfaces.HeaderBiddingLog;
import openrtb.bidrequest.model.Banner;
import openrtb.bidrequest.model.BidRequest;
import openrtb.bidrequest.model.Impression;
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
public class AuctionBidRequestLogProcessor extends Thread {

	private static final Logger log = LoggerFactory.getLogger(AuctionBidRequestLogProcessor.class);

	public static final AuctionBidRequestLogProcessor instance = new AuctionBidRequestLogProcessor();
	private static final int LOG_SIZE = 200;
	private final BlockingQueue<String> logQueue = new ArrayBlockingQueue<>(LOG_SIZE, true);
	private boolean shuttingDown, loggerTerminated;

	private AuctionBidRequestLogProcessor() {
		super.start();
	}

	@Override
	public void run() {
		try {
			while (shuttingDown == false) {
				final String item = logQueue.take();
				if (item != null) {
                    //System.out.println(getClass().getSimpleName()+"-QQ:"+logQueue.size()+"::"+item.getRequestId());
                  //  LogFacade.logAuction(item);
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
	 * @param supplier
	 * @param request
	 */
    public void setLogData(Supplier supplier, BidRequest request) {
        if (logQueue.size() == LOG_SIZE-2) {
            logQueue.clear();
            System.out.println(getClass().getSimpleName()+"-KQ:"+logQueue.size());
        }
        if (shuttingDown || loggerTerminated) {
            return;
        }

//		HeaderBiddingLog.AuctionBidRequestLogEntry.Builder ale = HeaderBiddingLog.AuctionBidRequestLogEntry.newBuilder();
//        List<Impression> impressions = request.getAllImp();
//        for (Impression impression : impressions) {
//            try {
//                ale.setPrebidRequestId(request.getId());
//                ale.setSupplierId(supplier.getSupplierId().toString());
//                ale.setImpressionId(impression.getId());
//                Banner banner = impression.getBanner();
//                ale.setSize(banner.getW()+","+banner.getH());
//                ale.setPromoSizes(banner.getFormatAsString());
//                ale.setBidfloorMicros(LogFacade.convertToMicroDollars(impression.getBidfloor()));
////                ale.setType(HeaderBiddingLog.AuctionTypeEnum.valueOf(impression.get)); TODO:
//                ale.setType(HeaderBiddingLog.AdFormatTypeEnum.BANNER);
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
//        }
    }

	/**
	 * Sets an indicator to shutdown this thread.
	 */
	public void shutDown() {
		shuttingDown = true;
		log.info("shutDown request received");
	}

}
