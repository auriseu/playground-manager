package com.aurimas.playground.controller;

import com.aurimas.playground.service.PlaygroundService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/playground")
@Tag(name = "Playground controller")
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
