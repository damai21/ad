package com.atg.openssp.common.core.exchange.cookiesync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * The default handler returns false when asked if it supports cookie sync, but implement the behavior in
 * a non-persistent map.
 */
public class ExampleCookieSyncHandler implements CookieSyncHandler {
    private final static Logger LOG = LoggerFactory.getLogger(ExampleCookieSyncHandler.class);
    private static ExampleCookieSyncHandler singleton;
    private HashMap<String, CookieSyncDTO> map = new HashMap<>();

    private ExampleCookieSyncHandler() {
    }

    @Override
    public CookieSyncDTO get(String key) {
        return map.get(key);
    }

    @Override
    public void set(String key, CookieSyncDTO dto) {
        map.put(key, dto);
    }

    @Override
    public boolean supportsCookieSync() {
        return true;
    }

    public synchronized static ExampleCookieSyncHandler getInstance() {
        if (singleton == null) {
            singleton = new ExampleCookieSyncHandler();
        }
        return singleton;
    }
}
