package com.aurimas.playground.repository;

import static com.aurimas.playground.schema.Tables.ATTRACTION;
import static com.aurimas.playground.schema.Tables.PLAYSITE;
import static com.aurimas.playground.schema.Tables.PLAYSITE_ATTRACTIONS;
import static com.aurimas.playground.schema.Tables.PLAYSITE_CUSTOMERS;

import com.aurimas.playground.domain.AttractionDetails;
import com.aurimas.playground.domain.Playsite;
import com.aurimas.playground.domain.PlaysiteAttraction;
import com.aurimas.playground.domain.PlaysiteInfo;
import com.aurimas.playground.schema.tables.records.PlaysiteAttractionsRecord;
import com.aurimas.playground.schema.tables.records.PlaysiteRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep3;
import org.jooq.Records;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class PlaysiteRepository {

  private final DSLContext dsl;

  public PlaysiteRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Long save(Playsite playsite) {
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

  public List<Playsite> findAll() {
    return dsl.selectFrom(PLAYSITE)
        .fetch()
        .map(this::map);
  }

  public Optional<Playsite> findById(Long id) {
    return dsl.selectFrom(PLAYSITE)
        .where(PLAYSITE.ID.eq(id))
        .fetchOptional()
        .map(this::map);
  }

  public void deleteById(Long id) {
    dsl.deleteFrom(PLAYSITE)
        .where(PLAYSITE.ID.eq(id))
        .execute();
  }

  public void addAttractions(Long playsiteId, List<PlaysiteAttraction> attractions) {
    if (attractions.isEmpty()) {
      return;
    }

    InsertValuesStep3<PlaysiteAttractionsRecord, Long, Long, Integer> step = dsl.insertInto(
        PLAYSITE_ATTRACTIONS,
        PLAYSITE_ATTRACTIONS.PLAYSITE,
        PLAYSITE_ATTRACTIONS.ATTRACTION,
        PLAYSITE_ATTRACTIONS.AMOUNT
    );

    for (PlaysiteAttraction attraction : attractions) {
      step.values(playsiteId, attraction.attractionId(), attraction.amount());
    }

    dsl.batch(step).execute();
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

  public Optional<PlaysiteInfo> getPlaysiteInfoById(Long playsiteId) {
    var PA = PLAYSITE_ATTRACTIONS.as("pa");
    var A = ATTRACTION.as("a");
    var PC = PLAYSITE_CUSTOMERS.as("pc");

    var maxCapacitySubquery = DSL.select(DSL.sum(A.MAX_CAPACITY.mul(PA.AMOUNT)))
        .from(A)
        .join(PA).on(PA.ATTRACTION.eq(A.ID))
        .where(PA.PLAYSITE.eq(PLAYSITE.ID));

    var currentVisitorSubquery = DSL.selectCount()
        .from(PC)
        .where(PC.PLAYSITE.eq(PLAYSITE.ID));

    var maxCapacityField = DSL.field(maxCapacitySubquery).as("maxCapacity").convertFrom(BigDecimal::intValue);
    var visitorCountField = DSL.field(currentVisitorSubquery).as("currentVisitorCount").convertFrom(Integer::intValue);

    var utilizationPercentField = DSL.field(
        "(({1}) * 100.0) / ({0})",
        Double.class,
        maxCapacitySubquery,
        currentVisitorSubquery
    ).as("utilizationPercent");

    var attractions = DSL.multiset(
        dsl.select(
                A.ID.as("attractionId"),
                A.NAME,
                A.MAX_CAPACITY,
                PA.AMOUNT.as("amountInPlaysite")
            )
            .from(PA)
            .join(A).on(PA.ATTRACTION.eq(A.ID))
            .where(PA.PLAYSITE.eq(PLAYSITE.ID))
    ).as("attractions").convertFrom(r -> r.into(AttractionDetails.class));

    return dsl.select(
            PLAYSITE.ID,
            PLAYSITE.NAME,
            maxCapacityField,
            visitorCountField,
            utilizationPercentField,
            attractions
        )
        .from(PLAYSITE)
        .where(PLAYSITE.ID.eq(playsiteId))
        .fetchOptional(Records.mapping(PlaysiteInfo::new));
  }

  private Playsite map(PlaysiteRecord record) {
    return new Playsite(
        record.getId(),
        record.getName()
    );
  }
}
