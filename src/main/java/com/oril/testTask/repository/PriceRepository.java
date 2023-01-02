package com.oril.testTask.repository;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface PriceRepository extends MongoRepository<Price, String> {
    Optional<Price> findFirstByCurrencyOrderByPriceAsc(Currency currency);
    Optional<Price> findFirstByCurrencyOrderByPriceDesc(Currency currency);
}
