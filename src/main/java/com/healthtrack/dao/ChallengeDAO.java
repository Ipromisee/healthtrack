package com.healthtrack.dao;

import com.healthtrack.model.Challenge;
import com.healthtrack.model.ChallengeParticipant;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengeDAO {
    
    public boolean createChallenge(Challenge challenge, int userId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert into ACTION table first
            String actionSql = "INSERT INTO ACTION (action_type, created_by) VALUES ('Challenge', ?)";
            PreparedStatement actionStmt = conn.prepareStatement(actionSql, Statement.RETURN_GENERATED_KEYS);
            actionStmt.setInt(1, userId);
            actionStmt.executeUpdate();
            
            ResultSet rs = actionStmt.getGeneratedKeys();
            int actionId = 0;
            if (rs.next()) {
                actionId = rs.getInt(1);
            }
            actionStmt.close();
            
            // Insert into CHALLENGE table
            String challengeSql = "INSERT INTO CHALLENGE (action_id, goal, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement challengeStmt = conn.prepareStatement(challengeSql);
            challengeStmt.setInt(1, actionId);
            challengeStmt.setString(2, challenge.getGoal());
            challengeStmt.setDate(3, challenge.getStartDate());
            challengeStmt.setDate(4, challenge.getEndDate());
            challengeStmt.setString(5, challenge.getStatus());
            challengeStmt.executeUpdate();
            challengeStmt.close();
            
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
    
    public List<Challenge> getChallengesByUserId(int userId) {
        List<Challenge> challenges = new ArrayList<>();
        String sql = "SELECT c.* FROM CHALLENGE c " +
                     "JOIN ACTION a ON c.action_id = a.action_id " +
                     "WHERE a.created_by = ? ORDER BY c.start_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                challenges.add(new Challenge(
                    rs.getInt("action_id"),
                    rs.getString("goal"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return challenges;
    }
    
    public boolean addParticipant(int actionId, int userId) {
        String sql = "INSERT INTO CHALLENGE_PARTICIPANT (action_id, user_id, participant_status) VALUES (?, ?, 'Invited') " +
                     "ON DUPLICATE KEY UPDATE participant_status = 'Invited'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, actionId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<ChallengeParticipant> getParticipantsByChallengeId(int actionId) {
        List<ChallengeParticipant> participants = new ArrayList<>();
        String sql = "SELECT cp.*, u.full_name, u.health_id FROM CHALLENGE_PARTICIPANT cp " +
                     "JOIN USER u ON cp.user_id = u.user_id " +
                     "WHERE cp.action_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, actionId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ChallengeParticipant participant = new ChallengeParticipant(
                    rs.getInt("challenge_participant_id"),
                    rs.getInt("action_id"),
                    rs.getInt("user_id"),
                    rs.getBigDecimal("progress_value"),
                    rs.getString("progress_unit"),
                    rs.getTimestamp("updated_at"),
                    rs.getString("participant_status")
                );
                participants.add(participant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }
    
    public List<Challenge> getChallengesWithMostParticipants(int limit) {
        List<Challenge> challenges = new ArrayList<>();
        String sql = "SELECT c.*, COUNT(cp.user_id) as participant_count " +
                     "FROM CHALLENGE c " +
                     "LEFT JOIN CHALLENGE_PARTICIPANT cp ON c.action_id = cp.action_id " +
                     "WHERE cp.participant_status IN ('Joined', 'Invited') " +
                     "GROUP BY c.action_id " +
                     "ORDER BY participant_count DESC " +
                     "LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                challenges.add(new Challenge(
                    rs.getInt("action_id"),
                    rs.getString("goal"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return challenges;
    }
}

