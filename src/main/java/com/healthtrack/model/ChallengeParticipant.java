package com.healthtrack.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ChallengeParticipant {
    private int challengeParticipantId;
    private int actionId;
    private int userId;
    private BigDecimal progressValue;
    private String progressUnit;
    private Timestamp updatedAt;
    private String participantStatus;
    private User user;
    private Challenge challenge;

    public ChallengeParticipant() {
    }

    public ChallengeParticipant(int challengeParticipantId, int actionId, int userId, BigDecimal progressValue, String progressUnit, Timestamp updatedAt, String participantStatus) {
        this.challengeParticipantId = challengeParticipantId;
        this.actionId = actionId;
        this.userId = userId;
        this.progressValue = progressValue;
        this.progressUnit = progressUnit;
        this.updatedAt = updatedAt;
        this.participantStatus = participantStatus;
    }

    // Getters and Setters
    public int getChallengeParticipantId() {
        return challengeParticipantId;
    }

    public void setChallengeParticipantId(int challengeParticipantId) {
        this.challengeParticipantId = challengeParticipantId;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(BigDecimal progressValue) {
        this.progressValue = progressValue;
    }

    public String getProgressUnit() {
        return progressUnit;
    }

    public void setProgressUnit(String progressUnit) {
        this.progressUnit = progressUnit;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(String participantStatus) {
        this.participantStatus = participantStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}
