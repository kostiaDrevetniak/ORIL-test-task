package com.oril.testTask.repository;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.repository.PriceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class PriceRepositoryTest {
    @Autowired
    private PriceRepository priceRepository;

    private List<Price> priceList;

    private static final Currency currency = new Currency("1", "currencyName");

    @BeforeEach
    public void setup() {
        Price minPrice = new Price("1", 0, currency);
        Price maxPrice = new Price("2", 9999999.0, currency);
        priceList = List.of(minPrice, maxPrice);
        priceRepository.insert(priceList);
    }

    @AfterEach
    public void clear() {
        priceRepository.deleteAll(priceList);
    }

    @Test
    public void testFindMinPriceByCurrency() {
        Optional<Price> minPrice = priceRepository.findFirstByCurrencyOrderByPriceAsc(currency);
        assertThat(minPrice.isEmpty()).isFalse();
        assertThat(minPrice.get()).isEqualTo(priceList.get(0));
    }

    @Test
    public void testFindMaxPriceByCurrency() {
        Optional<Price> maxPrice = priceRepository.findFirstByCurrencyOrderByPriceDesc(currency);
        assertThat(maxPrice.isEmpty()).isFalse();
        assertThat(maxPrice.get()).isEqualTo(priceList.get(1));
    }

    @Test
    public void testNotExistedCurrency() {
        Currency notExisted = new Currency("3", "notExisted");
        Optional<Price> maxPrice = priceRepository.findFirstByCurrencyOrderByPriceDesc(notExisted);
        Optional<Price> minPrice = priceRepository.findFirstByCurrencyOrderByPriceAsc(notExisted);
        assertThat(maxPrice.isEmpty()).isTrue();
        assertThat(minPrice.isEmpty()).isTrue();
    }
}
