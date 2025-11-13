package com.aurimas.playground.controller;

import com.aurimas.playground.domain.CustomerStatus;
import com.aurimas.playground.domain.Playsite;
import com.aurimas.playground.domain.PlaysiteAttraction;
import com.aurimas.playground.domain.PlaysiteInfo;
import com.aurimas.playground.service.PlaysiteAttractionService;
import com.aurimas.playground.service.PlaysiteCustomerService;
import com.aurimas.playground.service.PlaysiteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/playsites")
public class PlaysiteController {

  private final PlaysiteService playsiteService;
  private final PlaysiteAttractionService playsiteAttractionService;
  private final PlaysiteCustomerService playsiteCustomerService;

  public PlaysiteController(
      PlaysiteService playsiteService,
      PlaysiteAttractionService playsiteAttractionService,
      PlaysiteCustomerService playsiteCustomerService) {
    this.playsiteService = playsiteService;
    this.playsiteAttractionService = playsiteAttractionService;
    this.playsiteCustomerService = playsiteCustomerService;
  }

  @PostMapping
  public Playsite create(@RequestBody @Valid Playsite playsite) {
    return playsiteService.create(playsite);
  }

  @PutMapping("/{id}")
  public Playsite update(@PathVariable Long id, @RequestBody @Valid Playsite playsiteDetails) {
    return playsiteService.update(id, playsiteDetails);
  }

  @GetMapping("/{id}")
  public PlaysiteInfo getInfo(@PathVariable Long id) {
    return playsiteService.getInfo(id);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    playsiteService.delete(id);
  }

  @PostMapping("/{playsiteId}/attractions")
  public void addAttractions(
      @PathVariable Long playsiteId,
      @RequestBody @NotEmpty(message = "Attraction list cannot be empty")
      List<@NotNull(message = "Attraction must not be null") @Valid PlaysiteAttraction> attractions) {
    playsiteAttractionService.addAttractionsToPlaysite(playsiteId, attractions);
  }

  @PostMapping("/{playsiteId}/customers/{ticketNumber}")
  public ResponseEntity<CustomerStatus> addCustomerToPlaysite(
      @PathVariable Long playsiteId,
      @PathVariable String ticketNumber,
      @RequestParam(defaultValue = "true") Boolean waitInQueue) {

    CustomerStatus status = playsiteCustomerService.addCustomerToPlaysite(playsiteId, ticketNumber, waitInQueue);
    return switch (status.status()) {
      case ADDED_TO_PLAYSITE -> ResponseEntity.ok(status);
      case ALREADY_IN_QUEUE, ADDED_TO_QUEUE, ALREADY_IN_PLAYSITE ->
          ResponseEntity.status(HttpStatus.ACCEPTED).body(status);
      case REJECTED_NO_WAIT_IN_QUEUE -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
      default -> throw new IllegalStateException("Unexpected status: " + status.status());
    };
  }

  @DeleteMapping("/{playsiteId}/customers/{ticket}")
  public List<CustomerStatus> removeCustomerFromPlaysite(@PathVariable Long playsiteId, @PathVariable String ticket) {
    return playsiteCustomerService.removeCustomerFromPlaysite(playsiteId, ticket);
  }

  @DeleteMapping("/{playsiteId}/customers/{ticket}/queue")
  public void removeCustomerFromQueue(@PathVariable Long playsiteId, @PathVariable String ticket) {
    playsiteCustomerService.removeCustomerFromQueue(playsiteId, ticket);
  }
}
