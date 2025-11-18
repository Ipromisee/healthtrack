package com.healthtrack.dao;

import com.healthtrack.model.*;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT * FROM USER WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new User(
                    rs.getInt("user_id"),
                    rs.getString("health_id"),
                    rs.getString("full_name"),
                    rs.getString("account_status"),
                    rs.getTimestamp("created_at")
                );
                user.setEmails(getEmailsByUserId(userId));
                user.setPhone(getPhoneByUserId(userId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    public User getUserByHealthId(String healthId) {
        User user = null;
        String sql = "SELECT * FROM USER WHERE health_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, healthId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                user = new User(
                    userId,
                    rs.getString("health_id"),
                    rs.getString("full_name"),
                    rs.getString("account_status"),
                    rs.getTimestamp("created_at")
                );
                user.setEmails(getEmailsByUserId(userId));
                user.setPhone(getPhoneByUserId(userId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE USER SET full_name = ?, account_status = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getAccountStatus());
            pstmt.setInt(3, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Email> getEmailsByUserId(int userId) {
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT * FROM EMAIL WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                emails.add(new Email(
                    rs.getString("email"),
                    rs.getInt("user_id"),
                    rs.getBoolean("is_verified"),
                    rs.getTimestamp("verified_at"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }
    
    public boolean addEmail(Email email) {
        String sql = "INSERT INTO EMAIL (email, user_id, is_verified, verified_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.getEmail().toLowerCase());
            pstmt.setInt(2, email.getUserId());
            pstmt.setBoolean(3, email.isVerified());
            pstmt.setTimestamp(4, email.getVerifiedAt());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteEmail(String email) {
        String sql = "DELETE FROM EMAIL WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email.toLowerCase());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Phone getPhoneByUserId(int userId) {
        Phone phone = null;
        String sql = "SELECT * FROM PHONE WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                phone = new Phone(
                    rs.getString("phone_number"),
                    rs.getInt("user_id"),
                    rs.getBoolean("is_verified"),
                    rs.getTimestamp("verified_at"),
                    rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phone;
    }
    
    public boolean addPhone(Phone phone) {
        String sql = "INSERT INTO PHONE (phone_number, user_id, is_verified, verified_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone.getPhoneNumber());
            pstmt.setInt(2, phone.getUserId());
            pstmt.setBoolean(3, phone.isVerified());
            pstmt.setTimestamp(4, phone.getVerifiedAt());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePhone(String phoneNumber) {
        String sql = "DELETE FROM PHONE WHERE phone_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phoneNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<UserProvider> getUserProviders(int userId) {
        List<UserProvider> providers = new ArrayList<>();
        String sql = "SELECT up.*, p.provider_name, p.license_no FROM USER_PROVIDER up " +
                     "JOIN PROVIDER p ON up.provider_id = p.provider_id " +
                     "WHERE up.user_id = ? AND up.link_status = 'Active'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                UserProvider up = new UserProvider(
                    rs.getInt("user_provider_id"),
                    rs.getInt("user_id"),
                    rs.getInt("provider_id"),
                    rs.getBoolean("is_primary"),
                    rs.getString("link_status"),
                    rs.getTimestamp("linked_at"),
                    rs.getTimestamp("unlinked_at")
                );
                
                Provider provider = new Provider();
                provider.setProviderId(rs.getInt("provider_id"));
                provider.setProviderName(rs.getString("provider_name"));
                provider.setLicenseNo(rs.getString("license_no"));
                up.setProvider(provider);
                
                providers.add(up);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return providers;
    }
    
    public boolean addUserProvider(UserProvider userProvider) {
        String sql = "INSERT INTO USER_PROVIDER (user_id, provider_id, is_primary, link_status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userProvider.getUserId());
            pstmt.setInt(2, userProvider.getProviderId());
            pstmt.setBoolean(3, userProvider.isPrimary());
            pstmt.setString(4, userProvider.getLinkStatus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean removeUserProvider(int userProviderId) {
        String sql = "UPDATE USER_PROVIDER SET link_status = 'Inactive', unlinked_at = NOW() WHERE user_provider_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userProviderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

