package com.bttb.services;

import com.bttb.pojo.JdbcUtils;
import com.bttb.pojo.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServices {
    
    
    public List<String> getTechnicians() throws SQLException {
    List<String> technicians = new ArrayList<>();
    try (Connection conn = JdbcUtils.getConn()) {
        // Cập nhật truy vấn để lấy tên kỹ thuật viên thay vì chỉ lấy id
        String sql = "SELECT id FROM user WHERE role = 'technician'";
        PreparedStatement stm = conn.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        
        while (rs.next()) {
            // Cộng thêm tên kỹ thuật viên vào danh sách
            String technician = rs.getString("id"); // Giả sử 'name' là tên của kỹ thuật viên
            technicians.add(technician);
        }
    }
    return technicians;
}

}
