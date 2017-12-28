/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Aravind Sharma
 */
public class Crawler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        Connection conection;
        conection = SqliteController.Connector();
        int resultSet;

        if (conection == null) {
            System.exit(1);
        }

        String[] pages = {"a1", "a2", "b1", "b2", "c1", "c2", "d1", "d2", "e", "f", "g", "h", "i", "j", "k", "l", "m1",
            "m2", "n1", "n2", "o", "p1", "p2", "q", "r1", "r2", "s1", "s2", "t1", "t2", "u", "v", "w", "x", "y", "z"};
        for (String p : pages) {
            File input = new File("D:\\AASS\\wiktionary\\" + p + ".html");
            System.out.println(p);
            Document doc = Jsoup.parse(input, "utf-8");
            Elements links = doc.getElementsByTag("ol");
            for (Element link : links) {
                //  System.out.println("\nlink : " + link.attr("href")); 
                for (Element e : link.children()) {

                    String[] words = e.child(0).text().split(" +");

                    for (String t : words) {
                        //  System.out.println(t);

                        try {
                            //   System.out.println(e.child(0).text());\
                            PreparedStatement preparedStatement = null;

                            String query = "INSERT INTO LIST VALUES (?,?);";

                            preparedStatement = conection.prepareStatement(query);

                            preparedStatement.setString(2, t);

                            resultSet = preparedStatement.executeUpdate();
                            //     System.out.println("\n successful insertion" + path.toString());
                            preparedStatement.close();
                        } catch (Exception ex) {
        //                    Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

            }

        }

        try {
            conection.close();
        } catch (SQLException ex) {
//            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
