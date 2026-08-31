package com.ivan.expensetracker;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ExpenseService service = new ExpenseService();
        int option = -1;
        Scanner scan = new Scanner(System.in);

        try (var connection = DatabaseConnection.getConnection()) {
            System.out.println("Connected to PostgreSQL");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ExpenseRepository repository = new ExpenseRepository();

        try {
//            System.out.println(repository.findById(100L));
//            System.out.println(repository.findById(9999L));
            boolean deleted = repository.deleteById(100L);
            System.out.println(deleted);

            System.out.println(repository.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        while (option != 0) {

            System.out.println("Elige una opcion");
            System.out.println("1. Anadir Gasto");
            System.out.println("2. Buscar gasto por id");
            System.out.println("3. Listar Gastos");
            System.out.println("4. Borrar Gasto ");
            System.out.println("5. Filtrar gasto por categoria");
            System.out.println("6. Mostrar todo el dinero gastado");
            System.out.println("0. Salir");

            option = Integer.parseInt(scan.nextLine());

            switch (option) {
                case 1 -> {
                    System.out.println("id");
                    long id = Long.parseLong(scan.nextLine());

                    System.out.println("descripcion");
                    String description = scan.nextLine();

                    System.out.println("cantidad");
                    BigDecimal amount = new BigDecimal(scan.nextLine());

                    System.out.println("categoria");
                    Category category = Category.valueOf(scan.nextLine().toUpperCase());

                    System.out.println("fecha");
                    LocalDate date = LocalDate.parse(scan.nextLine());

                    Expense exp = new Expense(id, description, amount, category, date);
                    service.addExpense(exp);
                }

                case 2 -> {
                    System.out.println("id");

                    long id = Long.parseLong(scan.nextLine());

                    var result = service.findExpenseById(id);

                    System.out.println(result);
                }

                case 3 ->{

                    System.out.println("gastos");

                    var result = service.getAllExpenses();

                    System.out.println(result);
                }


                case 4 -> {

                    System.out.println("id");

                    Long id = Long.parseLong(scan.nextLine());

                    boolean result = service.deleteExpense(id);

                    System.out.println(result + "Borrado");
                }

                case 5 -> {

                    System.out.println("categoria");

                    Category category = Category.valueOf(scan.nextLine().toUpperCase());
                    var result= service.getExpensesByCategory(category);

                    System.out.println(result);
                }

                case 6 -> {

                    System.out.println("total de gastos");

                    var result = service.getTotalSpent();

                    System.out.println(result);
                }
                case 0 -> {
                    System.out.println("Saliendo");
                }
                default -> {
                    System.out.println("Opcion no valida");
                }


            }

        }
    }
}