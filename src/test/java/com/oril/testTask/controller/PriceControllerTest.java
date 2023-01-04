package com.oril.testTask.controller;

import com.oril.testTask.controler.PriceController;
import com.oril.testTask.controler.dto.CurrencyDTO;
import com.oril.testTask.controler.dto.PriceDTO;
import com.oril.testTask.controler.dto.mapper.CustomCurrencyMapper;
import com.oril.testTask.entity.Currency;
import com.oril.testTask.entity.Price;
import com.oril.testTask.exception.EntityNotFoundException;
import com.oril.testTask.service.PriceCSVService;
import com.oril.testTask.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
@AutoConfigureWebMvc
public class PriceControllerTest {
    private static final String EXISTED_CURRENCY_NAME = "Existed currency";
    private static final String NOT_EXISTED_CURRENCY_NAME = "Not existed currency";
    private static final CurrencyDTO EXISTED_CURRENCY_DTO = new CurrencyDTO(EXISTED_CURRENCY_NAME);
    private static final CurrencyDTO NOT_EXISTED_CURRENCY_DTO = new CurrencyDTO(NOT_EXISTED_CURRENCY_NAME);
    private static final Currency EXISTED_CURRENCY = new Currency("1", EXISTED_CURRENCY_NAME);

    private static final String ERROR_422_NOT_FOUND_ENTITY = "Not found currency with this name";
    private static final String ERROR_500 = "Not found price for this currency";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PriceService priceService;
    @MockBean
    private PriceCSVService csvService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private CustomCurrencyMapper currencyMapper;

    @BeforeEach
    public void setup() {
        Mockito.when(modelMapper.map(EXISTED_CURRENCY_DTO, Currency.class)).thenReturn(EXISTED_CURRENCY);
    }

    @Test
    public void testForNotExistingCurrency() throws Exception {
        Mockito.when(modelMapper.map(NOT_EXISTED_CURRENCY_DTO, Currency.class)).thenReturn(new Currency());
        List<String> url = List.of("/minprice", "/maxprice", "");
        for (String method : url) {
            mockMvc.perform(MockMvcRequestBuilders
                    .get("/cryptocurrencies" + method)
                    .param("name", NOT_EXISTED_CURRENCY_NAME)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$", is(ERROR_422_NOT_FOUND_ENTITY)));
        }
    }

    @Test
    public void testPriceNotFound() throws Exception {
        List<String> url = List.of("/minprice", "/maxprice", "/csv");
        Mockito.when(priceService.getMinPrice(EXISTED_CURRENCY)).thenThrow(new EntityNotFoundException("Not found price for this currency"));
        Mockito.when(priceService.getMaxPrice(EXISTED_CURRENCY)).thenThrow(new EntityNotFoundException("Not found price for this currency"));
        Mockito.when(csvService.generateCSVFile()).thenThrow(new EntityNotFoundException("Not found price for this currency"));
        for (String method : url) {
            mockMvc.perform(MockMvcRequestBuilders
                    .get("/cryptocurrencies" + method)
                    .param("name", EXISTED_CURRENCY_NAME)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$", is(ERROR_500)));
        }

    }

    @Test
    public void testGetMinPrice() throws Exception {
        Mockito.when(priceService.getMinPrice(EXISTED_CURRENCY)).thenReturn(0.0);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/cryptocurrencies/minprice")
                .param("name", EXISTED_CURRENCY_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0.0)));
    }

    @Test
    public void testGetMaxPrice() throws Exception {
        Mockito.when(priceService.getMaxPrice(EXISTED_CURRENCY)).thenReturn(0.0);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/cryptocurrencies/maxprice")
                .param("name", EXISTED_CURRENCY_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0.0)));
    }

    @Test
    public void testGetPricesPagesOutOfBounds() throws Exception {
        Mockito.when(priceService.getPrices(eq(EXISTED_CURRENCY), any(int.class), any(int.class)))
                .thenThrow(new IndexOutOfBoundsException("Page number out of document bounds"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/cryptocurrencies")
                .param("name", EXISTED_CURRENCY_NAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$", is("Page number out of document bounds")));
    }

    @Test
    public void testGetPrices() throws Exception {
        List<Price> prices = List.of(
                new Price("1", 0.0, EXISTED_CURRENCY),
                new Price("2", 50.0, EXISTED_CURRENCY),
                new Price("3", 100.0, EXISTED_CURRENCY)
        );
        Mockito.when(priceService.getPrices(eq(EXISTED_CURRENCY), any(int.class), eq(3)))
                .thenReturn(prices);
        for (Price price : prices) {
            Mockito.when(modelMapper.map(eq(price), eq(PriceDTO.class)))
                    .thenReturn(new PriceDTO(price.getPrice(), price.getCurrency().getName()));
        }
        mockMvc.perform(MockMvcRequestBuilders
                .get("/cryptocurrencies")
                .param("name", EXISTED_CURRENCY_NAME)
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].price", is(0.0)))
                .andExpect(jsonPath("$[1].currencyName", is(EXISTED_CURRENCY_NAME)));
    }

    @Test
    public void testCSVGeneration() throws Exception {
        String csvString = """
                Currency,min_price,max_price\r
                Currency1,0.0,50.0\r
                Currency2,10.0,80.0\r
                """;
        Mockito.when(csvService.generateCSVFile())
                .thenReturn(new ByteArrayInputStream(csvString.getBytes(StandardCharsets.UTF_8)));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/cryptocurrencies/csv")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Disposition", "attachment; filename=price.csv"))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(csvString));
    }
}
