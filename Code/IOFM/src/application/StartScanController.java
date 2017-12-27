package application;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author ARAVIND
 */
public class StartScanController implements Initializable {

    @FXML
    Label label;
    
    @FXML
    VBox topVBox;
    
    public static Stage window;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        
        Task<Boolean> task;

        if (StartPageController.scan) {
            //scan
            label.setText("SCANNING FILESYSTEM");
            task = new ScanFilesystem();
        } else {
            //from database
            label.setText("OBTAINING FILES");
            task = new DontScan();
        }
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
        
         
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent event) {

                //close scan indicatorWindow
                Stage olDstage = (Stage)topVBox.getScene().getWindow();
                olDstage.close();
                
                //open categories window
                window = new Stage();
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("categoriesFxml.fxml"));
                } catch (IOException ex) {
                    Logger.getLogger(StartScanController.class.getName()).log(Level.SEVERE, null, ex);
                }

                Scene scene = new Scene(root);
                window.setScene(scene);
                window.show();
            }

        });
    }

}
