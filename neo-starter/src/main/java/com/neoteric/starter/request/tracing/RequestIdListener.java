package com.neoteric.starter.request.tracing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestIdListener {

    void afterRequestIdSet(String requestId, HttpServletRequest request, HttpServletResponse response);
    void onCleanUp(String requestId, HttpServletRequest request, HttpServletResponse response);
}
