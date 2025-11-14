package com.aurimas.playground.controller;

import com.aurimas.playground.domain.Attraction;
import com.aurimas.playground.service.AttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attractions")
@Tag(name = "Attraction Management", description = "API for attraction management")
public class AttractionController {

  private final AttractionService attractionService;

  public AttractionController(AttractionService attractionService) {
    this.attractionService = attractionService;
  }

  @Operation(summary = "Create multiple attractions at once")
  @PostMapping
  public List<Attraction> createAll(@RequestBody List<@Valid Attraction> attractions) {
    return attractionService.createAll(attractions);
  }

  @Operation(summary = "Update existing attraction")
  @PutMapping("/{id}")
  public Attraction update(@PathVariable Long id, @RequestBody @Valid Attraction attractionDetails) {
    return attractionService.update(id, attractionDetails);
  }

  @Operation(summary = "Get Attraction information")
  @GetMapping("/{id}")
  public Attraction get(@PathVariable Long id) {
    return attractionService.get(id);
  }

  @Operation(summary = "Delete attraction")
  @DeleteMapping("/{id}")
  public void deleteAttraction(@PathVariable Long id) {
    attractionService.delete(id);
  }
}
