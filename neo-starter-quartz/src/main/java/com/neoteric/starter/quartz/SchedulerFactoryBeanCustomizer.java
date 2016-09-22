package com.neoteric.starter.quartz;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public interface SchedulerFactoryBeanCustomizer {

    void customize(SchedulerFactoryBean bean);
}