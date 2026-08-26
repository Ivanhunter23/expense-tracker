package com.ivan.expensetracker;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ExpenseService {
    private final List<Expense> expenses = new ArrayList<>();

    public void addExpense(Expense expense){
        expenses.add(expense);
    }

    public List<Expense> getAllExpenses() {
        return List.copyOf(expenses);
    }

    public Optional <Expense> findExpenseById(long id) {
        for(Expense expense : expenses){
            if (expense.id() == id ){
                return Optional.of(expense);
            }
        }
        return Optional.empty();
    }

    public boolean deleteExpense(long id) {
        Optional<Expense> expense = findExpenseById(id);

        if(expense.isPresent()){
            expenses.remove(expense.get());
            return true;
        }
        return false;
    }

    public List<Expense> getExpensesByCategory(Category category) {
    List<Expense> result = new ArrayList<>();

        for(Expense expense : expenses){
            if (expense.category() == category) {
                result.add(expense);
            }
        }
        return result;
    }
    public BigDecimal getTotalSpent() {
        BigDecimal total = BigDecimal.ZERO;

        for (Expense expense : expenses){
            total = total.add(expense.amount());

        }
        return total;
    }


}
