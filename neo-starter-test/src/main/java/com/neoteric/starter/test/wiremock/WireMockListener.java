package com.neoteric.starter.test.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.TestPropertySourceUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

public class WireMockListener extends AbstractTestExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(WireMockListener.class);

    private WireMockServer server;
    private int port;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        WireMockTest annotation = testContext.getTestClass().getAnnotation(WireMockTest.class);
        if (annotation == null || annotation.value().length == 0) {
            return;
        }
        port = getFreeServerPort();
        ConfigurableEnvironment environment = (ConfigurableEnvironment) testContext.getApplicationContext().getEnvironment();

        List<String> serviceMocks = Lists.newArrayList();
        Arrays.stream(annotation.value()).forEach(service ->
                serviceMocks.add(String.join("", service, ".ribbon.listOfServers=localhost:", String.valueOf(port))));

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(environment, serviceMocks.stream().toArray(String[]::new));
        server = new WireMockServer(port);
        LOG.info("WireMock started on port {}\n", port);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        if (server == null) {
            return;
        }
        server.start();
        WireMock.configureFor(port);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        if (server == null) {
            return;
        }
        server.stop();
    }

    private int getFreeServerPort() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            LOG.error("Socket Exception while opening socket");
            throw e;
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                LOG.error("Socket Exception while closing socket", e);
            }
        }
    }
}
