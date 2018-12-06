package com.atg.openssp.common.core.exchange.cookiesync.image;

import com.atg.openssp.common.core.exchange.cookiesync.CookieSyncImageBuilderStrategy;
import com.atg.openssp.common.demand.Supplier;
import openrtb.bidrequest.model.BidRequest;
import openrtb.bidrequest.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.Assert.*;

public class CookieSyncImageBuilderStrategyTest {
    private String context = "UT_CS";
    private Supplier supplier;
    private BidRequest bidRequest;
    private CookieSyncImageBuilderStrategy strategy;

    @Before
    public void setUp() throws Exception {
        supplier = new Supplier();
        supplier.setCookieSyncName("Wombat");
        bidRequest = new BidRequest();
        User user = new User();
        user.setId("ABC2112");
        bidRequest.setUser(user);
        supplier.setCookieSync("<img src='//Admins-MBP.pub.network:8082/user-sync?t=image&r={SSP_REDIR_URL}' style ='display:none' width='0' height='0'></img>");
        strategy = new CookieSyncImageBuilderStrategy(context, supplier, bidRequest);
    }

    @After
    public void tearDown() throws Exception {
    }

    //@Test
    public final void getContext() {
    }

    //@Test
    public final void getSupplier() {
    }

    //@Test
    public final void getBidRequest() {
    }

    //@Test
    public final void getCookieSync() {
    }

    //@Test
    public final void hasCookieSync() {
    }

    @Test
    public final void getImageString() {
        try {
            String test1 = "<img src='//Admins-MBP.pub.network:8082/user-sync?t=image&r=https://ssp.pub.network/UT_CS/cookiesync?fsuid=ABC2112&dsp=Wombat&dsp_uid={UID}' style ='display:none' width='0' height='0'></img>";
            assertEquals(test1, strategy.getImageString());


            supplier.setCookieSync("<img src='https://getintent.io/cookies/?freestar_id={SSP_USER_ID}' style ='display:none' width='0' height='0'></img>");
            strategy = new CookieSyncImageBuilderStrategy(context, supplier, bidRequest);
            String test2 = "<img src='https://getintent.io/cookies/?freestar_id=ABC2112' style ='display:none' width='0' height='0'></img>";
            assertEquals(test2, strategy.getImageString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail("problem");
        }
    }

}