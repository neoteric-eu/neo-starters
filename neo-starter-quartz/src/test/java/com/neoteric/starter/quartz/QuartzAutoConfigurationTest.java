package com.neoteric.starter.quartz;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.simpl.RAMJobStore;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.Banner.Mode.LOG;

@Slf4j
public class QuartzAutoConfigurationTest {

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void shouldRunInRAMWithNoConfiguration() throws Exception {
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(RAMJobStore.class);
    }

    @After
    public void closeContext() {
        if (this.context != null) {
            this.context.close();
        }
    }

    private void registerAndRefresh(Class<?>... annotatedClasses) {
        this.context.register(annotatedClasses);
        this.context.refresh();
    }
}