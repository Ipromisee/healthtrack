package com.healthtrack.model;

import java.sql.Timestamp;

public class Provider {
    private int providerId;
    private String licenseNo;
    private String providerName;
    private boolean isVerified;
    private Timestamp verifiedAt;
    private Timestamp createdAt;

    public Provider() {
    }

    public Provider(int providerId, String licenseNo, String providerName, boolean isVerified, Timestamp verifiedAt, Timestamp createdAt) {
        this.providerId = providerId;
        this.licenseNo = licenseNo;
        this.providerName = providerName;
        this.isVerified = isVerified;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
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

