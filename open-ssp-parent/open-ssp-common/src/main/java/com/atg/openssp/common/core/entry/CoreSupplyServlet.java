package com.atg.openssp.common.core.entry;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atg.openssp.common.core.exchange.RequestSessionAgent;
import com.atg.openssp.common.exception.ERROR_CODE;

import com.atg.openssp.common.logadapter.TimeInfoLogProcessor;
import openrtb.bidrequest.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atg.openssp.common.buffer.SSPLatencyBuffer;
import com.atg.openssp.common.core.exchange.Exchange;
import com.atg.openssp.common.exception.RequestException;
import com.google.common.base.Stopwatch;

/**
 * @author André Schmer
 */
public abstract class CoreSupplyServlet<T extends SessionAgent> extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CoreSupplyServlet.class);

    private static final long serialVersionUID = 1L;

    public static final String SCHEME = "http";

    private Exchange<T> server;

    @Override
    public void init() {
        server = getServer();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        long startTS = System.currentTimeMillis();
        final Stopwatch stopwatch = Stopwatch.createStarted();
        T agent = null;
        boolean hasResult = false;
        try {
            agent = getAgent(request, response);
            hasResult = server.processExchange(agent);
        } catch (final RequestException e) {
            TimeInfoLogProcessor.instance.setTimeInfoLogData(agent.getRequestid(), "fault-401");
            if (e.getCode() == ERROR_CODE.E906) {
                initHeader(agent, request, response);
                if (e.getMessage().startsWith("missing site or app (1)")) {
                    LOG.error(e.getMessage());
                    response.setStatus(200);
                } else {
                    response.sendError(400, e.getMessage());
                }
            } else {
                response.sendError(401, e.getMessage());
            }
        } catch (final CancellationException e) {
            TimeInfoLogProcessor.instance.setTimeInfoLogData(agent.getRequestid(), "fault-200");
            response.sendError(200, "exchange timeout");
        } catch (final Exception e) {
            TimeInfoLogProcessor.instance.setTimeInfoLogData(agent.getRequestid(), "fault");
            LOG.error(e.getMessage(), e);
        } finally {
            stopwatch.stop();
            if (hasResult) {
                SSPLatencyBuffer.getBuffer().bufferValue(stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
            if (agent != null) {
                agent.cleanUp();
            }
            long endTS = System.currentTimeMillis();
            TimeInfoLogProcessor.instance.setTimeInfoLogData(agent.getRequestid(), startTS, endTS, endTS - startTS);
            agent = null;
        }
    }

    private void initHeader(SessionAgent agent, HttpServletRequest request, HttpServletResponse response) {
	    // what site do we set origin to?
	    if (agent != null && agent instanceof RequestSessionAgent) {
	        RequestSessionAgent rsa = (RequestSessionAgent) agent;
            Site site = rsa.getBiddingServiceInfo().getSite();
            String originString;
            if (site != null) {
                originString = computeOriginString(agent, site, false);
                // values do not seem to be working
            } else {
                originString = computeOriginString(agent, site, true);
            }
            AccessControlTool.populateAccessControlHeaders(agent.getHttpResponse(), originString);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected abstract T getAgent(HttpServletRequest request, HttpServletResponse response) throws RequestException;

    protected abstract Exchange<T> getServer();

    public static String computeOriginString(SessionAgent agent, Site site, boolean concludeWithSplat) {
        String referrer = agent.getHttpRequest().getHeader("Origin");
        if (referrer == null || !referrer.startsWith("http")) {
            referrer = agent.getHttpRequest().getHeader("referer");
            if (referrer != null && referrer.startsWith("http")) {
                int index = referrer.indexOf("//");
                index = referrer.indexOf("/", index+2);
                if (index < 0) {
                    return referrer;
                } else {
                    return referrer.substring(0, index);
                }
            } else {
                if (concludeWithSplat) {
                    return "*";
                } else {
                    return SCHEME+"://" + site.getDomain();
                }
            }
        } else {
            return referrer;
        }
    }

}

