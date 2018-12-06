package com.atg.openssp.common.logadapter;

import com.atg.openssp.common.configuration.GlobalContext;
import com.atg.openssp.common.core.exchange.RtbAdProvider;
//import io.freestar.datacollector.api.ssp.interfaces.HeaderBiddingLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import openrtb.bidresponse.model.Bid;
import openrtb.bidresponse.model.SeatBid;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Acts as a facade to log4j2 logging framework.
 * 
 * @author André Schmer
 *
 */
public class LogFacade {


    public final static String DELIM = "\u0004";
	private static Logger rtbResponseLogger;
	private static Logger rtbRequestLogger;
    private static Logger cookieSyncLogger;
	private static Logger systemRequestLogger;
	private static Logger providerLogger;
    private static Logger timeInfoLogger;
	private static Logger dataBrokerLogger;
//	private static Logger headerBidRequestLogger;
//	private static Logger supplierBidRequestLogger;
//	private static Logger auctionLogger;
//    private static Logger auctionWinLogger;

	private static String REQUEST_INFO = "request";
	// private static String DEBUGGING = "debugging";
	private static String PROVIDER = "provider";
	// private static String PID = "pid";

	private static String BID_RESPONSE = "bid-response";
	private static String BID_REQUEST = "bid-request";

	private static String COOKIE_SYNC = "cookie-sync";

	private static String TIME_INFO = "time-info";
	private static String DATA_BROKER = "data-broker";
	private static String AUCTION = "auction";
	private static String AUCTION_WIN = "auction-win";

	// private static String ADSERVING_REQUEST = "adserving-request";
	// private static String ADSERVING_RESPONSE = "adserving-response";

	public static final String LOG_DELIMITER = "#";

	private static Level loglevel = Level.INFO;

    private static String project;
    private static String topic;
    private static GooglePubSubTopicPublisher publisher;

    static {
		// pidLogger = LogManager.getLogger(PID);
		// adservingRequestLogger = LogManager.getLogger(ADSERVING_REQUEST);
		// adservingResponseLogger = LogManager.getLogger(ADSERVING_RESPONSE);
    }

	public static void initLogging(final Level level) {
		loglevel = level;
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();
		final LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		loggerConfig.setLevel(level);
		ctx.updateLoggers();
	}

	private static synchronized void initPublisher() {
        project = GlobalContext.getAuctionLoggingProject();
        topic = GlobalContext.getAuctionLoggingTopic();
        if (project != null && topic != null) {
            publisher = new GooglePubSubTopicPublisher(project, topic);
            publisher.init();
        }
    }

	public static boolean isDebugEnabled() {
		return loglevel == Level.DEBUG;
	}

	// public static void logPid(final String msg) {
	// pidLogger.info(msg);
	// }

	public static void logRtbResponse(final String msg, final String... params) {
	    if (rtbResponseLogger == null) {
	        synchronized (LogFacade.class) {
                rtbResponseLogger = LogManager.getLogger(BID_RESPONSE);
            }
        }
        //bks rtbResponseLogger.debug("{} {}", params, msg);
        System.out.println("log-rtbResponseLogger::"+msg);
	}

	public static void logRtbRequest(final String msg, final String... params) {
        if (rtbRequestLogger == null) {
            synchronized (LogFacade.class) {
                rtbRequestLogger = LogManager.getLogger(BID_REQUEST);
            }
        }
        //bks rtbRequestLogger.debug("{} {}", params, msg);
        System.out.println("log-rtbRequestLogger::"+msg);
	}

    public static void logCookieSync(final String msg, final String... params) {
            if (cookieSyncLogger == null) {
            synchronized (LogFacade.class) {
                cookieSyncLogger = LogManager.getLogger(COOKIE_SYNC);
            }
        }
        //bks cookieSyncLogger.debug("{} {}", params, msg);
        System.out.println("log-cookieSyncLogger::"+msg);
    }

    // public static void logAdservingRequest(final String msg, final String... params) {
	// adservingRequestLogger.info("{} {}", msg, params);
	// }

	// public static void logAdservingResponse(final String msg, final String... params) {
	// adservingResponseLogger.info("{} {}", msg, params);
	// }

	public static void logRequestAsync(final String msg) {
        if (systemRequestLogger == null) {
            synchronized (LogFacade.class) {
                systemRequestLogger = LogManager.getLogger(REQUEST_INFO);
            }
        }
        //bks systemRequestLogger.info(msg);
        System.out.println("log-systemRequestLogger::"+msg);
	}

	public static void logProviderAsync(final String msg) {
        if (providerLogger == null) {
            synchronized (LogFacade.class) {
                providerLogger = LogManager.getLogger(PROVIDER);
            }
        }
        //bks providerLogger.info(msg);
        System.out.println("log-providerLogger::"+msg);
	}

