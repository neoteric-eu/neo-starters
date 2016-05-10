package com.neoteric.starter.saasmgr.model;

import lombok.Value;

import java.util.List;

@Value
public class User {
    private final String id;
    private final String email;
    private final List<Customer> customers;
}
