package com.neoteric.starter.jersey.endpoint;

import com.neoteric.starter.StarterConstants;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class EndpointLoggingListener implements ApplicationEventListener {

    public final String applicationPath;

    public EndpointLoggingListener(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        if (event.getType() == ApplicationEvent.Type.INITIALIZATION_APP_FINISHED) {
            final ResourceModel resourceModel = event.getResourceModel();
            final ResourceLogDetails logDetails = new ResourceLogDetails();
            resourceModel.getResources().stream().forEach((resource) -> {
                logDetails.addEndpointLogLines(getLinesFromResource(resource));
            });
            logDetails.log();
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }

    private Set<EndpointLogLine> getLinesFromResource(Resource resource) {
        Set<EndpointLogLine> logLines = new HashSet<>();
        populate(this.applicationPath, false, resource, logLines);
        return logLines;
    }

    private void populate(String basePath, boolean isLocator, Resource resource,
                          Set<EndpointLogLine> endpointLogLines) {
        if (!isLocator) {
            basePath = normalizePath(basePath, resource.getPath());
        }

        for (ResourceMethod method : resource.getResourceMethods()) {
            endpointLogLines.add(new EndpointLogLine(method.getHttpMethod(), basePath, null));
        }

        for (Resource childResource : resource.getChildResources()) {
            for (ResourceMethod method : childResource.getAllMethods()) {
                if (method.getType() == ResourceMethod.JaxrsType.RESOURCE_METHOD) {
                    final String path = normalizePath(basePath, childResource.getPath());
                    endpointLogLines.add(new EndpointLogLine(method.getHttpMethod(), path, null));
                } else if (method.getType() == ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR) {
                    final String path = normalizePath(basePath, childResource.getPath());
                    populate(path, true, childResource, endpointLogLines);
                }
            }
        }
    }

    private static String normalizePath(String basePath, String path) {
        if (path == null) {
            return basePath;
        }
        if (basePath.endsWith("/")) {
            return path.startsWith("/") ? basePath + path.substring(1) : basePath + path;
        }
        return path.startsWith("/") ? basePath + path : basePath + "/" + path;
    }

    @Slf4j
    private static class ResourceLogDetails {

        private static final Comparator<EndpointLogLine> COMPARATOR
                = Comparator.comparing((EndpointLogLine e) -> e.path)
                .thenComparing((EndpointLogLine e) -> e.httpMethod);

        private final Set<EndpointLogLine> logLines = new TreeSet<>(COMPARATOR);

        private void log() {
            StringBuilder sb = new StringBuilder();
            logLines.stream().forEach((line) -> {
                sb.append(line).append("\n");
            });
            LOG.info("{}All endpoints for Jersey application:\n{}", StarterConstants.LOG_PREFIX, sb.toString());
        }

        private void addEndpointLogLines(Set<EndpointLogLine> logLines) {
            this.logLines.addAll(logLines);
        }
    }

    private static class EndpointLogLine {

        private static final String DEFAULT_FORMAT = "   %-7s %s";
        final String httpMethod;
        final String path;
        final String format;

        private EndpointLogLine(String httpMethod, String path, String format) {
            this.httpMethod = httpMethod;
            this.path = path;
            this.format = format == null ? DEFAULT_FORMAT : format;
        }

        @Override
        public String toString() {
            return String.format(format, httpMethod, path);
        }
    }
}