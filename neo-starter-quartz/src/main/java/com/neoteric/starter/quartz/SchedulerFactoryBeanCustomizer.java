package com.neoteric.starter.quartz;

import com.google.common.collect.Maps;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Map;

public interface SchedulerFactoryBeanCustomizer {

    void customize(SchedulerFactoryBean bean);
    default Map<String, String> quartzProperties() {
        return Maps.newHashMap();
    }
}