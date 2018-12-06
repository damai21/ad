package com.atg.openssp.core.exchange;

import com.atg.openssp.common.cache.CurrencyCache;
import com.atg.openssp.common.configuration.GlobalContext;
import com.atg.openssp.common.core.cache.type.PricelayerCache;
import com.atg.openssp.common.core.exchange.BidRequestBuilderHandler;
import com.atg.openssp.common.core.exchange.RequestSessionAgent;
import com.atg.openssp.common.core.exchange.geo.AddressNotFoundException;
import com.atg.openssp.common.core.exchange.geo.FreeGeoIpInfoHandler;
import com.atg.openssp.common.core.exchange.geo.GeoIpInfoHandler;
import com.atg.openssp.common.core.exchange.geo.UnavailableHandlerException;
import com.atg.openssp.common.demand.BannerObjectParamValue;
import com.atg.openssp.common.demand.ParamValue;
import com.atg.openssp.common.exception.ERROR_CODE;
import com.atg.openssp.common.exception.EmptyCacheException;
import com.atg.openssp.common.exception.RequestException;
import com.google.gson.Gson;
import openrtb.bidrequest.model.*;
import openrtb.tables.GeoType;
import openrtb.tables.ImpressionSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

public class BannerObjectBidRequestBuilderHandler extends BidRequestBuilderHandler {
    private final Logger log = LoggerFactory.getLogger(BannerObjectBidRequestBuilderHandler.class);
    private final Base64.Decoder decoder;
    private GeoIpInfoHandler geoIpInfoHandler;


    public BannerObjectBidRequestBuilderHandler() {
        decoder = Base64.getDecoder();
        String handlerClassName = GlobalContext.getGeoIpInfoHandlerClass();
        if (handlerClassName == null) {
            geoIpInfoHandler = new FreeGeoIpInfoHandler();
        } else {
            try {
                Class handlerClass = Class.forName(handlerClassName);
                Constructor cc = handlerClass.getConstructor(new Class[]{});
                geoIpInfoHandler = (GeoIpInfoHandler) cc.newInstance(new Object[]{});
            } catch (Exception e) {
                log.error("could not load GeoIpInfoHandler as specified.  Loading default handler;");
                geoIpInfoHandler = new FreeGeoIpInfoHandler();
            }
        }
    }

    @Override
    public BidRequest constructRequest(RequestSessionAgent agent) throws RequestException {
        List<ParamValue> pValueList;
        try {
            pValueList = agent.getParamValues();
        } catch (RequestException e) {
            if (e.getCode() == ERROR_CODE.E906) {
                throw e;
            }
            log.warn(e.getMessage(), e);
            pValueList = new ArrayList();
            pValueList.add(new BannerObjectParamValue());
            log.warn(String.valueOf(pValueList));
        }
        BannerObjectParamValue masterValues = (BannerObjectParamValue) pValueList.get(0);
        log.error("this MASTERVALUE parameters" +masterValues);
//        log.error("this banner parameters" +new Gson().toJson(pValueList));

        Site site = masterValues.getSite().clone();
        String requestId = masterValues.getRequestId();

//        Device dd = new Device.Builder().build();
//        dd.setGeo(masterValues.getBidRequest().getDevice().getGeo());
//        dd.setUa(masterValues.getBrowserUserAgentString());
//        dd.setConnectiontype(masterValues.getBidRequest().getDevice().getConnectiontype());
//        dd.setCarrier(masterValues.getBidRequest().getDevice().getCarrier());
//        dd.setDidsha1(masterValues.getBidRequest().getDevice().getDidsha1());
//        dd.setDpidsha1(masterValues.getBidRequest().getDevice().getDpidsha1());
//        dd.setIp(masterValues.getBidRequest().getDevice().getIp());
//        dd.setMake(masterValues.getBidRequest().getDevice().getMake());
//        dd.setModel(masterValues.getBidRequest().getDevice().getModel());
//        dd.setOsv(masterValues.getBidRequest().getDevice().getOsv());
//        dd.setDevicetype(masterValues.getBidRequest().getDevice().getDevicetype());

//log.error(String.valueOf(dd));
        BidRequest bidRequest =  new BidRequest.Builder()
                .setId(masterValues.getBidRequest().getId())
                .setAt(masterValues.getBidRequest().getAt())
                .setSite(masterValues.getBidRequest().getSite())
                .setDevice(masterValues.getBidRequest().getDevice())
                .setUser(masterValues.getBidRequest().getUser()).
                        setExtension((masterValues.getBidRequest().getExt()))
                .addCur(CurrencyCache.instance.getBaseCurrency())
                .setTmax((int)GlobalContext.getExecutionTimeout())
                .build();

log.warn(String.valueOf(masterValues.getBidRequest().getDevice()));
        int idCount = 1;
        for (ParamValue pOrigin : pValueList) {
            BannerObjectParamValue pValues = (BannerObjectParamValue) pOrigin;

            Impression i = new Impression.Builder().build();
            i.setId(pValues.getBidRequest().getImp().get(0).getId());
            i.setImpid(pValues.getBidRequest().getImp().get(0).getImpid());


            i.setBanner(createBanner(pValues));
            //i.setNative(createNative(pValues));
//            i.setBidfloor((float) 2.0);
            i.setBidfloorcur(String.valueOf(1.0));
            i.setSecure(ImpressionSecurity.NON_SECURE);
//            log.error(String.valueOf(i));
//            log.error(String.valueOf(bidRequest));
            bidRequest.addImp(i);

        }

//        log.error(String.valueOf(bidRequest),"this is bidrequest");

        return bidRequest;
    }

