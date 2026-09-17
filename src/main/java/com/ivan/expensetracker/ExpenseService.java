package com.ivan.expensetracker;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense addExpense(Expense expense) {
        return repository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return repository.findAll();
    }

    public Optional<Expense> findExpenseById(long id) {
        return repository.findById(id);
    }

    public boolean deleteExpense(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }

        return false;
    }

    public List<Expense> getExpensesByCategory(Category category) {
        return repository.findByCategory(category);
    }

    public BigDecimal getTotalSpent() {
        return repository.findAll()
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<Expense> updateExpense(long id, Expense replacement) {
        return repository.findById(id)
                .map(existing -> repository.save(new Expense(
                        existing.getId(),
                        replacement.getDescription(),
                        replacement.getAmount(),
                        replacement.getCategory(),
                        replacement.getDate()
                )));
    }
}
