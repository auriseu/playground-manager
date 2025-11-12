package com.aurimas.playground.controller;

import com.aurimas.playground.domain.Customer;
import com.aurimas.playground.service.CustomerService;
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
public class CustomerController {

  private final CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping
  public Customer createCustomer(@Valid @RequestBody Customer customer) {
    return customerService.createCustomer(customer);
  }

  @GetMapping("/{ticketNumber}")
  public Customer getCustomer(@PathVariable String ticketNumber) {
    return customerService.getCustomerByTicketNumber(ticketNumber);
  }

  @DeleteMapping("/{ticketNumber}")
  public void deleteCustomer(@PathVariable String ticketNumber) {
    customerService.deleteCustomer(ticketNumber);
  }
}
