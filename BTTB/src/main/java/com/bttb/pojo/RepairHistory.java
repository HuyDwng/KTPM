package com.bttb.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepairHistory {

    private int id;
    private int deviceId;
    private String technician;
    private LocalDateTime repairDate;
    private LocalDateTime completionDate;
    private String status;
    private int deviceTypeId;
    private List<String> repairIssue; // Danh sách các lỗi
    private double cost;

    
    public RepairHistory(int id, int deviceId, String technician, List<String> repairIssue, LocalDateTime repairDate, LocalDateTime completionDate, String status, double cost) {
        this.id = id;
        this.deviceId = deviceId;
        this.technician = technician;
        this.repairIssue = repairIssue;
        this.repairDate = repairDate;
        this.completionDate = completionDate;
        this.status = status;
        this.cost = cost;
        
    }

    // Getter và Setter cho các trường
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public LocalDateTime getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(LocalDateTime repairDate) {
        this.repairDate = repairDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getRepairIssue() {
        return repairIssue;
    }

    public void setRepairIssue(List<String> repairIssue) {
        this.repairIssue = repairIssue;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(int deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }
}
