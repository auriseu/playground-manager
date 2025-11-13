package com.aurimas.playground.repository;

import static com.aurimas.playground.schema.Tables.PLAYSITE_CUSTOMERS;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class PlaysiteCustomerRepository {

  private final DSLContext dsl;

  public PlaysiteCustomerRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public void addCustomer(Long playsiteId, String ticketNumber) {
    dsl.insertInto(PLAYSITE_CUSTOMERS)
        .set(PLAYSITE_CUSTOMERS.PLAYSITE, playsiteId)
        .set(PLAYSITE_CUSTOMERS.CUSTOMER, ticketNumber)
        .execute();
  }

  public void removeCustomer(Long playsiteId, String ticketNumber) {
    dsl.deleteFrom(PLAYSITE_CUSTOMERS)
        .where(PLAYSITE_CUSTOMERS.PLAYSITE.eq(playsiteId))
        .and(PLAYSITE_CUSTOMERS.CUSTOMER.eq(ticketNumber))
        .execute();
  }

  public boolean isInPlaysite(Long playsiteId, String ticketNumber) {
    return dsl.fetchExists(
        dsl.select(PLAYSITE_CUSTOMERS.CUSTOMER)
            .from(PLAYSITE_CUSTOMERS)
            .where(PLAYSITE_CUSTOMERS.PLAYSITE.eq(playsiteId))
            .and(PLAYSITE_CUSTOMERS.CUSTOMER.eq(ticketNumber))
    );
  }

}
