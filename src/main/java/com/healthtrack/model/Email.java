package com.healthtrack.model;

import java.sql.Timestamp;

public class Email {
    private String email;
    private int userId;
    private boolean isVerified;
    private Timestamp verifiedAt;
    private Timestamp createdAt;

    public Email() {
    }

    public Email(String email, int userId, boolean isVerified, Timestamp verifiedAt, Timestamp createdAt) {
        this.email = email;
        this.userId = userId;
        this.isVerified = isVerified;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Timestamp getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Timestamp verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

