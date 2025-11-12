package com.aurimas.playground.controller;

import com.aurimas.playground.domain.Playsite;
import com.aurimas.playground.domain.PlaysiteAttraction;
import com.aurimas.playground.domain.PlaysiteInfo;
import com.aurimas.playground.service.PlaysiteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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

  public PlaysiteController(PlaysiteService playsiteService) {
    this.playsiteService = playsiteService;
  }

  @PostMapping
  public Playsite createPlaysite(@RequestBody @Valid Playsite playsite) {
    return playsiteService.createPlaysite(playsite);
  }

  @PutMapping("/{id}")
  public Playsite updatePlaysite(@PathVariable Long id, @RequestBody @Valid Playsite playsiteDetails) {
    return playsiteService.updatePlaysite(id, playsiteDetails);
  }

  @GetMapping
  public List<Playsite> getAllPlaysites() {
    return playsiteService.getAllPlaysites();
  }

  @GetMapping("/{id}")
  public Playsite getPlaysite(@PathVariable Long id) {
    return playsiteService.getPlaysiteById(id);
  }

  @GetMapping("/{id}/info")
  public PlaysiteInfo getPlaysiteInfo(@PathVariable Long id) {
    return playsiteService.getPlaysiteInfoById(id);
  }

  @DeleteMapping("/{id}")
  public void deletePlaysite(@PathVariable Long id) {
    playsiteService.deletePlaysite(id);
  }

  @PostMapping("/{playsiteId}/attractions")
  public void addAttractions(
      @PathVariable Long playsiteId,
      @RequestBody @NotEmpty(message = "Attraction list cannot be empty")
      List<@NotNull(message = "Attraction must not be null") @Valid PlaysiteAttraction> attractions) {
    playsiteService.addAttractionsToPlaysite(playsiteId, attractions);
  }

  @PostMapping("/{playsiteId}/customers/{ticketNumber}")
  public void addCustomerToPlaysite(
      @PathVariable Long playsiteId,
      @PathVariable String ticketNumber,
      @RequestParam(defaultValue = "true") Boolean waitInQueue) {
    playsiteService.addCustomerToPlaysite(playsiteId, ticketNumber, waitInQueue);
  }

  @DeleteMapping("/{playsiteId}/customers/{ticketNumber}")
  public void removeCustomerFromPlaysite(@PathVariable Long playsiteId, @PathVariable String ticketNumber) {
    playsiteService.removeCustomerFromPlaysite(playsiteId, ticketNumber);
  }
}
