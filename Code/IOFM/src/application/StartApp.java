package application;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
public class StartApp extends Application {

    public static DbModel demodbModel;
    public static Finder finder;

    public static Stage backgCreatDel;

    @Override
    public void start(Stage stage) {
        
        //Background Create Delete progress indicator
        backgCreatDel = new Stage();
        backgCreatDel.initModality(Modality.APPLICATION_MODAL);
        backgCreatDel.setResizable(false);
        VBox vBox = null;
        try {
            vBox = FXMLLoader.load(getClass().getResource("backgCreatDel.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(WatchDir.class.getName()).log(Level.SEVERE, null, ex);
        }

        backgCreatDel.setScene(new Scene(vBox));
        
        //Start Page
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("startPage.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(StartApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     *
     */
    public static void main(String[] args) {

        Finder.keys = new HashMap<>();
        try {
            Finder.watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(StartApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        IdentifyPartition idParObj = new IdentifyPartition();
        idParObj.identifyPartitionFunc();

       // String pattern;
        //pattern = "*.*";
        finder = new Finder();

        demodbModel = new DbModel();
        demodbModel.myScan();

        launch(args);
    }

}
