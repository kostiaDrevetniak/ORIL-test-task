package com.oril.testTask;

import com.oril.testTask.controler.PriceController;
import com.oril.testTask.repository.CurrencyRepository;
import com.oril.testTask.repository.PriceRepository;
import com.oril.testTask.service.PriceCSVService;
import com.oril.testTask.service.PriceService;
import com.oril.testTask.service.ScheduledPriceGettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestTaskApplicationTests {

    @Autowired
    private PriceController controller;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private PriceCSVService csvService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private ScheduledPriceGettingService priceGettingService;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(currencyRepository).isNotNull();
        assertThat(priceRepository).isNotNull();
        assertThat(csvService).isNotNull();
        assertThat(priceService).isNotNull();
        assertThat(priceGettingService).isNotNull();
    }

}
