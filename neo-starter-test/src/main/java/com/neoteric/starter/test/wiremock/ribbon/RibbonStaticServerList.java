package com.neoteric.starter.test.wiremock.ribbon;

import com.google.common.collect.Lists;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

import java.util.List;

public class RibbonStaticServerList implements ServerList<Server> {

    public static int port = 8000;

    @Override
    public List<Server> getInitialListOfServers() {
        return getUpdatedListOfServers();
    }

    @Override
    public List<Server> getUpdatedListOfServers() {
        return Lists.newArrayList(new Server("localhost", port));
    }
}