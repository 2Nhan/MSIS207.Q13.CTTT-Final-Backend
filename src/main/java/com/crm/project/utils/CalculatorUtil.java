package com.crm.project.utils;

import com.crm.project.internal.QuotationItemInfo;
import lombok.experimental.UtilityClass;

import java.math.RoundingMode;
import java.util.List;
import java.math.BigDecimal;

@UtilityClass
public class CalculatorUtil {

    public static final BigDecimal calculateItemSubtotal(QuotationItemInfo info) {
        BigDecimal discount = info.getDiscount();
        String discountType = info.getDiscountType();
        BigDecimal unitPrice = info.getUnitPrice();
        Integer quantity = info.getQuantity();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        if (discountType.equalsIgnoreCase("percent")) {
            BigDecimal discountRate = discount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal discountAmount = subtotal.multiply(discountRate);
            subtotal = subtotal.subtract(discountAmount);
        } else if (discountType.equalsIgnoreCase("currency")) {
            BigDecimal discountAmount = discount.multiply(BigDecimal.valueOf(quantity));
            subtotal = subtotal.subtract(discountAmount);
        }

        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public static final BigDecimal calculateQuotationTotal(List<QuotationItemInfo> quotationItems) {
        BigDecimal total = BigDecimal.ZERO;
        for (QuotationItemInfo info : quotationItems) {
            total = total.add(info.getSubtotal());
        }
        return total;
    }
}
