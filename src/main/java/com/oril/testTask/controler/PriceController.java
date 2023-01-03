package com.oril.testTask.controler;

import com.oril.testTask.controler.dto.CurrencyDTO;
import com.oril.testTask.controler.dto.PriceDTO;
import com.oril.testTask.controler.dto.mapper.CustomCurrencyMapper;
import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.exception.EntityNotFoundException;
import com.oril.testTask.service.PriceCSVService;
import com.oril.testTask.service.PriceService;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cryptocurrencies")
public class PriceController {

    private final PriceService priceService;
    private final PriceCSVService csvService;
    private final ModelMapper modelMapper;

    public PriceController(PriceService priceService, ModelMapper modelMapper, CustomCurrencyMapper currencyMapper, PriceCSVService csvService) {
        this.priceService = priceService;
        this.modelMapper = modelMapper;
        this.modelMapper.addConverter(currencyMapper);
        this.csvService = csvService;
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

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<Resource> generateCSV() {
        try {
            ByteArrayInputStream stream = csvService.generateCSVFile();
            InputStreamResource fileInputStream = new InputStreamResource(stream);
            String csvFileName = "price.csv";
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");
            return ResponseEntity.ok().headers(headers).body(fileInputStream);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
