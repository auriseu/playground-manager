package com.aurimas.playground.service;

import com.aurimas.playground.domain.CustomerStatus;
import com.aurimas.playground.domain.PlaysiteCapacity;
import com.aurimas.playground.repository.CustomerRepository;
import com.aurimas.playground.repository.PlaysiteCustomerRepository;
import com.aurimas.playground.repository.PlaysiteQueueRepository;
import com.aurimas.playground.repository.PlaysiteRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaysiteCustomerService {

  public static final String PLAYSITE_NOT_FOUND = "Playsite not found with ID: %s";
  public static final String CUSTOMER_NOT_FOUND = "Customer not found with ticketNumber: %s";

  private final PlaysiteRepository playsiteRepository;
  private final CustomerRepository customerRepository;
  private final PlaysiteCustomerRepository playsiteCustomerRepository;
  private final PlaysiteQueueRepository playsiteQueueRepository;

  public PlaysiteCustomerService(
      PlaysiteRepository playsiteRepository,
      CustomerRepository customerRepository,
      PlaysiteCustomerRepository playsiteCustomerRepository,
      PlaysiteQueueRepository playsiteQueueRepository) {
    this.playsiteRepository = playsiteRepository;
    this.customerRepository = customerRepository;
    this.playsiteCustomerRepository = playsiteCustomerRepository;
    this.playsiteQueueRepository = playsiteQueueRepository;
  }

  public CustomerStatus addCustomerToPlaysite(Long playsiteId, String ticketNumber, boolean waitInQueue) {

    if (!playsiteRepository.exists(playsiteId)) {
      throw new NoSuchElementException(PLAYSITE_NOT_FOUND.formatted(playsiteId));
    }

    if (!customerRepository.exists(ticketNumber)) {
      throw new NoSuchElementException(CUSTOMER_NOT_FOUND.formatted(ticketNumber));
    }

    if (playsiteCustomerRepository.isInPlaysite(playsiteId, ticketNumber)) {
      return new CustomerStatus(ticketNumber, CustomerStatus.Status.ALREADY_IN_PLAYSITE);
    }

    PlaysiteCapacity capacity = playsiteRepository.getCapacity(playsiteId)
        .orElseThrow(() -> new NoSuchElementException(PLAYSITE_NOT_FOUND.formatted(playsiteId)));

    if (capacity.currentVisitorCount() < capacity.maxCapacity()) {
      playsiteCustomerRepository.addCustomer(playsiteId, ticketNumber);
      return new CustomerStatus(ticketNumber, CustomerStatus.Status.ADDED_TO_PLAYSITE);
    }

    if (!waitInQueue) {
      return new CustomerStatus(ticketNumber, CustomerStatus.Status.REJECTED_NO_WAIT_IN_QUEUE);
    }

    if (playsiteQueueRepository.isInQueue(playsiteId, ticketNumber)) {
      return new CustomerStatus(ticketNumber, CustomerStatus.Status.ALREADY_IN_QUEUE);
    }

    playsiteQueueRepository.addCustomerToQueue(playsiteId, ticketNumber);

    return new CustomerStatus(ticketNumber, CustomerStatus.Status.ADDED_TO_QUEUE);
  }

  @Transactional
  public List<CustomerStatus> removeCustomerFromPlaysite(Long playsiteId, String ticketNumber) {
    if (!playsiteRepository.exists(playsiteId)) {
      throw new NoSuchElementException(PLAYSITE_NOT_FOUND.formatted(playsiteId));
    }

    if (!customerRepository.exists(ticketNumber)) {
      throw new NoSuchElementException(CUSTOMER_NOT_FOUND.formatted(ticketNumber));
    }

    playsiteCustomerRepository.removeCustomer(playsiteId, ticketNumber);

    List<CustomerStatus> customerStatuses = new LinkedList<>();
    customerStatuses.add(new CustomerStatus(ticketNumber, CustomerStatus.Status.REMOVED_FROM_PLAYSITE));

    playsiteQueueRepository.getFirstFromQueue(playsiteId)
        .ifPresent(ticket -> {
          playsiteQueueRepository.removeCustomerFromQueue(playsiteId, ticket);
          playsiteCustomerRepository.addCustomer(playsiteId, ticket);
          customerStatuses.add(new CustomerStatus(ticket, CustomerStatus.Status.ADDED_TO_PLAYSITE));
        });

    return customerStatuses;
  }

  public void removeCustomerFromQueue(Long playsiteId, String ticketNumber) {
    if (!playsiteRepository.exists(playsiteId)) {
      throw new NoSuchElementException(PLAYSITE_NOT_FOUND.formatted(playsiteId));
    }

    if (!customerRepository.exists(ticketNumber)) {
      throw new NoSuchElementException(CUSTOMER_NOT_FOUND.formatted(ticketNumber));
    }

    if (playsiteQueueRepository.isInQueue(playsiteId, ticketNumber)) {
      playsiteQueueRepository.removeCustomerFromQueue(playsiteId, ticketNumber);
    }
  }
}
