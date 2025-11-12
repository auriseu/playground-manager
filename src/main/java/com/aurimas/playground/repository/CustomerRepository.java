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

  public Optional<Customer> findById(String id) {
    return dsl.selectFrom(CUSTOMER)
        .where(CUSTOMER.ID.eq(id))
        .fetchOptional()
        .map(this::mapRecordToDomain);
  }

  public void deleteById(String id) {
    dsl.deleteFrom(CUSTOMER)
        .where(CUSTOMER.ID.eq(id))
        .execute();
  }

  private Customer mapRecordToDomain(CustomerRecord record) {
    return new Customer(
        record.getId(),
        record.getName(),
        record.getAge()
    );
  }
}
