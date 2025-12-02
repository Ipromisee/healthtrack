package com.healthtrack.dao;

import com.healthtrack.model.Challenge;
import com.healthtrack.model.ChallengeParticipant;
import com.healthtrack.model.User;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengeDAO {
    
    /**
     * 创建健康挑战（只有医疗提供者可以创建）
     */
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
    
    /**
     * 获取用户创建的挑战
     */
    public List<Challenge> getChallengesByUserId(int userId) {
        List<Challenge> challenges = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as creator_name FROM CHALLENGE c " +
                     "JOIN ACTION a ON c.action_id = a.action_id " +
                     "JOIN USER u ON a.created_by = u.user_id " +
                     "WHERE a.created_by = ? ORDER BY c.start_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Challenge challenge = new Challenge(
                    rs.getInt("action_id"),
                    rs.getString("goal"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
                challenge.setCreatorName(rs.getString("creator_name"));
                challenges.add(challenge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return challenges;
    }
    
    /**
     * 获取用户被邀请参与的挑战
     */
    public List<ChallengeParticipant> getInvitedChallenges(int userId) {
        List<ChallengeParticipant> invitations = new ArrayList<>();
        String sql = "SELECT cp.*, c.goal, c.start_date, c.end_date, c.status as challenge_status, " +
                     "u.full_name as creator_name, u.user_id as creator_id " +
                     "FROM CHALLENGE_PARTICIPANT cp " +
                     "JOIN CHALLENGE c ON cp.action_id = c.action_id " +
                     "JOIN ACTION a ON c.action_id = a.action_id " +
                     "JOIN USER u ON a.created_by = u.user_id " +
                     "WHERE cp.user_id = ? " +
                     "ORDER BY cp.updated_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ChallengeParticipant cp = new ChallengeParticipant(
                    rs.getInt("challenge_participant_id"),
                    rs.getInt("action_id"),
                    rs.getInt("user_id"),
                    rs.getBigDecimal("progress_value"),
                    rs.getString("progress_unit"),
                    rs.getTimestamp("updated_at"),
                    rs.getString("participant_status")
                );
                
                // 设置挑战信息
                Challenge challenge = new Challenge(
                    rs.getInt("action_id"),
                    rs.getString("goal"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("challenge_status")
                );
                challenge.setCreatorName(rs.getString("creator_name"));
                cp.setChallenge(challenge);
                
                invitations.add(cp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invitations;
    }
    
    /**
     * 获取所有活跃挑战（用于管理员查看）
     */
    public List<Challenge> getAllActiveChallenges() {
        List<Challenge> challenges = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as creator_name " +
                     "FROM CHALLENGE c " +
                     "JOIN ACTION a ON c.action_id = a.action_id " +
                     "JOIN USER u ON a.created_by = u.user_id " +
                     "WHERE c.status IN ('Draft', 'Active') " +
                     "ORDER BY c.start_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Challenge challenge = new Challenge(
                    rs.getInt("action_id"),
                    rs.getString("goal"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
                challenge.setCreatorName(rs.getString("creator_name"));
                challenges.add(challenge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return challenges;
    }
    
    /**
     * 邀请患者参与挑战
     */
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
    
    /**
     * 患者接受挑战邀请
     */
    public boolean acceptInvitation(int challengeParticipantId, int userId) {
        String sql = "UPDATE CHALLENGE_PARTICIPANT SET participant_status = 'Joined', updated_at = CURRENT_TIMESTAMP " +
                     "WHERE challenge_participant_id = ? AND user_id = ? AND participant_status = 'Invited'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, challengeParticipantId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 患者拒绝挑战邀请
     */
    public boolean declineInvitation(int challengeParticipantId, int userId) {
        String sql = "UPDATE CHALLENGE_PARTICIPANT SET participant_status = 'Declined', updated_at = CURRENT_TIMESTAMP " +
                     "WHERE challenge_participant_id = ? AND user_id = ? AND participant_status = 'Invited'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, challengeParticipantId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 更新参与者进度
     */
    public boolean updateProgress(int challengeParticipantId, int userId, double progressValue, String progressUnit) {
        String sql = "UPDATE CHALLENGE_PARTICIPANT SET progress_value = ?, progress_unit = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE challenge_participant_id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, progressValue);
            pstmt.setString(2, progressUnit);
            pstmt.setInt(3, challengeParticipantId);
            pstmt.setInt(4, userId);
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
                // 创建关联用户
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setHealthId(rs.getString("health_id"));
                participant.setUser(user);
                participants.add(participant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }
    
    public List<Challenge> getChallengesWithMostParticipants(int limit) {
        List<Challenge> challenges = new ArrayList<>();
        String sql = "SELECT c.*, COUNT(cp.user_id) as participant_count, u.full_name as creator_name " +
                     "FROM CHALLENGE c " +
                     "JOIN ACTION a ON c.action_id = a.action_id " +
                     "JOIN USER u ON a.created_by = u.user_id " +
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
                Challenge challenge = new Challenge(
                    rs.getInt("action_id"),
                    rs.getString("goal"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("status")
                );
                challenge.setCreatorName(rs.getString("creator_name"));
                challenge.setParticipantCount(rs.getInt("participant_count"));
                challenges.add(challenge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return challenges;
    }
}
