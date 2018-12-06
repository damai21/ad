package com.atg.openssp.core.entry.banner;

import com.atg.openssp.common.core.cache.type.*;
import com.atg.openssp.common.core.entry.EntryValidatorHandler;
import com.atg.openssp.common.demand.BannerObjectParamValue;
import com.atg.openssp.common.demand.HeaderBiddingParamValue;
import com.atg.openssp.common.demand.ParamValue;
import com.atg.openssp.common.exception.ERROR_CODE;
import com.atg.openssp.common.exception.EmptyCacheException;
import com.atg.openssp.common.exception.RequestException;
import com.atg.openssp.common.logadapter.HeaderBidImpressionLogProcessor;
import com.atg.openssp.core.entry.header.AdUnit;
import com.atg.openssp.core.entry.header.HeaderBiddingRequest;
import com.atg.openssp.core.exchange.ExchangeServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import openrtb.bidrequest.model.BidRequest;
import openrtb.bidrequest.model.Impression;
import openrtb.bidrequest.model.Site;
import org.luaj.vm2.ast.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class BannerObjectEntryValidatorHandler extends EntryValidatorHandler {
    private final Logger log = LoggerFactory.getLogger(BannerObjectEntryValidatorHandler.class);


    public BannerObjectEntryValidatorHandler()
    {


    }

    @Override
    public List<ParamValue> validateEntryParams(HttpServletRequest request) throws RequestException {
        final ArrayList<ParamValue> pmList = new ArrayList<>();
        final BannerObjectParamValue pm = new BannerObjectParamValue();

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
                log.error("I got content!!! : "+json);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
//        final String bidreqid = request.getParameter("id");
//        final String siteid = request.getParameter("site");
//
//        log.error("requested site: " + siteid);
//        final String appid = request.getParameter("app");
//
//        final String impressionid = request.getParameter("imp");
//        log.error("requested imp: " + impressionid);
//        final String deviceid = request.getParameter("device");
//        log.error("requested device: " + deviceid);
//        final String requestid = request.getParameter("requestId");
//        final String fsUid = request.getParameter("userid");
//        final String ua = request.getParameter("ua");
//        final String callbackUid = request.getParameter("callback_uid");
//        final String psa = request.getParameter("psa");
//        final String id = request.getParameter("id");
//        final String size = request.getParameter("size");
//        final String promoSizes = request.getParameter("promo_sizes");
//        final String referrer = request.getParameter("referrer");


//        try {
//            pm.setBidRequest(BidRequestDataCache.instance.get(bidreqid));
//            log.warn(bidreqid);
//        } catch (final EmptyCacheException e) {

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
                String requestedSite = params.get("id");
                log.info("requested site: " + requestedSite);
//                log.error("whats happening");
                BidRequest bidRequest = BidRequestDataCache.instance.get(requestedSite);
//                log.error("Fetched BidRequest: " + bidRequest);
                pm.setBidRequest(bidRequest);

                pm.setSite(bidRequest.getSite());
                pm.setDevice(bidRequest.getDevice());
                pm.setUser(bidRequest.getUser());
                pm.setMimes(Arrays.asList(bidRequest.getImp().get(0).getBanner().getMimes()));
                pm.setBrowserUserAgentString(bidRequest.getDevice().getUa());
                pm.setImp(bidRequest.getImp().get(0));
                pm.setSize(bidRequest.getImp().get(0).getBanner().getW()+"x"+bidRequest.getImp().get(0).getBanner().getH());


//                pm.setRef(request.getHeader("referer"));



//                log.error("banner stuff is " +pm);
//                pm.setSite(SiteDataCache.instance.get(requestedSite));

            } catch (final EmptyCacheException e1) {
                throw new RequestException(e1.getMessage());
            }


//
//
        pmList.add(pm);
        return pmList;
    }
}
