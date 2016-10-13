package com.neoteric.starter.quartz;

import com.novemberain.quartz.mongodb.MongoDBJobStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.simpl.RAMJobStore;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URL;
import java.net.URLClassLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.Banner.Mode.LOG;

@Slf4j
public class QuartzAutoConfigurationTest {

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @BeforeClass
    public static void setUp() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            System.out.println(url.getFile());
        }
    }

    @Test
    public void shouldRunInRAMWithNoConfiguration() throws Exception {
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(RAMJobStore.class);
    }

    @Test
    public void shouldUseProperties() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context, "neostarter.quartz.properties.org.quartz.threadPool.threadCount=1");
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getThreadPoolSize()).isEqualTo(1);
    }

    @Test
    public void testWithQuartzMongoDb() throws Exception {
        registerAndRefresh(QuartzAutoConfiguration.class, QuartzAutoConfiguration.QuartzMongoDbConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
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