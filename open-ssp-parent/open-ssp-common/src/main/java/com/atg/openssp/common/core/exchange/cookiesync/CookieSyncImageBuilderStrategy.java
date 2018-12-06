package com.atg.openssp.common.core.exchange.cookiesync;

import com.atg.openssp.common.demand.Supplier;
import openrtb.bidrequest.model.BidRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class CookieSyncImageBuilderStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(CookieSyncImageBuilderStrategy.class);
    private static final String SSP_REDIR_URL = "SSP_REDIR_URL";
    private static final String SSP_USER_ID = "SSP_USER_ID";
    private final String context;
    private final Supplier supplier;
    private final BidRequest bidRequest;
    private final String cookieSync;

    public CookieSyncImageBuilderStrategy(String context, Supplier supplier, BidRequest bidRequest) {
        this.context = context;
        this.supplier = supplier;
        this.bidRequest = bidRequest;
        cookieSync = supplier.getCookieSync();
    }

    protected String getContext() {
        return context;
    }

    protected Supplier getSupplier() {
        return supplier;
    }

    protected BidRequest getBidRequest() {
        return bidRequest;
    }

    protected String getCookieSync() {
        return cookieSync;
    }

    public boolean hasCookieSync() {
        return cookieSync != null && !"".equals(cookieSync);
    }

    public  String getImageString() throws UnsupportedEncodingException {
        if (cookieSync != null) {
            if (cookieSync.contains(SSP_USER_ID)) {
                String cookieSyncName = getSupplier().getCookieSyncName();
                LOG.info(cookieSyncName + " set cookie sync value");
                StringBuilder sspRedirUrl = new StringBuilder();
                String uid = getBidRequest().getUser().getId();
                return getCookieSync().replace("{"+SSP_USER_ID+"}", uid);

            } else { // e.g. if (SSP_REDIR_URL.equals(cookieSync)) {

                String cookieSyncName = getSupplier().getCookieSyncName();
                LOG.info(cookieSyncName + " set cookie sync value");
                StringBuilder sspRedirUrl = new StringBuilder();
                String uid = getBidRequest().getUser().getId();
                String addr = "ssp.pub.network";//getSessionAgent().getHttpRequest().getLocalAddr();
                sspRedirUrl.append("https://" + addr + "/" + getContext() + "/cookiesync?fsuid=" + uid + "&dsp=" + cookieSyncName + "&dsp_uid={UID}");
                return getCookieSync().replace("{"+SSP_REDIR_URL+"}", sspRedirUrl.toString());
            }
        } else {
            return null;
        }
    }

}
