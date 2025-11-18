package com.healthtrack.model;

import java.sql.Timestamp;

public class MonthlySummary {
    private int monthlySummaryId;
    private int userId;
    private int year;
    private int month;
    private int totalSteps;
    private int totalAppointments;
    private Timestamp lastUpdated;
    private boolean isFinalized;
    private User user;

    public MonthlySummary() {
    }

    public MonthlySummary(int monthlySummaryId, int userId, int year, int month, int totalSteps, int totalAppointments, Timestamp lastUpdated, boolean isFinalized) {
        this.monthlySummaryId = monthlySummaryId;
        this.userId = userId;
        this.year = year;
        this.month = month;
        this.totalSteps = totalSteps;
        this.totalAppointments = totalAppointments;
        this.lastUpdated = lastUpdated;
        this.isFinalized = isFinalized;
    }

    // Getters and Setters
    public int getMonthlySummaryId() {
        return monthlySummaryId;
    }

    public void setMonthlySummaryId(int monthlySummaryId) {
        this.monthlySummaryId = monthlySummaryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

