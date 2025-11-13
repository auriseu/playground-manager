package com.aurimas.playground.controller;

import com.aurimas.playground.service.PlaygroundService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/playground")
public class PlaygroundController {

  private final PlaygroundService playgroundService;

  public PlaygroundController(PlaygroundService playgroundService) {
    this.playgroundService = playgroundService;
  }

  @GetMapping("/visitors")
  public Integer getTotalVisitors() {
    return playgroundService.getTotalVisitorCount();
  }
}
