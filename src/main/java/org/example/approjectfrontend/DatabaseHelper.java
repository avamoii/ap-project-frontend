package org.example.approjectfrontend;

import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";

    // این متد جدول را (اگر وجود نداشت) می‌سازد
    public static void initialize() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "email TEXT UNIQUE NOT NULL" +
                    ");");
        }
    }

    // متد ثبت نام کاربر - ایمیل هم گرفته می‌شود
    public static boolean registerUser(String username, String password, String email) {
        String sql = "INSERT INTO users(username, password, email) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // خطا، مثلاً یوزرنیم یا ایمیل تکراری
        }
    }

    // بررسی ورود کاربر
    public static boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}
