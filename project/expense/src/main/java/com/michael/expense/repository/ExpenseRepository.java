package com.michael.expense.repository;

import com.michael.expense.entity.Expense;
import com.michael.expense.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);


    Page<Expense> findByUser(User user, Pageable pageable);

    //  Page<Expense> findByUserIdAndDateBetween(Long userId, Date startDate, Date endDate, Pageable pageable);


    Page<Expense> findByCategoryAndUser(String category, User user, Pageable pageable);

    List<Expense> findByUserAndMonth(User user, Month month);

    Page<Expense> findByMonthAndUser(Month month, User user, Pageable pageable);


    Page<Expense> findByUserAndExpenseDate(User user, LocalDate date, Pageable pageable);


    Page<Expense> findByNameAndUser(String name, User user, Pageable pageable);


    Page<Expense> findByUserAndNameContaining(User user, String name, Pageable pageable);

    Page<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);

}
