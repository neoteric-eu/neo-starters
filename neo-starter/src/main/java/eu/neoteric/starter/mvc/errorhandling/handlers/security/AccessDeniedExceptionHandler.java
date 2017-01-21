package eu.neoteric.starter.mvc.errorhandling.handlers.security;

import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;

@RestExceptionHandlerProvider(httpStatus = HttpStatus.FORBIDDEN, suppressException = true)
public class AccessDeniedExceptionHandler implements RestExceptionHandler<AccessDeniedException> {

    @Override
    public Object errorMessage(AccessDeniedException ex, HttpServletRequest request) {
        return ex.getMessage();
    }

}