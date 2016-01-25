package com.neoteric.starter.error;

import com.neoteric.starter.Constants;
import org.apache.catalina.connector.RequestFacade;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.time.ZonedDateTime;
import java.util.Optional;

public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {


    private static final HttpStatus GLOBAL_ERROR_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("Error", exception);
        Optional<RequestFacade> optionalRequestFacade = getRequestFacade();
        ErrorProperties.Builder errorBuilder = ErrorProperties
                .builder()
                .setTimestamp(ZonedDateTime.now())
                .setRequestId(String.valueOf(MDC.get(Constants.REQUEST_ID)))
                .setStatus(GLOBAL_ERROR_STATUS.value())
                .setError(GLOBAL_ERROR_STATUS.getReasonPhrase());

        if (optionalRequestFacade.isPresent()) {
            addPath(errorBuilder, optionalRequestFacade.get());
        }
        return Response.ok(errorBuilder.build()).build();
    }

    private void addPath(ErrorProperties.Builder errorBuilder, RequestFacade requestFacade) {
        errorBuilder.setPath(requestFacade.getRequestURI());
    }

    private Optional<RequestFacade> getRequestFacade() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return Optional.empty();
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        if (!(request instanceof RequestFacade)) {
            return Optional.empty();
        } else {
            return Optional.of((RequestFacade) request);
        }
    }
}