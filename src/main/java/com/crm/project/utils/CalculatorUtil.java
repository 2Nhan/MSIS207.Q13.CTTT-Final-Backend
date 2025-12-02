package com.crm.project.utils;

import com.crm.project.internal.QuotationItemInfo;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class CalculatorUtil {

    public static final BigDecimal calculateItemTotalPrice(QuotationItemInfo info) {
        BigDecimal discount = info.getDiscount();
        String discountType = info.getDiscountType();
        BigDecimal unitPrice = info.getUnitPrice();
        Integer quantity = info.getQuantity();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        if (discountType.equalsIgnoreCase("percent")) {
            totalPrice = totalPrice.multiply(discount.divide(BigDecimal.valueOf(100), 2));
        } else if (discountType.equalsIgnoreCase("currency")) {
            totalPrice = totalPrice.subtract(discount.multiply(BigDecimal.valueOf(quantity)));
        }
        return totalPrice;
    }
}
