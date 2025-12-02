package com.healthtrack.dao;

import com.healthtrack.model.CaregiverPatient;
import com.healthtrack.model.User;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 照顾者-患者关系数据访问对象
 */
public class CaregiverPatientDAO {
    
    /**
     * 获取照顾者关联的所有患者
     */
    public List<CaregiverPatient> getPatientsByCaregiver(int caregiverId) {
        List<CaregiverPatient> patients = new ArrayList<>();
        String sql = "SELECT cp.*, " +
                    "p.user_id as p_user_id, p.health_id as p_health_id, p.full_name as p_full_name, " +
                    "p.account_status as p_account_status, p.user_role as p_user_role, p.created_at as p_created_at " +
                    "FROM CAREGIVER_PATIENT cp " +
                    "JOIN USER p ON cp.patient_id = p.user_id " +
                    "WHERE cp.caregiver_id = ? AND cp.status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, caregiverId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CaregiverPatient cp = extractCaregiverPatient(rs);
                User patient = new User(
                    rs.getInt("p_user_id"),
                    rs.getString("p_health_id"),
                    rs.getString("p_full_name"),
                    rs.getString("p_account_status"),
                    rs.getString("p_user_role"),
                    rs.getTimestamp("p_created_at")
                );
                cp.setPatient(patient);
                patients.add(cp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
    
    /**
     * 获取患者的所有照顾者
     */
    public List<CaregiverPatient> getCaregiversByPatient(int patientId) {
        List<CaregiverPatient> caregivers = new ArrayList<>();
        String sql = "SELECT cp.*, " +
                    "c.user_id as c_user_id, c.health_id as c_health_id, c.full_name as c_full_name, " +
                    "c.account_status as c_account_status, c.user_role as c_user_role, c.created_at as c_created_at " +
                    "FROM CAREGIVER_PATIENT cp " +
                    "JOIN USER c ON cp.caregiver_id = c.user_id " +
                    "WHERE cp.patient_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CaregiverPatient cp = extractCaregiverPatient(rs);
                User caregiver = new User(
                    rs.getInt("c_user_id"),
                    rs.getString("c_health_id"),
                    rs.getString("c_full_name"),
                    rs.getString("c_account_status"),
                    rs.getString("c_user_role"),
                    rs.getTimestamp("c_created_at")
                );
                cp.setCaregiver(caregiver);
                caregivers.add(cp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return caregivers;
    }
    
    /**
     * 获取待处理的照顾请求（患者视角）
     */
    public List<CaregiverPatient> getPendingRequestsForPatient(int patientId) {
        List<CaregiverPatient> requests = new ArrayList<>();
        String sql = "SELECT cp.*, " +
                    "c.user_id as c_user_id, c.health_id as c_health_id, c.full_name as c_full_name, " +
                    "c.account_status as c_account_status, c.user_role as c_user_role, c.created_at as c_created_at " +
                    "FROM CAREGIVER_PATIENT cp " +
                    "JOIN USER c ON cp.caregiver_id = c.user_id " +
                    "WHERE cp.patient_id = ? AND cp.status = 'Pending'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CaregiverPatient cp = extractCaregiverPatient(rs);
                User caregiver = new User(
                    rs.getInt("c_user_id"),
                    rs.getString("c_health_id"),
                    rs.getString("c_full_name"),
                    rs.getString("c_account_status"),
                    rs.getString("c_user_role"),
                    rs.getTimestamp("c_created_at")
                );
                cp.setCaregiver(caregiver);
                requests.add(cp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    /**
     * 创建照顾关系请求
     */
    public boolean createCaregiverRequest(int caregiverId, int patientId, String relationship, String notes) {
        String sql = "INSERT INTO CAREGIVER_PATIENT (caregiver_id, patient_id, relationship, status, notes) " +
                    "VALUES (?, ?, ?, 'Pending', ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, caregiverId);
            pstmt.setInt(2, patientId);
            pstmt.setString(3, relationship);
            pstmt.setString(4, notes);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 患者批准照顾请求
     */
    public boolean approveRequest(int caregiverPatientId, int patientId) {
        String sql = "UPDATE CAREGIVER_PATIENT SET status = 'Active', approved_at = CURRENT_TIMESTAMP " +
                    "WHERE caregiver_patient_id = ? AND patient_id = ? AND status = 'Pending'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, caregiverPatientId);
            pstmt.setInt(2, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 患者拒绝照顾请求
     */
    public boolean rejectRequest(int caregiverPatientId, int patientId) {
        String sql = "UPDATE CAREGIVER_PATIENT SET status = 'Inactive' " +
                    "WHERE caregiver_patient_id = ? AND patient_id = ? AND status = 'Pending'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, caregiverPatientId);
            pstmt.setInt(2, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 终止照顾关系
     */
    public boolean terminateRelationship(int caregiverPatientId, int userId) {
        String sql = "UPDATE CAREGIVER_PATIENT SET status = 'Inactive' " +
                    "WHERE caregiver_patient_id = ? AND (caregiver_id = ? OR patient_id = ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, caregiverPatientId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 检查照顾关系是否存在
     */
    public boolean hasActiveRelationship(int caregiverId, int patientId) {
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
    
    private CaregiverPatient extractCaregiverPatient(ResultSet rs) throws SQLException {
        CaregiverPatient cp = new CaregiverPatient();
        cp.setCaregiverPatientId(rs.getInt("caregiver_patient_id"));
        cp.setCaregiverId(rs.getInt("caregiver_id"));
        cp.setPatientId(rs.getInt("patient_id"));
        cp.setRelationship(rs.getString("relationship"));
        cp.setStatus(rs.getString("status"));
        cp.setLinkedAt(rs.getTimestamp("linked_at"));
        cp.setApprovedAt(rs.getTimestamp("approved_at"));
        cp.setNotes(rs.getString("notes"));
        return cp;
    }
}


