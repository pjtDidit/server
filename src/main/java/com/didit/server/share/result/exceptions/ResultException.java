package com.didit.server.share.result.exceptions;

import com.didit.server.share.result.ResultError;

import java.util.List;

public class ResultException extends RuntimeException {

    private final List<ResultError> errors;

    public ResultException(List<ResultError> errors) {
        super("Result failed: " + errors);
        this.errors = List.copyOf(errors);
    }

    public List<ResultError> getErrors() {
        return errors;
    }
}
