/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.pojo;

/**
 *
 * @author nhanh
 */
public class Device {

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private int id;
    private String name;
    private String status;

    public Device() {
    }

    public Device(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

//    public Device(int id, String name) {
//        this.id = id;
//        this.name = name;
//    }

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

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

}
