package application;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;

/**
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
public class DbModel {

    static Connection conection;
    static String[] tname;
    static String[][] tExt = new String[5][];

    public static LinkedHashSet<String> audio = new LinkedHashSet<>();
    public static LinkedHashSet<String> document = new LinkedHashSet<>();
    public static LinkedHashSet<String> executables = new LinkedHashSet<>();
    public static LinkedHashSet<String> video = new LinkedHashSet<>();
    public static LinkedHashSet<String> image = new LinkedHashSet<>();
    public static LinkedHashSet<String> others = new LinkedHashSet<>();

  //  public static Comparator<String> desc = (String t, String t1) -> t1.compareTo(t);
//    public static Comparator<String> asc = (String t, String t1) -> t.compareTo(t1);
    public static boolean launch = false;

    //Filtered set corresponding to checkboxes other than C
    public class CaptionF {

        public ArrayList<String> filenames;
        public String root;

    }
    public static CaptionF[] oCaptionF;

    public DbModel() {

        oCaptionF = new CaptionF[IdentifyPartition.caption.size() - 1];

        conection = SqliteController.Connector("jdbc:sqlite:MAIN.sqlite");
        if (conection == null) {
            System.exit(1);
        }

        int count = 0;
        for (String caption : IdentifyPartition.caption) {
            if (!caption.equals("C:")) {
                oCaptionF[count] = new CaptionF();
                oCaptionF[count].root = caption;
                oCaptionF[count].filenames = new ArrayList<>();
                count++;
            }
        }

    }

    void insertPath(Path dir) {
        try {
            PreparedStatement preparedStatement = null;

            String query = "INSERT INTO PATHS VALUES (?);";
            preparedStatement = conection.prepareStatement(query);
            preparedStatement.setString(1, dir.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deletePath(Path path) {
        
       // System.out.println(path.toString()+"%");

        String deleteSQL = "DELETE from PATHS where NAME LIKE ?";

        try {

            PreparedStatement preparedStatement = conection.prepareStatement(deleteSQL);
            preparedStatement.setString(1, path.toString()+"%");
            // execute delete SQL stetement
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @param path
     * @return
     */
    public int insert(Path path) {
        int resultSet = 0, tIndex = 0;
        boolean flag = false;

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

        label:
        for (int j = 0; j < 5; j++) {
            for (String s : tExt[j]) {
                // System.out.println("\n"+extension);
                if (extension.compareToIgnoreCase(s) == 0) {
                    tIndex = j;
                    flag = true;

                    break label;
                }
            }
        }
        if (!flag) {

            try {
                PreparedStatement preparedStatement = null;

                String query = "INSERT INTO OTHERS VALUES (?,?, ?, ?,?,?);";
                preparedStatement = conection.prepareStatement(query);
                preparedStatement.setString(2, fname);
                preparedStatement.setString(3, extension);
                preparedStatement.setString(4, fpath);
                preparedStatement.setString(5, root);
                preparedStatement.setString(6, "ORD");

                resultSet = preparedStatement.executeUpdate();
                //     System.out.println("\n successful insertion" + path.toString());
                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
            }

            add2Set(others, fname, fpath);
        }

        if (flag) {

            //Database Insertion
            try {
                PreparedStatement preparedStatement = null;

                String query = "INSERT INTO " + tname[tIndex] + " VALUES (?,?, ?, ?,?,?);";
                preparedStatement = conection.prepareStatement(query);

                preparedStatement.setString(2, fname);
                preparedStatement.setString(3, extension);
                preparedStatement.setString(4, fpath);
                preparedStatement.setString(5, root);
                preparedStatement.setString(6, "ORD");

                resultSet = preparedStatement.executeUpdate();
                //     System.out.println("\n successful insertion" + path.toString());
                preparedStatement.close();

            } catch (SQLException ex) {
                Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Add fname to map
            switch (tname[tIndex]) {
                case "audio":
                    add2Set(audio, fname, fpath);

                    break;
                case "document":
                    add2Set(document, fname, fpath);
                    break;
                case "executables":
                    add2Set(executables, fname, fpath);

                    break;
                case "image":
                    add2Set(image, fname, fpath);
                    break;
                case "video":
                    add2Set(video, fname, fpath);
                    break;
                default:
                    break;
            }
        }

        return resultSet;
    }

    void myScan() {

        //ArrayList<String> alTname = new ArrayList<>();
        ArrayList<String> extList = new ArrayList<>();

        String[] tableNames = {"audio", "document", "video", "image", "executables"};
        for (int i = 0; i < tableNames.length; i++) {

            /*        
             */
          //  alTname.add(tableNames[i]);
            //   flag = false;

            //  System.out.println("tname is "+tname);
            String ext = null;

            String sql = "SELECT ext FROM " + tableNames[i] + "_EXT";
            ResultSet rs = null;
            try {
                Statement stmt = conection.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    ext = rs.getString("ext");

                    extList.add(ext);
                }

            } catch (SQLException ex) {
                Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (!extList.isEmpty()) {
                tExt[i] = new String[extList.size()];
                tExt[i] = extList.toArray(new String[extList.size()]);

            }
            extList = new ArrayList<>();

        }

        //tname = alTname.toArray(new String[alTname.size()]);
        tname = tableNames;

    }

    void removeEntry(Path path) {

       
        //To avoid Directory deletion
        if (returnContainsPath(path.toString())) {
            
            //Path is a file

            int tIndex = 0;
            // Statement stmt = null;

            LinkedHashSet set = null;
           String tableName = null;

            String fname;
            fname = path.getFileName().toString();
            String fpath = path.toString();
            String extension = "";
            boolean flag = false;

            int i = fname.lastIndexOf('.');
            if (i > 0) {
                extension = fname.substring(i + 1);
            }

            label:
            for (int j = 0; j < 5; j++) {
                for (String s : tExt[j]) {
                    // System.out.println("\n"+extension);
                    if (extension.compareToIgnoreCase(s) == 0) {
                        tIndex = j;
                        flag = true;

                        break label;
                    }

                }
            }
            if (!flag) {

                set = others;
                tableName = "others";

              
                String deleteSQL = "DELETE from OTHERS where PATH = ?";
                //stmt.executeUpdate(sql);
                //stmt.close();

                try {

                    PreparedStatement preparedStatement = conection.prepareStatement(deleteSQL);
                    preparedStatement.setString(1, fpath);
                    // execute delete SQL stetement
                    preparedStatement.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            if (flag) {

                //setting the ri8 map refernce
                switch (tname[tIndex]) {
                    case "audio":
                        set = audio;
                        tableName = "audio";
                    
                        break;
                    case "document":
                        set = document;
                        tableName = "document";

                        break;
                    case "executables":
                        set = executables;
                        tableName = "executables";
                    

                        break;
                    case "image":
                        set = image;
                        tableName = "image";

                      
                        break;
                    case "video":
                        set = video;
                        tableName = "video";

                      

                        break;
                    default:
                        break;
                }

                //deletion from db
                //stmt = conection.createStatement();
                String deleteSQL = "DELETE from " + tname[tIndex] + " where PATH = ?";
                //stmt.executeUpdate(sql);
                //stmt.close();

                PreparedStatement preparedStatement;
                try {
                    preparedStatement = conection.prepareStatement(deleteSQL);
                    preparedStatement.setString(1, fpath);
                    // execute delete SQL stetement
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
//////////////////////////////////////////////////////////////////////
            //Delete from hashmap
           /* ArrayList<String> hAl = set.get(fname);
            hAl.remove(fpath);*/
            
            if (!returnContains(fname, tableName)) {
                
                //No path exist for that file(all copies deleted)
                
                set.remove(fname);
               
                //Remove fname to corresponding checkbox sets
                String root = fpath.substring(0, 2);
                if (root.equals("C:")) {
                    if (fpath.startsWith(System.getProperty("user.home") + "\\Desktop")) {
                        CategoriesController.DesktopF.remove(fname);
                    } else if (fpath.startsWith(System.getProperty("user.home") + "\\Documents")) {
                        CategoriesController.DocumentsF.remove(fname);
                    } else if (fpath.startsWith(System.getProperty("user.home") + "\\Music")) {
                        CategoriesController.MusicF.remove(fname);
                    } else if (fpath.startsWith(System.getProperty("user.home") + "\\Videos")) {
                        CategoriesController.VideosF.remove(fname);
                    } else if (fpath.startsWith(System.getProperty("user.home") + "\\Downloads")) {
                        CategoriesController.DownloadsF.remove(fname);
                    } else if (fpath.startsWith(System.getProperty("user.home") + "\\Pictures")) {
                        CategoriesController.PicturesF.remove(fname);
                    }
                } else {
                    for (CaptionF x : oCaptionF) {
                        if (x.root.equals(root)) {
                            x.filenames.remove(fname);
                        }
                    }
                }

              
                //Reset masterset and masterscreen
                CategoriesController.masterSet.clear();

                CategoriesController.masterScreenAud.clear();
                CategoriesController.masterScreenDoc.clear();
                CategoriesController.masterScreenExec.clear();
                CategoriesController.masterScreenImg.clear();
                CategoriesController.masterScreenVid.clear();
                CategoriesController.masterScreenOthers.clear();

               
            }

        } else {
            //Path deletion
            deletePath(path);
        }
    }

    /**
     *
     * @param set
     * @param fname
     * @param fpath
     */
    public void add2Set(LinkedHashSet<String> set, String fname, String fpath) {

       
        if (!set.contains(fname)) {
         
            // we haven't seen this filenames             
            set.add(fname);            

            //Add fname to corresponding checkbox sets
            String root = fpath.substring(0, 2);
            if (root.equals("C:")) {
                if (fpath.startsWith(System.getProperty("user.home") + "\\Desktop")) {
                    CategoriesController.DesktopF.add(fname);
                } else if (fpath.startsWith(System.getProperty("user.home") + "\\Documents")) {
                    CategoriesController.DocumentsF.add(fname);
                } else if (fpath.startsWith(System.getProperty("user.home") + "\\Music")) {
                    CategoriesController.MusicF.add(fname);
                } else if (fpath.startsWith(System.getProperty("user.home") + "\\Videos")) {
                    CategoriesController.VideosF.add(fname);
                } else if (fpath.startsWith(System.getProperty("user.home") + "\\Downloads")) {
                    CategoriesController.DownloadsF.add(fname);
                } else if (fpath.startsWith(System.getProperty("user.home") + "\\Pictures")) {
                    CategoriesController.PicturesF.add(fname);
                }
            } else {
                for (CaptionF x : oCaptionF) {
                    if (x.root.equals(root)) {
                        if (!x.filenames.contains(fname)) {
                            x.filenames.add(fname);
                            //                     System.out.println("Adding to caption");
                        }
                    }
                }
            }


            //Reset masterset and masterscreen
            CategoriesController.masterSet.clear();

            CategoriesController.masterScreenAud.clear();
            CategoriesController.masterScreenDoc.clear();
            CategoriesController.masterScreenExec.clear();
            CategoriesController.masterScreenImg.clear();
            CategoriesController.masterScreenVid.clear();
            CategoriesController.masterScreenOthers.clear();

           
        }

    }

    public static ArrayList<String> returnPaths(String file, String table) {
        ArrayList<String> paths = new ArrayList<>();
        String sql = "SELECT PATH FROM " + table + " WHERE FNAME = ?";

        try {
            PreparedStatement preparedStatement = null;
            preparedStatement = conection.prepareStatement(sql);
            preparedStatement.setString(1, file);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                //  System.out.println(rs.getString("PATH"));
                paths.add(rs.getString("PATH"));
            }

            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.out.println();
        //System.out.println(paths);
        return paths;
    }

    public static ArrayList<String> returnPaths(String file) {
        ArrayList<String> paths = new ArrayList<>();

        String[] tableNames = {"audio", "document", "video", "image", "executables", "others"};
        for (String table : tableNames) {
            paths.addAll(returnPaths(file, table));
        }
        //System.out.println();
        //System.out.println(paths);
        return paths;
    }

    public static boolean returnContains(String file) {

        String[] tableNames = {"audio", "document", "video", "image", "executables", "others"};
        boolean contains = false;

        for (String s : tableNames) {
            String sql = "SELECT PATH FROM " + s + " WHERE FNAME = ?";

            try {
                PreparedStatement preparedStatement = null;
                preparedStatement = conection.prepareStatement(sql);
                preparedStatement.setString(1, file);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    System.out.println("hi");
                    contains = true;
                    preparedStatement.close();
                    break;
                }

                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return contains;

    }

    public static boolean returnContains(String file, String table) {

        if (returnPaths(file, table).size() > 0) {
            return true;
        } else {
            return false;
        }

    }
    public static boolean returnContainsPath(String path)
    {
        String[] tableNames = {"audio", "document", "video", "image", "executables","others"};
        boolean contains = false;

        for (String s : tableNames) {
            String sql = "SELECT * FROM " + s + " WHERE PATH = ?";

            try {
                PreparedStatement preparedStatement = null;
                preparedStatement = conection.prepareStatement(sql);
                preparedStatement.setString(1, path);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    System.out.println("hi");
                    contains = true;
                    preparedStatement.close();
                    break;
                }

                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(DbModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return contains;
    }

}
