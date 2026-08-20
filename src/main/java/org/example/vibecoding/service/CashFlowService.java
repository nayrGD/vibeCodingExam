package org.example.vibecoding.service;

import lombok.RequiredArgsConstructor;
import org.example.vibecoding.dao.CashFlowDao;
import org.example.vibecoding.model.CashFlow;
import org.example.vibecoding.model.Donation;
import org.example.vibecoding.model.Expense;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CashFlowService {

    private final CashFlowDao cashFlowDao;

    public List<CashFlow> getCashFlows(String type) {
        List<CashFlow> all = cashFlowDao.findAll();

        if ("donation".equalsIgnoreCase(type)) {
            return all.stream()
                    .filter(cf -> cf instanceof Donation)
                    .collect(Collectors.toList());
        } else if ("expense".equalsIgnoreCase(type)) {
            return all.stream()
                    .filter(cf -> cf instanceof Expense)
                    .collect(Collectors.toList());
        }
        return all;
    }

    public List<CashFlow> getCashFlowsByUserId(String userId) {
        return cashFlowDao.findByUserId(userId);
    }

    public Expense createExpense(Expense expense) {

        if (expense.getId() == null || expense.getId().isBlank()) {
            expense.setId(UUID.randomUUID().toString());
        }

        if (expense.getCreatedAt() == null) {
            expense.setCreatedAt(Instant.now());
        }

        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de la dépense doit être supérieur à zéro.");
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