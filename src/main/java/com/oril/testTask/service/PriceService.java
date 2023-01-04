package com.oril.testTask.service;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.exception.EntityNotFoundException;
import com.oril.testTask.repository.PriceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceService {

    private final PriceRepository priceRepository;
    private final MongoTemplate mongoTemplate;

    public PriceService(PriceRepository priceRepository, MongoTemplate mongoTemplate) {
        this.priceRepository = priceRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public double getMinPrice(Currency currency) throws EntityNotFoundException {
        return priceRepository.findFirstByCurrencyOrderByPriceAsc(currency)
                .orElseThrow(() -> new EntityNotFoundException("Not found price for this currency")).getPrice();
    }

    public double getMaxPrice(Currency currency) throws EntityNotFoundException {
        return priceRepository.findFirstByCurrencyOrderByPriceDesc(currency)
                .orElseThrow(() -> new EntityNotFoundException("Not found price for this currency")).getPrice();
    }

    public List<Price> getPrices(Currency currency, int page, int size) throws IndexOutOfBoundsException {
        AggregationResults<Price> aggregate = mongoTemplate.aggregate(Aggregation.newAggregation(
                Price.class,
                Aggregation.match(Criteria.where("Currency").is(currency)),
                Aggregation.skip((long) page * size),
                Aggregation.limit(size),
                Aggregation.sort(Sort.Direction.ASC, "price")
        ), Price.class);
        List<Price> results = aggregate.getMappedResults();
        if (results.isEmpty())
            throw new IndexOutOfBoundsException("Page number out of document bounds");
        return results;
    }
}
