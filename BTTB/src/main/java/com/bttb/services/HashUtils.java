/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bttb.services;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtils {

    // Mã hóa mật khẩu bằng BCrypt
    public static String hashPassword(String password) {
        // Mã hóa mật khẩu với độ khó (work factor) là 12
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    
    // Kiểm tra mật khẩu đầu vào với mật khẩu đã mã hóa
   public static boolean checkPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            // In ra thông báo lỗi nếu salt phiên bản không hợp lệ
            System.out.println("Invalid salt version");
            return false;
        }
    }
}
