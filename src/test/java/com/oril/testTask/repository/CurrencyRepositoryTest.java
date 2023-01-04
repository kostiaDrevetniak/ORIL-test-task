package com.oril.testTask.repository;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.repository.CurrencyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    private List<Currency> currencyList;

    @BeforeEach
    public void setup() {
        Currency currency1 = new Currency("1", "1currencyName");
        Currency currency2 = new Currency("2", "2currencyName");
        Currency currency3 = new Currency("3", "3currencyName");
        currencyList = List.of(currency1, currency2, currency3);
        currencyRepository.insert(currencyList);
    }

    @AfterEach
    public void clear() {
        currencyRepository.deleteAll(currencyList);
    }

    @Test
    public void testFindByName() {
        String currencyName = "2currencyName";
        Optional<Currency> byName = currencyRepository.findByName(currencyName);
        assertThat(byName.get()).isEqualTo(currencyList.get(1));
    }

    @Test
    public void testFindAll() {
        List<Currency> currencies = currencyRepository.findAll();
        assertThat(currencies.size()).isEqualTo(6);
        assertThat(currencies.containsAll(currencyList)).isTrue();
    }
}
