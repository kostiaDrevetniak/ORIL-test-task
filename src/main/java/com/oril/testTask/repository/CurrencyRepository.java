package com.oril.testTask.Repository;

import com.oril.testTask.Entity.Currency;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CurrencyRepository extends MongoRepository<Currency, String> {
    Optional<Currency> findByName(String name);
}
