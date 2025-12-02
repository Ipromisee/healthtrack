package com.healthtrack.dao;

import com.healthtrack.model.Appointment;
import com.healthtrack.model.Provider;
import com.healthtrack.model.User;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchDAO {
    
    public List<User> searchUsersByHealthId(String healthId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USER WHERE health_id LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + healthId + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("health_id"),
                    rs.getString("full_name"),
                    rs.getString("account_status"),
                    rs.getString("user_role"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public List<Appointment> searchAppointments(String healthId, Integer providerId, String consultationType, Date startDate, Date endDate) {
        List<Appointment> appointments = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT a.*, ap.*, p.provider_name, p.license_no, u.health_id, u.full_name " +
            "FROM ACTION a " +
            "JOIN APPOINTMENT ap ON a.action_id = ap.action_id " +
            "JOIN PROVIDER p ON ap.provider_id = p.provider_id " +
            "JOIN USER u ON a.created_by = u.user_id " +
            "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        if (healthId != null && !healthId.isEmpty()) {
            sql.append(" AND u.health_id LIKE ?");
            params.add("%" + healthId + "%");
        }
        if (providerId != null) {
            sql.append(" AND ap.provider_id = ?");
            params.add(providerId);
        }
        if (consultationType != null && !consultationType.isEmpty()) {
            sql.append(" AND ap.consultation_type = ?");
            params.add(consultationType);
        }
        if (startDate != null) {
            sql.append(" AND DATE(ap.scheduled_at) >= ?");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append(" AND DATE(ap.scheduled_at) <= ?");
            params.add(endDate);
        }
        
        sql.append(" ORDER BY ap.scheduled_at DESC");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getInt("action_id"),
                    rs.getInt("provider_id"),
                    rs.getTimestamp("scheduled_at"),
                    rs.getString("consultation_type"),
                    rs.getString("memo"),
                    rs.getString("status"),
                    rs.getString("cancel_reason"),
                    rs.getTimestamp("cancel_time")
                );
                
                Provider provider = new Provider();
                provider.setProviderId(rs.getInt("provider_id"));
                provider.setProviderName(rs.getString("provider_name"));
                provider.setLicenseNo(rs.getString("license_no"));
                appointment.setProvider(provider);
                
                User user = new User();
                user.setHealthId(rs.getString("health_id"));
                user.setFullName(rs.getString("full_name"));
                appointment.setUser(user);
                
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    public List<User> getMostActiveUsers(int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, " +
                     "COUNT(DISTINCT a.action_id) as action_count, " +
                     "COUNT(DISTINCT cp.challenge_participant_id) as challenge_count " +
                     "FROM USER u " +
                     "LEFT JOIN ACTION a ON u.user_id = a.created_by " +
                     "LEFT JOIN CHALLENGE_PARTICIPANT cp ON u.user_id = cp.user_id AND cp.participant_status = 'Joined' " +
                     "GROUP BY u.user_id " +
                     "ORDER BY (action_count + challenge_count) DESC " +
                     "LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("health_id"),
                    rs.getString("full_name"),
                    rs.getString("account_status"),
                    rs.getString("user_role"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}

