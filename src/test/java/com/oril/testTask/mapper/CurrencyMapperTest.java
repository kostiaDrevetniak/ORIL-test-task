package com.oril.testTask.mapper;

import com.oril.testTask.controler.dto.CurrencyDTO;
import com.oril.testTask.controler.dto.mapper.CustomCurrencyMapper;
import com.oril.testTask.entity.Currency;
import com.oril.testTask.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CurrencyMapperTest {
    private static final String EXISTED_CURRENCY_NAME = "Currency";
    private static final String NOT_EXISTED_CURRENCY_NAME = "Not existed";
    private static final Currency EXISTED_CURRENCY = new Currency("1", EXISTED_CURRENCY_NAME);
    private static final CurrencyDTO EXISTED_CURRENCY_DTO = new CurrencyDTO(EXISTED_CURRENCY_NAME);
    private static final CurrencyDTO NOT_EXISTED_CURRENCY_DTO = new CurrencyDTO(NOT_EXISTED_CURRENCY_NAME);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CustomCurrencyMapper currencyMapper;
    @MockBean
    private CurrencyRepository currencyRepository;

    @Test
    public void testMappingFromCurrencyDTOToCurrency() {
        Mockito.when(currencyRepository.findByName(EXISTED_CURRENCY_NAME)).thenReturn(Optional.of(EXISTED_CURRENCY));
        modelMapper.addConverter(currencyMapper);
        Currency existedCurrency = modelMapper.map(EXISTED_CURRENCY_DTO, Currency.class);
        assertThat(existedCurrency).isEqualTo(EXISTED_CURRENCY);
        assertThat(modelMapper.map(NOT_EXISTED_CURRENCY_DTO, Currency.class)).isEqualTo(new Currency());
    }
}
