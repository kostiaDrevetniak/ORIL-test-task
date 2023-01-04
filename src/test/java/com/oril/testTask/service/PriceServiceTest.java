package com.oril.testTask.service;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.exception.EntityNotFoundException;
import com.oril.testTask.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class PriceServiceTest {

    private static final Currency existedCurrency = new Currency("1", "existed");
    private static final Currency notExistedCurrency = new Currency("2", "notExisted");
    @Autowired
    private PriceService priceService;
    @MockBean
    private PriceRepository repository;

    @Test
    public void testGetMin() throws EntityNotFoundException {
        Price minPrice = new Price("1", 0.0, existedCurrency);
        Mockito.when(repository.findFirstByCurrencyOrderByPriceAsc(existedCurrency))
                .thenReturn(Optional.of(minPrice));
        assertThat(priceService.getMinPrice(existedCurrency)).isEqualTo(0.0);
        Mockito.when(repository.findFirstByCurrencyOrderByPriceAsc(notExistedCurrency)).
                thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> priceService.getMinPrice(notExistedCurrency));
    }

    @Test
    public void testGetMax() throws EntityNotFoundException {
        Price maxPrice = new Price("1", 999999999.0, existedCurrency);
        Mockito.when(repository.findFirstByCurrencyOrderByPriceDesc(existedCurrency))
                .thenReturn(Optional.of(maxPrice));
        assertThat(priceService.getMaxPrice(existedCurrency)).isEqualTo(999999999.0);
        Mockito.when(repository.findFirstByCurrencyOrderByPriceDesc(notExistedCurrency)).
                thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> priceService.getMaxPrice(notExistedCurrency));
    }

    @Test
    public void testGetPrices() {
        @SuppressWarnings("unchecked")
        AggregationResults<Price> aggregationResults = (AggregationResults<Price>) Mockito.mock(AggregationResults.class);
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        Mockito.when(mongoTemplate.aggregate(Mockito.any(TypedAggregation.class), Mockito.eq(Price.class))).thenReturn(aggregationResults);
        ReflectionTestUtils.setField(priceService, "mongoTemplate", mongoTemplate);
        Mockito.when(aggregationResults.getMappedResults())
                .thenReturn(List.of(
                        new Price("1", 0, existedCurrency),
                        new Price("2", 10, existedCurrency),
                        new Price("3", 50, existedCurrency)
                ));
        List<Price> prices = priceService.getPrices(existedCurrency, 0, 3);
        assertThat(prices.size()).isEqualTo(3);
        Mockito.when(aggregationResults.getMappedResults()).thenReturn(Collections.emptyList());
        assertThrows(IndexOutOfBoundsException.class, () -> priceService.getPrices(notExistedCurrency, 0, 10));
    }
}
