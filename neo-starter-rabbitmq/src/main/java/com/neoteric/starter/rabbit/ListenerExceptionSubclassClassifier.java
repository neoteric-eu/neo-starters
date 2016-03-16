package com.neoteric.starter.rabbit;

import org.springframework.classify.Classifier;
import org.springframework.classify.SubclassClassifier;
import org.springframework.retry.RetryPolicy;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ListenerExceptionSubclassClassifier implements Classifier<Throwable, RetryPolicy> {

    private ConcurrentMap<Class<? extends Throwable>, RetryPolicy> classified;

    private RetryPolicy defaultValue = null;

    /**
     * Create a {@link SubclassClassifier} with supplied default value.
     *
     * @param defaultValue
     */
    public ListenerExceptionSubclassClassifier(Map<Class<? extends Throwable>, RetryPolicy> typeMap, RetryPolicy defaultValue) {
        super();
        this.classified = new ConcurrentHashMap<>(typeMap);
        this.defaultValue = defaultValue;
    }

    /**
     * Return the value from the type map whose key is the class of the given
     * Throwable, or its nearest ancestor if a subclass.
     */
    public RetryPolicy classify(Throwable classifiable) {

        if (classifiable == null) {
            return defaultValue;
        }

        @SuppressWarnings("unchecked")
        Class<? extends Throwable> exceptionClass = classifiable.getCause().getClass();
        if (classified.containsKey(exceptionClass)) {
            return classified.get(exceptionClass);
        }

        // check for subclasses
        Set<Class<? extends Throwable>> classes = new TreeSet<>(new ClassComparator());
        classes.addAll(classified.keySet());
        for (Class<? extends Throwable> cls : classes) {
            if (cls.isAssignableFrom(exceptionClass)) {
                RetryPolicy value = classified.get(cls);
                this.classified.put(exceptionClass, value);
                return value;
            }
        }

        return defaultValue;
    }

    /**
     * Return the default value supplied in the constructor (default false).
     */
    final public RetryPolicy getDefault() {
        return defaultValue;
    }

    protected Map<Class<? extends Throwable>, RetryPolicy> getClassified() {
        return classified;
    }

    /**
     * Comparator for classes to order by inheritance.
     *
     * @author Dave Syer
     */
    @SuppressWarnings("serial")
    private static class ClassComparator implements Comparator<Class<?>>, Serializable {
        /**
         * @return 1 if arg0 is assignable from arg1, -1 otherwise
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Class<?> arg0, Class<?> arg1) {
            if (arg0.isAssignableFrom(arg1)) {
                return 1;
            }
            return -1;
        }
    }
}