package org.example.vibecoding.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Expense extends CashFlow {
    private String reason;
    private ExpenseFrequency frequency;

    public Expense(String id, Instant createdAt, BigDecimal amount, String userId, String reason, ExpenseFrequency frequency) {
        super(id, createdAt, amount, userId);
        this.reason = reason;
        this.frequency = frequency;
    }
}