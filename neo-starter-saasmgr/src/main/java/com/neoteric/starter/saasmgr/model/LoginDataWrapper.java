package com.neoteric.starter.saasmgr.model;

import lombok.Value;

import java.util.Map;

@Value
public class LoginDataWrapper {

    private final LoginData data;
    private final Map<String, Object> meta;
}