package com.atg.openssp.common.core.entry;

import javax.servlet.http.HttpServletResponse;

public final class AccessControlTool {
    private AccessControlTool() {
    }

    public static void populateAccessControlHeaders(HttpServletResponse httpResponse, String originString) {
        httpResponse.addHeader("Vary", "Origin");
        httpResponse.addHeader("Access-Control-Allow-Methods", "POST");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");
        httpResponse.addHeader("Access-Control-Allow-Origin", originString);
        if (!"*".equals(originString)) {
            httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }
}
