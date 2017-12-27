/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import static application.DbModel.audio;
import static application.DbModel.conection;
import static application.DbModel.document;
import static application.DbModel.executables;
import static application.DbModel.image;
import static application.DbModel.oCaptionF;
import static application.DbModel.others;
import static application.DbModel.tExt;
import static application.DbModel.tname;
import static application.DbModel.video;
import static application.StartApp.backgCreatDel;
import static application.StartApp.demodbModel;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author ARAVIND
 */
public class ExtModifyController implements Initializable {

    @FXML
    private Button audButton;
    @FXML
    private Button docButton;
    @FXML
    private Button execButton;
    @FXML
    private Button imgButton;
    @FXML
    private Button vidButton;
    @FXML
    private Label myLabel;
    @FXML
    private TableView<TableExtensions> myTable;
    @FXML
    private TableColumn<TableExtensions, String> extColumn;
    @FXML
    private TableColumn<TableExtensions, String> descColumn;
    @FXML
    private TextField addExt;
    @FXML
    private TextField addDesc;
    @FXML
    private Button addButton;

    String tableName = "AUDIO";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        docButton.setOnAction((ActionEvent event) -> {
            updateWindow(event);
        });
        execButton.setOnAction((ActionEvent event) -> {
            updateWindow(event);
        });
        audButton.setOnAction((ActionEvent event) -> {
            updateWindow(event);
        });
        vidButton.setOnAction((ActionEvent event) -> {
            updateWindow(event);
        });
        imgButton.setOnAction((ActionEvent event) -> {
            updateWindow(event);
        });

        extColumn.setCellValueFactory(new PropertyValueFactory<>("ext"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));

