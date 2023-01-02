package com.oril.testTask.service;

import com.oril.testTask.entity.Price;
import com.oril.testTask.repository.CurrencyRepository;
import com.oril.testTask.repository.PriceRepository;
import com.oril.testTask.service.util.RequestSender;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@EnableScheduling
@Service
public class ScheduledPriceGetingService {

    private static final String URL = "https://cex.io/api/last_price/";

    private final CurrencyRepository currencyRepository;
    private final PriceRepository priceRepository;

    public ScheduledPriceGetingService(CurrencyRepository currencyRepository, PriceRepository priceRepository) {
        this.currencyRepository = currencyRepository;
        this.priceRepository = priceRepository;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 5000, initialDelay = 1000)
    public void getPrice() throws IOException {
        currencyRepository.findAll().forEach(currency -> {
            String jsonString;
            try {
                jsonString = RequestSender.sendRequest(URL + currency.getName());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            JSONObject json = new JSONObject(jsonString);
            priceRepository.insert(new Price(json.getDouble("lprice"), currency));
        });
    }
}
