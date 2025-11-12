package com.aurimas.playground.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlaysiteAttraction(
    @NotNull(message = "Attraction ID can not be null")
    Long attractionId,
    @Min(value = 1, message = "Amount must be greater than 0")
    Integer amount) {
}
