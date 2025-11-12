package com.aurimas.playground.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Playsite(
    Long id,
    @NotBlank(message = "Playsite name can not be blank")
    @Size(min = 1, max = 255, message = "number of characters must be between {min} and {max}")
    String name
) {
  public Playsite withId(Long id) {
    return new Playsite(id, name);
  }
}
