package com.ivan.expensetracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController controller;

    Expense input = new Expense(null, "Cafe", new BigDecimal("2.50"), Category.FOOD, LocalDate.of(2026, 9, 13));

    Expense savedExpense = new Expense(10L, "Cafe", new BigDecimal("2.50"), Category.FOOD, LocalDate.of(2026, 9, 13));

    @Test
    void shouldReturnCreatedExpense() {

        when(expenseService.addExpense(input))
                .thenReturn(savedExpense);

        ResponseEntity<Expense> response =
                controller.createExpense(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(savedExpense, response.getBody());
        verify(expenseService).addExpense(input);
    }

    @Test
    void shouldReturnNoContentWhenExpenseIsDeleted() {
        when(expenseService.deleteExpense(10L))
                .thenReturn(true);

        ResponseEntity<Void> response =
                controller.deleteExpense(10L);

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        verify(expenseService).deleteExpense(10L);
    }
    @Test
    void shouldReturnNotFoundWhenExpenseDoesNotExist() {
        when(expenseService.deleteExpense(99L))
                .thenReturn(false);

        ResponseEntity<Void> response =
                controller.deleteExpense(99L);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(expenseService).deleteExpense(99L);
    }
}
