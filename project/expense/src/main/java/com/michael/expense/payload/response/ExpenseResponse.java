package com.michael.expense.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseResponse {
    private Long id;
    private String name;
    private String username;
    private String description;
    private BigDecimal amount;
    private String category;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Israel")
    private LocalDate expenseDate;
}
