package com.neoteric.starter.auth;


import com.neoteric.starter.auth.basics.UserAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserAuthenticationHolder {

    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationHolder.class);


    private static final ThreadLocal<UserAuthentication> userAuthentication =
            new ThreadLocal<UserAuthentication>();

    private UserAuthenticationHolder() {
        // Prevents instantiation of the class.
    }

    /**
     * Return the UserAuthentication currently bound to the thread.
     *
     * @return the UserAuthentication currently bound to the thread,
     * or {@code null} if none bound
     */
    public static UserAuthentication getUserAuthentication() {
        return userAuthentication.get();
    }

    public static String getUserId() {
        return userAuthentication.get().getUserId();
    }

    public static String getCustomerId() {
        return userAuthentication.get().getCustomerId();
    }


    /**
     * Return the UserAuthentication currently bound to the thread.
     * <p>Exposes the previously bound RequestAttributes instance, if any.
     *
     * @return the UserAuthentication currently bound to the thread
     * @throws IllegalStateException if no UserAuthentication object
     *                               is bound to the current thread
     * @see UserAuthentication
     */
    public static UserAuthentication current() throws IllegalStateException {
        UserAuthentication userAuthentication = getUserAuthentication();
        if (userAuthentication == null) {
            throw new IllegalStateException("No thread-bound request found: " +
                    "Are you referring to request attributes outside of an actual web request, " +
                    "or processing a request outside of the originally receiving thread?");
        }
        return userAuthentication;
    }

    /**
     * Bind the given UserAuthentication to the current thread.
     *
     * @param userAuthentication the UserAuthentication to expose,
     *                           or {@code null} to reset the thread-bound context
     */
    public static void set(UserAuthentication userAuthentication) {
        if (userAuthentication == null) {
            reset();
        } else {
            UserAuthenticationHolder.userAuthentication.set(userAuthentication);
        }
    }

    public static void reset() {
        userAuthentication.remove();
        LOG.debug("Cleared thread-bound user authentication: {}");
    }
}
