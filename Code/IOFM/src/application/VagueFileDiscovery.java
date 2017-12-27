package application;

import static application.CategoriesController.success;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

class VagueFileDiscovery extends Task<Boolean> {

    //vague filenames and paths
    //filenames
    static ObservableList<String> vfileNames = FXCollections.observableArrayList();
    //paths
    static ArrayList<String> valPaths = new ArrayList();

    @Override
    protected Boolean call() {

        vfileNames.clear();
        valPaths.clear();

        //all filenames and paths
        //filenames
        ObservableList<String> fileNames = FXCollections.observableArrayList();
        //paths
        ArrayList<String> alPaths = new ArrayList();

        boolean vague = true;

        Statement stmt = null;
        HashSet<String> dwords = new HashSet<>();
        String[] fwords;

        //Step1: Extract Words From Database
        System.out.println("Step1: Extract Words From Database");
        Connection conn = SqliteController.Connector("jdbc:sqlite:wiktionary.sqlite");

        String sql = "SELECT words FROM list";
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String word = rs.getString("words").toLowerCase();

                Pattern pattern = Pattern.compile("q+|w+|e+|r+|t+|y+|u+|i+|o+|p+|a+|s+|d+"
                        + "|f+|g+|h+|j+|k+|l+|z+|x+|c+|v+|b+|n+|m+|[0-9]");
                Matcher matcher = pattern.matcher(word);

                if (!word.equals("pdf") && !matcher.matches()) {
                    dwords.add(word);
                }

                //add interruption check
                if (isCancelled()) {
                    System.out.println("Thread interrupted, Inside Step1");
                    success = false;
                    return success;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(VagueFileDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Step2: Extract PDF Filenames And PAths
        System.out.println("Step2: Extract PDF Filenames And PAths");
        LinkedHashSet<String> set = DbModel.document;

        //add to items and alPaths
        for (String s : set) {

            String extension = "";
            int i = s.lastIndexOf('.');
            if (i > 0) {
                extension = s.substring(i + 1);
            }

           // ArrayList<String> al = set.get(s);
            if (extension.compareToIgnoreCase("pdf") == 0) {
                //get paths
                ArrayList<String> al = DbModel.returnPaths(s, "document");
                for (String path : al) {
                    fileNames.add(s);
                    alPaths.add(path);
                }
            }
            //add interruption check
            if (isCancelled()) {
                System.out.println("Thread interrupted, Inside Step2");
                success = false;
                return success;
            }
        }

        //Step 3:Obtain Vague pdfs
        System.out.println("Step 3:Obtain Vague pdfs");
        for (int i = 0; i < fileNames.size(); i++) {

            fwords = fileNames.get(i).split("\\s+|(\\p{Punct})+|[0-9]+");

            //    System.out.println("\n\nfwords");
     /*       for (String s : fwords) {
             System.out.print(s + ",");
             }
             */
            for (String s : fwords) {

                if (!s.equals("")) {

                    if (dwords.contains(s.toLowerCase())) {
                        vague = false;
                        break;
                    }
                }
            }

            if (vague) {
                vfileNames.add(fileNames.get(i));
                valPaths.add(alPaths.get(i));

            }
            vague = true;

            //addd interrruptioon checck
            if (isCancelled()) {
                System.out.println("Thread interrupted, Inside Step3");
                success = false;

                vfileNames.clear();
                valPaths.clear();
                return success;
            }

        }

        System.out.println("Thread done");
        return success;
    }
}
