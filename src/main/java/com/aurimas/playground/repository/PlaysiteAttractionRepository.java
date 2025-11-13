package com.aurimas.playground.repository;

import static com.aurimas.playground.schema.Tables.PLAYSITE_ATTRACTIONS;

import com.aurimas.playground.domain.PlaysiteAttraction;
import com.aurimas.playground.schema.tables.records.PlaysiteAttractionsRecord;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class PlaysiteAttractionRepository {
  private final DSLContext dsl;

  public PlaysiteAttractionRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public void addAttractions(Long playsiteId, List<PlaysiteAttraction> attractions) {
    if (attractions.isEmpty()) {
      return;
    }

    List<PlaysiteAttractionsRecord> records = attractions.stream().map(attraction -> {
      PlaysiteAttractionsRecord row = dsl.newRecord(PLAYSITE_ATTRACTIONS);
      row.setPlaysite(playsiteId);
      row.setAttraction(attraction.attractionId());
      row.setAmount(attraction.amount());
      return row;
    }).toList();

    dsl.batchInsert(records).execute();
  }
}
