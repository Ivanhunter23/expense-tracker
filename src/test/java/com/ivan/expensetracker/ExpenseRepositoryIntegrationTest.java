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
}
