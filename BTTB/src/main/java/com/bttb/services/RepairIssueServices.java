/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.services;

import com.bttb.pojo.RepairIssue;
import com.bttb.pojo.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairIssueServices {

    public List<RepairIssue> getRepairIssues() throws SQLException {
        List<RepairIssue> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM repair_issue";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                RepairIssue ri = new RepairIssue(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("device_type_id"),
                        rs.getDouble("cost")
                );
                list.add(ri);
            }
        }

        return list;
    }

    public List<RepairIssue> getRepairIssuesByDeviceType(int deviceTypeId) throws SQLException {
        List<RepairIssue> list = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM repair_issue WHERE device_type_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, deviceTypeId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                RepairIssue ri = new RepairIssue(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("device_type_id"),
                        rs.getDouble("cost")
                );
                list.add(ri);
            }
        }

        return list;
    }
}
