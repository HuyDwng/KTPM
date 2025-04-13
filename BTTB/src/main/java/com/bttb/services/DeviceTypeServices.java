/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.services;

import com.bttb.pojo.DeviceType;
import com.bttb.pojo.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeviceTypeServices {

    public List<DeviceType> getDeviceTypes() throws SQLException {
        List<DeviceType> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM device_type";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                DeviceType dt = new DeviceType(rs.getInt("id"), rs.getString("name"));
                list.add(dt);
            }
        }

        return list;
    }



    public DeviceType getDeviceTypeById(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM device_type WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return new DeviceType(rs.getInt("id"), rs.getString("name"));
            }
        }

        return null;
    }
}

