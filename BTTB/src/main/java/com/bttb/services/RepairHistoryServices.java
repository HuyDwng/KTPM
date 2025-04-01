package com.bttb.services;

import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.RepairHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RepairHistoryServices {
    public List<RepairHistory> getRepairHistories() throws SQLException {
        List<RepairHistory> histories = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM repair_history";
            PreparedStatement stm = conn.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                // Kiểm tra null trước khi gọi toLocalDateTime
                Timestamp repairDate = rs.getTimestamp("repair_date");
                Timestamp completionDate = rs.getTimestamp("completion_date");

                RepairHistory rh = new RepairHistory(
                    rs.getInt("id"),
                    rs.getInt("device_id"),
                    rs.getString("technician"),
                    repairDate != null ? repairDate.toLocalDateTime() : null,
                    completionDate != null ? completionDate.toLocalDateTime() : null,
                    rs.getString("status"),
                    
                    rs.getDouble("cost")
                );
                histories.add(rh);
            }
        }
        return histories;
    }
}
