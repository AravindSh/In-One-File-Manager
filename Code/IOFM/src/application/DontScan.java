package application;

import static application.DbModel.tname;
import static application.StartApp.demodbModel;
import static application.Finder.keys;
import static application.Finder.watcher;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchKey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author ARAVIND
 */
public class DontScan extends Task<Boolean> {

    Comparator<String> ascOrd
            = (String s1, String s2) -> s1.compareTo(s2);
    SortedSet<String> sortedSet;
    

    void register(Path dir) {
        //   System.out.println("register dir  :  "+dir);

        WatchKey key = null;
        try {
            key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException ex) {
            Logger.getLogger(DontScan.class.getName()).log(Level.SEVERE, null, ex);
        }

        Path prev = keys.get(key);

        keys.put(key, dir);

    }

    @Override
    protected Boolean call() {

        sortedSet = new TreeSet<>(ascOrd);

        String[] tableNames = {"AUDIO", "DOCUMENT", "VIDEO", "IMAGE", "EXECUTABLES", "OTHERS"};

        for (String tableName : tableNames) {
            try {
                Path path;
                String sql = "SELECT path FROM " + tableName;
                ResultSet rs = null;

                Statement stmt = null;

                stmt = DbModel.conection.createStatement();

                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    path = Paths.get(rs.getString("path"));

                    //set map and arraylists corresponding to categories
                    String fname;
                    fname = path.getFileName().toString();
                    String fpath = path.toString();

                    String extension = "";

                    int i = fname.lastIndexOf('.');
                    if (i > 0) {
                        extension = fname.substring(i + 1);
                    }
                    String root = fpath.substring(0, 1);
                    //            System.out.println("\n"+extension);

                    int tIndex = 0;
                    boolean flag = false;

                    label:
                    for (int j = 0; j < 5; j++) {
                        for (String s : StartApp.demodbModel.tExt[j]) {
                            // System.out.println("\n"+extension);
                            if (extension.compareToIgnoreCase(s) == 0) {
                                tIndex = j;
                                flag = true;
                                break label;
                            }
                        }
                    }

                    if (flag) {

                        //Add fname to map
                        switch (tname[tIndex]) {
                            case "audio":
                                demodbModel.add2Set(DbModel.audio, fname, fpath);

                                break;
                            case "document":
                                demodbModel.add2Set(DbModel.document, fname, fpath);
                                break;
                            case "executables":
                                demodbModel.add2Set(DbModel.executables, fname, fpath);

                                break;
                            case "image":
                                demodbModel.add2Set(DbModel.image, fname, fpath);
                                break;
                            case "video":
                                demodbModel.add2Set(DbModel.video, fname, fpath);
                                break;
                            default:
                                break;
                        }
                    }
                    if (!flag) {
                        demodbModel.add2Set(DbModel.others, fname, fpath);
                    }

                    //set map and arraylists corresponding to categories ENDS
                    //retrieve paths in sortedset
                }
            } catch (SQLException ex) {
                Logger.getLogger(DontScan.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        doit();

        for (String s : sortedSet) {
            //   System.out.println("sortedset is: " + s);
            register(Paths.get(s));
        }

        //  System.out.println(Finder.keys.keySet().size() + "    " + sortedSet.size());
        //  System.out.println("Other map "+DbModel.others);

        /*        for (WatchKey k : Finder.keys.keySet()) {
         System.out.println(k+" : "+Finder.keys.get(k));
         }
         */
        new WatchDir();

        return true;

    }

    public void doit(){

        String sql = "SELECT NAME FROM PATHS";
        ResultSet rs = null;
        String path = null;

        Statement stmt = null;

        try {
            stmt = DbModel.conection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                path = rs.getString("NAME");
                sortedSet.add(path);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DontScan.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
