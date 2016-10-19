package com.neoteric.starter.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;

import static com.neoteric.starter.quartz.StarterQuartzConstants.LOG_PREFIX;

@Slf4j
public class VetoAllListener extends TriggerListenerSupport {

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        LOG.trace("{} vetoed: {}", LOG_PREFIX, trigger.getDescription());
        return true;
    }

    @Override
    public String getName() {
        return "vetoAll";
    }
}
