package eu.neoteric.starter.mvc.errorhandling.handlers.common;

import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestExceptionHandlerProvider(httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class HttpMediaTypeNotSupportedExceptionHandler implements RestExceptionHandler<HttpMediaTypeNotSupportedException> {

    private static final String ACCEPT_HEADER = "Accept";

    @Override
    public Object errorMessage(HttpMediaTypeNotSupportedException exception, HttpServletRequest req) {
        return exception.getMessage();
    }

    @Override
    public void customizeResponse(HttpMediaTypeNotSupportedException exception, HttpServletRequest req, HttpServletResponse resp) {
        List<MediaType> mediaTypes = exception.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            resp.setHeader(ACCEPT_HEADER, MediaType.toString(mediaTypes));
        }
    }
}
