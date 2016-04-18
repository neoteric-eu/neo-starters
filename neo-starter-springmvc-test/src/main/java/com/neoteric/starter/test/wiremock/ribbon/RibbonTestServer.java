package com.neoteric.starter.test.wiremock.ribbon;

import com.netflix.loadbalancer.Server;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class RibbonTestServer {

    private static final String PORT_FIELD_NAME = "port";
    private static final String ID_FIELD_NAME = "id";

    private static class LazyHolder {
        static final Server INSTANCE = new Server("localhost", 8000);
    }

    public static Server get() {
        return LazyHolder.INSTANCE;
    }

    public static void setPort(int port) {
        Field portField = ReflectionUtils.findField(Server.class, PORT_FIELD_NAME);
        ReflectionUtils.makeAccessible(portField);
        ReflectionUtils.setField(portField, get(), port);

        Field idField = ReflectionUtils.findField(Server.class, ID_FIELD_NAME);
        ReflectionUtils.makeAccessible(idField);
        ReflectionUtils.setField(idField, get(), get().getHostPort());
    }
}
