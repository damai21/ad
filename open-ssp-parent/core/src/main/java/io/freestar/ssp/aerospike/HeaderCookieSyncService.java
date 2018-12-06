package io.freestar.ssp.aerospike;

import com.atg.openssp.common.core.exchange.cookiesync.CookieSyncDTO;
import com.atg.openssp.common.core.exchange.cookiesync.CookieSyncManager;
import com.atg.openssp.common.core.exchange.cookiesync.DspCookieDto;
import com.atg.openssp.common.logadapter.DspCookieSyncLogProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Servlet implementation class HeaderCookieSyncService
 * 
 * @author Brian Sorensen
 */
@WebServlet(value = "/cookiesync", asyncSupported = false, name = "HeaderBidding-Cookie-Sync-Service")
public class HeaderCookieSyncService extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(HeaderCookieSyncService.class);

    public HeaderCookieSyncService()
    {
    }

    @Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
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

        String fsuid = params.get("uid");
        if (fsuid == null) {
            fsuid = params.get("fsuid");
        }
        String dspShortName = params.get("dsp");
        String dspUid = params.get("dsp_uid");

        if (fsuid == null || "".equals(fsuid) || dspShortName == null || "".equals(dspShortName)) {
            response.sendError(400, "{\"error\": \"uid and dsp are required\"}");
        } else {
            long csBegin = System.currentTimeMillis();
            if (CookieSyncManager.getInstance().supportsCookieSync()) {
                CookieSyncDTO dto = CookieSyncManager.getInstance().get(fsuid);
                if (dto == null) {
                    dto = new CookieSyncDTO();
                    dto.setUid(fsuid);
                }
                DspCookieDto dspResult = dto.lookup(dspShortName);
                if (dspResult == null) {
                    dspResult = new DspCookieDto();
                    dspResult.setShortName(dspShortName);
                    dto.add(dspResult);
                }
                String checkUid = dspResult.getUid();
                if (checkUid == null) {
                    dspResult.setUid(dspUid);
                } else {
                    if (!checkUid.equals(dspUid)) {
                        dspResult.setUid(dspUid);
                    }
                }

                if (dto.isDirty()) {
                    CookieSyncManager.getInstance().set(fsuid, dto);
                    DspCookieSyncLogProcessor.instance.setLogData("cookie-sync", "update", fsuid, dspShortName, dspUid);
                }
                long csEnd = System.currentTimeMillis();
                log.info("Cookie Sync Lookup time: "+(csEnd-csBegin));
            }

            response.addHeader("Content-Type", "application/json");
//        response.addHeader("Access-Control-Allow-Origin", "https://webdesignledger.com");
//        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
//        response.addHeader("Access-Control-Allow-Credentials", "true");

//        info.setCharacterEncoding("UTF-8");
//        info.activateAccessAllowOrigin();
//        info.setAuctionType(AuctionType.FIRST_PRICE);
//        info.disableSendNurlNotifications();


            response.sendError(200, "{\"status\": \"success\"}");
        }

	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
