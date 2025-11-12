package com.aurimas.playground.controller;

import com.aurimas.playground.domain.Attraction;
import com.aurimas.playground.service.AttractionService;
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
public class AttractionController {

  private final AttractionService attractionService;

  public AttractionController(AttractionService attractionService) {
    this.attractionService = attractionService;
  }

  @PostMapping
  public List<Attraction> createAttractions(@RequestBody List<@Valid Attraction> attractions) {
    return attractionService.createAttractions(attractions);
  }

  @PutMapping("/{id}")
  public Attraction updateAttraction(@PathVariable Long id, @RequestBody @Valid Attraction attractionDetails) {
    return attractionService.updateAttraction(id, attractionDetails);
  }

  @GetMapping
  public List<Attraction> getAllAttractions() {
    return attractionService.getAllAttractions();
  }

  @GetMapping("/{id}")
  public Attraction getAttraction(@PathVariable Long id) {
    return attractionService.getAttractionById(id);
  }

  @DeleteMapping("/{id}")
  public void deleteAttraction(@PathVariable Long id) {
    attractionService.deleteAttraction(id);
  }
}
