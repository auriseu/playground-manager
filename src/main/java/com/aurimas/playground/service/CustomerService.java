package com.aurimas.playground.service;

import com.aurimas.playground.domain.Customer;
import com.aurimas.playground.repository.CustomerRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  public static final String CUSTOMER_NOT_FOUND = "Customer not found with ticket: %s";

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Customer create(Customer customer) {
    return customer.withId(customerRepository.save(customer));
  }

  public Customer getByTicketNumber(String ticketNumber) {
    return customerRepository.get(ticketNumber)
        .orElseThrow(() -> new NoSuchElementException(CUSTOMER_NOT_FOUND.formatted(ticketNumber)));
  }

  public void delete(String id) {
    customerRepository.delete(id);
  }
}
