package com.michael.expense.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseRequest implements Serializable {
    @NotBlank(message = "Expense name should not be empty")
    private String name;
    @NotBlank(message = "Description should not be empty")
    private String description;
   // @NotBlank(message = "Amount should not be empty")
    private BigDecimal amount;
    @NotBlank(message = "Category should not be empty")
    private String category;
    private LocalDate expenseDate;
}
