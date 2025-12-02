package com.healthtrack.dao;

import com.healthtrack.model.CaregiverPatient;
import com.healthtrack.model.User;
import com.healthtrack.model.MonthlySummary;
import com.healthtrack.model.Appointment;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaregiverDAO {
    
    /**
     * 获取照顾者关联的所有患者
     */
    public List<CaregiverPatient> getPatientsByCaregiver(int caregiverId) {
        List<CaregiverPatient> relationships = new ArrayList<>();
        String sql = "SELECT cp.*, " +
                     "u.user_id, u.health_id, u.full_name, u.account_status, u.user_role, u.created_at " +
                     "FROM CAREGIVER_PATIENT cp " +
                     "JOIN USER u ON cp.patient_id = u.user_id " +
                     "WHERE cp.caregiver_id = ? AND cp.status = 'Active' " +
                     "ORDER BY cp.linked_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, caregiverId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CaregiverPatient cp = new CaregiverPatient(
                    rs.getInt("caregiver_patient_id"),
                    rs.getInt("caregiver_id"),
                    rs.getInt("patient_id"),
                    rs.getString("relationship"),
                    rs.getString("status"),
                    rs.getTimestamp("linked_at"),
                    rs.getTimestamp("approved_at"),
                    rs.getString("notes")
                );
                
                User patient = new User(
                    rs.getInt("user_id"),
                    rs.getString("health_id"),
                    rs.getString("full_name"),
                    rs.getString("account_status"),
                    rs.getString("user_role"),
                    rs.getTimestamp("created_at")
                );
                cp.setPatient(patient);
                relationships.add(cp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return relationships;
    }
    
    /**
     * 获取患者的所有照顾者
     */
    public List<CaregiverPatient> getCaregiversByPatient(int patientId) {
        List<CaregiverPatient> relationships = new ArrayList<>();
        String sql = "SELECT cp.*, " +
                     "u.user_id, u.health_id, u.full_name, u.account_status, u.user_role, u.created_at " +
                     "FROM CAREGIVER_PATIENT cp " +
                     "JOIN USER u ON cp.caregiver_id = u.user_id " +
                     "WHERE cp.patient_id = ? " +
                     "ORDER BY cp.linked_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CaregiverPatient cp = new CaregiverPatient(
                    rs.getInt("caregiver_patient_id"),
                    rs.getInt("caregiver_id"),
                    rs.getInt("patient_id"),
                    rs.getString("relationship"),
                    rs.getString("status"),
                    rs.getTimestamp("linked_at"),
                    rs.getTimestamp("approved_at"),
                    rs.getString("notes")
                );
                
                User caregiver = new User(
                    rs.getInt("user_id"),
                    rs.getString("health_id"),
                    rs.getString("full_name"),
                    rs.getString("account_status"),
                    rs.getString("user_role"),
                    rs.getTimestamp("created_at")
                );
                cp.setCaregiver(caregiver);
                relationships.add(cp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return relationships;
    }
    
    /**
     * 获取待审批的照顾者请求（患者视角）
     */
    public List<CaregiverPatient> getPendingRequests(int patientId) {
        List<CaregiverPatient> relationships = new ArrayList<>();
        String sql = "SELECT cp.*, " +
                     "u.user_id, u.health_id, u.full_name, u.account_status, u.user_role, u.created_at " +
                     "FROM CAREGIVER_PATIENT cp " +
                     "JOIN USER u ON cp.caregiver_id = u.user_id " +
                     "WHERE cp.patient_id = ? AND cp.status = 'Pending' " +
                     "ORDER BY cp.linked_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CaregiverPatient cp = new CaregiverPatient(
                    rs.getInt("caregiver_patient_id"),
                    rs.getInt("caregiver_id"),
                    rs.getInt("patient_id"),
                    rs.getString("relationship"),
                    rs.getString("status"),
                    rs.getTimestamp("linked_at"),
                    rs.getTimestamp("approved_at"),
                    rs.getString("notes")
                );
                
                User caregiver = new User(
                    rs.getInt("user_id"),
                    rs.getString("health_id"),
                    rs.getString("full_name"),
                    rs.getString("account_status"),
                    rs.getString("user_role"),
                    rs.getTimestamp("created_at")
                );
                cp.setCaregiver(caregiver);
                relationships.add(cp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return relationships;
    }
    
    /**
     * 照顾者请求关联患者
     */
    public boolean requestCaregiverLink(int caregiverId, String patientHealthId, String relationship, String notes) {
        // 首先查找患者
        String findPatientSql = "SELECT user_id FROM USER WHERE health_id = ? AND user_role = 'Patient'";
        String insertSql = "INSERT INTO CAREGIVER_PATIENT (caregiver_id, patient_id, relationship, status, notes) " +
                          "VALUES (?, ?, ?, 'Pending', ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            // 查找患者
            PreparedStatement findStmt = conn.prepareStatement(findPatientSql);
            findStmt.setString(1, patientHealthId);
            ResultSet rs = findStmt.executeQuery();
            
            if (!rs.next()) {
                return false; // 患者不存在
            }
            int patientId = rs.getInt("user_id");
            findStmt.close();
            
            // 插入关联请求
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, caregiverId);
            insertStmt.setInt(2, patientId);
            insertStmt.setString(3, relationship);
            insertStmt.setString(4, notes);
            return insertStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 患者审批照顾者请求
     */
    public boolean approveRequest(int caregiverPatientId, boolean approve) {
        String sql;
        if (approve) {
            sql = "UPDATE CAREGIVER_PATIENT SET status = 'Active', approved_at = CURRENT_TIMESTAMP WHERE caregiver_patient_id = ?";
        } else {
            sql = "UPDATE CAREGIVER_PATIENT SET status = 'Inactive' WHERE caregiver_patient_id = ?";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, caregiverPatientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 取消照顾者关联
     */
    public boolean removeCaregiverLink(int caregiverPatientId) {
        String sql = "UPDATE CAREGIVER_PATIENT SET status = 'Inactive' WHERE caregiver_patient_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, caregiverPatientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取患者的月度健康汇总（照顾者监控用）
     */
    public List<MonthlySummary> getPatientSummaries(int patientId) {
        List<MonthlySummary> summaries = new ArrayList<>();
        String sql = "SELECT * FROM MONTHLY_SUMMARY WHERE user_id = ? ORDER BY year DESC, month DESC LIMIT 12";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MonthlySummary summary = new MonthlySummary(
                    rs.getInt("monthly_summary_id"),
                    rs.getInt("user_id"),
                    rs.getInt("year"),
                    rs.getInt("month"),
                    rs.getInt("total_steps"),
                    rs.getInt("total_appointments"),
                    rs.getTimestamp("last_updated"),
                    rs.getBoolean("is_finalized")
                );
                summaries.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }
    
    /**
     * 获取患者的预约记录（照顾者监控用）
     */
    public List<Appointment> getPatientAppointments(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.provider_name, p.license_no " +
                     "FROM APPOINTMENT a " +
                     "JOIN ACTION ac ON a.action_id = ac.action_id " +
                     "JOIN PROVIDER p ON a.provider_id = p.provider_id " +
                     "WHERE ac.created_by = ? " +
                     "ORDER BY a.scheduled_at DESC LIMIT 10";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
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
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    /**
     * 检查照顾者是否可以访问患者数据
     */
    public boolean canAccessPatient(int caregiverId, int patientId) {
        String sql = "SELECT COUNT(*) FROM CAREGIVER_PATIENT " +
                     "WHERE caregiver_id = ? AND patient_id = ? AND status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, caregiverId);
            pstmt.setInt(2, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 获取所有患者（用于照顾者搜索）
     */
    public List<User> getAllPatients() {
        List<User> patients = new ArrayList<>();
        String sql = "SELECT * FROM USER WHERE user_role = 'Patient' AND account_status = 'Active' ORDER BY full_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User patient = new User(
                    rs.getInt("user_id"),
                    rs.getString("health_id"),
                    rs.getString("full_name"),
                    rs.getString("account_status"),
                    rs.getString("user_role"),
                    rs.getTimestamp("created_at")
                );
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
}

