package com.atg.openssp.common.logadapter;

import com.atg.openssp.common.core.entry.SessionAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Threadsafe logging of rtb request data with a {@link BlockingQueue}
 * 
 * @author Brian Sorensen
 */
public class TimeInfoLogProcessor extends Thread {

	private static final Logger log = LoggerFactory.getLogger(TimeInfoLogProcessor.class);

	public static final TimeInfoLogProcessor instance = new TimeInfoLogProcessor();
	private final BlockingQueue<String> logQueue = new ArrayBlockingQueue<>(1000, true);
	private boolean shuttingDown, loggerTerminated;

	private TimeInfoLogProcessor() {
		super.start();
	}

	@Override
	public void run() {
		try {
			while (shuttingDown == false) {
				final String item = logQueue.take();
				LogFacade.logTimeInfo(item);
			}
		} catch (final InterruptedException e) {
			log.error(e.getMessage());
			loggerTerminated = true;
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Writes data to file with request information.
	 * 
	 * @param loggingId
	 * @param startTS
	 * @param endTS
	 * @param delta
	 *            {@link SessionAgent}
	 */
	public void setTimeInfoLogData(String loggingId, long startTS, long endTS, long delta) {
		if (shuttingDown || loggerTerminated) {
			return;
		}
		StringBuilder sb = new StringBuilder(loggingId);
		sb.append(LogFacade.DELIM);
		sb.append("SSP");
		sb.append(LogFacade.DELIM);
		sb.append(startTS);
		sb.append(LogFacade.DELIM);
		sb.append(endTS);
		sb.append(LogFacade.DELIM);
		sb.append(delta);
		try {
			logQueue.put(sb.toString());
		} catch (final InterruptedException e) {
			try {
				// try again
				logQueue.put(sb.toString());
			} catch (final InterruptedException ignore) {
				log.error("interrupted again, giving up.");
				Thread.currentThread().interrupt();
			}
		}
	}

    /**
     * Writes data to file with request information.
     *
     * @param loggingId
     * @param supplierId
     * @param supplierName
     * @param startTS
     * @param endTS
     * @param delta
     *            {@link SessionAgent}
     */
    public void setTimeInfoLogData(String loggingId, String requestId, String userId, Long supplierId, String supplierName, long startTS, long endTS, long delta) {
        if (shuttingDown || loggerTerminated) {
            return;
        }
        StringBuilder sb = new StringBuilder(loggingId);
        sb.append(LogFacade.DELIM);
        sb.append("DSP");
        sb.append(LogFacade.DELIM);
        sb.append(requestId);
        sb.append(LogFacade.DELIM);
        sb.append(userId);
        sb.append(LogFacade.DELIM);
        sb.append(supplierId);
        sb.append(LogFacade.DELIM);
        sb.append(supplierName);
        sb.append(LogFacade.DELIM);
        sb.append(startTS);
        sb.append(LogFacade.DELIM);
        sb.append(endTS);
        sb.append(LogFacade.DELIM);
        sb.append(delta);
        try {
            logQueue.put(sb.toString());
        } catch (final InterruptedException e) {
            try {
                // try again
                logQueue.put(sb.toString());
            } catch (final InterruptedException ignore) {
                log.error("interrupted again, giving up.");
                Thread.currentThread().interrupt();
            }
        }
    }

	public void setTimeInfoLogData(final String loggingId, final String memo) {
		if (shuttingDown || loggerTerminated) {
			return;
		}
		StringBuilder sb = new StringBuilder(loggingId);
        sb.append(LogFacade.DELIM);
        sb.append("DSP");
		sb.append(LogFacade.DELIM);
		sb.append(memo);
		try {
			logQueue.put(memo);
		} catch (final InterruptedException e) {
			try {
				// try again
				logQueue.put(memo);
			} catch (final InterruptedException ignore) {
				log.error("interrupted again, giving up.");
				Thread.currentThread().interrupt();
			}
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
