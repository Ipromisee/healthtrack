package com.healthtrack.dao;

import com.healthtrack.model.Provider;
import com.healthtrack.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProviderDAO {
    
    public List<Provider> getAllProviders() {
        List<Provider> providers = new ArrayList<>();
        String sql = "SELECT * FROM PROVIDER ORDER BY provider_name";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                providers.add(new Provider(
                    rs.getInt("provider_id"),
                    rs.getString("license_no"),
                    rs.getString("provider_name"),
                    rs.getBoolean("is_verified"),
                    rs.getTimestamp("verified_at"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return providers;
    }
    
    public Provider getProviderById(int providerId) {
        Provider provider = null;
        String sql = "SELECT * FROM PROVIDER WHERE provider_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, providerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                provider = new Provider(
                    rs.getInt("provider_id"),
                    rs.getString("license_no"),
                    rs.getString("provider_name"),
                    rs.getBoolean("is_verified"),
                    rs.getTimestamp("verified_at"),
                    rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return provider;
    }
    
    public Provider getProviderByLicenseNo(String licenseNo) {
        Provider provider = null;
        String sql = "SELECT * FROM PROVIDER WHERE license_no = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseNo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                provider = new Provider(
                    rs.getInt("provider_id"),
                    rs.getString("license_no"),
                    rs.getString("provider_name"),
                    rs.getBoolean("is_verified"),
                    rs.getTimestamp("verified_at"),
                    rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return provider;
    }
    
    public List<Provider> searchProviders(String keyword) {
        List<Provider> providers = new ArrayList<>();
        String sql = "SELECT * FROM PROVIDER WHERE provider_name LIKE ? OR license_no LIKE ? ORDER BY provider_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                providers.add(new Provider(
                    rs.getInt("provider_id"),
                    rs.getString("license_no"),
                    rs.getString("provider_name"),
                    rs.getBoolean("is_verified"),
                    rs.getTimestamp("verified_at"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return providers;
    }
    
    /**
     * 验证医疗提供者
     */
    public boolean verifyProvider(int providerId) {
        String sql = "UPDATE PROVIDER SET is_verified = TRUE, verified_at = CURRENT_TIMESTAMP WHERE provider_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, providerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

