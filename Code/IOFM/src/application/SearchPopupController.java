package application;

import static application.CategoriesController.openExp;
import static application.StartApp.backgCreatDel;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ARAVIND
 */
public class SearchPopupController implements Initializable {

    @FXML
    BorderPane root;

    @FXML
    private Label label;
    @FXML
    private Separator sp;
    @FXML
    private HBox hb;
    @FXML
    private Label sLb;
    @FXML
    private TextField searchF;
    @FXML
    private TableView<TableSearch> table;
    @FXML
    private TableColumn<TableSearch, String> tcFname;
    @FXML
    private TableColumn<TableSearch, String> tcExt;
    @FXML
    private Label count;

    public static String sPopContLabel;
    public static ObservableList<String> sPopContSearchList;

    private String tableName;

    ContextMenu cmTag = new ContextMenu();

    LinkedHashSet<String> set = new LinkedHashSet<>();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        // System.out.println("INITIALIZE");
        //set label of new window
        // System.out.println("t.getText()   " + text);
        label.setText(sPopContLabel);
        count.setText(String.valueOf(sPopContSearchList.size()));

        // System.out.println("tcFname,tcExt  " + tcFname.getText() + "  " + tcExt.getText());
        //set table
        tcFname.setCellValueFactory(new PropertyValueFactory<>("fname"));//Fname column   

        tcExt.setCellValueFactory(new PropertyValueFactory<>("extension")); //Extension column

        table.setItems(getDetails(sPopContSearchList)); //Set table columns

        if (!(sPopContLabel.equals("BACKUP") || sPopContLabel.equals("STAR") || sPopContLabel.equals("Watched")
                || sPopContLabel.equals("Not Watched") || sPopContLabel.equals("Duplicate"))) {

            //get corresponding hashmap
            switch (sPopContLabel) {
                case "Documents":
                    set = DbModel.document;
                    tableName = "DOCUMENT";
                    break;
                case "Images":
                    set = DbModel.image;
                    tableName = "IMAGE";
                    break;
                case "Audio":
                    set = DbModel.audio;
                    tableName = "AUDIO";
                    break;
                case "Video":
                    set = DbModel.video;
                    tableName = "VIDEO";
                    break;
                case "Executables":
                    set = DbModel.executables;
                    tableName = "EXECUTABLES";
                    break;
                case "Others":
                    set = DbModel.others;
                    tableName = "OTHERS";
                    break;

            }

            //final Map<String, ArrayList<String>> fmap = set;
            //set action for ri8 click
            table.setOnMouseClicked(et -> {
                if (et.getButton() == MouseButton.SECONDARY) {
                    // System.out.println("cloick on table    " + table.getSelectionModel().getSelectedItem().getFname());
                    openExp(table, et, table.getSelectionModel().getSelectedItem().getFname(), tableName);
                }
            });
        } else {

            table.setOnMouseClicked(et -> {
                if (et.getButton() == MouseButton.SECONDARY) {
                    // System.out.println("cloick on table    " + table.getSelectionModel().getSelectedItem().getFname());
                    tagOpenExp(table, et, table.getSelectionModel().getSelectedItem().getFname());
                }
            });

        }

