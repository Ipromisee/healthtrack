package com.healthtrack.dao;

import com.healthtrack.model.MonthlySummary;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SummaryDAO {
    
    public MonthlySummary getMonthlySummary(int userId, int year, int month) {
        MonthlySummary summary = null;
        String sql = "SELECT * FROM MONTHLY_SUMMARY WHERE user_id = ? AND year = ? AND month = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                summary = new MonthlySummary(
                    rs.getInt("monthly_summary_id"),
                    rs.getInt("user_id"),
                    rs.getInt("year"),
                    rs.getInt("month"),
                    rs.getInt("total_steps"),
                    rs.getInt("total_appointments"),
                    rs.getTimestamp("last_updated"),
                    rs.getBoolean("is_finalized")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }
    
    public List<MonthlySummary> getMonthlySummariesByUserId(int userId) {
        List<MonthlySummary> summaries = new ArrayList<>();
        String sql = "SELECT * FROM MONTHLY_SUMMARY WHERE user_id = ? ORDER BY year DESC, month DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                summaries.add(new MonthlySummary(
                    rs.getInt("monthly_summary_id"),
                    rs.getInt("user_id"),
                    rs.getInt("year"),
                    rs.getInt("month"),
                    rs.getInt("total_steps"),
                    rs.getInt("total_appointments"),
                    rs.getTimestamp("last_updated"),
                    rs.getBoolean("is_finalized")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }
    
    public double getAverageStepsPerMonth(int userId, int year) {
        String sql = "SELECT AVG(total_steps) as avg_steps FROM MONTHLY_SUMMARY WHERE user_id = ? AND year = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_steps");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getMinStepsPerMonth(int userId, int year) {
        String sql = "SELECT MIN(total_steps) as min_steps FROM MONTHLY_SUMMARY WHERE user_id = ? AND year = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("min_steps");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getMaxStepsPerMonth(int userId, int year) {
        String sql = "SELECT MAX(total_steps) as max_steps FROM MONTHLY_SUMMARY WHERE user_id = ? AND year = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("max_steps");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

