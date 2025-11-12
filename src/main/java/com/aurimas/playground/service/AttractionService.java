package com.aurimas.playground.service;

import com.aurimas.playground.domain.Attraction;
import com.aurimas.playground.exception.PlaygroundException;
import com.aurimas.playground.repository.AttractionRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttractionService {

  private final AttractionRepository attractionRepository;

  public AttractionService(AttractionRepository attractionRepository) {
    this.attractionRepository = attractionRepository;
  }

  @Transactional
  public List<Attraction> createAttractions(List<Attraction> attractions) {
    return attractionRepository.saveAll(attractions);
  }

  @Transactional
  public Attraction updateAttraction(Long id, Attraction updatedAttraction) {
    if (attractionRepository.findById(id).isEmpty()) {
      throw new NoSuchElementException("Attraction with ticketNumber " + id + " does not exist");
    }

    attractionRepository.update(updatedAttraction.withId(id));

    return attractionRepository.findById(id)
        .orElseThrow(() -> new PlaygroundException("Attraction updated but failed to retrieve: " + id));
  }

  public List<Attraction> getAllAttractions() {
    return attractionRepository.findAll();
  }

  public Attraction getAttractionById(Long id) {
    return attractionRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Attraction not found with ID: " + id));
  }

  public void deleteAttraction(Long id) {
    attractionRepository.deleteById(id);
  }
}
