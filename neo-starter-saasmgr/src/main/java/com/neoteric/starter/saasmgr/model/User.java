package com.neoteric.starter.saasmgr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.util.List;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private final String id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final List<Customer> customers;
}
