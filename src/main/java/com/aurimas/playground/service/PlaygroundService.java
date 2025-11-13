package com.aurimas.playground.service;

import com.aurimas.playground.repository.PlaygroundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaygroundService {

  private final PlaygroundRepository playgroundRepository;

  public PlaygroundService(PlaygroundRepository playgroundRepository) {
    this.playgroundRepository = playgroundRepository;
  }

  @Transactional(readOnly = true)
  public Integer getTotalVisitorCount() {
    return playgroundRepository.getTotalVisitorCount();
  }
}
