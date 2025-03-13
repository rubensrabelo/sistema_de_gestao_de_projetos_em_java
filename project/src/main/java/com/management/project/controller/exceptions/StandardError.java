package com.management.project.controller.exceptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record StandardError(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}
