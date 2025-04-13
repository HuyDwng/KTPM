/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.pojo;


public class Device {
    private int id;
    private String name;
    private String status;
    private int device_type_id;
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
    public Device(int id, String name, String status, int device_type_id) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.device_type_id = device_type_id;
    }
    
    
    public int getDevice_type_id() {
        return device_type_id;
    }

    public Device(String name, String status, int device_type_id) {
        this.name = name;
        this.status = status;
        this.device_type_id = device_type_id;
    }

    public void setDevice_type_id(int device_type_id) {
        this.device_type_id = device_type_id;
    }
    public Device() {
    }

    public Device(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Device(String name, String status) {
        this.name = name;
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " - " + name;  // Hiển thị dạng "1 - Laptop"
    }
  
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
}
