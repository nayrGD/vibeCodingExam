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


    public List<CashFlow> findAll() {
        List<CashFlow> list = new ArrayList<>();
        String sql = "SELECT cf.id, cf.created_at, cf.amount, cf.user_id, " +
                "d.comment, e.reason, e.frequency " +
                "FROM cash_flow cf " +
                "LEFT JOIN donation d ON cf.id = d.id " +
                "LEFT JOIN expense e ON cf.id = e.id " +
                "ORDER BY cf.created_at DESC";

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
                stmt2.setString(3, expense.getFrequency().name());
                stmt2.executeUpdate();

                conn.commit();
                System.out.println("Dépense insérée avec succès !");

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


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
        Timestamp createdAt = rs.getTimestamp("created_at");
        BigDecimal amount = rs.getBigDecimal("amount");
        String userId = rs.getString("user_id");

        String comment = rs.getString("comment");
        if (comment != null) {
            return new Donation(id, createdAt.toInstant(), amount, userId, comment);
        }

        String reason = rs.getString("reason");
        if (reason != null) {
            String freqStr = rs.getString("frequency");
            ExpenseFrequency frequency = freqStr != null ? ExpenseFrequency.valueOf(freqStr) : ExpenseFrequency.NONE;
            return new Expense(id, createdAt.toInstant(), amount, userId, reason, frequency);
        }

        return new CashFlow(id, createdAt.toInstant(), amount, userId);
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