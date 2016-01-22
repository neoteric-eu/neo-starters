package com.neoteric.starter.metrics.report.elastic;

public class ElasticsearchConnectionException extends RuntimeException {

    public ElasticsearchConnectionException(String message) {
        super(message);
    }

    public ElasticsearchConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
