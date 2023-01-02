package com.oril.testTask.Repository;

import com.oril.testTask.Entity.Price;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PriceRepository extends MongoRepository<Price, String> {
}
