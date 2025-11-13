package com.aurimas.playground.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.aurimas.playground.domain.CustomerStatus;
import com.aurimas.playground.domain.CustomerStatus.Status;
import com.aurimas.playground.domain.PlaysiteCapacity;
import com.aurimas.playground.repository.CustomerRepository;
import com.aurimas.playground.repository.PlaysiteCustomerRepository;
import com.aurimas.playground.repository.PlaysiteQueueRepository;
import com.aurimas.playground.repository.PlaysiteRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaysiteCustomerServiceTest {

  private static final Long PLAYSITE_ID = 1L;
  private static final String TICKET_NUMBER = "UUID-123";
  private static final String NEXT_QUEUE_TICKET = "UUID-456";

  @Mock
  private PlaysiteRepository playsiteRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private PlaysiteCustomerRepository playsiteCustomerRepository;
  @Mock
  private PlaysiteQueueRepository playsiteQueueRepository;

  @InjectMocks
  private PlaysiteCustomerService playsiteCustomerService;

  private PlaysiteCapacity availableCapacity;
  private PlaysiteCapacity fullCapacity;

  @BeforeEach
  void setUp() {
    availableCapacity = new PlaysiteCapacity(10, 9, 0.9);
    fullCapacity = new PlaysiteCapacity(10, 10, 1.0);
  }

  @Nested
  class AddPlaysiteCustomerTests {
    @Test
    @DisplayName("Should throw NoSuchElementException if Playsite does not exist")
    void addCustomerToPlaysite_PlaysiteNotFound_ThrowsException() {
      when(playsiteRepository.exists(PLAYSITE_ID)).thenReturn(false);

      var ex = assertThrows(NoSuchElementException.class,
          () -> playsiteCustomerService.addCustomerToPlaysite(PLAYSITE_ID, TICKET_NUMBER, true));

      assertTrue(ex.getMessage().contains(String.valueOf(PLAYSITE_ID)));
      verifyNoInteractions(customerRepository);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException if Customer does not exist")
    void addCustomerToPlaysite_CustomerNotFound_ThrowsException() {
      when(playsiteRepository.exists(PLAYSITE_ID)).thenReturn(true);
      when(customerRepository.exists(TICKET_NUMBER)).thenReturn(false);

      var ex = assertThrows(NoSuchElementException.class,
          () -> playsiteCustomerService.addCustomerToPlaysite(PLAYSITE_ID, TICKET_NUMBER, true));

      assertTrue(ex.getMessage().contains(TICKET_NUMBER));
      verifyNoInteractions(playsiteCustomerRepository);
    }

    @Test
    @DisplayName("Should return ALREADY_IN_PLAYSITE if customer is already inside")
    void addCustomerToPlaysite_CustomerAlreadyInside_ReturnsAlreadyInPlaysite() {
      setupSuccessfulExistence();

      when(playsiteCustomerRepository.isInPlaysite(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(true);

      CustomerStatus status = playsiteCustomerService.addCustomerToPlaysite(PLAYSITE_ID, TICKET_NUMBER, true);

      assertEquals(Status.ALREADY_IN_PLAYSITE, status.status());
      verifyNoMoreInteractions(playsiteRepository);
    }

    @Test
    @DisplayName("Should successfully ADD_TO_PLAYSITE when capacity is available")
    void addCustomerToPlaysite_CapacityAvailable_AddsToPlaysite() {
      setupSuccessfulExistence();
      when(playsiteCustomerRepository.isInPlaysite(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(false);
      when(playsiteRepository.getCapacity(PLAYSITE_ID)).thenReturn(Optional.of(availableCapacity));

      CustomerStatus status = playsiteCustomerService.addCustomerToPlaysite(PLAYSITE_ID, TICKET_NUMBER, true);

      assertEquals(Status.ADDED_TO_PLAYSITE, status.status());
      verify(playsiteCustomerRepository).addCustomer(PLAYSITE_ID, TICKET_NUMBER);
      verifyNoInteractions(playsiteQueueRepository);
    }

    @Test
    @DisplayName("Should return REJECTED_NO_WAIT_IN_QUEUE when full and waitInQueue is false")
    void addCustomerToPlaysite_FullNoWait_ReturnsRejected() {
      setupSuccessfulExistence();
      when(playsiteCustomerRepository.isInPlaysite(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(false);
      when(playsiteRepository.getCapacity(PLAYSITE_ID)).thenReturn(Optional.of(fullCapacity));

      CustomerStatus status = playsiteCustomerService.addCustomerToPlaysite(PLAYSITE_ID, TICKET_NUMBER, false);

      assertEquals(Status.REJECTED_NO_WAIT_IN_QUEUE, status.status());
      verify(playsiteCustomerRepository, never()).addCustomer(any(), any());
      verifyNoInteractions(playsiteQueueRepository);
    }

    @Test
    @DisplayName("Should return ALREADY_IN_QUEUE when full and customer is already waiting")
    void addCustomerToPlaysite_FullAndAlreadyInQueue_ReturnsAlreadyInQueue() {
      setupSuccessfulExistence();
      when(playsiteCustomerRepository.isInPlaysite(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(false);
      when(playsiteRepository.getCapacity(PLAYSITE_ID)).thenReturn(Optional.of(fullCapacity));
      when(playsiteQueueRepository.isInQueue(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(true);

      CustomerStatus status = playsiteCustomerService.addCustomerToPlaysite(PLAYSITE_ID, TICKET_NUMBER, true);

      assertEquals(Status.ALREADY_IN_QUEUE, status.status());
      verify(playsiteCustomerRepository, never()).addCustomer(any(), any());
      verify(playsiteQueueRepository, never()).addCustomerToQueue(any(), any());
    }

    @Test
    @DisplayName("Should return ADDED_TO_QUEUE when full and customer is added to queue")
    void addCustomerToPlaysite_FullAndAddedToQueue_ReturnsAddedToQueue() {
      setupSuccessfulExistence();
      when(playsiteCustomerRepository.isInPlaysite(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(false);
      when(playsiteRepository.getCapacity(PLAYSITE_ID)).thenReturn(Optional.of(fullCapacity));
      when(playsiteQueueRepository.isInQueue(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(false);

      CustomerStatus status = playsiteCustomerService.addCustomerToPlaysite(PLAYSITE_ID, TICKET_NUMBER, true);

      assertEquals(Status.ADDED_TO_QUEUE, status.status());
      verify(playsiteCustomerRepository, never()).addCustomer(any(), any());
      verify(playsiteQueueRepository).addCustomerToQueue(PLAYSITE_ID, TICKET_NUMBER);
    }
  }

  @Nested
  class RemovePlaysiteCustomerFromPlaysiteTests {
    @Test
    @DisplayName("Should throw NoSuchElementException if Playsite does not exist")
    void removeCustomerFromPlaysite_PlaysiteNotFound_ThrowsException() {
      when(playsiteRepository.exists(PLAYSITE_ID)).thenReturn(false);

      var ex = assertThrows(NoSuchElementException.class,
          () -> playsiteCustomerService.removeCustomerFromPlaysite(PLAYSITE_ID, TICKET_NUMBER));

      assertTrue(ex.getMessage().contains(String.valueOf(PLAYSITE_ID)));
      verifyNoInteractions(customerRepository);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException if Customer does not exist")
    void removeCustomerFromPlaysite_CustomerNotFound_ThrowsException() {
      when(playsiteRepository.exists(PLAYSITE_ID)).thenReturn(true);
      when(customerRepository.exists(TICKET_NUMBER)).thenReturn(false);

      var ex = assertThrows(NoSuchElementException.class,
          () -> playsiteCustomerService.removeCustomerFromPlaysite(PLAYSITE_ID, TICKET_NUMBER));

      assertTrue(ex.getMessage().contains(TICKET_NUMBER));
      verifyNoInteractions(playsiteCustomerRepository);
    }

    @Test
    @DisplayName("Should remove customer and move next from queue when queue is not empty")
    void removeCustomerFromPlaysite_QueueNotEmpty_MovesNextCustomer() {
      setupSuccessfulExistence();
      when(playsiteQueueRepository.getFirstFromQueue(PLAYSITE_ID)).thenReturn(Optional.of(NEXT_QUEUE_TICKET));

      List<CustomerStatus> statuses = playsiteCustomerService.removeCustomerFromPlaysite(PLAYSITE_ID, TICKET_NUMBER);

      assertEquals(2, statuses.size());
      assertEquals(Status.REMOVED_FROM_PLAYSITE, statuses.get(0).status());
      assertEquals(Status.ADDED_TO_PLAYSITE, statuses.get(1).status());
      assertEquals(NEXT_QUEUE_TICKET, statuses.get(1).ticketNumber());

      verify(playsiteCustomerRepository).removeCustomer(PLAYSITE_ID, TICKET_NUMBER);
      verify(playsiteQueueRepository).removeCustomerFromQueue(PLAYSITE_ID, NEXT_QUEUE_TICKET);
      verify(playsiteCustomerRepository).addCustomer(PLAYSITE_ID, NEXT_QUEUE_TICKET);
    }

    @Test
    @DisplayName("Should only remove customer when queue is empty")
    void removeCustomerFromPlaysite_QueueEmpty_OnlyRemovesCustomer() {
      setupSuccessfulExistence();
      when(playsiteQueueRepository.getFirstFromQueue(PLAYSITE_ID)).thenReturn(Optional.empty());

      List<CustomerStatus> statuses = playsiteCustomerService.removeCustomerFromPlaysite(PLAYSITE_ID, TICKET_NUMBER);

      assertEquals(1, statuses.size());
      assertEquals(Status.REMOVED_FROM_PLAYSITE, statuses.getFirst().status());

      verify(playsiteCustomerRepository).removeCustomer(PLAYSITE_ID, TICKET_NUMBER);
      verify(playsiteQueueRepository, never()).removeCustomerFromQueue(any(), any());
      verify(playsiteCustomerRepository, never()).addCustomer(any(), any());
    }
  }

  @Nested
  class RemovePlaysiteCustomerFromQueueTests {
    @Test
    @DisplayName("Should throw NoSuchElementException if Playsite does not exist")
    void removeCustomerFromPlaysiteQueue_PlaysiteNotFound_ThrowsException() {
      when(playsiteRepository.exists(PLAYSITE_ID)).thenReturn(false);

      var ex = assertThrows(NoSuchElementException.class,
          () -> playsiteCustomerService.removeCustomerFromQueue(PLAYSITE_ID, TICKET_NUMBER));

      assertTrue(ex.getMessage().contains(String.valueOf(PLAYSITE_ID)));
      verifyNoInteractions(customerRepository);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException if Customer does not exist")
    void removeCustomerFromPlaysiteQueue_CustomerNotFound_ThrowsException() {
      when(playsiteRepository.exists(PLAYSITE_ID)).thenReturn(true);
      when(customerRepository.exists(TICKET_NUMBER)).thenReturn(false);

      var ex = assertThrows(NoSuchElementException.class,
          () -> playsiteCustomerService.removeCustomerFromQueue(PLAYSITE_ID, TICKET_NUMBER));

      assertTrue(ex.getMessage().contains(TICKET_NUMBER));
      verifyNoInteractions(playsiteCustomerRepository);
    }

    @Test
    @DisplayName("Should remove customer if they are in the queue")
    void removeCustomerFromQueue_CustomerIsInQueue_RemovesFromQueue() {
      setupSuccessfulExistence();
      when(playsiteQueueRepository.isInQueue(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(true);

      assertDoesNotThrow(() -> playsiteCustomerService.removeCustomerFromQueue(PLAYSITE_ID, TICKET_NUMBER));

      verify(playsiteQueueRepository).removeCustomerFromQueue(PLAYSITE_ID, TICKET_NUMBER);
    }

    @Test
    @DisplayName("Should do nothing if customer is not in the queue")
    void removeCustomerFromQueue_CustomerIsNotInQueue_DoesNothing() {
      setupSuccessfulExistence();
      when(playsiteQueueRepository.isInQueue(PLAYSITE_ID, TICKET_NUMBER)).thenReturn(false);

      assertDoesNotThrow(() -> playsiteCustomerService.removeCustomerFromQueue(PLAYSITE_ID, TICKET_NUMBER));

      verify(playsiteQueueRepository, never()).removeCustomerFromQueue(any(), any());
    }
  }

  private void setupSuccessfulExistence() {
    when(playsiteRepository.exists(PLAYSITE_ID)).thenReturn(true);
    when(customerRepository.exists(TICKET_NUMBER)).thenReturn(true);
  }
}
