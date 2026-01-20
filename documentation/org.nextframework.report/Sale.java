package org.nextframework.report.example;

import java.math.BigDecimal;

/**
 * Entity class for the Sales Report example.
 */
public class Sale {

    private String region;
    private String product;
    private String description;
    private Integer quantity;
    private BigDecimal amount;

    public Sale(String region, String product, String description, Integer quantity, BigDecimal amount) {
        this.region = region;
        this.product = product;
        this.description = description;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getRegion() { return region; }
    public String getProduct() { return product; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getAmount() { return amount; }
}
