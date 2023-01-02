package com.oril.testTask.repository;

import com.oril.testTask.entity.Currency;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CurrencyRepository extends MongoRepository<Currency, String> {
    Optional<Currency> findByName(String name);
}
