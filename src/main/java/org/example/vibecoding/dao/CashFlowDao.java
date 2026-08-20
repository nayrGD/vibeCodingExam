package org.example.vibecoding.dao;

import org.example.vibecoding.model.CashFlow;
import org.example.vibecoding.model.Donation;
import org.example.vibecoding.model.Expense;
import org.example.vibecoding.model.ExpenseFrequency;
import org.example.vibecoding.util.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CashFlowDao {

    // Pour : GET /cash-flows?type=donation | expense
    public List<CashFlow> findByType(String type) {
        List<CashFlow> list = new ArrayList<>();
        String sql = "SELECT cf.id, cf.created_at, cf.amount, cf.user_id, " +
                "d.comment, e.reason, e.frequency " +
                "FROM cash_flow cf " +
                "LEFT JOIN donation d ON cf.id = d.id " +
                "LEFT JOIN expense e ON cf.id = e.id ";

        if ("donation".equalsIgnoreCase(type)) {
            sql += "WHERE d.id IS NOT NULL ";
        } else if ("expense".equalsIgnoreCase(type)) {
            sql += "WHERE e.id IS NOT NULL ";
        }

        sql += "ORDER BY cf.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToCashFlow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Pour : GET /users/{id}/cash-flows
    public List<CashFlow> findByUserId(String userId) {
        List<CashFlow> list = new ArrayList<>();
        String sql = "SELECT cf.id, cf.created_at, cf.amount, cf.user_id, " +
                "d.comment, e.reason, e.frequency " +
                "FROM cash_flow cf " +
                "LEFT JOIN donation d ON cf.id = d.id " +
                "LEFT JOIN expense e ON cf.id = e.id " +
                "WHERE cf.user_id = ? " +
                "ORDER BY cf.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCashFlow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public void saveExpense(Expense expense) {
        String sqlCashFlow = "INSERT INTO cash_flow (id, created_at, amount, user_id) VALUES (?, ?, ?, ?)";
        String sqlExpense = "INSERT INTO expense (id, reason, frequency) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(sqlCashFlow);
                 PreparedStatement stmt2 = conn.prepareStatement(sqlExpense)) {

                stmt1.setString(1, expense.getId());
                stmt1.setTimestamp(2, Timestamp.from(expense.getCreatedAt()));
                stmt1.setBigDecimal(3, expense.getAmount());
                stmt1.setString(4, expense.getUserId());
                stmt1.executeUpdate();

                stmt2.setString(1, expense.getId());
                stmt2.setString(2, expense.getReason());
                stmt2.setString(3, expense.getFrequency() != null ? expense.getFrequency().name() : ExpenseFrequency.NONE.name());
                stmt2.executeUpdate();

                conn.commit(); // Validation
                System.out.println("Dépense insérée avec succès !");

            } catch (SQLException e) {
                conn.rollback(); // Annulation en cas d'erreur
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Pour : GET /balance
    public BigDecimal sumDonations() {
        String sql = "SELECT COALESCE(SUM(cf.amount), 0) FROM cash_flow cf INNER JOIN donation d ON cf.id = d.id";
        return executeSumQuery(sql);
    }

    public BigDecimal sumExpenses() {
        String sql = "SELECT COALESCE(SUM(cf.amount), 0) FROM cash_flow cf INNER JOIN expense e ON cf.id = e.id";
        return executeSumQuery(sql);
    }

    private CashFlow mapResultSetToCashFlow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        BigDecimal amount = rs.getBigDecimal("amount");
        String userId = rs.getString("user_id");

        String comment = rs.getString("comment");
        if (comment != null) {
            return new Donation(id, createdAtTimestamp != null ? createdAtTimestamp.toInstant() : null, amount, userId, comment);
        }

        String reason = rs.getString("reason");
        String freqStr = rs.getString("frequency");
        ExpenseFrequency frequency = freqStr != null ? ExpenseFrequency.valueOf(freqStr) : ExpenseFrequency.NONE;

        return new Expense(id, createdAtTimestamp != null ? createdAtTimestamp.toInstant() : null, amount, userId, reason, frequency);
    }

    private BigDecimal executeSumQuery(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}