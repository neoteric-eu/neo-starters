package com.neoteric.starter.rabbit;

import org.springframework.classify.Classifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class ListenerExceptionClassifierRetryPolicy implements RetryPolicy {

    private Classifier<Throwable, RetryPolicy> exceptionClassifier;


    public ListenerExceptionClassifierRetryPolicy(Map<Class<? extends Throwable>, RetryPolicy> policyMap, RetryPolicy defaultPolicy) {
        this.exceptionClassifier = new ListenerExceptionSubclassClassifier(policyMap, defaultPolicy);
    }

    /**
     * Delegate to the policy currently activated in the context.
     *
     * @see org.springframework.retry.RetryPolicy#canRetry(org.springframework.retry.RetryContext)
     */
    public boolean canRetry(RetryContext context) {
        RetryPolicy policy = (RetryPolicy) context;
        return policy.canRetry(context);
    }

    /**
     * Delegate to the policy currently activated in the context.
     *
     * @see org.springframework.retry.RetryPolicy#close(org.springframework.retry.RetryContext)
     */
    public void close(RetryContext context) {
        RetryPolicy policy = (RetryPolicy) context;
        policy.close(context);
    }

    /**
     * Create an active context that proxies a retry policy by choosing a target
     * from the policy map.
     *
     * @see org.springframework.retry.RetryPolicy#open(RetryContext)
     */
    public RetryContext open(RetryContext parent) {
        return new ExceptionClassifierRetryContext(parent, exceptionClassifier).open(parent);
    }

    /**
     * Delegate to the policy currently activated in the context.
     *
     * @see org.springframework.retry.RetryPolicy#registerThrowable(org.springframework.retry.RetryContext,
     * Throwable)
     */
    public void registerThrowable(RetryContext context, Throwable throwable) {
        RetryPolicy policy = (RetryPolicy) context;
        policy.registerThrowable(context, throwable);
        ((RetryContextSupport) context).registerThrowable(throwable);
    }

    @SuppressWarnings("serial")
    private static class ExceptionClassifierRetryContext extends RetryContextSupport implements RetryPolicy {

        final private Classifier<Throwable, RetryPolicy> exceptionClassifier;

        // Dynamic: depends on the latest exception:
        private RetryPolicy policy;

        // Dynamic: depends on the policy:
        private RetryContext context;

        final private Map<RetryPolicy, RetryContext> contexts = new HashMap<>();

        public ExceptionClassifierRetryContext(RetryContext parent,
                                               Classifier<Throwable, RetryPolicy> exceptionClassifier) {
            super(parent);
            this.exceptionClassifier = exceptionClassifier;
        }

        public boolean canRetry(RetryContext context) {
            return this.context == null || policy.canRetry(this.context);
        }

        public void close(RetryContext context) {
            // Only close those policies that have been used (opened):
            for (RetryPolicy policy : contexts.keySet()) {
                policy.close(getContext(policy, context.getParent()));
            }
        }

        public RetryContext open(RetryContext parent) {
            return this;
        }

        public void registerThrowable(RetryContext context, Throwable throwable) {
            policy = exceptionClassifier.classify(throwable);
            Assert.notNull(policy, "Could not locate policy for exception=[" + throwable + "].");
            this.context = getContext(policy, context.getParent());
            policy.registerThrowable(this.context, throwable);
        }

        private RetryContext getContext(RetryPolicy policy, RetryContext parent) {
            RetryContext context = contexts.get(policy);
            if (context == null) {
                context = policy.open(parent);
                contexts.put(policy, context);
            }
            return context;
        }
    }
}
