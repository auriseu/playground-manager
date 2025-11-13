package com.aurimas.playground.repository;

import static com.aurimas.playground.schema.Tables.ATTRACTION;
import static com.aurimas.playground.schema.Tables.PLAYSITE;
import static com.aurimas.playground.schema.Tables.PLAYSITE_ATTRACTIONS;
import static com.aurimas.playground.schema.Tables.PLAYSITE_CUSTOMERS;
import static com.aurimas.playground.schema.tables.PlaysiteQueue.PLAYSITE_QUEUE;

import com.aurimas.playground.domain.AttractionDetails;
import com.aurimas.playground.domain.Playsite;
import com.aurimas.playground.domain.PlaysiteCapacity;
import com.aurimas.playground.domain.PlaysiteInfo;
import com.aurimas.playground.schema.tables.Attraction;
import com.aurimas.playground.schema.tables.PlaysiteAttractions;
import com.aurimas.playground.schema.tables.PlaysiteCustomers;
import com.aurimas.playground.schema.tables.PlaysiteQueue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class PlaysiteRepository {

  private static final PlaysiteAttractions PLAYSITE_ATTRACTIONS_ALIAS = PLAYSITE_ATTRACTIONS.as("pa");
  private static final Attraction ATTRACTION_ALIAS = ATTRACTION.as("a");
  private static final PlaysiteCustomers PLAYSITE_CUSTOMER_ALIAS = PLAYSITE_CUSTOMERS.as("pc");
  private static final PlaysiteQueue PLAYSITE_QUEUE_ALIAS = PLAYSITE_QUEUE.as("pq");

  private static final Field<Integer> MAX_CAPACITY_FIELD = DSL.field(
          DSL.select(DSL.coalesce(DSL.sum(ATTRACTION_ALIAS.MAX_CAPACITY.mul(PLAYSITE_ATTRACTIONS_ALIAS.AMOUNT)), 0))
              .from(ATTRACTION_ALIAS)
              .join(PLAYSITE_ATTRACTIONS_ALIAS)
              .on(PLAYSITE_ATTRACTIONS_ALIAS.ATTRACTION.eq(ATTRACTION_ALIAS.ID))
              .where(PLAYSITE_ATTRACTIONS_ALIAS.PLAYSITE.eq(PLAYSITE.ID))
      ).as("maxCapacity")
      .convertFrom(val -> val == null ? 0 : ((BigDecimal) val).intValue());

  private static final Field<Integer> VISITOR_COUNT_FIELD = DSL.field(
          DSL.selectCount()
              .from(PLAYSITE_CUSTOMER_ALIAS)
              .where(PLAYSITE_CUSTOMER_ALIAS.PLAYSITE.eq(PLAYSITE.ID))
      ).as("currentVisitorCount")
      .convertFrom(val -> val == null ? 0 : val);

  private final DSLContext dsl;

  public PlaysiteRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Long create(Playsite playsite) {
    return dsl.insertInto(PLAYSITE)
        .set(PLAYSITE.NAME, playsite.name())
        .returningResult(PLAYSITE.ID)
        .fetchSingle(PLAYSITE.ID);
  }

  public void update(Playsite playsite) {
    dsl.update(PLAYSITE)
        .set(PLAYSITE.NAME, playsite.name())
        .where(PLAYSITE.ID.eq(playsite.id()))
        .execute();
  }

  public boolean exists(Long id) {
    return dsl.fetchExists(dsl.selectFrom(PLAYSITE).where(PLAYSITE.ID.eq(id)));
  }

  public void delete(Long id) {
    dsl.deleteFrom(PLAYSITE)
        .where(PLAYSITE.ID.eq(id))
        .execute();
  }

  public Optional<PlaysiteCapacity> getCapacity(Long playsiteId) {
    return dsl.select(
            MAX_CAPACITY_FIELD,
            VISITOR_COUNT_FIELD
        )
        .from(PLAYSITE)
        .where(PLAYSITE.ID.eq(playsiteId))
        .fetchOptional()
        .map(r -> {
          int maxCapacity = Optional.ofNullable(r.get(MAX_CAPACITY_FIELD)).orElse(0);
          int visitorCount = Optional.ofNullable(r.get(VISITOR_COUNT_FIELD)).orElse(0);
          return new PlaysiteCapacity(
              maxCapacity,
              visitorCount,
              maxCapacity > 0 ? (double) (visitorCount * 100) / maxCapacity : 0
          );
        });
  }

  public Optional<PlaysiteInfo> getInfo(Long playsiteId) {
    Field<List<String>> currentQueue = DSL.multiset(
            DSL.select(PLAYSITE_QUEUE_ALIAS.CUSTOMER)
                .from(PLAYSITE_QUEUE_ALIAS)
                .where(PLAYSITE_QUEUE_ALIAS.PLAYSITE.eq(PLAYSITE.ID))
                .orderBy(PLAYSITE_QUEUE_ALIAS.ADDED_TIME.asc())
        ).as("currentQueue")
        .convertFrom(r -> r.map(row -> row.get(PLAYSITE_QUEUE_ALIAS.CUSTOMER)));

    Field<List<AttractionDetails>> attractions = DSL.multiset(
        dsl.select(
                ATTRACTION_ALIAS.ID.as("attractionId"),
                ATTRACTION_ALIAS.NAME,
                ATTRACTION_ALIAS.MAX_CAPACITY,
                PLAYSITE_ATTRACTIONS_ALIAS.AMOUNT.as("amountInPlaysite")
            )
            .from(PLAYSITE_ATTRACTIONS_ALIAS)
            .join(ATTRACTION_ALIAS).on(PLAYSITE_ATTRACTIONS_ALIAS.ATTRACTION.eq(ATTRACTION_ALIAS.ID))
            .where(PLAYSITE_ATTRACTIONS_ALIAS.PLAYSITE.eq(PLAYSITE.ID))
    ).as("attractions").convertFrom(r -> r.into(AttractionDetails.class));

    return dsl.select(
            PLAYSITE.ID,
            PLAYSITE.NAME,
            MAX_CAPACITY_FIELD,
            VISITOR_COUNT_FIELD,
            currentQueue,
            attractions
        )
        .from(PLAYSITE)
        .where(PLAYSITE.ID.eq(playsiteId))
        .fetchOptional()
        .map(r -> {
          int maxCapacity = Optional.ofNullable(r.get(MAX_CAPACITY_FIELD)).orElse(0);
          int visitorCount = Optional.ofNullable(r.get(VISITOR_COUNT_FIELD)).orElse(0);
          return new PlaysiteInfo(
              r.get(PLAYSITE.ID),
              r.get(PLAYSITE.NAME),
              new PlaysiteCapacity(
                  maxCapacity,
                  visitorCount,
                  maxCapacity > 0 ? (double) (visitorCount * 100) / maxCapacity : 0
              ),
              r.get(currentQueue),
              r.get(attractions));
        });
  }

  public Integer getTotalVisitorCount() {
    return dsl.selectCount()
        .from(PLAYSITE_CUSTOMERS)
        .fetchOne(0, Integer.class);
  }
}
