package com.aurimas.playground.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerStatus(String ticketNumber, Status status) {

  @JsonFormat(shape = JsonFormat.Shape.OBJECT)
  public enum Status {
    ADDED_TO_PLAYSITE("Customer successfully entered the playsite."),
    ADDED_TO_QUEUE("Playsite is full. Customer added to the waiting queue."),
    ALREADY_IN_PLAYSITE("Customer is already inside the playsite."),
    ALREADY_IN_QUEUE("Customer is already waiting in the queue for this playsite."),
    REJECTED_NO_WAIT_IN_QUEUE("Playsite is full and customer declined to wait in the queue."),
    REMOVED_FROM_PLAYSITE("Customer successfully exited the playsite.");

    private final String description;

    Status(String description) {
      this.description = description;
    }

    @JsonProperty("message")
    public String getDescription() {
      return description;
    }

    @JsonProperty("key")
    public String getKey() {
      return this.name();
    }
  }
}

