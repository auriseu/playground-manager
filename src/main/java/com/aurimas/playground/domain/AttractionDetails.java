package com.aurimas.playground.domain;

public record AttractionDetails(
    Long attractionId,
    String name,
    Integer maxCapacity,
    Integer amountInPlaysite
) {
}
