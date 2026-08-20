package org.example.vibecoding.controller;

import lombok.RequiredArgsConstructor;
import org.example.vibecoding.model.CashFlow;
import org.example.vibecoding.model.Expense;
import org.example.vibecoding.service.CashFlowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class CashFlowController {

    private final CashFlowService cashFlowService;

    @GetMapping("/cash-flows")
    public ResponseEntity<List<CashFlow>> getCashFlows(
            @RequestParam(required = false) String type) {
        List<CashFlow> flows = cashFlowService.getCashFlows(type);
        return ResponseEntity.ok(flows);
    }


    @GetMapping("/users/{id}/cash-flows")
    public ResponseEntity<List<CashFlow>> getCashFlowsByUserId(@PathVariable("id") String userId) {
        List<CashFlow> userFlows = cashFlowService.getCashFlowsByUserId(userId);
        return ResponseEntity.ok(userFlows);
    }


    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Expense createdExpense = cashFlowService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }


    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance() {
        BigDecimal balance = cashFlowService.calculateBalance();
        return ResponseEntity.ok(Map.of("balance", balance));
    }
}
