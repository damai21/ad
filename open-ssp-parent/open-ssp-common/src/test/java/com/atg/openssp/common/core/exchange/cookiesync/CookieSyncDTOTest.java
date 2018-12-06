package com.atg.openssp.common.core.exchange.cookiesync;
import com.atg.openssp.common.logadapter.DspCookieSyncLogProcessor;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class CookieSyncDTOTest {

    @Test
    public void setFsuid() {
    }

    @Test
    public void getFsuid() {
    }

    @Test
    public void lookup() {
    }

    @Test
    public void add() {
    }

    @Test
    public void isDirty() {

        Gson gson = new Gson();

        CookieSyncDTO dto = new CookieSyncDTO();
        assertFalse(dto.isDirty());
        dto.setUid("frogs-uid");
        assertTrue(dto.isDirty());
        String json = gson.toJson(dto);
        assertEquals("{\"dsp_uids\":{},\"uid\":\"frogs-uid\"}", json);
        CookieSyncDTO dto2 = gson.fromJson(json, CookieSyncDTO.class);
        assertFalse(dto2.isDirty());

        dto.clearDirty();
        assertFalse(dto.isDirty());
        json = gson.toJson(dto);
        assertEquals("{\"dsp_uids\":{},\"uid\":\"frogs-uid\"}", json);
        dto2 = gson.fromJson(json, CookieSyncDTO.class);
        assertFalse(dto2.isDirty());

        DspCookieDto dC = new DspCookieDto();
        assertFalse(dC.isDirty());
        dC.setShortName("wombat");
        assertTrue(dC.isDirty());
        dC.clearDirty();
        assertFalse(dC.isDirty());

        dto.add(dC);
        assertTrue(dto.isDirty());
        assertFalse(dC.isDirty());
        dto.clearDirty();
        assertFalse(dto.isDirty());
        assertFalse(dC.isDirty());

        dC.setUid("foobar");
        assertTrue(dto.isDirty());
        assertTrue(dC.isDirty());
    }

    @Test
    public void cookieSyncDTOTest()
    {
        /*
        CookieSyncDTO dto = CookieSyncManager.getInstance().get("41624435-5e3e-47c6-b382-a938abb79283");

        CookieSyncDTO xxx = new CookieSyncDTO();

        CookieSyncManager.getInstance().set("41624435-5e3e-47c6-b382-a938abb79283", xxx);

        System.out.println(dto);
        System.out.println(dto);
        */

        String fsuid1 = "41624435-5e3e-47c6-b382-a938abb79283";
        String fsuid2 = "41624435-5e3e-47c6-b382-a938abb79281";
        String dspShortName = "MangoMedia-desktop";
        String dspUid1 = "2112";
        String dspUid2 = "2111";


        CookieSyncDTO dto1 = CookieSyncManager.getInstance().get(fsuid1);
        CookieSyncDTO dto2 = CookieSyncManager.getInstance().get(fsuid2);
//        dto = null;
        if (dto1 == null) {
            dto1 = new CookieSyncDTO();
            dto1.setUid(fsuid1);
        }
        DspCookieDto dspResult = dto1.lookup(dspShortName);
        if (dspResult == null) {
            dspResult = new DspCookieDto();
            dspResult.setShortName(dspShortName);
            dto1.add(dspResult);
        }
        String checkUid = dspResult.getUid();
        if (checkUid == null) {
            dspResult.setUid(dspUid1);
        } else {
            if (!checkUid.equals(dspUid1)) {
                dspResult.setUid(dspUid1);
            }
        }

        if (dto1.isDirty()) {
            CookieSyncManager.getInstance().set(fsuid1, dto1);
            DspCookieSyncLogProcessor.instance.setLogData("cookie-sync", "update", fsuid1, dspShortName, dspUid1);
        }

    }
}