package com.aurimas.playground.controller;

import com.aurimas.playground.domain.Customer;
import com.aurimas.playground.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@Validated
@Tag(name = "Customer Management", description = "API for customer management")
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @Operation(summary = "Create customer")
  @PostMapping
  public Customer create(@Valid @RequestBody Customer customer) {
    return customerService.create(customer);
  }

  @Operation(summary = "Get customer information")
  @GetMapping("/{ticketNumber}")
  public Customer getByTicketNumber(@PathVariable String ticketNumber) {
    return customerService.getByTicketNumber(ticketNumber);
  }

  @Operation(summary = "Delete customer")
  @DeleteMapping("/{ticketNumber}")
  public void delete(@PathVariable String ticketNumber) {
    customerService.delete(ticketNumber);
  }
}