        myLabel.setText(tableName);
        myTable.setItems(getDetails());
    }

    void updateWindow(ActionEvent event) {

        Button b = (Button) event.getSource();
        tableName = b.getText();

        myLabel.setText(tableName);
        myTable.setItems(getDetails());
    }

    private ObservableList<TableExtensions> getDetails() {

        String ext = null, info = null;
        ObservableList<TableExtensions> details = FXCollections.observableArrayList();

        try {
            PreparedStatement preparedStatement = null;

            String query = "SELECT * FROM " + tableName + "_EXT";
            preparedStatement = DbModel.conection.prepareStatement(query);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ext = rs.getString("EXT");
                info = rs.getString("INFO");
                details.add(new TableExtensions(ext, info));
            }
            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(ExtModifyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return details;
    }

    @FXML
    void addExtensionHandler() {

        boolean valid = true;

        if (addDesc.getText().equals("") || addDesc.getText() == null) {
            addDesc.setPromptText("INVALID");
            valid = false;
            
        }

        if (addExt.getText().equals("") || addExt.getText() == null) {
            addExt.setPromptText("INVALID");
            valid = false;
           
        }

        System.out.println(addDesc.getText()+", "+addExt.getText()+", "+valid);
       
        if (valid) {
            //System.out.println("Should not get printed");

            Task<Boolean> addTask = new Task<Boolean>() {

                @Override
                protected Boolean call() throws Exception {

                    String newExt = addExt.getText();
                    String newDesc = addDesc.getText();

                    addDesc.setPromptText("Description");
                    addExt.setPromptText("Extension");

                    ArrayList<String> removePaths = new ArrayList<>();

                    /////////////////
                    int tIndex = 0;
                    boolean flag = false;
                    LinkedHashSet<String> set = null;
                    String tableLocal = null;
                    //find whether the extension to be added exist in other 4 categories
                    label:
                    for (int j = 0; j < 5; j++) {
                        for (String s : tExt[j]) {

                            if (newExt.compareToIgnoreCase(s) == 0) {
                                tIndex = j;
                                flag = true;

                                break label;
                            }
                        }
                    }

                    if (flag) {

                        addDesc.setPromptText("Invalid");
                        addExt.setPromptText("Invalid");

                    } else {
                        set = DbModel.others;
                        tableLocal = "others";

                        //filter files containing that extension
                        for (String file : set) {

                            int i = file.lastIndexOf('.');
                            String sExt = null;
                            if (i > 0) {
                                sExt = file.substring(i + 1);
                            }

                            if (sExt.compareToIgnoreCase(newExt) == 0) {
                                //removeFiles.add(file);
                                removePaths.addAll(DbModel.returnPaths(file, tableLocal));

                            }

                        }
                        System.out.println(removePaths);

                    //Remove files containing that Extension
                        //For each path
                        for (String path : removePaths) {
                            //for all copies of the file i.e for all paths
                            demodbModel.removeEntry(Paths.get(path));
                        }
                    /////////////////

                        //Add extension to desired ext Table
                        try {
                            PreparedStatement preparedStatement = null;

                            String query = "INSERT INTO " + myLabel.getText() + "_EXT VALUES (?,?);";
                            preparedStatement = DbModel.conection.prepareStatement(query);
                            preparedStatement.setString(1, newExt.toUpperCase());
                            preparedStatement.setString(2, newDesc);

                            preparedStatement.executeUpdate();
                            preparedStatement.close();

                        } catch (SQLException ex) {
                            Logger.getLogger(ExtModifyController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //Call Myscan to update tExt
                        demodbModel.myScan();

                    //Insert Removed Files 
                        //For each file
                        for (String path : removePaths) {
                            //for all copies of the file i.e for all paths
                            demodbModel.insert(Paths.get(path));
                        }

                        removePaths.clear();
                        
                    }
                    return true;
                }
            };

            backgCreatDel.show();
            Thread t = new Thread(addTask);
            t.start();

            addTask.setOnSucceeded((WorkerStateEvent event) -> {
                CategoriesController.listPropertyAud.set(FXCollections.observableArrayList(audio));
                CategoriesController.listPropertyDoc.set(FXCollections.observableArrayList(document));
                CategoriesController.listPropertyVid.set(FXCollections.observableArrayList(video));
                CategoriesController.listPropertyExec.set(FXCollections.observableArrayList(executables));
                CategoriesController.listPropertyImg.set(FXCollections.observableArrayList(image));
                CategoriesController.listPropertyOthers.set(FXCollections.observableArrayList(others));

                //Redisplay Table
                myTable.setItems(getDetails());
                backgCreatDel.close();

            });

        }

    }

    @FXML
    void resetHandler() {

        Task<Boolean> resetTask = new Task<Boolean>() {

            @Override
            protected Boolean call() throws Exception {

                Connection resetCon = SqliteController.Connector("jdbc:sqlite:RESET.sqlite");

                String[] extTableNames = {"audio_ext", "document_ext", "video_ext", "image_ext", "executables_ext"};

                //Delete from extTables from MAIN.sqlite
                for (String tb : extTableNames) {

                    String deleteSQL = "DELETE from " + tb;

                    try {
                        PreparedStatement preparedStatement = DbModel.conection.prepareStatement(deleteSQL);
                        // execute delete SQL stetement
                        preparedStatement.executeUpdate();
                        preparedStatement.close();

                    } catch (SQLException ex) {
                        Logger.getLogger(ExtModifyController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //Retrieve from RESET.sqlite
                for (String tB : extTableNames) {
                    String sql = "SELECT * FROM " + tB;

                    try {
                        PreparedStatement preparedStatement = null;
                        preparedStatement = resetCon.prepareStatement(sql);

                        ResultSet rs = preparedStatement.executeQuery();

                        while (rs.next()) {

                            String ext = rs.getString("EXT");
                            String info = rs.getString("INFO");
                            //Insert into MAIN.sqlite
                            try {
                                PreparedStatement pS = null;

                                String query = "INSERT INTO " + tB + " VALUES (?,?);";
                                pS = DbModel.conection.prepareStatement(query);

                                pS.setString(1, ext);
                                pS.setString(2, info);

                                pS.executeUpdate();
                                //     System.out.println("\n successful insertion" + path.toString());
                                pS.close();

                            } catch (SQLException ex) {
                                Logger.getLogger(ExtModifyController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                        preparedStatement.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(ExtModifyController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //Call DbModel.myScan() TO set reset tExt 
                demodbModel.myScan();

                //1.Iterate through all the tables EXCEPT OTHERS
                //2.Find whether the file belongs to that table by comparing extension
                //3.If no then insert the entire entry in others
                //4.Remove entry from current table
                for (int i = 0; i < tname.length; i++) {

                    LinkedHashSet<String> set = null;

                    switch (tname[i]) {
                        case "audio":
                            set = DbModel.audio;
                            break;
                        case "document":
                            set = DbModel.document;
                            break;
                        case "executables":
                            set = DbModel.executables;
                            break;
                        case "image":
                            set = DbModel.image;
                            break;
                        case "video":
                            set = DbModel.video;
                            break;
                        default:
                            break;
                    }

                    String sql = "SELECT * FROM " + tname[i];
                    ArrayList<String> corresExtList = new ArrayList<>(Arrays.asList(tExt[i]));

                    try {
                        PreparedStatement preparedStatement = null;
                        preparedStatement = conection.prepareStatement(sql);

                        ResultSet rs = preparedStatement.executeQuery();

                        while (rs.next()) {
                            //  System.out.println(rs.getString("EXTENSION"));
                            String ext = rs.getString("EXTENSION").toUpperCase();
                            String fpath = rs.getString("PATH");
                            String fname = rs.getString("FNAME");

                            if (!corresExtList.contains(ext)) {

                                //NO, insert entire row in others
                                demodbModel.insert(Paths.get(fpath));

                                //Delete the entire entry from current table(dont call removeEntry)
                                String deleteSQL = "DELETE from " + tname[i] + " where PATH = ?";

                                try {

                                    PreparedStatement pStat = conection.prepareStatement(deleteSQL);
                                    pStat.setString(1, fpath);
                                    // execute delete SQL stetement
                                    pStat.executeUpdate();
                                    pStat.close();

                                } catch (SQLException ex) {
                                    Logger.getLogger(ExtModifyController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                //Remove from set
                                if (set.contains(fname)) {

                                    set.remove(fname);

                                    //Remove fname from corresponding checkbox sets
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
                                        for (DbModel.CaptionF x : oCaptionF) {
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
                                    //Delete the entire entry from current table(dont call removeEntry)
                                }

                            }

                        }

                        preparedStatement.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(ExtModifyController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                return true;
            }
        };

        backgCreatDel.show();
        Thread t = new Thread(resetTask);
        t.start();

        resetTask.setOnSucceeded((WorkerStateEvent event) -> {
            CategoriesController.listPropertyAud.set(FXCollections.observableArrayList(audio));
            CategoriesController.listPropertyDoc.set(FXCollections.observableArrayList(document));
            CategoriesController.listPropertyVid.set(FXCollections.observableArrayList(video));
            CategoriesController.listPropertyExec.set(FXCollections.observableArrayList(executables));
            CategoriesController.listPropertyImg.set(FXCollections.observableArrayList(image));
            CategoriesController.listPropertyOthers.set(FXCollections.observableArrayList(others));

            //Redisplay
            tableName = "AUDIO";
            myLabel.setText(tableName);
            myTable.setItems(getDetails());

            backgCreatDel.close();

        });

    }

}
