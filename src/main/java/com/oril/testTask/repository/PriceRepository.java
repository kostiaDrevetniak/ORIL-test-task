package com.oril.testTask.repository;

import com.oril.testTask.entity.Price;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PriceRepository extends MongoRepository<Price, String> {
}
