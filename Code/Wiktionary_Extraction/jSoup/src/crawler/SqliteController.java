
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.sql.*;

/**
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
public class SqliteController {

    public static Connection Connector() {
        try {
            Class<?> forName;
            forName = Class.forName("org.sqlite.JDBC");
            Connection conn;
            conn = DriverManager.getConnection("jdbc:sqlite:D:\\AASS\\wiktionary\\wiktionary.sqlite");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
 //           System.err.println(e);
            return null;
            //TODO:handle Expression
        }
    }

}
