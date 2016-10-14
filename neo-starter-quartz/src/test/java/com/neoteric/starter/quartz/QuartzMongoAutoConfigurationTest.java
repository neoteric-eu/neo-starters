package com.neoteric.starter.quartz;

import com.novemberain.quartz.mongodb.MongoDBJobStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.simpl.RAMJobStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class QuartzMongoAutoConfigurationTest {

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void shouldUseMongoIfQuartzMongoOnClassPath() throws Exception {
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(MongoDBJobStore.class);
    }

    @Test
    public void shouldAddCollectionPrefixIfPropertySet() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context,
                "neostarter.quartz.mongo.collectionPrefix=pref");
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(MongoDBJobStore.class);
        //TODO: Check if property added
    }

    @Test
    public void shouldUseGenericQuartzProperties() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context,
                "neostarter.quartz.properties.org.quartz.scheduler.instanceId=SomeId");
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(MongoDBJobStore.class);
        assertThat(scheduler.getSchedulerInstanceId()).isEqualTo("SomeId");
    }

    @Test
    public void shouldUseRamJobIfForced() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context,
                "neostarter.quartz.forceRamJobStore=true");
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(RAMJobStore.class);
    }

    @Test
    public void shouldDisableScheduler() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context,
                "neostarter.quartz.enabled=false");
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getListenerManager().getTriggerListeners())
                .hasSize(1)
                .hasOnlyElementsOfType(VetoAllListener.class);
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