package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost/pos_java", 
                    "root", 
                    ""
                );
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return conn;
    }
    
    public static boolean isDatabaseOnline() throws SQLException {
        try (Connection testConn = DriverManager.getConnection(
                "jdbc:mysql://localhost/pos_java",
                "root",
                "")) {
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) {
                throw e;
            }
            return false;
        }
    }
}
