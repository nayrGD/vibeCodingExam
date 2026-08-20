package org.example.vibecoding.service;



import lombok.RequiredArgsConstructor;
import org.example.vibecoding.model.CashFlow;
import org.example.vibecoding.model.Expense;
import org.example.vibecoding.repository.CashFlowRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashFlowService {

    private final CashFlowRepository cashFlowRepository;

    public List<CashFlow> getCashFlows(String type) {
        if ("donation".equalsIgnoreCase(type)) {
            return cashFlowRepository.findAllDonations();
        } else if ("expense".equalsIgnoreCase(type)) {
            return cashFlowRepository.findAllExpenses();
        }
        return cashFlowRepository.findAll();
    }

    public List<CashFlow> getCashFlowsByUserId(String userId) {
        return cashFlowRepository.findByUserId(userId);
    }

    public Expense createExpense(Expense expense) {
        // Règle métier : Génération automatique de l'ID UUID si non fourni
        if (expense.getId() == null || expense.getId().isBlank()) {
            expense.setId(UUID.randomUUID().toString());
        }

        // Règle métier : Horodatage à l'instant présent lors de la création
        if (expense.getCreatedAt() == null) {
            expense.setCreatedAt(Instant.now());
        }

        // Validation simple du montant
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant de la dépense doit être supérieur à zéro.");
        }

        return cashFlowRepository.saveExpense(expense);
    }

    public BigDecimal calculateBalance() {
        BigDecimal totalDonations = cashFlowRepository.sumAllDonations();
        BigDecimal totalExpenses = cashFlowRepository.sumAllExpenses();

        // Solde = Donations - Dépenses
        return totalDonations.subtract(totalExpenses);
    }
}
