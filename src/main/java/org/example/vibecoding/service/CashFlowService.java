package org.example.vibecoding.service;

import lombok.RequiredArgsConstructor;
import org.example.vibecoding.dao.CashFlowDao;
import org.example.vibecoding.model.CashFlow;
import org.example.vibecoding.model.Expense;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashFlowService {

    private final CashFlowDao cashFlowDao;

    public List<CashFlow> getCashFlows(String type) {
        if (type != null && !type.isBlank()) {
            if (!"donation".equalsIgnoreCase(type) && !"expense".equalsIgnoreCase(type)) {
                throw new IllegalArgumentException("Le type doit être 'donation' ou 'expense'");
            }
            return cashFlowDao.findByType(type);
        }
        return cashFlowDao.findByType(null);
    }

    public List<CashFlow> getCashFlowsByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("L'identifiant utilisateur ne peut pas être vide.");
        }
        return cashFlowDao.findByUserId(userId);
    }

    public Expense createExpense(Expense expense) {
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de la dépense doit être supérieur à zéro.");
        }

        if (expense.getId() == null || expense.getId().isBlank()) {
            expense.setId(UUID.randomUUID().toString());
        }

        if (expense.getCreatedAt() == null) {
            expense.setCreatedAt(Instant.now());
        }

        cashFlowDao.saveExpense(expense);
        return expense;
    }

    public BigDecimal calculateBalance() {
        BigDecimal totalDonations = cashFlowDao.sumDonations();
        BigDecimal totalExpenses = cashFlowDao.sumExpenses();

        return totalDonations.subtract(totalExpenses);
    }
}