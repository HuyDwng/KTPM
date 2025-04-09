/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.pojo;

public class RepairIssue {
    private int id;
    private String name;
    private int deviceTypeId;
    private double cost;

    public RepairIssue() {
    }

    public RepairIssue(int id, String name, int deviceTypeId, double cost) {
        this.id = id;
        this.name = name;
        this.deviceTypeId = deviceTypeId;
        this.cost = cost;
    }

    public RepairIssue(String name, int deviceTypeId, double cost) {
        this.name = name;
        this.deviceTypeId = deviceTypeId;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(int deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
}
