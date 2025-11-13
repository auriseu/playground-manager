package com.aurimas.playground.repository;

import com.aurimas.playground.schema.Tables;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class PlaygroundRepository {

  private final DSLContext dsl;

  public PlaygroundRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Integer getTotalVisitorCount() {
    return dsl.selectCount()
        .from(Tables.PLAYSITE_CUSTOMERS)
        .fetchOne(0, Integer.class);
  }
}
