package com.ivan.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class ExpenseRepositoryIntegrationTest {

    @Autowired
    private ExpenseRepository repository;

    @Autowired
    private ExpenseService service;

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldStartPostgres() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void shouldSaveAndFindExpenseByCategory() {
        Expense expense = new Expense(
                null,
                "Bici",
                new BigDecimal("200"),
                Category.TRANSPORT,
                LocalDate.of(2026, 9, 1)
        );
        Expense saved = repository.save(expense);
        List<Expense> results = repository.findByCategory(Category.TRANSPORT);

        assertNotNull(saved.getId());
        assertEquals(1, results.size());
        assertEquals(saved.getId(), results.get(0).getId());
        assertEquals("Bici", results.get(0).getDescription());
    }

    @Test
    void shouldUpdateExpenseInPostgres() {
        Expense original = repository.save(new Expense(
                null,
                "Medico",
                new BigDecimal("200"),
                Category.HEALTH,
                LocalDate.of(2026, 9, 29)
        ));

        Expense replacement = new Expense(
                null,
                "Dentista",
                new BigDecimal("250"),
                Category.HEALTH,
                LocalDate.of(2026, 9, 29)
        );

        var result = service.updateExpense(original.getId(), replacement);

        Expense reloaded = repository.findById(original.getId()).orElseThrow();

        assertTrue(result.isPresent());
        assertEquals(original.getId(), reloaded.getId());
        assertEquals(replacement.getDescription(), reloaded.getDescription());
        assertEquals(0, replacement.getAmount().compareTo(reloaded.getAmount()));
    }
}
