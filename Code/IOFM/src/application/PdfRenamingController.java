package application;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import pdfreadtest.PDfTitleExtract;
/**
 * FXML Controller class
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
public class PdfRenamingController implements Initializable {

    @FXML
    ListView fileNamesLv;

    //value from combobox
    String newFName = " ";

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //filenames
   
//Previous Steps in VagueFileDiscovery        
//Step4:Open PDF renamind Dialog
        System.out.println("Step4:Open PDF renamind Dialog");
        
        fileNamesLv.setItems(VagueFileDiscovery.vfileNames);

        ArrayList<String> vPaths = VagueFileDiscovery.valPaths;
        
        fileNamesLv.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {

                //Stage Order: renameDialog ->messageDialog
                int lvIndex = fileNamesLv.getSelectionModel().getSelectedIndex();
               // System.out.println(lvIndex);

                try {
                    Stage renameDialog = new Stage();
                    renameDialog.initModality(Modality.APPLICATION_MODAL);
                    renameDialog.setResizable(false);
                    VBox renameDialogRoot = FXMLLoader.load(getClass().getResource("renameDialog.fxml"));
                    renameDialog.setScene(new Scene(renameDialogRoot));
                    renameDialog.setResizable(false);

                    //Set Path to label
                    Label lb = (Label) renameDialogRoot.getChildren().get(1);
                    lb.setText(vPaths.get(lvIndex));

                    //set Label used for displaying unsuccessful message
                    Label msgLabel = (Label) renameDialogRoot.getChildren().get(4);

                    //retrieve combobox
                    ComboBox<String> coB = (ComboBox<String>) renameDialogRoot.getChildren().get(2);
                    coB.setOnAction(e -> {
                        newFName = coB.getSelectionModel().getSelectedItem();

                       // System.out.println("combobox handler newFname" + newFName);
                    });

                    //setting dummy items
                    coB.getItems().addAll(PDfTitleExtract.ExtractTitle(vPaths.get(lvIndex)));
                    //coB.getItems().addAll("Got Screwed","Crab");

                    HBox hb = (HBox) renameDialogRoot.getChildren().get(3);
                    Button okB = (Button) hb.getChildren().get(0);
                    Button cancelB = (Button) hb.getChildren().get(1);

                    cancelB.setOnAction(e -> renameDialog.close());
                    okB.setOnAction(e -> {

                        //Get Name from combobox
                        //         String newFName = coB.getValue();
                      //  System.out.println("ok button handler newFname" + newFName);

                        //pattern for files starting with whitespaces
                        Pattern pattern = Pattern.compile("(^\\s+)(.*)");
                        Matcher matcher = pattern.matcher(newFName);

                        boolean check = newFName == null || newFName.compareTo("") == 0 || matcher.matches()
                                || newFName.contains("/") || newFName.contains("\\") || newFName.contains(":")
                                || newFName.contains("*") || newFName.contains("?") || newFName.contains("\"")
                                || newFName.contains("<") || newFName.contains(">") || newFName.contains("|");

                        if (check) {
                            //User enters nothing
                            msgLabel.setText("Please Give A Valid Name");
                          //  System.out.println("heloooooooooooo");

                        } else {
                            
                            Path oldPath = Paths.get(vPaths.get(lvIndex));

                            Path newPath = oldPath.resolveSibling(newFName + ".pdf");
                            
                           // System.out.println("newPath  " + newPath);

                            try {
                                
                                Files.move(oldPath,newPath);
//                              
                                //System.out.println("\n\noldName.resolveSibling(newPath): "+newPath.toString());

                                Stage messageDialog = new Stage();
                                messageDialog.setResizable(false);
                                messageDialog.initModality(Modality.APPLICATION_MODAL);
                                VBox messageDialogRoot = null;
                                try {
                                    messageDialogRoot = FXMLLoader.load(getClass().getResource("messageDialog.fxml"));
                                } catch (IOException ex) {
                                    Logger.getLogger(PdfRenamingController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                messageDialog.setScene(new Scene(messageDialogRoot));

                                //System.out.println("renamed");
                                HBox tH = (HBox) messageDialogRoot.getChildren().get(1);
                                Button okWindowB = (Button) tH.getChildren().get(0);

                                //show renamed file in explorer
                                okWindowB.setOnAction(ee -> {
                                   
                                        new ExplorerSelect().openInExplorer(newPath.toString());
                                        messageDialog.close();
                                        renameDialog.close();
                                   
                                });

                                messageDialog.show();

                            } catch (IOException ex) {
                                Logger.getLogger(PdfRenamingController.class.getName()).log(Level.SEVERE, null, ex);
                                System.out.println("Error");
                                //renaming unsuccessful
                                msgLabel.setText("RENAMING UNSUCCESSFUL");
                            }


                        }
                    });

                    renameDialog.show();

                } catch (IOException ex) {
                    Logger.getLogger(PdfRenamingController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

    }

}
