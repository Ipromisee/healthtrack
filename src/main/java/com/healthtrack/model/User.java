package com.healthtrack.model;

import java.sql.Timestamp;
import java.util.List;

public class User {
    private int userId;
    private String healthId;
    private String fullName;
    private String accountStatus;
    private String userRole;
    private Timestamp createdAt;
    private List<Email> emails;
    private Phone phone;
    private List<UserProvider> providers;

    public User() {
    }

    public User(int userId, String healthId, String fullName, String accountStatus, String userRole, Timestamp createdAt) {
        this.userId = userId;
        this.healthId = healthId;
        this.fullName = fullName;
        this.accountStatus = accountStatus;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public List<UserProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<UserProvider> providers) {
        this.providers = providers;
    }
}

