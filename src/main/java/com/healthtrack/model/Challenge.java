package com.healthtrack.model;

import java.sql.Date;
import java.util.List;

public class Challenge {
    private int actionId;
    private String goal;
    private Date startDate;
    private Date endDate;
    private String status;
    private List<ChallengeParticipant> participants;

    public Challenge() {
    }

    public Challenge(int actionId, String goal, Date startDate, Date endDate, String status) {
        this.actionId = actionId;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and Setters
    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ChallengeParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ChallengeParticipant> participants) {
        this.participants = participants;
    }
}

