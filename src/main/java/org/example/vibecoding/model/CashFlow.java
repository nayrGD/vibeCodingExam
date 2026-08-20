package org.example.vibecoding.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class CashFlow {
    private String id;
    private Instant createdAt;
    private BigDecimal amount;
    private String userId;
}