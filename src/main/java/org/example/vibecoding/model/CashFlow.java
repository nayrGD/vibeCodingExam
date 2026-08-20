package org.example.vibecoding.model;

import java.math.BigDecimal;
import java.time.Instant;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CashFlow {
    private String id;
    private Instant createdAt;
    private BigDecimal amount;
    private String userId;
}
