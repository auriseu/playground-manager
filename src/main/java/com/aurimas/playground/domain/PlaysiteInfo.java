package com.aurimas.playground.domain;

import java.util.List;

public record PlaysiteInfo(
    long id,
    String name,
    PlaysiteCapacity capacity,
    List<String> currentQueue,
    List<AttractionDetails> attractions
) {
}
