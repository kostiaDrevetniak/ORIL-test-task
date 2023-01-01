package com.oril.testTask.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Price {
    @Id
    private String id;
    private double price;
    private Currency currency;
}
