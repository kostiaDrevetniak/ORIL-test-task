package com.oril.testTask.repository;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PriceRepository extends MongoRepository<Price, String> {
    Optional<Price> findFirstByCurrencyOrderByPriceAsc(Currency currency);
    Optional<Price> findFirstByCurrencyOrderByPriceDesc(Currency currency);
//    @Query("{$skip: ?1}, $limit: &2 ")
//    List<Price> findByCurrencyAndSize(Currency currency, int skip, int limit);
}
