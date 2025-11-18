package com.healthtrack.model;

import java.sql.Timestamp;

public class Appointment {
    private int actionId;
    private int providerId;
    private Timestamp scheduledAt;
    private String consultationType;
    private String memo;
    private String status;
    private String cancelReason;
    private Timestamp cancelTime;
    private Provider provider;
    private User user;

    public Appointment() {
    }

    public Appointment(int actionId, int providerId, Timestamp scheduledAt, String consultationType, String memo, String status, String cancelReason, Timestamp cancelTime) {
        this.actionId = actionId;
        this.providerId = providerId;
        this.scheduledAt = scheduledAt;
        this.consultationType = consultationType;
        this.memo = memo;
        this.status = status;
        this.cancelReason = cancelReason;
        this.cancelTime = cancelTime;
    }

    // Getters and Setters
    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public Timestamp getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Timestamp scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public void setConsultationType(String consultationType) {
        this.consultationType = consultationType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Timestamp getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Timestamp cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

