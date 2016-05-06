package com.neoteric.starter.mvc.errorhandling.handlers.common;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class MethodArgumentNotValidExceptionHandlerTest {

    private static final MockHttpServletRequest MOCK_REQUEST = new MockHttpServletRequest();
    private MethodArgumentNotValidExceptionHandler handler;

    @Before
    public void setUp() {
        handler = new MethodArgumentNotValidExceptionHandler();
    }

    @Test
    public void errorMessage() throws Exception {
        //TODO
        BindingResult error = new BeanPropertyBindingResult("bean", "message");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, error);
        Object o = handler.errorMessage(exception, MOCK_REQUEST);
    }


}