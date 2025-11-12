package com.aurimas.playground.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Customer(
    String ticketNumber,
    @NotBlank(message = "Name cannot be blank")
    String name,
    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be positive")
    Integer age
) {
  public Customer withId(String id) {
    return new Customer(id, name, age);
  }
}
