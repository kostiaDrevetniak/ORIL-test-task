package com.oril.testTask.controler;

import com.oril.testTask.controler.dto.CurrencyDTO;
import com.oril.testTask.controler.dto.PriceDTO;
import com.oril.testTask.controler.dto.mapper.CustomCurrencyMapper;
import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.exception.EntityNotFoundException;
import com.oril.testTask.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cryptocurrencies")
public class PriceController {

    private final PriceService priceService;
    private final ModelMapper modelMapper;

    public PriceController(PriceService priceService, ModelMapper modelMapper, CustomCurrencyMapper currencyMapper) {
        this.priceService = priceService;
        this.modelMapper = modelMapper;
        this.modelMapper.addConverter(currencyMapper);
    }

    @GetMapping("/minprice")
    public ResponseEntity<Double> getMinPrice(CurrencyDTO currencyDTO) {
        try {
            Currency currency = modelMapper.map(currencyDTO, Currency.class);
            if (currency.getId() == null)
                return ResponseEntity.notFound().build();
            double minPrice = priceService.getMinPrice(currency);
            return ResponseEntity.ok().body(minPrice);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/maxprice")
    public ResponseEntity<Double> getMaxPrice(CurrencyDTO currencyDTO) {
        try {
            Currency currency = modelMapper.map(currencyDTO, Currency.class);
            if (currency.getId() == null)
                return ResponseEntity.notFound().build();
            double minPrice = priceService.getMaxPrice(currency);
            return ResponseEntity.ok().body(minPrice);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PriceDTO>> getPrices(
            CurrencyDTO currencyDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Price> prices = priceService.getPrices(modelMapper.map(currencyDTO, Currency.class), page, size);
        return ResponseEntity.ok().body(prices.stream().map(price -> modelMapper.map(price, PriceDTO.class)).collect(Collectors.toList()));
    }
}
