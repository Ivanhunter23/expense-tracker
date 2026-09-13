package com.ivan.expensetracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @InjectMocks
    private ExpenseService service;


    @Test
    void shouldAddExpense() {
        Expense expense = new Expense(
                1L,
                "Mercadona",
                new BigDecimal("20.50"),
                Category.FOOD,
                LocalDate.of(2026, 9, 1)
        );


        when(repository.save(expense))
                .thenReturn(expense);

        Expense result = service.addExpense(expense);


        assertSame(expense, result);

        verify(repository).save(expense);
    }


    @Test
    void shouldFindExpenseById() {
        Expense expense = new Expense(
                1L,
                "Mercadona",
                new BigDecimal("20.50"),
                Category.FOOD,
                LocalDate.of(2026, 8, 26)
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(expense));

        var result = service.findExpenseById(1L);

        assertTrue(result.isPresent());
        assertSame(expense, result.get());
    }


    @Test
    void shouldReturnEmptyWhenExpenseIdDoesNotExist() {

        when(repository.findById(2L))
                .thenReturn(Optional.empty());

        var result = service.findExpenseById(2L);

        assertTrue(result.isEmpty());
    }


    @Test
    void shouldDeleteExpenseWhenIdExists() {

        when(repository.existsById(1L))
                .thenReturn(true);

        boolean deleted = service.deleteExpense(1L);

        assertTrue(deleted);

        verify(repository).deleteById(1L);
    }


    @Test
    void shouldReturnFalseWhenDeletingNonExistingExpense() {

        when(repository.existsById(1L))
                .thenReturn(false);

        boolean deleted = service.deleteExpense(1L);

        assertFalse(deleted);

        verify(repository, never()).deleteById(1L);
    }


    @Test
    void shouldReturnExpensesByCategory() {
        Expense expense1 = new Expense(
                1L,
                "Mercadona",
                new BigDecimal("20.50"),
                Category.FOOD,
                LocalDate.of(2026, 8, 26)
        );

        Expense expense2 = new Expense(
                2L,
                "Alimerka",
                new BigDecimal("25.50"),
                Category.FOOD,
                LocalDate.of(2026, 9, 12)
        );

        when(repository.findByCategory(Category.FOOD))
                .thenReturn(List.of(expense1, expense2));

        var result = service.getExpensesByCategory(Category.FOOD);

        assertEquals(2, result.size());
        assertTrue(result.contains(expense1));
        assertTrue(result.contains(expense2));
    }


    @Test
    void shouldReturnTotalSpent() {
        Expense expense1 = new Expense(
                1L,
                "Mercadona",
                new BigDecimal("20.50"),
                Category.FOOD,
                LocalDate.of(2026, 8, 26)
        );

        Expense expense2 = new Expense(
                2L,
                "Alimerka",
                new BigDecimal("25.50"),
                Category.FOOD,
                LocalDate.of(2026, 9, 12)
        );

        Expense expense3 = new Expense(
                3L,
                "Bus",
                new BigDecimal("3.50"),
                Category.TRANSPORT,
                LocalDate.of(2026, 9, 26)
        );

        when(repository.findAll())
                .thenReturn(List.of(expense1, expense2, expense3));

        var result = service.getTotalSpent();

        assertEquals(new BigDecimal("49.50"), result);
    }


    @Test
    void shouldReturnZeroWhenThereAreNoExpenses() {

        when(repository.findAll())
                .thenReturn(List.of());

        var result = service.getTotalSpent();

        assertEquals(BigDecimal.ZERO, result);
    }
}