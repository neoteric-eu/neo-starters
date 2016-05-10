package com.neoteric.starter.saasmgr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.util.Map;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDataWrapper {

    private final LoginData data;
    private final Map<String, Object> meta;
}