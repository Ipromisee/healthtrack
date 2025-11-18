package com.healthtrack.model;

import java.sql.Timestamp;

public class UserProvider {
    private int userProviderId;
    private int userId;
    private int providerId;
    private boolean isPrimary;
    private String linkStatus;
    private Timestamp linkedAt;
    private Timestamp unlinkedAt;
    private Provider provider;

    public UserProvider() {
    }

    public UserProvider(int userProviderId, int userId, int providerId, boolean isPrimary, String linkStatus, Timestamp linkedAt, Timestamp unlinkedAt) {
        this.userProviderId = userProviderId;
        this.userId = userId;
        this.providerId = providerId;
        this.isPrimary = isPrimary;
        this.linkStatus = linkStatus;
        this.linkedAt = linkedAt;
        this.unlinkedAt = unlinkedAt;
    }

    // Getters and Setters
    public int getUserProviderId() {
        return userProviderId;
    }

    public void setUserProviderId(int userProviderId) {
        this.userProviderId = userProviderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public String getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(String linkStatus) {
        this.linkStatus = linkStatus;
    }

    public Timestamp getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(Timestamp linkedAt) {
        this.linkedAt = linkedAt;
    }

    public Timestamp getUnlinkedAt() {
        return unlinkedAt;
    }

    public void setUnlinkedAt(Timestamp unlinkedAt) {
        this.unlinkedAt = unlinkedAt;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}

