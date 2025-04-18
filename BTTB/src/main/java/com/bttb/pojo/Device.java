/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.pojo;

public class Device {
    private int id;
    private String name;
    private String status;
    private int deviceTypeId;
    private String deviceTypeName; 

    public Device(int id, String name, String status, String deviceTypeName) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.deviceTypeName = deviceTypeName;
    }

    
    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public Device(String name, String status, int deviceTypeId) {
        this.name = name;
        this.status = status;
        this.deviceTypeId = deviceTypeId;
    }
    
    public Device() {
    }

    public Device(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
    
    public Device(String name, String status) {
        this.name = name;
        this.status = status;
    }
    
//     public Device(int id, String name, String status, int deviceTypeId) {
//        this.id = id;
//        this.name = name;
//        this.status = status;
//        this.deviceTypeId = deviceTypeId;
//    }
  
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
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * @return the deviceTypeId
     */
    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    /**
     * @param deviceTypeId the deviceTypeId to set
     */
    public void setDeviceTypeId(int deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }
}   