    private User createUser(BannerObjectParamValue pValues) {
        String userId = pValues.getFsUid();
        return new User.Builder()
//                .setBuyeruid()
                .setGender(Gender.convert(pValues.getBidRequest().getUser().getGender()))
                .setId(userId)
                .setYob(pValues.getBidRequest().getUser().getYob())
//                .setGeo(createSiteGeo(pValues))
                .build();

    }


    private Geo createSiteGeo(ParamValue pValues) {
        Geo geo = new Geo.Builder().build();
        String ipAddress = pValues.getIpAddress();
        if (ipAddress != null && !ipAddress.equalsIgnoreCase("localhost") && !ipAddress.equalsIgnoreCase("0:0:0:0:0:0:0:1") && !ipAddress.equalsIgnoreCase("127.0.0.1")) {
            try {
                GeoIpInfo geoInfo = geoIpInfoHandler.lookupGeoInfo(ipAddress);
                geo.setLat(geoInfo.getLat());
                geo.setLon(geoInfo.getLon());
                geo.setZip(geoInfo.getZip());
                geo.setCity(geoInfo.getCity());
                geo.setCountry(geoInfo.getCountryCode());
                geo.setMetro(geoInfo.getMetroCode());
                geo.setRegion(geoInfo.getRegionCode());
                geo.setType(GeoType.IP);
                //geo.setUtcOffset(?);
                geo.setIpServiceType(geoInfo.getIpServiceType());
                //geo.setExt(?)
            } catch (IOException e) {
                log.warn("could not obtain geo code: "+e.getMessage(), e);
                return null;
            } catch (UnavailableHandlerException e) {
                log.warn("could not obtain geo code: "+e.getMessage());
                return null;
            } catch (AddressNotFoundException e) {
                log.warn("could not find ip address");
                return null;
            }
        }
        return geo;
    }

    private Banner createBanner(BannerObjectParamValue pValues) {
        Banner b = new Banner.Builder().setId(pValues.getId()).build();
//        log.error("=======\nBannerObjectParams are:\n" + new Gson().toJson(pValues));
        List<Banner.BannerSize> sizes = new ArrayList<>();

        Banner.BannerSize size = new Banner.BannerSize(pValues.getSize().toLowerCase());
        b.setW(size.getW());
        b.setH(size.getH());
        sizes.add(size);

//        StringTokenizer st = new StringTokenizer(pValues.getPromoSizes(), ",");
//        while(st.hasMoreTokens()) {
//            String token = st.nextToken();
//            Banner.BannerSize ts = new Banner.BannerSize(token);
//            sizes.add(ts);
//        }
        Collections.sort(sizes);
//        b.setWmin(sizes.get(0).getW());
//        b.setHmin(sizes.get(0).getH());
//        b.setWmax(sizes.get(sizes.size()-1).getW());
//        b.setHmax(sizes.get(sizes.size()-1).getH());
//        b.setFormat(sizes.toArray());

//        b.setBattr();
//        b.setApi();
//        b.setBtype();
//        b.setExpdir();
        b.setMimes(pValues.getBidRequest().getImp().get(0).getBanner().getMimes());
        b.setPos(pValues.getBidRequest().getImp().get(0).getBanner().getPos());
//        b.setTopframe();
        b.setExt(pValues.getBidRequest().getImp().get(0).getBanner().getExt());
        return b;
    }

    private String selectAppropriateId(String requestId, String agentRequestid) {
        if (requestId !=null) {
            return requestId;
        } else {
            return agentRequestid;
        }
    }

}
