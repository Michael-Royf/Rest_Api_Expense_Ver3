package com.michael.expense.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "expenses")
public class Expense implements Serializable {
    @Id
    @SequenceGenerator(name = "expense_sequence",
            sequenceName = "expense_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "expense_sequence"
    )
    private Long id;
    @Column(name = "expense_name", nullable = false)
    private String name;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "expense_description")
    private String description;
    @Column(name = "expense_amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "expense_category", nullable = false)
    private String category;
    @Column(name = "expense_month", nullable = false)
    private Month month;

    @Column(name = "expense_date",nullable = false)
    private LocalDate expenseDate;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "udated_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;
}
