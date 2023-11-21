package com.michael.expense.service;

import com.michael.expense.payload.request.ExpenseRequest;
import com.michael.expense.payload.response.ExpensePaginationResponse;
import com.michael.expense.payload.response.ExpenseResponse;
import com.michael.expense.payload.response.MessageResponse;

import java.time.LocalDate;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest expenseRequest);

    ExpenseResponse getExpenseById(Long expenseId);

    ExpensePaginationResponse getAllUserExpenses(int pageNo, int pageSiZe, String sortBy, String sortDir);

    ExpensePaginationResponse getExpensesByName(String name,
                                                int pageNo, int pageSiZe, String sortBy, String sortDir);

    ExpensePaginationResponse getExpensesByNameContaining(String name,
                                                          int pageNo, int pageSiZe, String sortBy, String sortDir);

    ExpensePaginationResponse getExpensesByCategory(String category,
                                                    int pageNo, int pageSiZe, String sortBy, String sortDir);

    ExpensePaginationResponse getExpensesByMonth(String month, int monthInt,
                                                 int pageNo, int pageSiZe,
                                                 String sortBy, String sortDir);

    ExpensePaginationResponse getExpenseByCurrentDay(int pageNo, int pageSiZe,
                                                     String sortBy, String sortDir);

    ExpensePaginationResponse getExpensesByPeriod(LocalDate startDate, LocalDate endDate,
                                        int pageNo, int pageSiZe, String sortBy, String sortDir);


    ExpenseResponse updateExpense(Long expenseId, ExpenseRequest expenseRequest);

    MessageResponse deleteExpense(Long expenseId);
}
