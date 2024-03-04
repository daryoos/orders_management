package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    public static Connection init() {
        String url = "jdbc:mysql://localhost:3306/order_management";
        String password = "root@123";

        try {
            return DriverManager.getConnection(url, "root", password);
        } catch (SQLException e) {
            System.out.println("\nLog Error db connection " + e);
            return null;
        }
    }
}
