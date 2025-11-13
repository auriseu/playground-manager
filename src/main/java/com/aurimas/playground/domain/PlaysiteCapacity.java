package com.aurimas.playground.domain;

public record PlaysiteCapacity(
    int maxCapacity,
    int currentVisitorCount,
    double utilizationPrecent
) {
}
