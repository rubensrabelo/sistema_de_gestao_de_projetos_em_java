package com.management.project.service.exceptions;

import java.io.Serial;

public class DuplicateAssignmentException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateAssignmentException(String message) {
        super(message);
    }
}
