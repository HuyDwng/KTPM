package com.bttb.pojo;

import java.time.LocalDateTime;

public class RepairHistory {
    private int id;
    private int deviceId;
    private String technician;
    private LocalDateTime repairDate;
    private LocalDateTime completionDate;
    private String status;
    
    private double cost;

    public RepairHistory(int id, int deviceId, String technician, LocalDateTime repairDate, LocalDateTime completionDate, String status, double cost) {
        this.id = id;
        this.deviceId = deviceId;
        this.technician = technician;
        this.repairDate = repairDate;
        this.completionDate = completionDate;
        this.status = status;
        this.cost = cost;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the deviceId
     */
    public int getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return the repairDate
     */
    public LocalDateTime getRepairDate() {
        return repairDate;
    }

    /**
     * @param repairDate the repairDate to set
     */
    public void setRepairDate(LocalDateTime repairDate) {
        this.repairDate = repairDate;
    }

    /**
     * @return the technician
     */
    public String getTechnician() {
        return technician;
    }

    /**
     * @param technician the technician to set
     */
    public void setTechnician(String technician) {
        this.technician = technician;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * @return the completionDate
     */
    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    /**
     * @param completionDate the completionDate to set
     */
    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
