package org.guercifzone.DataBase;



import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";

    public DatabaseHelper() {
        initializeDatabase();
    }
    // Add these methods to DatabaseHelper.java
    public ResultSet getAllLogs() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM users ORDER BY last_login DESC");
    }
    // Add this method to DatabaseHelper.java
    public void exportToCSV(String filePath) throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT email, ip_address, last_login FROM users");
             java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(filePath))) {

            // Write CSV header
            writer.println("Email,IP Address,Last Login");

            // Write data rows
            while (rs.next()) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\"",
                        rs.getString("email"),
                        rs.getString("ip_address"),
                        rs.getString("last_login")));
            }
        }
    }
    public String[][] getLogsAsArray() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {

            rs.next();
            int rowCount = rs.getInt(1);
            String[][] data = new String[rowCount][4];

            try (ResultSet logsRs = getAllLogs()) {
                int i = 0;
                while (logsRs.next()) {
                    data[i][0] = logsRs.getString("email");
                    data[i][1] = "******"; // Mask password
                    data[i][2] = logsRs.getString("ip_address");
                    data[i][3] = logsRs.getString("last_login");
                    i++;
                }
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0][0];
        }
    }
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "email TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "ip_address TEXT," +
                    "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public boolean addUser(String email, String password, String ipAddress) {
        String sql = "INSERT INTO users(email, password, ip_address) VALUES(?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, ipAddress);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public boolean validateUser(String email, String password) {
        String sql = "SELECT 1 FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            return false;
        }
    }

    public void updateLoginInfo(String email, String ipAddress) {
        String sql = "UPDATE users SET ip_address = ?, last_login = CURRENT_TIMESTAMP WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ipAddress);
            pstmt.setString(2, email);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating login info: " + e.getMessage());
        }
    }
}