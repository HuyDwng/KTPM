/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.services;

import com.bttb.pojo.DeviceType;
import com.bttb.pojo.JdbcUtils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DeviceTypeService {

    public List<DeviceType> getAllDeviceTypes() {
        List<DeviceType> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM device_type";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                DeviceType dt = new DeviceType(rs.getInt("id"), rs.getString("name"));
                list.add(dt);
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // hoặc log lỗi
        }

        return list;
    }
}

