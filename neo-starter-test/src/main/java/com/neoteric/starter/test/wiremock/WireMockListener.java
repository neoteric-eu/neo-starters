package com.neoteric.starter.test.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.neoteric.starter.test.utils.TestContextHelper;
import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

import static com.neoteric.starter.test.StarterTestConstants.LOG_PREFIX;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@Slf4j
public class WireMockListener extends AbstractTestExecutionListener {

    private WireMockServer server;
    private int port;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        if (contextHelper.testClassAnnotationNotPresent(WireMock.class)) {
            return;
        }

        port = getFreeServerPort();
        server = new WireMockServer(port);


        AbstractLoadBalancer mockedBalancer = contextHelper.getBean(AbstractLoadBalancer.class);
        when(mockedBalancer.chooseServer(any())).thenReturn(new Server("localhost", port));
        when(mockedBalancer.getLoadBalancerStats()).thenReturn(new LoadBalancerStats("test-load-balancer"));
        LOG.info("{}WireMock started on port {}\n", LOG_PREFIX, port);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        if (contextHelper.testClassAnnotationNotPresent(WireMock.class) || server == null) {
            return;
        }
        server.start();
        com.github.tomakehurst.wiremock.client.WireMock.configureFor(port);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        if (contextHelper.testClassAnnotationNotPresent(WireMock.class) || server == null) {
            return;
        }
        server.resetRequests();
        server.resetMappings();
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        if (contextHelper.testClassAnnotationNotPresent(WireMock.class) || server == null) {
            return;
        }
        server.stop();
        AbstractLoadBalancer mockedBalancer = contextHelper.getBean(AbstractLoadBalancer.class);
        reset(mockedBalancer);
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
