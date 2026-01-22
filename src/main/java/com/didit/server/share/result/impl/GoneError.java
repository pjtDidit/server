package com.didit.server.share.result.impl;

public class GoneError extends SimpleError {
    public GoneError(String message) {
        super(410, message);
    }
}
