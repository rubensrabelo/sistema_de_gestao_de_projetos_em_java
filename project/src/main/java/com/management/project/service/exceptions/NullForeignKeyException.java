package com.management.project.service.exceptions;

import java.io.Serial;

public class NullForeignKeyException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NullForeignKeyException(String message) {
        super(message);
    }
}
