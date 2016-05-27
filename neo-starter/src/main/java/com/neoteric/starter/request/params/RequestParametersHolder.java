package com.neoteric.starter.request.params;

import com.neoteric.starter.request.RequestParameters;

public final class RequestParametersHolder {

    private static final ThreadLocal<RequestParameters> requestParametersHolder =
            new ThreadLocal<RequestParameters>();

    private RequestParametersHolder() {
        // Prevents instantiation of the class.
    }

    /**
     * Return the RequestParameters currently bound to the thread.
     *
     * @return the RequestParameters currently bound to the thread,
     * or {@code null} if none bound
     */
    public static RequestParameters get() {
        return requestParametersHolder.get();
    }


    /**
     * Return the RequestParameters currently bound to the thread.
     * <p>Exposes the previously bound RequestAttributes instance, if any.
     *
     * @return the RequestParameters currently bound to the thread
     * @throws IllegalStateException if no RequestParameters object
     *                               is bound to the current thread
     * @see RequestParameters
     */
    public static RequestParameters current() throws IllegalStateException {
        RequestParameters requestParameters = get();
        if (requestParameters == null) {
            throw new IllegalStateException("No thread-bound com.neoteric.starter.request found: " +
                    "Are you referring to com.neoteric.starter.request attributes outside of an actual web com.neoteric.starter.request, " +
                    "or processing a com.neoteric.starter.request outside of the originally receiving thread?");
        }
        return requestParameters;
    }

    /**
     * Bind the given RequestParameters to the current thread.
     *
     * @param requestParameters the RequestParameters to expose,
     *                          or {@code null} to reset the thread-bound context
     */
    public static void set(RequestParameters requestParameters) {
        if (requestParameters == null) {
            reset();
        } else {
            requestParametersHolder.set(requestParameters);
        }
    }

    public static void reset() {
        requestParametersHolder.remove();
    }
}
