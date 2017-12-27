package application;

import java.sql.*;

/**
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
public class SqliteController {

    public static Connection Connector(String s) {
        try {
            Class<?> forName;
            forName = Class.forName("org.sqlite.JDBC");
            Connection conn;
            conn = DriverManager.getConnection(s);
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
            return null;
            //TODO:handle Expression
        }
    }

}
