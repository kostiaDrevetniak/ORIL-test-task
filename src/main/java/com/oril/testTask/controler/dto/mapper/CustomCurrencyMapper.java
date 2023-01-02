package com.oril.testTask.controler.dto.mapper;

import com.oril.testTask.controler.dto.CurrencyDTO;
import com.oril.testTask.entity.Currency;
import com.oril.testTask.repository.CurrencyRepository;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class CustomCurrencyMapper implements Converter<CurrencyDTO, Currency> {

    private final CurrencyRepository currencyRepository;

    public CustomCurrencyMapper(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @Override
    public Currency convert(MappingContext<CurrencyDTO, Currency> mappingContext) {
        return currencyRepository.findByName(mappingContext.getSource().getName()).orElseGet(Currency::new);
    }
}
