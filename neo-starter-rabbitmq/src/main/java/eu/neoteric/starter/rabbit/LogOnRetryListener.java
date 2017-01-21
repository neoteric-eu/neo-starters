package eu.neoteric.starter.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

public class LogOnRetryListener extends RetryListenerSupport {

    private static final Logger LOG = LoggerFactory.getLogger(LogOnRetryListener.class);

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        LOG.error("AMQP Message processing error [RetryCount: {}]: {}", context.getRetryCount(), throwable);
    }
}
