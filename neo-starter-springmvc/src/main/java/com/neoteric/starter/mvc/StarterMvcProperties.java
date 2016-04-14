package com.neoteric.starter.mvc;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "neostarter.mvc")
@Getter
@Setter
public class StarterMvcProperties {

    private Map<String, String> classSuffixToPrefix;
    private CaseFormat caseFormat = CaseFormat.LOWER_HYPHEN;
}
