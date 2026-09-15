package com.ivan.expensetracker;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Expense> getExpenses(
            @RequestParam(required = false) Category category
    ) {
        if (category != null) {
            return expenseService.getExpensesByCategory(category);
        }

        return expenseService.getAllExpenses();
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(
            @Valid @RequestBody CreateExpenseRequest request
    ) {
        Expense expense = new Expense(
                null,
                request.description(),
                request.amount(),
                request.category(),
                request.date()
        );

        Expense savedExpense = expenseService.addExpense(expense);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedExpense);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable long id) {

        return expenseService.findExpenseById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable long id) {

        boolean deleted = expenseService.deleteExpense(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}