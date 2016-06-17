package com.neoteric.starter.test.jersey.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.neoteric.starter.test.jersey.StarterTestUtils;
import com.neoteric.starter.test.jersey.StarterTestConstants;
import com.neoteric.starter.test.jersey.StarterTestProfiles;
import com.neoteric.starter.test.jersey.wiremock.ribbon.RibbonTestServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
public class WiremockListener extends AbstractTestExecutionListener {

    private WireMockServer server;
    private int port;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {

        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, StarterTestProfiles.WIREMOCK)) {
            return;
        }

        port = getFreeServerPort();
        server = new WireMockServer(port);
        RibbonTestServer.setPort(port);
        LOG.info("{}WireMock started on port {}\n", StarterTestConstants.LOG_PREFIX, port);

    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, StarterTestProfiles.WIREMOCK) || server == null) {
            return;
        }
        server.start();
        WireMock.configureFor(port);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, StarterTestProfiles.WIREMOCK) || server == null) {
            return;
        }
        server.resetRequests();
        server.resetMappings();
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, StarterTestProfiles.WIREMOCK) || server == null) {
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
