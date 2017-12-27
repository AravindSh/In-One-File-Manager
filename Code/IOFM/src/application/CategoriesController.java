package application;

import static application.PdfRenameWaitDialogController.thread;
import static application.SearchPopupController.sPopContSearchList;
import static application.StartScanController.window;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
public class CategoriesController implements Initializable {

    @FXML
    public MenuItem pdfRenWindow;
    @FXML
    public MenuItem editExt;

    @FXML
    public ListView<String> lvAud;
    @FXML
    public ListView<String> lvDoc;
    @FXML
    public ListView<String> lvExec;
    @FXML
    public ListView<String> lvVid;
    @FXML
    public ListView<String> lvImg;
    @FXML
    public ListView<String> lvOthers;

    @FXML
    public MenuButton filter;

    @FXML
    public Button btnBack;

    @FXML
    public Button btnForward;

    @FXML
    public Accordion accordion;

    @FXML
    public BorderPane borderPane;

    @FXML
    public VBox dirVbox;

    @FXML
    public SplitPane splitLeft;

    @FXML
    public TitledPane titledDoc;
    @FXML
    public TitledPane titledAud;
    @FXML
    public TitledPane titledVid;
    @FXML
    public TitledPane titledImg;
    @FXML
    public TitledPane titledExec;
    @FXML
    public TitledPane titledOthers;

    // private double timeNow;
   // public ListView<String> searchResults;

    //Childern of C:
    public static CheckBox[] childOcbC;

    //Checkboxes for partitions other than C:
    int sizeOcArr = IdentifyPartition.caption.size() - 1;
    public static CheckBox[] cArr;

    public static ListProperty<String> listPropertyDoc = new SimpleListProperty<>();
    public static ListProperty<String> listPropertyAud = new SimpleListProperty<>();
    public static ListProperty<String> listPropertyImg = new SimpleListProperty<>();
    public static ListProperty<String> listPropertyVid = new SimpleListProperty<>();
    public static ListProperty<String> listPropertyExec = new SimpleListProperty<>();
    public static ListProperty<String> listPropertyOthers = new SimpleListProperty<>();

    //Filtered Set corresponds to checkboxes
    public static ArrayList<String> DesktopF = new ArrayList<>();
    public static ArrayList<String> DocumentsF = new ArrayList<>();
    public static ArrayList<String> DownloadsF = new ArrayList<>();
    public static ArrayList<String> MusicF = new ArrayList<>();
    public static ArrayList<String> VideosF = new ArrayList<>();
    public static ArrayList<String> PicturesF = new ArrayList<>();
    public static ArrayList<String> OthersF = new ArrayList<>();

    public BorderPane rootSearch = null;

    //Masterset: sum of selected checkboxes
    static ArrayList<String> masterSet = new ArrayList<>();

    //Master Screen corresponding to Categories
    static ArrayList<String> masterScreenDoc = new ArrayList<>();
    static ArrayList<String> masterScreenAud = new ArrayList<>();
    static ArrayList<String> masterScreenVid = new ArrayList<>();
    static ArrayList<String> masterScreenImg = new ArrayList<>();
    static ArrayList<String> masterScreenExec = new ArrayList<>();
    static ArrayList<String> masterScreenOthers = new ArrayList<>();

    //for opening categories in new window
    public static ContextMenu cm;
    public MenuItem cmItem1;
    public TableView<TableSearch> table;
    public TableColumn<TableSearch, String> tcFname;
    public TableColumn<TableSearch, String> tcExt;
    public TextField searchF;
//     ObservableList<String> searchList;
    TitledPane t;
    static boolean success;

    static Task<Boolean> task;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // TODO
        //Set C:\ 
        window.setOnCloseRequest(e -> performOnClose());

        String s[] = {"Desktop", "Documents", "Downloads", "Music", "Pictures", "Videos"};

        childOcbC = new CheckBox[6];

