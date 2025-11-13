package com.aurimas.playground.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aurimas.playground.IntegrationTestCase;
import com.aurimas.playground.domain.Attraction;
import com.aurimas.playground.domain.Customer;
import com.aurimas.playground.domain.Playsite;
import com.aurimas.playground.domain.PlaysiteAttraction;
import com.aurimas.playground.domain.PlaysiteInfo;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlaysiteControllerIT extends IntegrationTestCase {

  public static final String BASE_PATH = "/api/v1";
  private static final String PLAYSITES_PATH = BASE_PATH + "/playsites";

  private static final String CUSTOMER_ID_A = "00000000-0000-0000-0000-00000000000A";
  private static final String CUSTOMER_ID_B = "00000000-0000-0000-0000-00000000000B";

  private final Customer customerA = new Customer(CUSTOMER_ID_A, "Alice", 5);
  private final Customer customerB = new Customer(CUSTOMER_ID_B, "Bob", 6);
  private final Playsite playsite = new Playsite(null, "Capacity Test Area");
  private final Attraction swingAttraction = new Attraction(null, "Single Swing", 1);

  private Long playsiteId;

  @BeforeEach
  void setUp() {
    // Create Playsite
    playsiteId = given().contentType(ContentType.JSON)
        .body(playsite)
        .when().post(PLAYSITES_PATH)
        .then().statusCode(200)
        .extract().jsonPath().getLong("id");

    // Create Attraction
    Long attractionId = given().contentType(ContentType.JSON)
        .body(List.of(swingAttraction))
        .when().post(BASE_PATH + "/attractions")
        .then().statusCode(200)
        .extract().jsonPath().getLong("[0].id");

    // Add Attraction to Playsite (Sets Max Capacity = 1)
    given().contentType(ContentType.JSON)
        .body(List.of(new PlaysiteAttraction(attractionId, 1)))
        .when().post(PLAYSITES_PATH + "/{playsiteId}/attractions", playsiteId)
        .then().statusCode(200);

    // Create Customer A & B
    given().contentType(ContentType.JSON)
        .body(customerA)
        .when().post(BASE_PATH + "/customers") // Assumed Customer endpoint
        .then().statusCode(200);

    given().contentType(ContentType.JSON)
        .body(customerB)
        .when().post(BASE_PATH + "/customers")
        .then().statusCode(200);
  }

  @Test
  @Order(1)
  @DisplayName("1. Customer A added to Playsite")
  void customerShouldBeAddedToPlaysite() {
    given()
        .pathParam("playsiteId", playsiteId)
        .pathParam("ticketNumber", CUSTOMER_ID_A)
        .queryParam("waitInQueue", true)
        .when()
        .post(PLAYSITES_PATH + "/{playsiteId}/customers/{ticketNumber}")
        .then()
        .statusCode(200)
        .body("status.key", is("ADDED_TO_PLAYSITE"));

    PlaysiteInfo playsiteInfo = given()
        .get(PLAYSITES_PATH + "/{id}", playsiteId)
        .then()
        .statusCode(200)
        .extract()
        .as(PlaysiteInfo.class);

    assertEquals(playsiteId, playsiteInfo.id());
    assertEquals(playsite.name(), playsiteInfo.name());
    assertEquals(0, playsiteInfo.currentQueue().size());
    assertEquals(1, playsiteInfo.attractions().size());
    assertEquals(1, playsiteInfo.capacity().maxCapacity());
    assertEquals(1, playsiteInfo.capacity().currentVisitorCount());
    assertEquals(100.0, playsiteInfo.capacity().utilizationPrecent());
  }

  @Test
  @Order(2)
  @DisplayName("2. Customer B is Queued")
  void customerShouldBeQueued() {
    customerShouldBeAddedToPlaysite();

    given()
        .pathParam("playsiteId", playsiteId)
        .pathParam("ticketNumber", CUSTOMER_ID_B)
        .queryParam("waitInQueue", true)
        .when()
        .post(PLAYSITES_PATH + "/{playsiteId}/customers/{ticketNumber}")
        .then()
        .statusCode(202)
        .body("status.key", is("ADDED_TO_QUEUE"));

    PlaysiteInfo playsiteInfo = given()
        .get(PLAYSITES_PATH + "/{id}", playsiteId)
        .then()
        .statusCode(200)
        .extract()
        .as(PlaysiteInfo.class);

    assertEquals(playsiteId, playsiteInfo.id());
    assertEquals(playsite.name(), playsiteInfo.name());
    assertEquals(1, playsiteInfo.currentQueue().size());
    assertEquals(1, playsiteInfo.attractions().size());
    assertEquals(1, playsiteInfo.capacity().maxCapacity());
    assertEquals(1, playsiteInfo.capacity().currentVisitorCount());
    assertEquals(100.0, playsiteInfo.capacity().utilizationPrecent());
  }

  @Test
  @Order(3)
  @DisplayName("3. Customer B is rejected when waitInQueue=false")
  void customerShouldBeRejected() {
    customerShouldBeAddedToPlaysite();

    given()
        .pathParam("playsiteId", playsiteId)
        .pathParam("ticketNumber", CUSTOMER_ID_B)
        .queryParam("waitInQueue", false)
        .when()
        .post(PLAYSITES_PATH + "/{playsiteId}/customers/{ticketNumber}")
        .then()
        .statusCode(400)
        .body("status.key", is("REJECTED_NO_WAIT_IN_QUEUE"));
  }

  @Test
  @Order(4)
  @DisplayName("4. Removal triggers FIFO move")
  void shouldTriggerQueueMoveOnCustomerRemoval() {
    customerShouldBeQueued();

    given()
        .pathParam("playsiteId", playsiteId)
        .pathParam("ticket", CUSTOMER_ID_A)
        .when()
        .delete(PLAYSITES_PATH + "/{playsiteId}/customers/{ticket}")
        .then()
        .statusCode(200)
        .body("size()", is(2))
        .body("[0].status.key", is("REMOVED_FROM_PLAYSITE"))
        .body("[1].status.key", is("ADDED_TO_PLAYSITE"));

    PlaysiteInfo playsiteInfo = given()
        .get(PLAYSITES_PATH + "/{id}", playsiteId)
        .then()
        .statusCode(200)
        .extract()
        .as(PlaysiteInfo.class);

    assertEquals(playsiteId, playsiteInfo.id());
    assertEquals(playsite.name(), playsiteInfo.name());
    assertEquals(0, playsiteInfo.currentQueue().size());
    assertEquals(1, playsiteInfo.attractions().size());
    assertEquals(1, playsiteInfo.capacity().maxCapacity());
    assertEquals(1, playsiteInfo.capacity().currentVisitorCount());
    assertEquals(100.0, playsiteInfo.capacity().utilizationPrecent());
  }
}
