package eu.neoteric.starter.request.tracing;

import eu.neoteric.starter.StarterConstants;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class RestTemplateRequestIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String requestId = MDC.get(StarterConstants.REQUEST_ID_HEADER);
        if (StringUtils.hasLength(requestId)) {
            request.getHeaders().add(StarterConstants.REQUEST_ID_HEADER, requestId);
        }
        return execution.execute(request, body);
    }
}
