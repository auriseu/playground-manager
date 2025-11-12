package com.aurimas.playground.repository;

import static com.aurimas.playground.schema.Tables.ATTRACTION;

import com.aurimas.playground.domain.Attraction;
import com.aurimas.playground.schema.tables.records.AttractionRecord;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep2;
import org.springframework.stereotype.Repository;

@Repository
public class AttractionRepository {

  private final DSLContext dsl;

  public AttractionRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<Attraction> saveAll(List<Attraction> attractions) {
    if (attractions.isEmpty()) {
      return List.of();
    }

    InsertValuesStep2<AttractionRecord, String, Integer> step =
        dsl.insertInto(ATTRACTION, ATTRACTION.NAME, ATTRACTION.MAX_CAPACITY);

    for (Attraction attraction : attractions) {
      step.values(attraction.name(), attraction.maxCapacity());
    }

    return step.returning()
        .fetch()
        .stream()
        .map(this::mapRecordToDomain)
        .toList();
  }

  public int update(Attraction attraction) {
    return dsl.update(ATTRACTION)
        .set(ATTRACTION.NAME, attraction.name())
        .set(ATTRACTION.MAX_CAPACITY, attraction.maxCapacity())
        .where(ATTRACTION.ID.eq(attraction.id()))
        .execute();
  }

  public List<Attraction> findAll() {
    return dsl.selectFrom(ATTRACTION)
        .fetch()
        .map(this::mapRecordToDomain);
  }

  public Optional<Attraction> findById(Long id) {
    return dsl.selectFrom(ATTRACTION)
        .where(ATTRACTION.ID.eq(id))
        .fetchOptional()
        .map(this::mapRecordToDomain);
  }

  public void deleteById(Long id) {
    dsl.deleteFrom(ATTRACTION)
        .where(ATTRACTION.ID.eq(id))
        .execute();
  }

  public int countExistingAttractions(List<Long> attractionIds) {
    if (attractionIds.isEmpty()) {
      return 0;
    }

    Integer existingCount = dsl.selectCount()
        .from(ATTRACTION)
        .where(ATTRACTION.ID.in(attractionIds))
        .fetchOne(0, Integer.class);

    return existingCount != null ? existingCount : 0;
  }

  private Attraction mapRecordToDomain(AttractionRecord row) {
    return new Attraction(
        row.getId(),
        row.getName(),
        row.getMaxCapacity()
    );
  }
}
