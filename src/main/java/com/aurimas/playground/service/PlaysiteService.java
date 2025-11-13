package com.aurimas.playground.service;

import com.aurimas.playground.domain.Playsite;
import com.aurimas.playground.domain.PlaysiteInfo;
import com.aurimas.playground.repository.PlaysiteRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaysiteService {

  public static final String PLAYSITE_NOT_FOUND = "Playsite not found with ID: %s";

  private final PlaysiteRepository playsiteRepository;

  public PlaysiteService(PlaysiteRepository playsiteRepository) {
    this.playsiteRepository = playsiteRepository;
  }

  public Playsite create(Playsite playsite) {
    return playsite.withId(playsiteRepository.create(playsite));
  }

  public Playsite update(Long id, Playsite playsite) {
    if (!playsiteRepository.exists(id)) {
      throw new NoSuchElementException(PLAYSITE_NOT_FOUND.formatted(id));
    }

    playsiteRepository.update(playsite.withId(id));

    return playsite.withId(id);
  }

  @Transactional(readOnly = true)
  public PlaysiteInfo getInfo(Long id) {
    return playsiteRepository.getInfo(id)
        .orElseThrow(() -> new NoSuchElementException(PLAYSITE_NOT_FOUND.formatted(id)));
  }

  public void delete(Long id) {
    playsiteRepository.delete(id);
  }
}
