package com.atg.openssp.common.logadapter;

import com.atg.openssp.common.core.entry.SessionAgent;
//import io.freestar.datacollector.api.ssp.interfaces.HeaderBiddingLog;
import openrtb.bidrequest.model.Site;
import openrtb.bidrequest.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Threadsafe logging of rtb request data with a {@link BlockingQueue}
 * 
 * @author Brian Sorensen
 */
public class HeaderBidImpressionLogProcessor extends Thread {

	private static final Logger log = LoggerFactory.getLogger(HeaderBidImpressionLogProcessor.class);

	public static final HeaderBidImpressionLogProcessor instance = new HeaderBidImpressionLogProcessor();
	private static final int LOG_SIZE = 200;
	private final BlockingQueue<String> logQueue = new ArrayBlockingQueue<>(LOG_SIZE, true);
	private boolean shuttingDown, loggerTerminated;

	private HeaderBidImpressionLogProcessor() {
		super.start();
	}

	@Override
	public void run() {
		try {
			while (shuttingDown == false) {
				final String item = logQueue.take();
				if (item != null) {
                    //System.out.println(getClass().getSimpleName()+"-QQ:"+logQueue.size()+"::"+item.getRequestId());
                  //  LogFacade.logHeaderBidImpression(item);
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
    public void setLogData(long timestamp, String requestId, Site site, String userId, String impressionId) {
        if (shuttingDown || loggerTerminated) {
            return;
        }
//        HeaderBiddingLog.HeaderBidImpressionEntry.Builder ale = HeaderBiddingLog.HeaderBidImpressionEntry.newBuilder();
//        try {
//            ale.setPrebidRequestId(requestId);
//            if (site != null) {
//                ale.setSiteUrl(site.getPage());
//                ale.setSiteId(site.getId());
//            }
//            if (userId != null) {
//				ale.setUserId(userId);
//			}
//			ale.setTimestamp(timestamp);
//			ale.setImpressionId(impressionId);
//            logQueue.put(ale.build());
//            Thread.yield();
//        } catch (final InterruptedException e) {
//            try {
//                Thread.yield();
//                // try again
//                logQueue.put(ale.build());
//            } catch (final InterruptedException ignore) {
//                log.error("interrupted again, giving up.");
//                Thread.currentThread().interrupt();
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
