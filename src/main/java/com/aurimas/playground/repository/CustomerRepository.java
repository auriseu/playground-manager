package com.aurimas.playground.repository;

import static com.aurimas.playground.schema.Tables.CUSTOMER;

import com.aurimas.playground.domain.Customer;
import com.aurimas.playground.schema.tables.records.CustomerRecord;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

  private final DSLContext dsl;

  public CustomerRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public String save(Customer customer) {
    String customerId = (customer.ticketNumber() == null || customer.ticketNumber().isBlank())
        ? UUID.randomUUID().toString()
        : customer.ticketNumber();

    return dsl.insertInto(CUSTOMER)
        .set(CUSTOMER.ID, customerId)
        .set(CUSTOMER.NAME, customer.name())
        .set(CUSTOMER.AGE, customer.age())
        .returningResult(CUSTOMER.ID)
        .fetchSingle(CUSTOMER.ID);
  }

  public boolean exists(String id) {
    return dsl.fetchExists(dsl.selectFrom(CUSTOMER).where(CUSTOMER.ID.eq(id)));
  }

  public Optional<Customer> get(String id) {
    return dsl.selectFrom(CUSTOMER)
        .where(CUSTOMER.ID.eq(id))
        .fetchOptional()
        .map(this::mapRecordToDomain);
  }

  public void delete(String id) {
    dsl.deleteFrom(CUSTOMER)
        .where(CUSTOMER.ID.eq(id))
        .execute();
  }

  private Customer mapRecordToDomain(CustomerRecord row) {
    return new Customer(
        row.getId(),
        row.getName(),
        row.getAge()
    );
  }
}
