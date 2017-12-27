package application;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
public class StartPageController implements Initializable {

    @FXML
    Text good;
    @FXML
    Text greeting;
    @FXML
    Text nam;
    @FXML
    VBox topVBox;

    static public boolean scan = true;
    @FXML
    private Label label;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        //SET NAME
        nam.setText(System.getProperty("user.name"));
        
        //DETERMINE GREETING MESSAGE 
        Date date = new Date();
        SimpleDateFormat simpDate;

        //simpDate = new SimpleDateFormat("kk:mm:ss");
        simpDate = new SimpleDateFormat("kk");

        //System.out.println(simpDate.format(date));
        int time = Integer.parseInt(simpDate.format(date));

        String msg;
        if (time >= 17) {
            msg = "EVENING";
        } else if (time >= 12) {
            msg = "AFTERNOON";
        } else {
            msg = "MORNING";
        }
        
        greeting.setText(msg);
        
        //GOOD MORNING MESAGE STYLE
        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);

        DropShadow ds = new DropShadow();
        ds.setColor(Color.rgb(255, 51, 0, 0.3));
        ds.setOffsetX(5);
        ds.setOffsetY(5);
        ds.setRadius(5);
        ds.setSpread(0.2);

        blend.setBottomInput(ds);

        DropShadow ds1 = new DropShadow();
        ds1.setColor(Color.web("#FF3300"));
        ds1.setRadius(20);
        ds1.setSpread(0.2);

        Blend blend2 = new Blend();
        blend2.setMode(BlendMode.MULTIPLY);

        InnerShadow is = new InnerShadow();
        is.setColor(Color.web("#FFFF00"));
        is.setRadius(9);
        is.setChoke(0.8);
        blend2.setBottomInput(is);

        InnerShadow is1 = new InnerShadow();
        //       is1.setColor(Color.web("#f13a00"));
        is1.setColor(Color.web("#FF3300"));
        is1.setRadius(5);
        is1.setChoke(0.4);
        blend2.setTopInput(is1);

        Blend blend1 = new Blend();
        blend1.setMode(BlendMode.MULTIPLY);
        blend1.setBottomInput(ds1);
        blend1.setTopInput(blend2);

        blend.setTopInput(blend1);

        good.setEffect(blend);
        greeting.setEffect(blend);
        nam.setEffect(blend);

        
    }

    @FXML
    void setScan() {
        scan = true;
        closeOpen();

    }

    @FXML
    void unsetScan() {
        scan = false;
        closeOpen();
    }

    void closeOpen() {
        //close the startPage
        Stage stage = (Stage) topVBox.getScene().getWindow();
        stage.close();

        //open the startScan
        VBox vBox = null;
        try {
            vBox = FXMLLoader.load(getClass().getResource("startScan.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(StartPageController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Stage startScan = new Stage();
        startScan.setResizable(false);
        
        startScan.setOnCloseRequest(e -> {
            try {
                System.out.println("Closing Connection");
                DbModel.conection.close();
            } catch (SQLException ex) {
                Logger.getLogger(StartPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Scene scene = new Scene(vBox);
        startScan.setScene(scene);
        startScan.show();
    }

}
