package com.neoteric.starter.exception.mapper;

public interface ExceptionMapper<E extends Throwable> {

    Response toResponse(E exception);
}
