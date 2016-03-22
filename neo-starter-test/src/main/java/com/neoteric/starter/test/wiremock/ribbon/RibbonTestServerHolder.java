package com.neoteric.starter.test.wiremock.ribbon;

import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class RibbonTestServerHolder {

    public static Server SERVER = new Server("localhost", 8000);
    private static final Logger LOG = LoggerFactory.getLogger(RibbonTestServerHolder.class);

    public static void setPort(int port) {
        try {
            Field portField = Server.class.getDeclaredField("port");
            portField.setAccessible(true);
            portField.set(SERVER, port);

            Field idField = Server.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(SERVER, SERVER.getHostPort());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOG.error("Could not change Server id and port properties: {}", e);
        }
    }
}
