package com.neoteric.starter.saasmgr.client;

import com.neoteric.starter.saasmgr.model.LoginData;
import com.neoteric.starter.saasmgr.model.LoginDataWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.json.XMLTokener.entity;

@Slf4j
@AllArgsConstructor
public class RestTemplateSaasMgrClient implements SaasMgrClient {

    private final String hostName;
    private final RestTemplate restTemplate;

    @Override
    public LoginData getLoginInfo(String token, String customerId) {
        URI targetUrl = UriComponentsBuilder.fromUriString(hostName)
                .path("/api/v2/users/authToken")
                .build().toUri();

        try {
            LoginDataWrapper wrapper = restTemplate.getForObject(targetUrl, LoginDataWrapper.class);
            return wrapper.getData();
        } catch (HttpClientErrorException e) {
            if (HttpStatus.UNAUTHORIZED.equals(e.getStatusCode())) {
                throw new BadCredentialsException("SaasMgr authentication failed.", e);
            }
            throw new IllegalStateException(e);
        } catch (Exception e) {
            throw new AuthenticationServiceException("SaasMgr authentication returned error.", e);
        }
    }
}
