package com.aurimas.playground.service;

import com.aurimas.playground.domain.Customer;
import com.aurimas.playground.repository.CustomerRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Customer createCustomer(Customer customer) {
    return customer.withId(customerRepository.save(customer));
  }

  public Customer getCustomerByTicketNumber(String id) {
    return customerRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Customer not found with ticket: " + id));
  }

  public void deleteCustomer(String id) {
    customerRepository.deleteById(id);
  }
}
