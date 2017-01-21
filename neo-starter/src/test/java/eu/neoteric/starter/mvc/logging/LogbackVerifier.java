package eu.neoteric.starter.mvc.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.Appender;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LogbackVerifier implements TestRule {
    private List<ExpectedLogEvent> expectedEvents = new LinkedList<>();

    @Mock
    private Appender<ILoggingEvent> appender;

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();
                try {
                    base.evaluate();
                    verify();
                } finally {
                    after();
                }
            }
        };
    }

    public void expectMessage(Level level) {
        expectMessage(level, "");
    }

    public void expectMessage(Level level, String msg) {
        expectMessage(level, msg, null);
    }

    public void expectMessage(Level level, String msg, Class<? extends Throwable> throwableClass) {
        expectedEvents.add(new ExpectedLogEvent(level, msg, throwableClass));
    }

    private void before() {
        initMocks(this);
        when(appender.getName()).thenReturn("MOCK");
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(appender);
    }

    private void verify() throws Throwable {
        ArgumentCaptor<ILoggingEvent> captor = ArgumentCaptor.forClass(ILoggingEvent.class);
        Mockito.verify(appender, atMost(10)).doAppend(captor.capture());
        List<ILoggingEvent> allValues = captor.getAllValues();
        assertThat(expectedEvents)
                .filteredOn(event -> allValues.stream().noneMatch(event::matches))
                .hasSize(0);
    }

    private void after() {
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).detachAppender(appender);
    }

    private final static class ExpectedLogEvent {
        private final String message;
        private final Level level;
        private final Class<? extends Throwable> throwableClass;

        private ExpectedLogEvent(Level level, String message, Class<? extends Throwable> throwableClass) {
            this.message = message;
            this.level = level;
            this.throwableClass = throwableClass;
        }

        private boolean matches(ILoggingEvent actual) {
            boolean match = actual.getFormattedMessage().contains(message);
            match &= actual.getLevel().equals(level);
            match &= matchThrowables(actual);
            return match;
        }

        private boolean matchThrowables(ILoggingEvent actual) {
            IThrowableProxy eventProxy = actual.getThrowableProxy();
            return throwableClass == null || eventProxy != null && throwableClass.getName().equals(eventProxy.getClassName());
        }
    }
}