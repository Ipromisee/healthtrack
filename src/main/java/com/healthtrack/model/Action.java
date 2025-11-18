package com.healthtrack.model;

import java.sql.Timestamp;

public class Action {
    private int actionId;
    private String actionType;
    private int createdBy;
    private Timestamp createdAt;

    public Action() {
    }

    public Action(int actionId, String actionType, int createdBy, Timestamp createdAt) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

