package com.atg.openssp.core.entry.video;

import com.atg.openssp.common.core.cache.type.*;
import com.atg.openssp.common.core.entry.EntryValidatorHandler;
import com.atg.openssp.common.demand.ParamValue;
import com.atg.openssp.common.demand.VideoObjectParamValue;
import com.atg.openssp.common.exception.EmptyCacheException;
import com.atg.openssp.common.exception.RequestException;
import com.atg.openssp.core.exchange.ExchangeServer;
import openrtb.bidrequest.model.App;
import openrtb.bidrequest.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

public class VideoObjectEntryValidatorHandler extends EntryValidatorHandler {
    private final Logger log = LoggerFactory.getLogger(VideoObjectEntryValidatorHandler.class);

    public VideoObjectEntryValidatorHandler()
    {

    }

    @Override
    public List<ParamValue> validateEntryParams(HttpServletRequest request) throws RequestException {
        final ArrayList<ParamValue> pmList = new ArrayList<ParamValue>();
        final VideoObjectParamValue pm = new VideoObjectParamValue();

        Cookie[] cList = request.getCookies();
        if (cList != null) {
            for (Cookie c : cList) {
                log.info("cookie: "+c.getName());
            }
        } else {
            log.info("no cookies");
        }

        // Note:
        // You may define your individual parameter or payloadto work with.
        // Neither the "ParamValue" - object nor the list of params may fit to your requirements out of the box.

        // geo data could be solved by a geo lookup service and ipaddress

        if (request.getContentLength() > 0) {
            byte[] buffer = new byte[request.getContentLength()];
            try {
                ServletInputStream is = request.getInputStream();
                is.read(buffer);
                String json = new String(buffer);
                log.info("I got content!!! : "+json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        HashMap<String, String> params = new LinkedHashMap();
        Enumeration<String> penum = request.getParameterNames();
        while (penum.hasMoreElements()) {
            String key = penum.nextElement();
            List<String> values = Arrays.asList(request.getParameterValues(key));
            if (values.size() > 0) {
                params.put(key, values.get(0));
            }
            log.debug("param: " + key + " : " + values);
        }
        try {
            String requestedSite = params.get("site");
            log.info("requested site: " + requestedSite);
            Site site = SiteDataCache.instance.get(requestedSite);
            site.setDomain(params.get("site"));
            site.setPage(ExchangeServer.SCHEME + "://" + site.getDomain() + params.get("page"));
            site.setRef(request.getHeader("referer"));
            pm.setSite(site);
        } catch (final EmptyCacheException e) {
//                try {
//                    String requestedApp = params.get("app");
//                    log.debug("requested app: "+requestedApp);
//                    pm.setApp(AppDataCache.instance.get(requestedApp));
//                } catch (final EmptyCacheException e1) {
//                    throw new RequestException(ERROR_CODE.E906, "missing site or app (2)");
//            }
        }
        /*
        {
        site=[Ljava.lang.String;@1d9a3344,
        callback=[Ljava.lang.String;@b1dbe05,
        callback_uid=[Ljava.lang.String;@4047a76c,
        psa=[Ljava.lang.String;@78ba2df4,
        id=[Ljava.lang.String;@347621b4,
        size=[Ljava.lang.String;@527d5ca9,
        promo_sizes=[Ljava.lang.String;@2ffcfd4d,
        referrer=[Ljava.lang.String;@600a0cb}
         */
        final String siteId = request.getParameter("site");
        final String publisherId = request.getParameter("publisher");
        final String appid = request.getParameter("app");

        final String impressionid = request.getParameter("impression");
        final String deviceid = request.getParameter("device");
        final String requestid = request.getParameter("requestId");
        final String fsUid = request.getParameter("userid");
        final String id = request.getParameter("id");
        final String size = request.getParameter("size");
        final String promoSizes = request.getParameter("promo_sizes");
        final String referrer = request.getParameter("referrer");


        try {
            pm.setSite(SiteDataCache.instance.get(siteId));
        } catch (final EmptyCacheException e) {
//            try {
//                pm.setPublisher(PublisherDataCache.instance.get(publisherId));
//            } catch (final EmptyCacheException e1) {
//                throw new RequestException(e1.getMessage());
//            }
            try {
                pm.setApp(AppDataCache.instance.get(appid));
            } catch (final EmptyCacheException e1) {
                throw new RequestException(e1.getMessage());

            }
            try {
                pm.setImp(ImpressionDataCache.instance.get(impressionid));
            } catch (final EmptyCacheException e1) {
                throw new RequestException(e1.getMessage());

            }
            try {
                pm.setDevice(DeviceDataCache.instance.get(deviceid));
            } catch (final EmptyCacheException e1) {
                throw new RequestException(e1.getMessage());

            }
        }
        pm.setRequestId(requestid);
//        pm.se(callbackUid);
//        pm.setPsa(psa);
        pm.setFsUid(fsUid);


        pm.setId(id);
//        pm.setSize(size);
//        pm.setPromoSizes(promoSizes);
//        pm.setReferrer(referrer);

        pmList.add(pm);
        return pmList;
    }
}