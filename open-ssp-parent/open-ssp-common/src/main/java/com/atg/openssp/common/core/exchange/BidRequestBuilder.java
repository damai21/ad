package com.atg.openssp.common.core.exchange;

import com.atg.openssp.common.configuration.GlobalContext;
import com.atg.openssp.common.core.entry.BiddingServiceInfo;
import com.atg.openssp.common.core.entry.SessionAgent;
import com.atg.openssp.common.core.entry.SessionAgentType;
import com.atg.openssp.common.exception.RequestException;
import openrtb.bidrequest.model.*;
import openrtb.tables.ContentCategory;
import openrtb.tables.VideoBidResponseProtocol;

/**
 * RequestBuilder builds the BidRequest Object for RTB Exchange.
 *
 * @author André Schmer
 */
public final class BidRequestBuilder {
    private static BidRequestBuilder instance;
    private BidRequestBuilderHandler handler;
    private boolean initialized;

    private BidRequestBuilder() {
    }

    /**
     * Build a request object regarding to the OpenRTB Specification.
     *
     * @return {@see BidRequest}
     */
    public BidRequest build(final RequestSessionAgent agent) throws RequestException {
        if (!initialized) {
            initialized = true;

            BiddingServiceInfo info = agent.getBiddingServiceInfo();
            SessionAgentType type = info.getType();
            if (type == SessionAgentType.VIDEO) {
                try {
                    String handlerClassName = GlobalContext.getBidRequestBuilderHandlerForVideoObjectsClass();
                    Class c = Class.forName(handlerClassName);
                    handler = (BidRequestBuilderHandler) c.getConstructor(null).newInstance(null);
                } catch (Exception e) {
                    handler = new TestBidRequestBuilderHandler();
                }
            } else if (type == SessionAgentType.BANNER) {
                try {
                    String handlerClassName = GlobalContext.getBidRequestBuilderHandlerForBannerObjectsClass();
                    Class c = Class.forName(handlerClassName);
                    handler = (BidRequestBuilderHandler) c.getConstructor(null).newInstance(null);
                } catch (Exception e) {
                    handler = new TestBidRequestBuilderHandler();
                }
            } else if (type == SessionAgentType.HEADER) {
                try {
                    String handlerClassName = GlobalContext.getBidRequestBuilderHandlerForHeaderBiddingClass();
                    Class c = Class.forName(handlerClassName);
                    handler = (BidRequestBuilderHandler) c.getConstructor(null).newInstance(null);
                } catch (Exception e) {
                    handler = new TestBidRequestBuilderHandler();
                }
            } else {
                handler = new TestBidRequestBuilderHandler();
            }
        }

        return handler.constructRequest(agent);
    }

    public synchronized static BidRequestBuilder getInstance() {
        if (instance == null) {
            instance = new BidRequestBuilder();
        }
        return instance;
    }
//    public static BidRequest build(final RequestSessionAgent agent) throws RequestException {
//
//        final BidRequest bidRequest = new BidRequest.Builder().
//                setId(agent.getRequestid()).
//                setSite(new Site.Builder()
//                                .setId("1234")
//                                .setDomain("yourdomain.com")
//                                .addPagecat(ContentCategory.IAB2)
//                                .addSectioncat(ContentCategory.IAB3)
//                                .addCat(ContentCategory.IAB1)
//                                .addCat(ContentCategory.IAB11)
//                                .setName("yourSiteObject")
//                                .setPage("page").build()
//                ).
//
//                setDevice(new Device.Builder().
//                        setGeo(
//                                new Geo.Builder().
//                                        setCity("Hamburg").
//                                        setCountry("DEU").
//                                        setLat(53.563452f).
//                                        setLon(9.925742f).
//                                        setZip("22761").
//                                        build()).build()).
//                addImp(new Impression.Builder().
//                        setId(
//                                "1").setVideo(new Video.Builder().
//                        addMime("application/x-shockwave-flash").
//                        setH(400).
//                        setW(600).
//                        setMaxduration(100).
//                        setMinduration(30).
////                        addProtocol(VideoBidResponseProtocol.VAST_2_0.getValue()).
//                        setStartdelay(1).
//                        build()).build()).
//                setUser(new User.Builder().
//                        setBuyeruid("HHcFrt-76Gh4aPl")
//                        .setGender(Gender.MALE).
//                                setId("99").
//                                setYob(1981).build()).build();
//
//        return bidRequest;
//    }
}
