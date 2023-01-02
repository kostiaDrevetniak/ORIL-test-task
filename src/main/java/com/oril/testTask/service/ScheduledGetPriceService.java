package com.oril.testTask.Service;

import com.oril.testTask.Entity.Price;
import com.oril.testTask.Repository.CurrencyRepository;
import com.oril.testTask.Repository.PriceRepository;
import com.oril.testTask.Service.Util.RequestSender;
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
public class ScheduledGetPriceService {

    private static final String URL = "https://cex.io/api/last_price/";

    private final CurrencyRepository currencyRepository;
    private final PriceRepository priceRepository;

    public ScheduledGetPriceService(CurrencyRepository currencyRepository, PriceRepository priceRepository) {
        this.currencyRepository = currencyRepository;
        this.priceRepository = priceRepository;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 3000, initialDelay = 1000)
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
