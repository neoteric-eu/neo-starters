package com.neoteric.starter.mvc.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Violation {

    String property;
    String type;
    Object invalidValue;
    String message;

}
