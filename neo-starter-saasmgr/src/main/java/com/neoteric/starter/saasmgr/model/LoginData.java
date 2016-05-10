package com.neoteric.starter.saasmgr.model;

import lombok.Value;

@Value
public class LoginData {
    private final String token;
    private final User user;
}
