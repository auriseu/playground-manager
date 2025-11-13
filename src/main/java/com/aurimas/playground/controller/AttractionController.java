package com.aurimas.playground.controller;

import com.aurimas.playground.domain.Attraction;
import com.aurimas.playground.service.AttractionService;
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
@Tag(name = "Attraction controller")
public class AttractionController {

  private final AttractionService attractionService;

  public AttractionController(AttractionService attractionService) {
    this.attractionService = attractionService;
  }

  @PostMapping
  public List<Attraction> createAll(@RequestBody List<@Valid Attraction> attractions) {
    return attractionService.createAll(attractions);
  }

  @PutMapping("/{id}")
  public Attraction update(@PathVariable Long id, @RequestBody @Valid Attraction attractionDetails) {
    return attractionService.update(id, attractionDetails);
  }

  @GetMapping("/{id}")
  public Attraction get(@PathVariable Long id) {
    return attractionService.get(id);
  }

  @DeleteMapping("/{id}")
  public void deleteAttraction(@PathVariable Long id) {
    attractionService.delete(id);
  }
}
