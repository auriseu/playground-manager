package com.aurimas.playground;

import com.aurimas.playground.schema.Tables;
import io.restassured.RestAssured;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTestCase {

  @Autowired
  private DSLContext dsl;

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;

    // For H2 compatibility need to do these tricks to truncate tables
    dsl.execute("SET REFERENTIAL_INTEGRITY FALSE;");
    try {
      dsl.truncate(Tables.PLAYSITE_ATTRACTIONS).execute();
      dsl.truncate(Tables.PLAYSITE_CUSTOMERS).execute();
      dsl.truncate(Tables.PLAYSITE_QUEUE).execute();
      dsl.truncate(Tables.ATTRACTION).restartIdentity().execute();
      dsl.truncate(Tables.CUSTOMER).execute();
      dsl.truncate(Tables.PLAYSITE).execute();
    } finally {
      dsl.execute("SET REFERENTIAL_INTEGRITY TRUE;");
    }
  }
}
