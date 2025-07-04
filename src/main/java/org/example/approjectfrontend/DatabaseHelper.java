package org.example.approjectfrontend;

import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";
    public static String getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // این متد جدول را (اگر وجود نداشت) می‌سازد
    public static void initialize() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "email TEXT," +
                    "phone TEXT UNIQUE NOT NULL,"+
                    "role TEXT NOT NULL" +
                    ");");
            stmt.execute("CREATE UNIQUE INDEX IF NOT EXISTS unique_email_idx ON users(email) WHERE email IS NOT NULL;");
        }
    }

    // متد ثبت نام کاربر - ایمیل هم گرفته می‌شود
    public static boolean registerUser(String username, String password, String email, String phone,String role)  {
        String sql = "INSERT INTO users(username, password, email, phone, role) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            if (email == null || email.trim().isEmpty()) {
                pstmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(3, email.trim());
            }
            pstmt.setString(4, phone);
            pstmt.setString(5, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // خطا، تکراری
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
            e.printStackTrace();
            return false;
        }
    }
}
