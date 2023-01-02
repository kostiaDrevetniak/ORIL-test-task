package com.oril.testTask.service;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.exception.EntityNotFoundException;
import com.oril.testTask.repository.PriceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public double getMinPrice(Currency currency) throws EntityNotFoundException {
        return priceRepository.findFirstByCurrencyOrderByPriceAsc(currency)
                .orElseThrow(() -> new EntityNotFoundException("Not found price for this currency")).getPrice();
    }

    public double getMaxPrice(Currency currency) throws EntityNotFoundException {
        return priceRepository.findFirstByCurrencyOrderByPriceDesc(currency)
                .orElseThrow(() -> new EntityNotFoundException("Not found price for this currency")).getPrice();
    }
}
