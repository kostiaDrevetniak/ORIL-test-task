package com.oril.testTask.service;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.exception.EntityNotFoundException;
import com.oril.testTask.repository.CurrencyRepository;
import com.oril.testTask.repository.PriceRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class PriceCSVService {

    private static final String[] CSV_HEADERS = {"Currency", "min_price", "max_price"};


    private final CurrencyRepository currencyRepository;
    private final PriceRepository priceRepository;

    public PriceCSVService(CurrencyRepository currencyRepository, PriceRepository priceRepository) {
        this.currencyRepository = currencyRepository;
        this.priceRepository = priceRepository;
    }

    public ByteArrayInputStream generateCSVFile() throws EntityNotFoundException {
        ByteArrayInputStream byteArrayInputStream;
        List<Currency> allCurrencies = currencyRepository.findAll();
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out),
                        CSVFormat.DEFAULT.withHeader(CSV_HEADERS))
        ) {
            for (Currency currency : allCurrencies) {
                Price min = priceRepository.findFirstByCurrencyOrderByPriceAsc(currency)
                        .orElseThrow(() -> new EntityNotFoundException("Not found any prices for this currency"));
                Price max = priceRepository.findFirstByCurrencyOrderByPriceDesc(currency)
                        .orElseThrow(() -> new EntityNotFoundException("Not found any prices for this currency"));
                csvPrinter.printRecord(currency.getName(), min.getPrice(), max.getPrice());
            }
            csvPrinter.flush();
            byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return byteArrayInputStream;
    }
}
