package com.neoteric.starter.test.wiremock;

import com.google.common.collect.Lists;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

import java.util.List;

public class VeryStaticServerList implements ServerList<Server> {

    public static int port = 8000;

    @Override
    public List<Server> getInitialListOfServers() {
        return Lists.newArrayList(new Server("localhost", port));
    }

    @Override
    public List<Server> getUpdatedListOfServers() {
        return Lists.newArrayList(new Server("localhost", port));
    }
}
