package com.oril.testTask.Config;

import com.oril.testTask.Entity.Currency;
import com.oril.testTask.Repository.CurrencyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
public class DBConfig {
    @Bean
    CommandLineRunner commandLineRunner(
            CurrencyRepository currencyRepository,
            MongoTemplate mongoTemplate
    ){
        return args -> {
            mongoTemplate.getDb().drop();
            Currency currency1 = new Currency();
            currency1.setName("BTC/USD");
            Currency currency2 = new Currency();
            currency2.setName("ETH/USD");
            Currency currency3 = new Currency();
            currency3.setName("XRP/USD");
            currencyRepository.insert(List.of(currency1, currency2, currency3));
        };
    }
}
