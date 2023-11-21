package com.michael.expense.controller;

import com.michael.expense.payload.request.ExpenseRequest;
import com.michael.expense.payload.response.ExpensePaginationResponse;
import com.michael.expense.payload.response.ExpenseResponse;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.michael.expense.constant.PaginationConstants.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;


    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse> createExpense(@RequestBody @Valid ExpenseRequest expenseRequest) {
        return new ResponseEntity<>(expenseService.createExpense(expenseRequest), CREATED);
    }

    @GetMapping("/get/{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable("expenseId") Long expenseId) {
        return new ResponseEntity<>(expenseService.getExpenseById(expenseId), OK);
    }

    @GetMapping("/get")
    public ResponseEntity<ExpensePaginationResponse> getAllUserExpense(
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(expenseService.getAllUserExpenses(page, pageSize, sortBy, sortDir), OK);
    }

    @GetMapping("/get/name/{name}")
    public ResponseEntity<ExpensePaginationResponse> getAllUserExpenseByName(
            @PathVariable("name") String name,
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(expenseService.getExpensesByName(name, page, pageSize, sortBy, sortDir), OK);
    }

    @GetMapping("/get/name_contain/{name}")
    public ResponseEntity<ExpensePaginationResponse> getAllUserExpenseByNameContaining(
            @PathVariable("name") String name,
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(expenseService.getExpensesByNameContaining(name, page, pageSize, sortBy, sortDir), OK);
    }


    @GetMapping("/get/category/{category}")
    public ResponseEntity<ExpensePaginationResponse> getAllUserExpenseByCategory(
            @PathVariable("category") String category,
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(expenseService.getExpensesByCategory(category, page, pageSize, sortBy, sortDir), OK);
    }

    @GetMapping("/get/current_day")
    public ResponseEntity<ExpensePaginationResponse> getAllUserExpenseByCurrentDay(
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(expenseService.getExpenseByCurrentDay(page, pageSize, sortBy, sortDir), OK);
    }

    @GetMapping("/get/month")
    public ResponseEntity<ExpensePaginationResponse> getAllUserExpenseByMonth(
            @RequestParam(value = "month", required = false, defaultValue = "") String month,
            @RequestParam(value = "monthInt", required = false, defaultValue = "0") int monthInt,
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(expenseService.getExpensesByMonth(month, monthInt,
                page, pageSize, sortBy, sortDir), OK);
    }



    @GetMapping("/get/period")
    public ResponseEntity<ExpensePaginationResponse> getExpensesByPeriod(
            @RequestParam(value = "startDate", required = false)LocalDate startDate,
            @RequestParam(value = "endDate", required = false)LocalDate endDate,
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return new ResponseEntity<>(expenseService.getExpensesByPeriod(startDate, endDate, page, pageSize, sortBy, sortDir), OK);
    }


    @PutMapping("/update/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateResponse(@PathVariable("expenseId") Long expenseId,
                                                          @Valid @RequestBody ExpenseRequest expenseRequest) {
        return new ResponseEntity<>(expenseService.updateExpense(expenseId, expenseRequest), OK);
    }

    @DeleteMapping("/delete/{expenseId}")
    public ResponseEntity<MessageResponse> deleteExpense(@PathVariable("expenseId") Long expenseId) {
        return new ResponseEntity<>(expenseService.deleteExpense(expenseId), OK);
    }

}
