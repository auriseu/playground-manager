package com.aurimas.playground.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Attraction(
    Long id,

    @NotBlank(message = "Name cannot be blank")
    String name,

    @NotNull(message = "Max capacity can not be null")
    @Min(value = 1, message = "Max capacity must be greater than 0")
    Integer maxCapacity
) {
  public Attraction withId(Long id) {
    return new Attraction(id, name, maxCapacity);
  }
}
