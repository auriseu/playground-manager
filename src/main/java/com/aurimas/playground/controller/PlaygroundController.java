package com.aurimas.playground.controller;

import com.aurimas.playground.service.PlaygroundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/playground")
@Tag(name = "Playground Management", description = "API for playground management")
public class PlaygroundController {

  private final PlaygroundService playgroundService;

  public PlaygroundController(PlaygroundService playgroundService) {
    this.playgroundService = playgroundService;
  }

  @Operation(summary = "Get total visitor count in all playsites")
  @GetMapping("/visitors")
  public Integer getTotalVisitors() {
    return playgroundService.getTotalVisitorCount();
  }
}
