package com.oril.testTask.service;

import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.exception.EntityNotFoundException;
import com.oril.testTask.repository.CurrencyRepository;
import com.oril.testTask.repository.PriceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CSVServiceTest {
    private static final Currency CURRENCY1 = new Currency("1", "Currency1");
    private static final Currency CURRENCY2 = new Currency("2", "Currency2");
    private static final List<Currency> CURRENCY_LIST = List.of(CURRENCY1, CURRENCY2);
    private static final Map<Currency, Price> MIN_PRICE = Map.of(
            CURRENCY1, new Price("1", 0, CURRENCY1),
            CURRENCY2, new Price("2", 10, CURRENCY2)
    );
    private static final Map<Currency, Price> MAX_PRICE = Map.of(
            CURRENCY1, new Price("3", 50, CURRENCY1),
            CURRENCY2, new Price("4", 80, CURRENCY2)
    );
    private static final String CSV_STRING = """
            Currency,min_price,max_price\r
            Currency1,0.0,50.0\r
            Currency2,10.0,80.0\r
            """;
    @Autowired
    private PriceCSVService csvService;
    @MockBean
    private CurrencyRepository currencyRepository;
    @MockBean
    private PriceRepository priceRepository;

    @Test
    public void testCSVGeneration() throws EntityNotFoundException, IOException {
        Mockito.when(currencyRepository.findAll()).thenReturn(CURRENCY_LIST);
        Mockito.when(priceRepository.findFirstByCurrencyOrderByPriceAsc(CURRENCY1)).
                thenReturn(Optional.ofNullable(MIN_PRICE.get(CURRENCY1)));
        Mockito.when(priceRepository.findFirstByCurrencyOrderByPriceAsc(CURRENCY2)).
                thenReturn(Optional.ofNullable(MIN_PRICE.get(CURRENCY2)));
        Mockito.when(priceRepository.findFirstByCurrencyOrderByPriceDesc(CURRENCY1)).
                thenReturn(Optional.ofNullable(MAX_PRICE.get(CURRENCY1)));
        Mockito.when(priceRepository.findFirstByCurrencyOrderByPriceDesc(CURRENCY2)).
                thenReturn(Optional.ofNullable(MAX_PRICE.get(CURRENCY2)));
        ByteArrayInputStream byteArrayInputStream = csvService.generateCSVFile();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(byteArrayInputStream.readAllBytes());
        assertThat(byteArrayOutputStream.toString()).isEqualTo(CSV_STRING);
    }
}
