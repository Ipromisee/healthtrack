package com.healthtrack.dao;

import com.healthtrack.model.Appointment;
import com.healthtrack.model.Provider;
import com.healthtrack.model.User;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    
    public boolean createAppointment(Appointment appointment, int userId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into ACTION table first
            String actionSql = "INSERT INTO ACTION (action_type, created_by) VALUES ('Appointment', ?)";
            PreparedStatement actionStmt = conn.prepareStatement(actionSql, Statement.RETURN_GENERATED_KEYS);
            actionStmt.setInt(1, userId);
            actionStmt.executeUpdate();
            
            ResultSet rs = actionStmt.getGeneratedKeys();
            int actionId = 0;
            if (rs.next()) {
                actionId = rs.getInt(1);
            }
            actionStmt.close();
            
            // Insert into APPOINTMENT table
            String appointmentSql = "INSERT INTO APPOINTMENT (action_id, provider_id, scheduled_at, consultation_type, memo, status) VALUES (?, ?, ?, ?, ?, 'Scheduled')";
            PreparedStatement appointmentStmt = conn.prepareStatement(appointmentSql);
            appointmentStmt.setInt(1, actionId);
            appointmentStmt.setInt(2, appointment.getProviderId());
            appointmentStmt.setTimestamp(3, appointment.getScheduledAt());
            appointmentStmt.setString(4, appointment.getConsultationType());
            appointmentStmt.setString(5, appointment.getMemo());
            appointmentStmt.executeUpdate();
            appointmentStmt.close();
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public List<Appointment> getAppointmentsByUserId(int userId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, ap.*, p.provider_name, p.license_no " +
                     "FROM ACTION a " +
                     "JOIN APPOINTMENT ap ON a.action_id = ap.action_id " +
                     "JOIN PROVIDER p ON ap.provider_id = p.provider_id " +
                     "WHERE a.created_by = ? ORDER BY ap.scheduled_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
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
                
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    public boolean cancelAppointment(int actionId, String cancelReason) {
        String sql = "UPDATE APPOINTMENT SET status = 'Cancelled', cancel_reason = ?, cancel_time = NOW() WHERE action_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cancelReason);
            pstmt.setInt(2, actionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
            sql.append(" AND u.health_id = ?");
            params.add(healthId);
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
    
    public int getTotalAppointmentsInRange(int userId, Date startDate, Date endDate) {
        String sql = "SELECT COUNT(*) as total FROM ACTION a " +
                     "JOIN APPOINTMENT ap ON a.action_id = ap.action_id " +
                     "WHERE a.created_by = ? AND DATE(ap.scheduled_at) BETWEEN ? AND ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

