package org.example.vibecoding.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Donation extends CashFlow {
    private String comment;

    public Donation(String id, Instant createdAt, BigDecimal amount, String userId, String comment) {
        super(id, createdAt, amount, userId);
        this.comment = comment;
    }
}
