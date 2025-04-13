package com.bttb.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepairHistory {

    private int id;
    private int deviceId;
    private String deviceName;
    private int technicianId;
    private String technicianName;
    private LocalDateTime repairDate;
    private LocalDateTime completionDate;
    private String status;
    private int deviceTypeId;
    private List<String> repairIssue; // Danh sách các lỗi
    private double cost;

    public RepairHistory(int id, int deviceId, int technicianId, List<String> repairIssue, LocalDateTime repairDate, LocalDateTime completionDate, String status, double cost) {
        this.id = id;
        this.deviceId = deviceId;
        this.technicianId = technicianId;
        this.repairIssue = repairIssue;
        this.repairDate = repairDate;
        this.completionDate = completionDate;
        this.status = status;
        this.cost = cost;
    }

    public RepairHistory(int id, int deviceId, String deviceName, int technicianId, String technicianName, List<String> repairIssue, LocalDateTime repairDate, LocalDateTime completionDate, String status, double cost) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.technicianId = technicianId;
        this.technicianName = technicianName;
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

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
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

    /**
     * @return the deviceName
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * @param deviceName the deviceName to set
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * @return the technicianId
     */
    public int getTechnicianId() {
        return technicianId;
    }

    /**
     * @param technicianId the technicianId to set
     */
    public void setTechnicianId(int technicianId) {
        this.technicianId = technicianId;
    }
}
