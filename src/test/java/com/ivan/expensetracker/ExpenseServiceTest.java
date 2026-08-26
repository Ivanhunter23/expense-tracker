package com.ivan.expensetracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseServiceTest {

    private ExpenseService service;

    @BeforeEach
    void setUp() {
        service = new ExpenseService();
    }

    @Test
    void shouldAddExpense(){
        Expense expense = new Expense(
                1L,
                "Mercadona",
                new BigDecimal("20.50"),
                Category.FOOD,
                LocalDate.of(2026,8,26)
        );

        service.addExpense(expense);

        assertEquals(1, service.getAllExpenses().size());
        assertEquals(expense, service.getAllExpenses().get(0));
    }
    @Test
    void shouldFindExpenseById(){
        Expense expense = new Expense(
                1L,
                "Mercadona",
                new BigDecimal("20.50"),
                Category.FOOD,
                LocalDate.of(2026,8,26)
        );

        service.addExpense(expense);
        var result = service.findExpenseById(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().id());



    }
    @Test
    void shouldReturnEmptyWhenExpenseIdDoesNotExist() {

        var result = service.findExpenseById(2);

        assertTrue(result.isEmpty());

    }
    @Test
    void shouldDeleteExpenseWhenIdExists() {

        Expense expense = new Expense(
                1L,
                "Mercadona",
                new BigDecimal("20.50"),
                Category.FOOD,
                LocalDate.of(2026, 8, 26)
        );

        service.addExpense(expense);

        boolean deleted = service.deleteExpense(1);


        assertTrue(deleted);
        assertTrue(service.getAllExpenses().isEmpty());
    }
    @Test
    void shouldReturnFalseWhenDeletingNonExistingExpense() {

        boolean deleted = service.deleteExpense(1);

        assertFalse(deleted);
    }
    @Test
    void shouldReturnChosenCategory(){
        Expense expense = new Expense(
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
        service.addExpense(expense);
        service.addExpense(expense2);
        service.addExpense(expense3);

        var category = service.getExpensesByCategory(Category.FOOD);


        assertEquals(2, category.size());

        assertTrue(category.contains(expense));
        assertTrue(category.contains(expense2));
        assertFalse(category.contains(expense3));
    }

    @Test
    void shouldReturnTotalSpent() {

        Expense expense = new Expense(
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
        service.addExpense(expense);
        service.addExpense(expense2);
        service.addExpense(expense3);

        var result = service.getTotalSpent();

        // comprueba que da 49.50
        assertEquals(new BigDecimal("49.50"), result);
    }

    @Test
    void shouldReturnZeroWhenThereAreNoExpenses() {
        var result = service.getTotalSpent();

        assertEquals(BigDecimal.ZERO, result);


    }
    @Test
    void shouldNotAllowModificationOfReturnedExpenses() {
        Expense expense = new Expense(
                1L,
                "Mercadona",
                new BigDecimal("20.50"),
                Category.FOOD,
                LocalDate.of(2026, 8, 26)
        );

        service.addExpense(expense);

        var expenses = service.getAllExpenses();

        assertThrows(
                UnsupportedOperationException.class,
                () -> expenses.clear()
        );
    }

}
