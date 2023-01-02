package com.oril.testTask.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    @Id
    private String id;
    private double price;
    private Currency currency;

    public Price(double price, Currency currency) {
        this.price = price;
        this.currency = currency;
    }
}
