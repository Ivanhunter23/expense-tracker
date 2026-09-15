package com.ivan.expensetracker;

import java.util.Map;

public record ValidationErrorResponse(
        Map<String, String> errors
) {
}
