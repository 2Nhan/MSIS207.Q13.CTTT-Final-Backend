package com.crm.project.dto.representation;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCsvRepresentation {
    @CsvBindByName(column = "sku")
    private String sku;

    @CsvBindByName(column = "name")
    private String name;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "unit")
    private String unit;

    @CsvBindByName(column = "quantity")
    private Integer quantity;

    @CsvBindByName(column = "price")
    private BigDecimal price;

    @CsvBindByName(column = "image")
    private String imageUrl;
}
