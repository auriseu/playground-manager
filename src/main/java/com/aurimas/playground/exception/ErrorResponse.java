package com.aurimas.playground.exception;

import java.util.List;

public record ErrorResponse(int status, String exception, List<String> messages) {
}