        //set search searchF listener
        searchF.setOnAction(ee -> displaySearchResults());

    }

    public ObservableList<TableSearch> getDetails(ObservableList<String> ol) {
        ObservableList<TableSearch> details = FXCollections.observableArrayList();

        for (String fname : ol) {

            String extension = "";

            int i = fname.lastIndexOf('.');
            if (i > 0) {
                extension = fname.substring(i + 1);
            }
            // System.out.println("name,dExtension  " + fname + "  " + extension);
            details.add(new TableSearch(fname, extension));
        }

        return details;

    }

    public void displaySearchResults() {

        Comparator<String> ascOrd
                = (String s1, String s2) -> s1.compareTo(s2);

        //Sorted Items
        SortedSet<String> items = new TreeSet<>(ascOrd);
        items.addAll(sPopContSearchList);

        //Items To Display
        SortedSet<String> items2D = new TreeSet<>(ascOrd);

        if (searchF.getText() == null || searchF.getText().equals("")) {

            table.setItems(getDetails(FXCollections.observableArrayList(items)));
            count.setText(String.valueOf(items.size()));
        } else {

            for (String s : items) {
                boolean contains = s.toLowerCase().contains(searchF.getText().toLowerCase());
                if (contains) {
                    items2D.add(s);
                }
            }
            table.setItems(getDetails(FXCollections.observableArrayList(items2D))); //Set table columns
            count.setText(String.valueOf(items2D.size()));
        }

    }

    private void tagOpenExp(Node n, MouseEvent et, String fname) {

        //System.out.println("Hell: "+fname);
        //System.out.println("MAp: "+set.toString());
        //get path or paths from set
        if (cmTag != null) {
            cmTag.hide();
            System.out.println("Its working fine");
        }

        //ArrayList<String> al = map.get(fname);
        ArrayList<String> al = DbModel.returnPaths(fname);

        cmTag = new ContextMenu();

        if (al.size() == 1) {
            //filename is unique in filesystem

            MenuItem cmItemList = new MenuItem("Open in Explorer");

            cmTag.getItems().add(cmItemList);

            //set action for contextmenu
            cmItemList.setOnAction(e -> {
                //   System.out.println(" Success");

                new ExplorerSelect().openInExplorer(al.get(0));

            });
            //  cmTag.show(n, et.getScreenX(), et.getScreenY());
        } else {
            //filename has more than one path
            //cmTag = new ContextMenu();
            Menu menu = new Menu("Open in Explorer");
            //set menuItems
            MenuItem[] menuItems = new MenuItem[al.size()];
            //set paths to menuitems and add menuitems to menu
            for (int i = 0; i < al.size(); i++) {
                final String path = al.get(i);
                //Create corresponding menuitems
                menuItems[i] = new MenuItem(path);
                menu.getItems().add(menuItems[i]);

                //SET ACTION i.e clickable
                menuItems[i].setOnAction((ActionEvent e) -> {
                    //     System.out.println(" Success 2");

                    new ExplorerSelect().openInExplorer(path);

                });

            }
            cmTag.getItems().add(menu);
            //  cmTag.show(n, et.getScreenX(), et.getScreenY());
        }
        //Remove Tag
        if (sPopContLabel.equals("BACKUP") || sPopContLabel.equals("STAR")) {

            MenuItem rmTag = new MenuItem("Remove Tag");
            cmTag.getItems().add(rmTag);

            rmTag.setOnAction(e -> {

                Task<Boolean> removeTagTask = new Task<Boolean>() {

                    @Override
                    protected Boolean call() throws Exception {
                        String[] tableDb = {"audio", "document", "video", "image", "executables", "others"};

                        for (String tb : tableDb) {
                            for (String path : DbModel.returnPaths(fname, tb)) {

                                ////////////
                                try {
                                    PreparedStatement ps = DbModel.conection.prepareStatement(
                                            "UPDATE " + tb + " SET TAG = ? WHERE PATH = ?");

                                    // set the preparedstatement parameters
                                    ps.setString(1, "ORD");
                                    ps.setString(2, path);

                                    // call executeUpdate to execute our sql update statement
                                    ps.executeUpdate();
                                    ps.close();

                                } catch (SQLException ex) {
                                    Logger.getLogger(SearchPopupController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                ////////////
                            }
                        }

                        return true;
                    }
                };

                Thread t = new Thread(removeTagTask);
                backgCreatDel.show();
                t.start();

                removeTagTask.setOnSucceeded(ee -> {
                    sPopContSearchList.remove(fname);
                    table.setItems(getDetails(sPopContSearchList));
                    count.setText(String.valueOf(sPopContSearchList.size()));
                    backgCreatDel.close();
                });
            });

        }

        cmTag.show(n, et.getScreenX(), et.getScreenY());
    }

}