	public static void logTimeInfo(final String msg, final String... params) {
		if (timeInfoLogger == null) {
			synchronized (LogFacade.class) {
                timeInfoLogger = LogManager.getLogger(TIME_INFO);
			}
		}
        //bks timeInfoLogger.info("{} {}", params, msg);
        System.out.println("log-timeInfoLogger::"+msg);
	}

	public static void logDataBroker(final String msg, final String... params) {
		if (dataBrokerLogger == null) {
			synchronized (LogFacade.class) {
				dataBrokerLogger = LogManager.getLogger(DATA_BROKER);
			}
		}
        //bks dataBrokerLogger.info("{} {}", params, msg);
        System.out.println("log-dataBrokerLogger::"+msg);
	}

//	public static void logHeaderBidImpression(final HeaderBiddingLog.HeaderBidImpressionEntry hbie, final String... params) {
//        if (publisher == null) {
//            initPublisher();
//            if (publisher == null) {
//                return;
//            }
//        }
//
//        HeaderBiddingLog.HeaderBiddingLogMSG.Builder msg = HeaderBiddingLog.HeaderBiddingLogMSG.newBuilder();
//        if (StringUtils.isBlank(msg.getMessageId())) {
//            msg.setMessageId(UUID.randomUUID().toString());
//        }
//        msg.setServerTime(System.currentTimeMillis());
//        msg.addHeaderBidImpressionEntry(hbie);
//
//        //save message
//        publisher.send(msg.build().toByteString());
//	}

//	public static void logAuction(final HeaderBiddingLog.AuctionBidRequestLogEntry abre, final String... params) {
//        if (publisher == null) {
//            initPublisher();
//            if (publisher == null) {
//                return;
//            }
//        }
//        HeaderBiddingLog.HeaderBiddingLogMSG.Builder msg = HeaderBiddingLog.HeaderBiddingLogMSG.newBuilder();
//        if (StringUtils.isBlank(msg.getMessageId())) {
//            msg.setMessageId(UUID.randomUUID().toString());
//        }
//        msg.setServerTime(System.currentTimeMillis());
//        msg.addAuctionBidRequestLogEntry(abre);
//
//        //save message
//        publisher.send(msg.build().toByteString());
//	}

//    public static void logAuction(final HeaderBiddingLog.AuctionBidResponseLogEntry abrle, final String... params) {
//        if (publisher == null) {
//            initPublisher();
//            if (publisher == null) {
//                return;
//            }
//        }
//        HeaderBiddingLog.HeaderBiddingLogMSG.Builder msg = HeaderBiddingLog.HeaderBiddingLogMSG.newBuilder();
//        if (StringUtils.isBlank(msg.getMessageId())) {
//            msg.setMessageId(UUID.randomUUID().toString());
//        }
//        msg.setServerTime(System.currentTimeMillis());
//        msg.addAuctionBidResponseLogEntry(abrle);
//
//        //save message
//        publisher.send(msg.build().toByteString());
//    }
//
//    public static void logAuctionWin(final HeaderBiddingLog.AuctionWinLogEntry awle, final String... params) {
//        if (publisher == null) {
//            initPublisher();
//            if (publisher == null) {
//                return;
//            }
//        }
//        HeaderBiddingLog.HeaderBiddingLogMSG.Builder msg = HeaderBiddingLog.HeaderBiddingLogMSG.newBuilder();
//        if (StringUtils.isBlank(msg.getMessageId())) {
//            msg.setMessageId(UUID.randomUUID().toString());
//        }
//        msg.setServerTime(System.currentTimeMillis());
//        msg.addAuctionWinLogEntry(awle);
//
//        //save message
//        publisher.send(msg.build().toByteString());



    private static void populate(JsonArray winners, long supplierId, SeatBid seatBid) {
	    for (Bid bid : seatBid.getBid()) {
	        populate(winners, supplierId, bid);
        }
    }

    private static void populate(JsonArray winners, long supplierId, Bid bid) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", bid.getImpid());
        obj.addProperty("supplier_id", supplierId);
        obj.addProperty("slot_id", bid.getImpid());
        obj.addProperty("adid", bid.getAdid());
        obj.addProperty("price", convertToMicroDollars(bid.getPrice()));
        winners.add(obj);
    }

    public static long convertToMicroDollars(double price) {
	    long micro = Math.round(price * 1000000d);
	    return micro;
    }

    public static double convertFromMicroDollars(long microDollars) {
        double dollars = (double)microDollars / 1000000d;
        return dollars;
    }

    private static void populate(JsonArray winners, List<RtbAdProvider> providers) {
	    ArrayList list = new ArrayList();
	    for (RtbAdProvider provider : providers) {
	        if (!list.contains(provider.getWinningSeat().getBid().get(0).getImpid())) {
	            list.add(provider.getWinningSeat().getBid().get(0).getImpid());
	            populate(winners, provider.getSupplier().getSupplierId(), provider.getWinningSeat());
            }
        }
    }

    public static String getLogLevel() {
		return loglevel.name();
	}

}
