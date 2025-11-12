package com.aurimas.playground.domain;

import java.util.List;

public record PlaysiteInfo(
    long id,
    String name,
   int maxCapacity,
    int currentVisitorCount,
    double utilizationPrecent,
   /*  int currentQueue,*/
    List<AttractionDetails> attractions
) {
}
