package com.ivan.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

        String requestBody =
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

    @Test
    void shouldReturnAllExpenses() throws Exception {
        Expense expense = new Expense(
                301L,
                "Bus",
                new BigDecimal("3.20"),
                Category.TRANSPORT,
                LocalDate.of(2026, 9, 15)
        );
        when(expenseService.getAllExpenses()).thenReturn(List.of(expense));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(301));

        verify(expenseService).getAllExpenses();

    }

    @Test
    void shouldReturnExpensesFilteredByCategory() throws Exception {
        Expense expense = new Expense(
                302L,
                "Lunch",
                new BigDecimal("10.50"),
                Category.FOOD,
                LocalDate.of(2026, 9, 15)
        );

        when(expenseService.getExpensesByCategory(Category.FOOD))
                .thenReturn(List.of(expense));

        mockMvc.perform(
                        get("/api/expenses")
                                .param("category", "FOOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("FOOD"));

        verify(expenseService).getExpensesByCategory(Category.FOOD);
    }
    @Test
    void shouldReturnExpenseByIdWhenItExists() throws Exception {
        Expense expense = new Expense(
                303L,
                "Medicine",
                new BigDecimal("8.40"),
                Category.HEALTH,
                LocalDate.of(2026, 9, 16)
        );

        when(expenseService.findExpenseById(303))
                .thenReturn(Optional.of(expense));

        mockMvc.perform(get("/api/expenses/303"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(303))
                .andExpect(jsonPath("$.description").value("Medicine"));

        verify(expenseService).findExpenseById(303);
    }

    @Test
    void shouldReturnNotFoundWhenExpenseDoesNotExist() throws Exception {
        when(expenseService.findExpenseById(999))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/expenses/999"))
                .andExpect(status().isNotFound());

        verify(expenseService).findExpenseById(999);
    }

    @Test
    void shouldDeleteExistingExpense() throws Exception {
        when(expenseService.deleteExpense(303))
                .thenReturn(true);

        mockMvc.perform(delete("/api/expenses/303"))
                .andExpect(status().isNoContent());

        verify(expenseService).deleteExpense(303);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingExpense() throws Exception {
        when(expenseService.deleteExpense(999))
                .thenReturn(false);

        mockMvc.perform(delete("/api/expenses/999"))
                .andExpect(status().isNotFound());

        verify(expenseService).deleteExpense(999);
    }

    @Test
    void shouldUpdateExistingExpense() throws Exception {
        Expense updated = new Expense(
                303L,
                "Train",
                new BigDecimal("20.50"),
                Category.TRANSPORT,
                LocalDate.of(2026, 8, 26)
        );

        String requestBody =
                """
                        {
                          "description": "Train",
                          "amount": 20.50,
                          "category": "TRANSPORT",
                          "date": "2026-08-26"
                        }
                        """;

        when(expenseService.updateExpense(eq(303L), any(Expense.class)))
                .thenReturn(Optional.of(updated));

        mockMvc.perform(
                        put("/api/expenses/303")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(303))
                .andExpect(jsonPath("$.description").value("Train"));

        verify(expenseService).updateExpense(eq(303L), any(Expense.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingExpense() throws Exception {
        String requestBody =
                """
                        {
                          "description": "Taxi",
                          "amount": 25.50,
                          "category": "TRANSPORT",
                          "date": "2026-08-26"
                        }
                        """;

        when(expenseService.updateExpense(eq(999L), any(Expense.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        put("/api/expenses/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isNotFound());

        verify(expenseService).updateExpense(eq(999L), any(Expense.class));
    }
}
