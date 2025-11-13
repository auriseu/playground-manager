package com.aurimas.playground.service;

import com.aurimas.playground.domain.Attraction;
import com.aurimas.playground.repository.AttractionRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttractionService {

  public static final String ATTRACTION_NOT_FOUND = "Attraction not found with ID: %s";

  private final AttractionRepository attractionRepository;

  public AttractionService(AttractionRepository attractionRepository) {
    this.attractionRepository = attractionRepository;
  }

  @Transactional
  public List<Attraction> createAll(List<Attraction> attractions) {
    return attractionRepository.createAll(attractions);
  }

  public Attraction update(Long id, Attraction attraction) {
    if (!attractionRepository.exists(id)) {
      throw new NoSuchElementException(ATTRACTION_NOT_FOUND.formatted(id));
    }

    attractionRepository.update(attraction.withId(id));

    return attraction.withId(id);
  }

  public Attraction get(Long id) {
    return attractionRepository.get(id)
        .orElseThrow(() -> new NoSuchElementException(ATTRACTION_NOT_FOUND.formatted(id)));
  }

  public void delete(Long id) {
    attractionRepository.delete(id);
  }
}
