package com.neoteric.starter.saasmgr.client.feign;

import com.neoteric.starter.saasmgr.client.SaasMgrClient;
import com.neoteric.starter.saasmgr.model.LoginData;
import com.neoteric.starter.saasmgr.model.LoginDataWrapper;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;

@Slf4j
public class FeignSaasMgrClient implements SaasMgrClient {

    @Autowired
    private SaasMgr saasMgr;

    @Override
    public LoginData getLoginInfo(String token, String customerId) {
        try {
            return saasMgr.getLoginInfo(token, customerId).getData();
        } catch (HystrixRuntimeException e) {
            if (e.getCause() instanceof FeignException) {
                FeignException feignException = (FeignException) e.getCause();
                if (feignException.status() == HttpStatus.UNAUTHORIZED.value()) {
                    throw new BadCredentialsException("SaasMgr authentication failed.", e);
                } else {
                    throw new AuthenticationServiceException("SaasMgr authentication returned error", e);
                }
            } else {
                LOG.error("Other Hystrix Exception: ", e);
                throw new AuthenticationServiceException("SaasMgr authentication returned error.", e);
            }
        } catch (RuntimeException e) {
            LOG.error("General error: ", e);
            throw new AuthenticationServiceException("SaasMgr authentication returned error.", e);
        }
    }
}
