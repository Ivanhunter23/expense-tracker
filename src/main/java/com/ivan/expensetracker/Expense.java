package com.ivan.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Expense(
        long id,
        String description,
        BigDecimal amount,
        Category category,
        LocalDate date
) {

}
