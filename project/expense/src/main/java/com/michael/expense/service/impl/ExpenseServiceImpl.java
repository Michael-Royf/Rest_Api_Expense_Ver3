package com.michael.expense.service.impl;

import com.michael.expense.entity.Expense;
import com.michael.expense.entity.User;
import com.michael.expense.exceptions.payload.ExpenseNotFoundException;
import com.michael.expense.exceptions.payload.MonthNotFoundException;
import com.michael.expense.payload.request.ExpenseRequest;
import com.michael.expense.payload.response.ExpensePaginationResponse;
import com.michael.expense.payload.response.ExpenseResponse;
import com.michael.expense.payload.response.MessageResponse;
import com.michael.expense.repository.ExpenseRepository;
import com.michael.expense.service.ExpenseService;
import com.michael.expense.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.michael.expense.constant.UserConstant.EXPENSE_DELETED;
import static com.michael.expense.constant.UserConstant.NO_EXPENSE_FOUND_BY_ID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ModelMapper mapper;
    private final UserService userService;


    @Override
    public ExpenseResponse createExpense(ExpenseRequest expenseRequest) {
        User user = userService.getLoggedInUser();

        LocalDate localDate = expenseRequest.getExpenseDate();
        Month month = localDate.getMonth();
        int year = localDate.getYear();
        int dayOfMonth = localDate.getDayOfMonth();
        //  localDate.

        Expense expense = Expense.builder()
                .name(expenseRequest.getName())
                .username(user.getUsername())
                .description(expenseRequest.getDescription())
                .amount(expenseRequest.getAmount())
                .month(month)
                .category(expenseRequest.getCategory())
                .user(user)
                .expenseDate(localDate)
                .build();

        expense = expenseRepository.save(expense);
        return mapper.map(expense, ExpenseResponse.class);
    }

    @Override
    public ExpenseResponse getExpenseById(Long expenseId) {
        return mapper.map(findExpenseByIdInDB(expenseId), ExpenseResponse.class);
    }


    @Override
    public ExpensePaginationResponse getAllUserExpenses(int pageNo, int pageSiZe,
                                                        String sortBy, String sortDir) {
        User user = userService.getLoggedInUser();
        Pageable pageable = createPageable(pageNo, pageSiZe, sortBy, sortDir);
        Page<Expense> expenses = expenseRepository.findByUser(user, pageable);
        List<ExpenseResponse> expenseResponses = mapExpenseToExpenseResponse(expenses);
        return createExpensePaginationResponse(expenseResponses, expenses);
    }


    @Override
    public ExpensePaginationResponse getExpensesByName(String name,
                                                       int pageNo, int pageSiZe,
                                                       String sortBy, String sortDir) {
        User user = userService.getLoggedInUser();
        Pageable pageable = createPageable(pageNo, pageSiZe, sortBy, sortDir);
        Page<Expense> expenses = expenseRepository.findByNameAndUser(name, user, pageable);
        List<ExpenseResponse> expenseResponses = mapExpenseToExpenseResponse(expenses);
        return createExpensePaginationResponse(expenseResponses, expenses);
    }

    @Override
    public ExpensePaginationResponse getExpensesByNameContaining(String name,
                                                                 int pageNo, int pageSiZe,
                                                                 String sortBy, String sortDir) {
        User user = userService.getLoggedInUser();
        Pageable pageable = createPageable(pageNo, pageSiZe, sortBy, sortDir);
        Page<Expense> expenses = expenseRepository.findByUserAndNameContaining(user, name, pageable);
        List<ExpenseResponse> expenseResponses = mapExpenseToExpenseResponse(expenses);
        return createExpensePaginationResponse(expenseResponses, expenses);
    }


    @Override
    public ExpensePaginationResponse getExpensesByCategory(String category,
                                                           int pageNo, int pageSiZe,
                                                           String sortBy, String sortDir) {
        User user = userService.getLoggedInUser();
        Pageable pageable = createPageable(pageNo, pageSiZe, sortBy, sortDir);
        Page<Expense> expenses = expenseRepository.findByCategoryAndUser(category, user, pageable);
        List<ExpenseResponse> expenseResponses = mapExpenseToExpenseResponse(expenses);
        return createExpensePaginationResponse(expenseResponses, expenses);
    }

    @Override
    public ExpensePaginationResponse getExpensesByMonth(String month, int monthInt,
                                                        int pageNo, int pageSiZe,
                                                        String sortBy, String sortDir) {
        User user = userService.getLoggedInUser();
        Pageable pageable = createPageable(pageNo, pageSiZe, sortBy, sortDir);
        Month monthEnum = null;
        if (!month.isEmpty()) {
            monthEnum = createMonth(month);
        }
        if (monthInt > 0) {
            monthEnum = createMonth(monthInt);
        }
        Page<Expense> expenses = expenseRepository.findByMonthAndUser(monthEnum, user, pageable);
        List<ExpenseResponse> expenseResponses = mapExpenseToExpenseResponse(expenses);
        return createExpensePaginationResponse(expenseResponses, expenses);
    }

    @Override
    public ExpensePaginationResponse getExpenseByCurrentDay(int pageNo, int pageSiZe, String sortBy, String sortDir) {
        User user = userService.getLoggedInUser();
        Pageable pageable = createPageable(pageNo, pageSiZe, sortBy, sortDir);
        LocalDate date = LocalDate.now();
        Page<Expense> expenses = expenseRepository.findByUserAndExpenseDate(user, date, pageable);
        List<ExpenseResponse> expenseResponses = mapExpenseToExpenseResponse(expenses);
        return createExpensePaginationResponse(expenseResponses, expenses);
    }

    @Override
    public ExpensePaginationResponse getExpensesByPeriod(LocalDate startDate, LocalDate endDate,
                                                         int pageNo, int pageSiZe,
                                                         String sortBy, String sortDir) {
        User user = userService.getLoggedInUser();
        Pageable pageable = createPageable(pageNo, pageSiZe, sortBy, sortDir);
        if (startDate == null) {
            startDate = LocalDate.EPOCH;
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        Page<Expense> expenses = expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate, pageable);

        List<ExpenseResponse> expenseResponses = mapExpenseToExpenseResponse(expenses);
        return createExpensePaginationResponse(expenseResponses, expenses);
    }

    @Override
    public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest expenseRequest) {
        User user = userService.getLoggedInUser();
        Expense expense = findExpenseByIdInDB(expenseId);
        isPostBelongUser(user, expense);
        expense.setName(expenseRequest.getName());
        expense.setDescription(expenseRequest.getDescription());
        expense.setCategory(expenseRequest.getCategory());
        expense.setAmount(expenseRequest.getAmount());
        expense = expenseRepository.save(expense);
        return mapper.map(expense, ExpenseResponse.class);
    }

    @Override
    public MessageResponse deleteExpense(Long expenseId) {
        Expense expense = findExpenseByIdInDB(expenseId);
        User user = userService.getLoggedInUser();
        isPostBelongUser(user, expense);
        expenseRepository.delete(expense);
        return new MessageResponse(String.format(EXPENSE_DELETED, expenseId));
    }


    private Expense findExpenseByIdInDB(Long expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(String.format(NO_EXPENSE_FOUND_BY_ID, expenseId)));
    }

    private void isPostBelongUser(User user, Expense expense) {
        if (!user.getUsername().equals(expense.getUsername())) {
            throw new RuntimeException("This expense doesn't belong to you, you can't remove or update this expense");
        }
    }

    private Pageable createPageable(int pageNo, int pageSiZe, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSiZe, sort);
    }

    private List<ExpenseResponse> mapExpenseToExpenseResponse(Page<Expense> expenses) {
        return expenses.getContent().stream()
                .map(expense -> mapper.map(expense, ExpenseResponse.class))
                .collect(Collectors.toList());
    }

    private ExpensePaginationResponse createExpensePaginationResponse(List<ExpenseResponse> expenseResponses, Page<Expense> expenses) {
        return ExpensePaginationResponse.builder()
                .totalAmount(calculateTotalAmount(expenseResponses))
                .content(expenseResponses)
                .pageNo(expenses.getNumber())
                .pageSize(expenses.getSize())
                .totalElements(expenses.getTotalElements())
                .totalPages(expenses.getTotalPages())
                .last(expenses.isLast())
                .build();
    }

    private BigDecimal calculateTotalAmount(List<ExpenseResponse> expenses) {
        return expenses
                .stream()
                .map(ExpenseResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private Month createMonth(String month) {
        Month enumMonth;
        try {
            enumMonth = Month.valueOf(month.toUpperCase(Locale.ROOT));
        } catch (Exception exception) {
            throw new MonthNotFoundException(String.format("The month of: %s does not exist", month));
        }
        return enumMonth;
    }

    private Month createMonth(int month) {
        Month enumMonth;
        try {
            enumMonth = Month.of(month);
        } catch (Exception exception) {
            throw new MonthNotFoundException(String.format("The month of: %s does not exist", month));
        }
        return enumMonth;
    }


}
