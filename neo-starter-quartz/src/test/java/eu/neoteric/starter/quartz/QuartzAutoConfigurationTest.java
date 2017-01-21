package eu.neoteric.starter.quartz;

import org.junit.After;
import org.junit.Test;
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
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

//@RunWith(FilteredClassPathRunner.class)
//@ClassPathExclusions("quartz-mongodb-*.jar")
// TODO: Fix this...
// shouldUseDatabaseWhenDatasourceAvailable won't work in IDE, but should work properly in Maven
// Classpath related issue. Find a way to properly exclude library from classpath for a single test, and make sure it work on Jenkins
public class QuartzAutoConfigurationTest {

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void shouldUseRamStoreWithDefaultConfiguration() throws Exception {
        registerAndRefresh();
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(RAMJobStore.class);
    }

    @Test
    public void shouldUseDatabaseWhenDatasourceAvailable() throws Exception {
        registerAndRefresh(EmbeddedDataSourceConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            System.out.println(url.getFile());
        }


        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(LocalDataSourceJobStore.class);
    }

    @Test
    public void shouldUseRamWhenDatasourceAvailableButRamForced() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context,
                "neostarter.quartz.forceRamJobStore=true");
        registerAndRefresh(EmbeddedDataSourceConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);

        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(RAMJobStore.class);
    }

    @Test
    public void shouldUseProperties() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context, "neostarter.quartz.properties.org.quartz.threadPool.threadCount=1");
        registerAndRefresh();
        Scheduler scheduler = this.context.getBean(Scheduler.class);
        assertThat(scheduler).isNotNull();
        assertThat(scheduler.getMetaData().getThreadPoolSize()).isEqualTo(1);
    }

    @Test
    public void withConfiguredCalendars() throws Exception {
        registerAndRefresh(EmbeddedDataSourceConfiguration.class,
                QuartzCalendarsConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);

        assertThat(scheduler.getCalendar("weekly")).isNotNull();
        assertThat(scheduler.getCalendar("monthly")).isNotNull();
    }

    @Test
    public void withConfiguredJobAndTrigger() throws Exception {
        registerAndRefresh(EmbeddedDataSourceConfiguration.class, QuartzJobConfiguration.class);
        Scheduler scheduler = this.context.getBean(Scheduler.class);

        assertThat(scheduler.getJobDetail(JobKey.jobKey("fooJob"))).isNotNull();
        assertThat(scheduler.getTrigger(TriggerKey.triggerKey("fooTrigger"))).isNotNull();
        ConfigurableEnvironment environment = context.getEnvironment();
        await().atMost(2, TimeUnit.SECONDS).until(() -> "hi".equals(environment.getProperty("testVar")));
    }

    @Test
    public void withCustomizer() throws Exception {
        registerAndRefresh(QuartzCustomConfig.class);
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

    private void registerAndRefresh() {
        this.context.register(QuartzAutoConfiguration.class);
        this.context.register(DefaultFactoryBeanConfiguration.class);
        this.context.register(QuartzDataSourceConfiguration.class);
        this.context.register(QuartzRamJobConfiguration.class);
        this.context.refresh();
    }


    private void registerAndRefresh(Class<?>... annotatedClasses) {
        this.context.register(annotatedClasses);
        registerAndRefresh();
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
                    .withIntervalInMilliseconds(500)
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
        private ConfigurableEnvironment env;

        @Override
        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            EnvironmentTestUtils.addEnvironment(env, "testVar=hi");
        }
    }
}