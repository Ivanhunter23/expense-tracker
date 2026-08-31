package com.ivan.expensetracker;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ExpenseRepository {

    public void save(Expense expense) throws SQLException {
        String sql = """
                INSERT INTO expenses (id, description, amount, category, date)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, expense.id());
            statement.setString(2, expense.description());
            statement.setBigDecimal(3, expense.amount());
            statement.setString(4, expense.category().name());
            statement.setObject(5, expense.date());

            statement.executeUpdate();
        }


    }

    public Optional<Expense> findById(long id) throws SQLException {
        String sql = """
                SELECT id, description, amount, category, date
                FROM expenses
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    long expenseId = resultSet.getLong("id");
                    String description = resultSet.getString("description");
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    Category category =
                            Category.valueOf(resultSet.getString("category"));
                    LocalDate date =
                            resultSet.getObject("date", LocalDate.class);

                    Expense expense = new Expense(
                            expenseId,
                            description,
                            amount,
                            category,
                            date
                    );

                    return Optional.of(expense);
                }

                return Optional.empty();
            }
        }
    }

    public List<Expense> findAll() throws SQLException {
        String sql =
                """ 
                        SELECT id, description, amount, category, date
                        FROM expenses
                        ORDER BY id
                        """;
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Expense> expenses = new ArrayList<>();
                while (resultSet.next()) {

                    long expenseId = resultSet.getLong("id");
                    String description = resultSet.getString("description");
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    Category category =
                            Category.valueOf(resultSet.getString("category"));
                    LocalDate date =
                            resultSet.getObject("date", LocalDate.class);

                    Expense expense = new Expense(
                            expenseId,
                            description,
                            amount,
                            category,
                            date
                    );

                    expenses.add(expense);
                }

                return expenses;

            }


        }
    }
        public boolean deleteById(long id) throws SQLException{
            String sql =
                    """ 
                            DELETE FROM expenses
                            WHERE id = ?
                            """;


            try (
                    Connection connection = DatabaseConnection.getConnection();
                    PreparedStatement statement = connection.prepareStatement(sql)
            ) {
                statement.setLong(1, id);
                int affectedRows = statement.executeUpdate();

                return affectedRows > 0;
            }
        }
}
