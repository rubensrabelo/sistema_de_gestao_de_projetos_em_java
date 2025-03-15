package com.management.project.service.exceptions;

import java.io.Serial;

public class InvalidNameSizeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidNameSizeException(String message) {
        super(message);
    }
}
