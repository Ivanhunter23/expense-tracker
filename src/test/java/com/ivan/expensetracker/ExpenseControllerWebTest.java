package com.ivan.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    void shouldReturnFieldErrorsForInvalidExpense() throws Exception {
        String requestBody =
                """
                        {
                          "description": " ",
                          "amount": 5
                        }
                        """;

        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").value("must not be blank"))
                .andExpect(jsonPath("$.errors.amount").doesNotExist())
                .andExpect(jsonPath("$.errors.category").value("must not be null"))
                .andExpect(jsonPath("$.errors.date").value("must not be null"));

        verifyNoInteractions(expenseService);
    }

    @Test
    void shouldCreateValidExpense() throws Exception {
        Expense savedExpense = new Expense(300L,
                "Cafe",
                new BigDecimal("2.50"),
                Category.FOOD,
                LocalDate.of(2026, 9, 13));
        when(expenseService.addExpense(any(Expense.class)))
                .thenReturn(savedExpense);

        String requestBody   =
                """
                        {
                          "description": "Cafe",
                          "amount": 2.50,
                          "category": "FOOD",
                          "date": "2026-09-13"
                        }
                        """;
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(300));

        verify(expenseService).addExpense(any(Expense.class));
    }
}