package application;

import static application.CategoriesController.task;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ARAVIND
 */
public class PdfRenameWaitDialogController implements Initializable {

    @FXML
    Button cancelB;
    @FXML
    VBox vbox;

    Stage pdfRenameWaitDialog, pdfRenaming;
    static Thread thread;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       // System.out.println("Inside PdfRenameWaitDialogController initialize()");

     
    }

    @FXML
    void handleCancelB() {

        task.cancel();
        ( (Stage)vbox.getScene().getWindow() ).close();
    }

    
}
