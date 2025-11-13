package com.aurimas.playground.service;

import com.aurimas.playground.domain.PlaysiteAttraction;
import com.aurimas.playground.repository.AttractionRepository;
import com.aurimas.playground.repository.PlaysiteAttractionRepository;
import com.aurimas.playground.repository.PlaysiteRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaysiteAttractionService {

  public static final String PLAYSITE_NOT_FOUND = "Playsite not found with ID: %s";
  public static final String ATTRACTION_DO_NOT_EXIST = "One or more Attraction IDs do not exist";

  private final PlaysiteAttractionRepository playsiteAttractionRepository;
  private final PlaysiteRepository playsiteRepository;
  private final AttractionRepository attractionRepository;

  public PlaysiteAttractionService(
      PlaysiteAttractionRepository playsiteAttractionRepository,
      PlaysiteRepository playsiteRepository,
      AttractionRepository attractionRepository) {
    this.playsiteAttractionRepository = playsiteAttractionRepository;
    this.playsiteRepository = playsiteRepository;
    this.attractionRepository = attractionRepository;
  }

  @Transactional
  public void addAttractionsToPlaysite(Long playsiteId, List<PlaysiteAttraction> attractions) {

    if (!playsiteRepository.exists(playsiteId)) {
      throw new NoSuchElementException(PLAYSITE_NOT_FOUND.formatted(playsiteId));
    }

    List<Long> ids = attractions.stream().map(PlaysiteAttraction::attractionId).toList();
    if (attractionRepository.countExistingAttractions(ids) != ids.size()) {
      throw new NoSuchElementException(ATTRACTION_DO_NOT_EXIST);
    }

    playsiteAttractionRepository.addAttractions(playsiteId, attractions);
  }
}
