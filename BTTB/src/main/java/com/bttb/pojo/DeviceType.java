/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.pojo;

public class DeviceType {
    private int id;
    private String name;

    public DeviceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public DeviceType() {}

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

    // Hiển thị tên trong ComboBox
    @Override
    public String toString() {
        return name;
    }
}

