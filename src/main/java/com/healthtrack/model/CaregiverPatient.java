package com.healthtrack.model;

import java.sql.Timestamp;

/**
 * 照顾者-患者关系模型
 * 用于表示照顾者与被照顾患者之间的关联关系
 */
public class CaregiverPatient {
    private int caregiverPatientId;
    private int caregiverId;
    private int patientId;
    private String relationship;
    private String status; // Active, Inactive, Pending
    private Timestamp linkedAt;
    private Timestamp approvedAt;
    private String notes;
    
    // 关联的用户对象
    private User caregiver;
    private User patient;
    
    public CaregiverPatient() {}
    
    public CaregiverPatient(int caregiverPatientId, int caregiverId, int patientId, 
                           String relationship, String status, Timestamp linkedAt, 
                           Timestamp approvedAt, String notes) {
        this.caregiverPatientId = caregiverPatientId;
        this.caregiverId = caregiverId;
        this.patientId = patientId;
        this.relationship = relationship;
        this.status = status;
        this.linkedAt = linkedAt;
        this.approvedAt = approvedAt;
        this.notes = notes;
    }
    
    // Getters and Setters
    public int getCaregiverPatientId() {
        return caregiverPatientId;
    }
    
    public void setCaregiverPatientId(int caregiverPatientId) {
        this.caregiverPatientId = caregiverPatientId;
    }
    
    public int getCaregiverId() {
        return caregiverId;
    }
    
    public void setCaregiverId(int caregiverId) {
        this.caregiverId = caregiverId;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public String getRelationship() {
        return relationship;
    }
    
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getLinkedAt() {
        return linkedAt;
    }
    
    public void setLinkedAt(Timestamp linkedAt) {
        this.linkedAt = linkedAt;
    }
    
    public Timestamp getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public User getCaregiver() {
        return caregiver;
    }
    
    public void setCaregiver(User caregiver) {
        this.caregiver = caregiver;
    }
    
    public User getPatient() {
        return patient;
    }
    
    public void setPatient(User patient) {
        this.patient = patient;
    }
}
