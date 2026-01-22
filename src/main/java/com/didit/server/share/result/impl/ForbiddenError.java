package com.didit.server.share.result.impl;

import java.util.Map;

public class ForbiddenError extends SimpleError{
    public ForbiddenError(String message) {
        super(403, message);
    }
}
