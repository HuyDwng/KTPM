/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.pojo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author LEGION
 */
public class MaintenanceSchedule {

    private int id;
    private String deviceName;
    private String executorName;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private String frequency;
    private LocalDate nextMaintenanceDate;
    private LocalDate completedDate;
    private LocalDate createdAt;

    public MaintenanceSchedule(int id, String deviceName, String executorName,
            LocalDate scheduledDate, LocalTime scheduledTime,
            String frequency, LocalDate nextMaintenanceDate, LocalDate createdAt, LocalDate completedDate) {
        this.id = id;
        this.deviceName = deviceName;
        this.executorName = executorName;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.frequency = frequency;
        this.nextMaintenanceDate = nextMaintenanceDate;
        this.createdAt = createdAt;
        this.completedDate = completedDate; // Khởi tạo ngày hoàn thành
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public MaintenanceSchedule(int id, String deviceName, String executorName,
            LocalDate scheduledDate, LocalTime scheduledTime,
            String frequency, LocalDate nextMaintenanceDate, LocalDate createdAt) {
        this.id = id;
        this.deviceName = deviceName;
        this.executorName = executorName;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.frequency = frequency;
        this.nextMaintenanceDate = nextMaintenanceDate;
        this.createdAt = createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getFrequency() {
        return frequency;
    }

    public void calculateNextMaintenanceDate() {
        if (completedDate != null) {
            if ("Hàng tuần".equalsIgnoreCase(frequency)) {
                this.nextMaintenanceDate = completedDate.plusWeeks(1);
            } else if ("Hàng tháng".equalsIgnoreCase(frequency)) {
                this.nextMaintenanceDate = completedDate.plusMonths(1);
            } 
            
        }
    }
}
