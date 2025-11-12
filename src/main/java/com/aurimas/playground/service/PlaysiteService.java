package com.aurimas.playground.service;

import com.aurimas.playground.domain.Playsite;
import com.aurimas.playground.domain.PlaysiteAttraction;
import com.aurimas.playground.domain.PlaysiteInfo;
import com.aurimas.playground.exception.PlaygroundException;
import com.aurimas.playground.repository.AttractionRepository;
import com.aurimas.playground.repository.CustomerRepository;
import com.aurimas.playground.repository.PlaysiteRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaysiteService {

  private final PlaysiteRepository playsiteRepository;
  private final AttractionRepository attractionRepository;
  private final CustomerRepository customerRepository;

  public PlaysiteService(
      PlaysiteRepository playsiteRepository,
      AttractionRepository attractionRepository,
      CustomerRepository customerRepository) {
    this.playsiteRepository = playsiteRepository;
    this.attractionRepository = attractionRepository;
    this.customerRepository = customerRepository;
  }

  public Playsite createPlaysite(Playsite playsite) {
    return playsite.withId(playsiteRepository.save(playsite));
  }

  @Transactional
  public Playsite updatePlaysite(Long id, Playsite playsite) {
    if (playsiteRepository.findById(id).isEmpty()) {
      throw new NoSuchElementException("Playsite with ticketNumber " + id + " does not exist");
    }

    playsiteRepository.update(playsite.withId(id));

    return playsiteRepository.findById(id)
        .orElseThrow(() -> new PlaygroundException("Playsite updated but failed to retrieve: " + id));
  }

  public List<Playsite> getAllPlaysites() {
    return playsiteRepository.findAll();
  }

  public Playsite getPlaysiteById(Long id) {
    return playsiteRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Playsite not found with ID: " + id));
  }

  public PlaysiteInfo getPlaysiteInfoById(Long id) {
    return playsiteRepository.getPlaysiteInfoById(id)
        .orElseThrow(() -> new NoSuchElementException("Playsite info not found with ID: " + id));
  }

  public void deletePlaysite(Long id) {
    playsiteRepository.deleteById(id);
  }

  @Transactional
  public void addAttractionsToPlaysite(Long playsiteId, List<PlaysiteAttraction> attractions) {

    if (playsiteRepository.findById(playsiteId).isEmpty()) {
      throw new NoSuchElementException("Playsite not found with ID: " + playsiteId);
    }

    List<Long> ids = attractions.stream().map(PlaysiteAttraction::attractionId).toList();
    if (attractionRepository.countExistingAttractions(ids) != ids.size()) {
      throw new NoSuchElementException("One or more Attraction IDs do not exist.");
    }

    playsiteRepository.addAttractions(playsiteId, attractions);
  }

  public void addCustomerToPlaysite(Long playsiteId, String ticketNumber, boolean waitInQueue) {

    if (playsiteRepository.findById(playsiteId).isEmpty()) {
      throw new NoSuchElementException("Playsite not found with ID: " + playsiteId);
    }

    if (customerRepository.findById(ticketNumber).isEmpty()) {
      throw new NoSuchElementException("Customer not found with ticketNumber: " + ticketNumber);
    }

    // TODO: Add to queue if accepts wait

    playsiteRepository.addCustomer(playsiteId, ticketNumber);
  }

  public void removeCustomerFromPlaysite(Long playsiteId, String ticketNumber) {
    if (playsiteRepository.findById(playsiteId).isEmpty()) {
      throw new NoSuchElementException("Playsite not found with ID: " + playsiteId);
    }

    if (customerRepository.findById(ticketNumber).isEmpty()) {
      throw new NoSuchElementException("Customer not found with ticketNumber: " + ticketNumber);
    }

    playsiteRepository.removeCustomer(playsiteId, ticketNumber);

    // TODO: Check queue and add waiting customer
  }
}
