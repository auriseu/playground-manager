package com.aurimas.playground.repository;

import static com.aurimas.playground.schema.Tables.PLAYSITE_QUEUE;

import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class PlaysiteQueueRepository {

  private final DSLContext dsl;

  public PlaysiteQueueRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public void addCustomerToQueue(Long playsiteId, String customerId) {
    dsl.insertInto(PLAYSITE_QUEUE)
        .set(PLAYSITE_QUEUE.PLAYSITE, playsiteId)
        .set(PLAYSITE_QUEUE.CUSTOMER, customerId)
        .execute();
  }

  public void removeCustomerFromQueue(Long playsiteId, String customerId) {
    dsl.deleteFrom(PLAYSITE_QUEUE)
        .where(PLAYSITE_QUEUE.PLAYSITE.eq(playsiteId))
        .and(PLAYSITE_QUEUE.CUSTOMER.eq(customerId))
        .execute();
  }

  public boolean isInQueue(Long playsiteId, String customerId) {
    return dsl.fetchExists(
        dsl.select(PLAYSITE_QUEUE.CUSTOMER)
            .from(PLAYSITE_QUEUE)
            .where(PLAYSITE_QUEUE.PLAYSITE.eq(playsiteId)
                .and(PLAYSITE_QUEUE.CUSTOMER.eq(customerId)))
    );
  }

  public Optional<String> getFirstFromQueue(Long playsiteId) {
    return dsl.select(PLAYSITE_QUEUE.CUSTOMER)
        .from(PLAYSITE_QUEUE)
        .where(PLAYSITE_QUEUE.PLAYSITE.eq(playsiteId))
        .orderBy(PLAYSITE_QUEUE.ADDED_TIME.asc())
        .limit(1)
        .fetchOptional(PLAYSITE_QUEUE.CUSTOMER);
  }

}
