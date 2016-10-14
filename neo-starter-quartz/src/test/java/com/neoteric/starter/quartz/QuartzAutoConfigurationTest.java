package com.neoteric.starter.quartz;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.calendar.MonthlyCalendar;
import org.quartz.impl.calendar.WeeklyCalendar;
import org.quartz.simpl.RAMJobStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.scheduling.quartz.QuartzJobBean;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(FilteredClassPathRunner.class)
@ClassPathExclusions("quartz-mongodb-*.jar")
public class QuartzAutoConfigurationTest {

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void shouldUseRamStoreWithDefaultConfiguration() throws Exception {
        registerAndRefresh(QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(RAMJobStore.class);
    }

    @Test
    public void shouldUseDatabaseWhenDatasourceAvailable() throws Exception {
        registerAndRefresh(EmbeddedDataSourceConfiguration.class,
                QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);

        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(LocalDataSourceJobStore.class);
    }

    @Test
    public void shouldUseRamWhenDatasourceAvailableButRamForced() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context,
                "neostarter.quartz.forceRamJobStore=true");
        registerAndRefresh(EmbeddedDataSourceConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                QuartzAutoConfiguration.class);
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
    public void withConfiguredCalendars() throws Exception {
        registerAndRefresh(EmbeddedDataSourceConfiguration.class,
                QuartzCalendarsConfiguration.class,
                QuartzAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);

        assertThat(scheduler.getCalendar("weekly")).isNotNull();
        assertThat(scheduler.getCalendar("monthly")).isNotNull();
    }

    @Test
    public void withConfiguredJobAndTrigger() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context,
                "test-name=withConfiguredJobAndTrigger");
        registerAndRefresh(EmbeddedDataSourceConfiguration.class,
                QuartzAutoConfiguration.class, QuartzJobConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);

        assertThat(scheduler.getJobDetail(JobKey.jobKey("fooJob"))).isNotNull();
        assertThat(scheduler.getTrigger(TriggerKey.triggerKey("fooTrigger"))).isNotNull();
    }

    @Test
    public void withCustomizer() throws Exception {
        registerAndRefresh(QuartzAutoConfiguration.class, QuartzCustomConfig.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);

        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getSchedulerName()).isEqualTo("fooScheduler");
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

    @Configuration
    protected static class QuartzJobConfiguration {

        @Bean
        public JobDetail fooJob() {
            return JobBuilder.newJob()
                    .ofType(FooJob.class)
                    .withIdentity("fooJob")
                    .storeDurably()
                    .build();
        }

        @Bean
        public Trigger fooTrigger() {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMilliseconds(1000)
                    .repeatForever();

            return TriggerBuilder.newTrigger()
                    .forJob(fooJob())
                    .withIdentity("fooTrigger")
                    .withSchedule(scheduleBuilder)
                    .build();
        }
    }

    @Configuration
    protected static class QuartzCalendarsConfiguration {

        @Bean
        public Calendar weekly() {
            return new WeeklyCalendar();
        }

        @Bean
        public Calendar monthly() {
            return new MonthlyCalendar();
        }
    }

    @Configuration
    protected static class QuartzCustomConfig {

        @Bean
        public SchedulerFactoryBeanCustomizer customizer() {
            return schedulerFactoryBean -> schedulerFactoryBean.setSchedulerName("fooScheduler");
        }

    }

    public static class FooJob extends QuartzJobBean {

        @Autowired
        private Environment env;

        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            System.out.println(this.env.getProperty("test-name", "unknown"));
        }

    }
}