        for (int i = 0; i < 6; i++) {
            childOcbC[i] = new CheckBox(s[i]);
            childOcbC[i].setFont(new Font(15));
            childOcbC[i].setPrefSize(228, 48);
            dirVbox.getChildren().add(childOcbC[i]);

        }

        //Adda separator
        dirVbox.getChildren().add(new Separator());

        for (int i = 0; i <= 5; i++) {
            childOcbC[i].setOnAction(new Clas4CoC());
        }

        //Bind listViews
        listPropertyDoc.set(FXCollections.observableArrayList(DbModel.document));
        lvDoc.itemsProperty().bind(listPropertyDoc);
        //handler for RMB on an item in listView
        lvDoc.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                openExp(lvDoc,  event, lvDoc.getSelectionModel().getSelectedItem(), "DOCUMENT");
            }
        }
        );

        listPropertyAud.set(FXCollections.observableArrayList(DbModel.audio));
        lvAud.itemsProperty().bind(listPropertyAud);
        //handler for RMB on an item in listView
        lvAud.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                openExp(lvAud,  event, lvAud.getSelectionModel().getSelectedItem(), "AUDIO");
            }
        }
        );

        listPropertyExec.set(FXCollections.observableArrayList(DbModel.executables));
        lvExec.itemsProperty().bind(listPropertyExec);
        //handler for RMB on an item in listView
        lvExec.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                openExp(lvExec,  event, lvExec.getSelectionModel().getSelectedItem(), "EXECUTABLES");
            }
        } //handler for RMB on an item in listView
        );

        listPropertyImg.set(FXCollections.observableArrayList(DbModel.image));
        lvImg.itemsProperty().bind(listPropertyImg);
        //handler for RMB on an item in listView
        lvImg.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                openExp(lvImg,  event, lvImg.getSelectionModel().getSelectedItem(), "IMAGE");
            }
        }
        );

        listPropertyVid.set(FXCollections.observableArrayList(DbModel.video));
        lvVid.itemsProperty().bind(listPropertyVid);
        //handler for RMB on an item in listView
        lvVid.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                openExp(lvVid, event, lvVid.getSelectionModel().getSelectedItem(), "VIDEO");
            }
        }
        );

        listPropertyOthers.set(FXCollections.observableArrayList(DbModel.others));
        lvOthers.itemsProperty().bind(listPropertyOthers);
        //handler for RMB on an item in listView
        lvOthers.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                openExp(lvOthers,  event, lvOthers.getSelectionModel().getSelectedItem(), "OTHERS");
            }
        }
        );

        //Display checkboxes for partitons !C:\ and register events
        int cCount = -1;
        cArr = new CheckBox[sizeOcArr];
        for (int i = 0; i < IdentifyPartition.caption.size(); i++) {

            if (!IdentifyPartition.caption.get(i).equals("C:")) {
                cCount++;
                cArr[cCount] = new CheckBox();
                cArr[cCount].setFont(new Font(15));
                cArr[cCount].setPrefSize(228, 48);

                if (IdentifyPartition.ext.get(i)) {
                    cArr[cCount].setText(IdentifyPartition.caption.get(i) + "(External)");
                } else {
                    cArr[cCount].setText(IdentifyPartition.caption.get(i));
                }
                cArr[cCount].setOnAction(new Clas4CArr());
                dirVbox.getChildren().add(cArr[cCount]);
            }
        }

        //Context Menu
        ContextMenu cmT = new ContextMenu();

        cmItem1 = new MenuItem("New Window");
        cmT.getItems().add(cmItem1);

        cmItem1.setOnAction((ActionEvent e) -> {

            t = (TitledPane) cmT.getOwnerNode();

            //set appropriate title for searchPopUpController
            SearchPopupController.sPopContLabel = t.getText();

//            System.out.println("ID: " + t.getId());
            ListView lv = (ListView) t.getContent();

            //set appropriate TableItems for searchPopupController
            SearchPopupController.sPopContSearchList = FXCollections.observableArrayList(lv.getItems());

            //Get root and table of new window
            try {
                rootSearch = (BorderPane) FXMLLoader.load(getClass().getResource("searchPopup.fxml"));
            } catch (IOException ex) {
                Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Stage searchPopup = new Stage();
            searchPopup.initModality(Modality.APPLICATION_MODAL);
            searchPopup.setOnCloseRequest(ee -> sPopContSearchList.clear());
            Scene scene = new Scene(rootSearch);
            searchPopup.setScene(scene);
            searchPopup.show();

            //System.out.println(lv.getItems());
        });

        //Set right click action
        titledDoc.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // System.out.println("Conext Menu Requested");
                cmT.show(titledDoc, e.getScreenX(), e.getScreenY());
            }
        });
        titledAud.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                //System.out.println("Conext Menu Requested");
                cmT.show(titledAud, e.getScreenX(), e.getScreenY());
            }
        });
        titledExec.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // System.out.println("Conext Menu Requested");
                cmT.show(titledExec, e.getScreenX(), e.getScreenY());
            }
        });
        titledVid.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // System.out.println("Conext Menu Requested");
                cmT.show(titledVid, e.getScreenX(), e.getScreenY());
            }
        });
        titledImg.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // System.out.println("Conext Menu Requested");
                cmT.show(titledImg, e.getScreenX(), e.getScreenY());
            }
        });
        titledOthers.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                //System.out.println("Conext Menu Requested");
                cmT.show(titledOthers, e.getScreenX(), e.getScreenY());
            }
        });

        DbModel.launch = true;

    }

    //open in explorer
    public static void openExp(Node n, MouseEvent event, String fname,
            String tableName) {

        
        //testing
        if (cm != null) {
            cm.hide();
            System.out.println("Its working fine");
        }
        //testing

       // ArrayList<String> al = set.get(fname);
        //get path or paths 
        ArrayList<String> al = DbModel.returnPaths(fname, tableName);

        // FOR TAGS
        Menu tagMenu = new Menu("TAGS");
        MenuItem starMenu = new MenuItem("STARRED");
        MenuItem backupMenu = new MenuItem("Backup");
        tagMenu.getItems().addAll(starMenu, backupMenu);

        starMenu.setOnAction(e -> {
            try {
                PreparedStatement ps = DbModel.conection.prepareStatement(
                        "UPDATE " + tableName + " SET TAG = ? WHERE PATH = ?");

                // set the preparedstatement parameters
                ps.setString(1, "STAR");
                ps.setString(2, al.get(0));

                // call executeUpdate to execute our sql update statement
                ps.executeUpdate();
                ps.close();

            } catch (SQLException ex) {
                Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        backupMenu.setOnAction(e -> {
            try {
                PreparedStatement ps = DbModel.conection.prepareStatement(
                        "UPDATE " + tableName + " SET TAG = ? WHERE PATH = ?");

                // set the preparedstatement parameters
                ps.setString(1, "BACKUP");
                ps.setString(2, al.get(0));

                // call executeUpdate to execute our sql update statement
                ps.executeUpdate();
                ps.close();

            } catch (SQLException ex) {
                Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        cm = new ContextMenu();

        if (al.size() == 1) {
            //filename is unique in filesystem

            MenuItem cmItemList = new MenuItem("Open in Explorer");

            cm.getItems().addAll(cmItemList, tagMenu);

            //set action for contextmenu
            cmItemList.setOnAction(e -> {
                //   System.out.println(" Success");

                new ExplorerSelect().openInExplorer(al.get(0));

            });
            cm.show(n, event.getScreenX(), event.getScreenY());
        } else {
            //filename has more than one path
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
            cm.getItems().addAll(menu, tagMenu);
            cm.show(n, event.getScreenX(), event.getScreenY());
        }

    }

    private void performOnClose() {

        try {
            // System.out.println("Closing conection to database");
            DbModel.conection.close();
        } catch (SQLException ex) {
            Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class Clas4CoC implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            for (CheckBox c : childOcbC) {
                if (event.getSource() == c) {

                    if (borderPane.getCenter() == accordion) {
                        performFilteringCoC(c, DbModel.audio,
                                listPropertyAud, "audio", masterScreenAud);
                        performFilteringCoC(c, DbModel.image,
                                listPropertyImg, "image", masterScreenImg);
                        performFilteringCoC(c, DbModel.video,
                                listPropertyVid, "video", masterScreenVid);
                        performFilteringCoC(c, DbModel.document,
                                listPropertyDoc, "document", masterScreenDoc);
                        performFilteringCoC(c, DbModel.executables,
                                listPropertyExec, "executables", masterScreenExec);
                        //new addition
                        performFilteringCoC(c, DbModel.others,
                                listPropertyOthers, "others", masterScreenOthers);

                    }

                }
            }
        }

        public void performFilteringCoC(CheckBox c,
                Collection<String> origList, ListProperty<String> prop,
                String categories, ArrayList<String> masterScreen) {

            ArrayList<String> localSet = new ArrayList<>();

            switch (c.getText()) {
                case "Desktop":
                    localSet = DesktopF;
                    break;

                case "Music":
                    localSet = MusicF;
                    break;
                case "Documents":
                    localSet = DocumentsF;
                    break;
                case "Videos":
                    localSet = VideosF;
                    break;
                case "Pictures":
                    localSet = PicturesF;
                    break;
                case "Downloads":
                    localSet = DownloadsF;
                    break;

            }

            if (c.isSelected()) {

                for (String s : localSet) {
                    if (!masterSet.contains(s)) {
                        masterSet.add(s);
                    }
                }

                //screening according to categories
                if (!categories.equals("others")) {

                    for (int i = 0; i < DbModel.tname.length; i++) {
                        if (DbModel.tname[i].equals(categories)) {
                            for (String s : localSet) {

                                String extension = "";
                                int j = s.lastIndexOf('.');
                                if (j > 0) {
                                    extension = s.substring(j + 1);
                                }

                                for (String e : DbModel.tExt[i]) {
                                    if (e.compareToIgnoreCase(extension) == 0) {
                                        masterScreen.add(s);
                                        //break can be added to improve efficiency
                                    }

                                }
                            }

                        }
                    }
                } else {

                    for (String s : localSet) {

                        String extension = "";
                        int j = s.lastIndexOf('.');
                        if (j > 0) {
                            extension = s.substring(j + 1);
                        }

                        boolean oFlag = true;

                        label:
                        for (int i = 0; i < DbModel.tname.length; i++) {

                            for (String e : DbModel.tExt[i]) {
                                if (e.compareToIgnoreCase(extension) == 0) {

                                    oFlag = false;
                                    break label;
                                }

                            }

                        }
                        if (oFlag) {
                            masterScreen.add(s);
                        }
                    }

                }

                //               setR.removeAll(masterScreen);
                //set masterset to lv
                prop.set(FXCollections.observableArrayList(masterScreen));

            } else {

                masterScreen.removeAll(localSet);
                masterSet.removeAll(localSet);

//                for (String s : localSet) {
//                  if (!setR.contains(s)) {
//                        setR.add(s);
//                    }
//                }
                if (masterSet.isEmpty()) {

//                    setR.clear();
//                    setR.addAll(origList);
                    prop.set(FXCollections.observableArrayList(origList));
                } else {

                    prop.set(FXCollections.observableArrayList(masterScreen));
                }

            }

        }

    }

    public class Clas4CArr implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            for (CheckBox c : cArr) {
                if (event.getSource() == c) {

                    if (borderPane.getCenter() == accordion) {
                        performFilteringCarr(c, DbModel.audio,
                                listPropertyAud, "audio", masterScreenAud);
                        performFilteringCarr(c, DbModel.image,
                                listPropertyImg, "image", masterScreenImg);
                        performFilteringCarr(c, DbModel.video,
                                listPropertyVid, "video", masterScreenVid);
                        performFilteringCarr(c, DbModel.document,
                                listPropertyDoc, "document", masterScreenDoc);
                        performFilteringCarr(c, DbModel.executables,
                                listPropertyExec, "executables", masterScreenExec);
                        performFilteringCarr(c, DbModel.others,
                                listPropertyOthers, "others", masterScreenOthers);

                    }

                }
            }
        }

        public void performFilteringCarr(CheckBox c,
                Collection<String> origList,
                ListProperty<String> prop,
                String categories, ArrayList<String> masterScreen) {

            ArrayList<String> localSet = null;

            for (DbModel.CaptionF x : DbModel.oCaptionF) {
                if (x.root.equals(c.getText().substring(0, 2))) {
                    localSet = x.filenames;
//                    System.out.println("Localset "+x.filenames);
                }

            }

            if (c.isSelected()) {

                for (String s : localSet) {
                    if (!masterSet.contains(s)) {
                        masterSet.add(s);
                    }
                }

                //screening according to categories
                if (!categories.equals("others")) {

                    for (int i = 0; i < DbModel.tname.length; i++) {
                        if (DbModel.tname[i].equals(categories)) {
                            for (String s : localSet) {

                                String extension = "";
                                int j = s.lastIndexOf('.');
                                if (j > 0) {
                                    extension = s.substring(j + 1);
                                }

                                for (String e : DbModel.tExt[i]) {
                                    if (e.compareToIgnoreCase(extension) == 0) {
                                        masterScreen.add(s);
                                        //break can be added to improve efficiency
                                    }

                                }
                            }

                        }
                    }
                } else {
                    // for (int i = 0; i < DbModel.tname.length; i++) {

                    for (String s : localSet) {

                        String extension = "";
                        int j = s.lastIndexOf('.');
                        if (j > 0) {
                            extension = s.substring(j + 1);
                        }

                        boolean oFlag = true;

                        label:
                        for (int i = 0; i < DbModel.tname.length; i++) {

                            for (String e : DbModel.tExt[i]) {
                                if (e.compareToIgnoreCase(extension) == 0) {

                                    oFlag = false;
                                    break label;
                                }

                            }

                        }
                        if (oFlag) {
                            masterScreen.add(s);
                        }
                    }

                }

                //               setR.removeAll(masterScreen);
                //set masterscreen to lv
                prop.set(FXCollections.observableArrayList(masterScreen));

            } else {

                masterScreen.removeAll(localSet);
                masterSet.removeAll(localSet);

//                for (String s : localSet) {
//                    if (!setR.contains(s)) {
//                        setR.add(s);
//                    }
//                }
                if (masterSet.isEmpty()) {

//                    setR.clear();
//                    setR.addAll(origList);
                    prop.set(FXCollections.observableArrayList(origList));
                } else {

                    prop.set(FXCollections.observableArrayList(masterScreen));
                }

            }

        }

    }

    @FXML
    void openpdfRenaming() {

        //Vague file listing
        Stage pdfRenaming = new Stage();
        pdfRenaming.initModality(Modality.APPLICATION_MODAL);

        //Wait dialog
        Stage pdfRenameWaitDialog = new Stage();
        pdfRenameWaitDialog.setResizable(false);
        pdfRenameWaitDialog.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("pdfRenameWaitDialog.fxml"));

        VBox pdfRenameWaitDialogRoot = null;
        try {
            // System.out.println("Before set root");
            pdfRenameWaitDialogRoot = loader.load();
            //  System.out.println("ID:  "+pdfRenameWaitDialogRoot.getId());
            // System.out.println("After set root");
        } catch (IOException ex) {
            Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        pdfRenameWaitDialog.setScene(new Scene(pdfRenameWaitDialogRoot));

//        System.out.println("Before pdfRenameWaitDialog show()");
        pdfRenameWaitDialog.show();
//        System.out.println("After pdfRenameWaitDialog show()");

//        VagueFileDiscovery vfd = new VagueFileDiscovery("vfd");
//        thread = vfd.t;
        success = true;
        //init the thread
        task = new VagueFileDiscovery();

        //init the thread
        thread = new Thread(task);
        thread.start();

        //On Clicking X 
        pdfRenameWaitDialog.setOnCloseRequest(e
                -> {
                    task.cancel();
                    pdfRenameWaitDialog.close();

                }
        );

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {
                if (success) {
                    // System.out.println("pdfRenaming.show();");

                    VBox pdfRenamingRoot = null;
                    try {
                        pdfRenamingRoot = FXMLLoader.load(getClass().getResource("pdfRenaming.fxml"));
                    } catch (IOException ex) {
                        Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    pdfRenaming.setScene(new Scene(pdfRenamingRoot));
                    pdfRenaming.setOnCloseRequest(e ->{
                        VagueFileDiscovery.vfileNames.clear();
                        VagueFileDiscovery.valPaths.clear();
                    });
                    pdfRenaming.show();
                    pdfRenameWaitDialog.close();
                }

            }

        });

    }

    @FXML
    void wHandler() {

        SearchPopupController.sPopContLabel = "Watched";
        ObservableList<String> tempList = FXCollections.observableArrayList();

        for (String s : DbModel.video) {
            //String sPath = DbModel.video.get(s).get(0);
            String sPath = DbModel.returnPaths(s, "video").get(0);

            DosFileAttributes attr = null;

            try {
                attr = Files.readAttributes(Paths.get(sPath), DosFileAttributes.class);
            } catch (IOException ex) {
                Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
            }

            String cS = attr.creationTime().toString(), aS = attr.lastAccessTime().toString();

            Date createDate = new Date(Integer.parseInt(cS.substring(0, 4)),
                    Integer.parseInt(cS.substring(5, 7)),
                    Integer.parseInt(cS.substring(8, 10)),
                    Integer.parseInt(cS.substring(11, 13)),
                    Integer.parseInt(cS.substring(14, 16)));

            Date accessDate = new Date(Integer.parseInt(aS.substring(0, 4)),
                    Integer.parseInt(aS.substring(5, 7)),
                    Integer.parseInt(aS.substring(8, 10)),
                    Integer.parseInt(aS.substring(11, 13)),
                    Integer.parseInt(aS.substring(14, 16)));

            if (accessDate.after(createDate)) {
                tempList.add(s);
            }
            System.out.println(s);

            System.out.print("Creation Time: " + cS);

            System.out.println();

            System.out.print("Acces Time: " + aS);

            System.out.println();
            System.out.println();
        }

        SearchPopupController.sPopContSearchList = FXCollections.observableArrayList(tempList);

        try {
            rootSearch = (BorderPane) FXMLLoader.load(getClass().getResource("searchPopup.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Stage searchPopup = new Stage();
        searchPopup.initModality(Modality.APPLICATION_MODAL);
        searchPopup.setOnCloseRequest(ee -> sPopContSearchList.clear());
        Scene scene = new Scene(rootSearch);
        searchPopup.setScene(scene);
        searchPopup.show();
    }

    @FXML
    void nwHandler() {

        SearchPopupController.sPopContLabel = "Not Watched";
        ObservableList<String> tempList = FXCollections.observableArrayList();

        for (String s : DbModel.video) {
          //  String sPath = DbModel.video.get(s).get(0);
            String sPath = DbModel.returnPaths(s, "video").get(0);
            DosFileAttributes attr = null;

            try {
                attr = Files.readAttributes(Paths.get(sPath), DosFileAttributes.class);
            } catch (IOException ex) {
                Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
            }

            String cS = attr.creationTime().toString(), aS = attr.lastAccessTime().toString();

            Date createDate = new Date(Integer.parseInt(cS.substring(0, 4)),
                    Integer.parseInt(cS.substring(5, 7)),
                    Integer.parseInt(cS.substring(8, 10)),
                    Integer.parseInt(cS.substring(11, 13)),
                    Integer.parseInt(cS.substring(14, 16)));

            Date accessDate = new Date(Integer.parseInt(aS.substring(0, 4)),
                    Integer.parseInt(aS.substring(5, 7)),
                    Integer.parseInt(aS.substring(8, 10)),
                    Integer.parseInt(aS.substring(11, 13)),
                    Integer.parseInt(aS.substring(14, 16)));

            if (accessDate.equals(createDate)) {
                tempList.add(s);
            }
             
            System.out.println(s);

            System.out.print("Creation Time: " + cS);

            System.out.println();

            System.out.print("Acces Time: " + aS);

            System.out.println();
            System.out.println();
           
        }

        SearchPopupController.sPopContSearchList = FXCollections.observableArrayList(tempList);

        try {
            rootSearch = (BorderPane) FXMLLoader.load(getClass().getResource("searchPopup.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Stage searchPopup = new Stage();
        searchPopup.initModality(Modality.APPLICATION_MODAL);
        searchPopup.setOnCloseRequest(ee -> sPopContSearchList.clear());
        Scene scene = new Scene(rootSearch);
        searchPopup.setScene(scene);
        searchPopup.show();
    }

    @FXML
    void dupHandler() {
        SearchPopupController.sPopContLabel = "Duplicate";
        ObservableList<String> tempList = FXCollections.observableArrayList();
        LinkedHashSet<String> set = new LinkedHashSet<String>();

        set.addAll(DbModel.audio);
        set.addAll(DbModel.document);
        set.addAll(DbModel.executables);
        set.addAll(DbModel.image);
        set.addAll(DbModel.others);
        set.addAll(DbModel.video);

        for (String s : set) {
            //ArrayList<String> al = set.get(s);
            ArrayList<String> al = DbModel.returnPaths(s);

            if (al.size() > 1) {
                tempList.add(s);
            }
        }

        SearchPopupController.sPopContSearchList = FXCollections.observableArrayList(tempList);

        try {
            rootSearch = (BorderPane) FXMLLoader.load(getClass().getResource("searchPopup.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Stage searchPopup = new Stage();
        searchPopup.initModality(Modality.APPLICATION_MODAL);
        searchPopup.setOnCloseRequest(ee -> sPopContSearchList.clear());
        Scene scene = new Scene(rootSearch);
        searchPopup.setScene(scene);
        searchPopup.show();

    }

    @FXML
    void starHandle() {
        openInSearchPopup("STAR");
    }

    @FXML
    void backupHandle() {
        openInSearchPopup("BACKUP");
    }

    private void openInSearchPopup(String tag) {

        SearchPopupController.sPopContLabel = tag;

        ObservableList<String> tempList = FXCollections.observableArrayList();
        String tb[] = {"AUDIO", "DOCUMENT", "VIDEO", "IMAGE", "EXECUTABLES", "OTHERS"};

        for (String tableName : tb) {

            try {

                PreparedStatement statement = DbModel.conection.prepareStatement("select FNAME "
                        + "from " + tableName + " where TAG = ?");
                statement.setString(1, tag);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    tempList.add(resultSet.getString("FNAME"));
                }

            } catch (SQLException ex) {
                Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        SearchPopupController.sPopContSearchList = FXCollections.observableArrayList(tempList);

        try {
            rootSearch = (BorderPane) FXMLLoader.load(getClass().getResource("searchPopup.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Stage searchPopup = new Stage();
        searchPopup.initModality(Modality.APPLICATION_MODAL);
        searchPopup.setOnCloseRequest(ee -> sPopContSearchList.clear());
        Scene scene = new Scene(rootSearch);
        searchPopup.setScene(scene);
        searchPopup.show();

    }

    @FXML
    void modifyExt() {
                
        BorderPane root = null;
        try {
            root = (BorderPane) FXMLLoader.load(getClass().getResource("extModify.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(CategoriesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Stage extModWindow = new Stage();
        extModWindow.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        extModWindow.setScene(scene);
        extModWindow.setResizable(false);
        extModWindow.show();
    }

}
