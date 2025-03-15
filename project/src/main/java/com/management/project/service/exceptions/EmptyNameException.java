package com.management.project.service.exceptions;

import java.io.Serial;

public class EmptyNameException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmptyNameException(String message) {
        super(message);
    }
}
