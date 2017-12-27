package application;

import static application.StartApp.finder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author ARAVIND
 */
public class ScanFilesystem extends Task<Boolean> {

    @Override
    protected Boolean call() {

        //drop tables and vacuum
        String[] tableNames = {"AUDIO", "DOCUMENT", "VIDEO", "IMAGE", "EXECUTABLES", "OTHERS", "PATHS", "SQLITE_SEQUENCE"};

        for (String tb : tableNames) {
            try {
                PreparedStatement preparedStatement = null;

                String query = "delete from " + tb;
                preparedStatement = DbModel.conection.prepareStatement(query);

                preparedStatement.executeUpdate();

                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(ScanFilesystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //VACUUM
        try {
            PreparedStatement preparedStatement = null;

            String query = "VACUUM";
            preparedStatement = DbModel.conection.prepareStatement(query);

            preparedStatement.executeUpdate();

            preparedStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(ScanFilesystem.class.getName()).log(Level.SEVERE, null, ex);
        }

        // DbModel db = finder.getter();
        Path startingDir;
        Path startingDir1 = Paths.get(System.getProperty("user.home") + "\\Desktop");
        Path startingDir2 = Paths.get(System.getProperty("user.home") + "\\Documents");
        Path startingDir3 = Paths.get(System.getProperty("user.home") + "\\Music");
        Path startingDir4 = Paths.get(System.getProperty("user.home") + "\\Videos");
        Path startingDir5 = Paths.get(System.getProperty("user.home") + "\\Downloads");
        Path startingDir6 = Paths.get(System.getProperty("user.home") + "\\Pictures");

        double timeNow = System.currentTimeMillis();

       /* try {
            Files.walkFileTree(Paths.get("G:\\"), finder);
        } catch (IOException ex) {
            Logger.getLogger(ScanFilesystem.class.getName()).log(Level.SEVERE, null, ex);
        }*/

        for (int i = 0; i < IdentifyPartition.ext.size(); i++) {

            if (IdentifyPartition.caption.get(i).compareToIgnoreCase("C:") == 0) {

                try {
                   Files.walkFileTree(startingDir1, finder);
        //            Files.walkFileTree(startingDir2, finder);
          //          Files.walkFileTree(startingDir3, finder);
            //        Files.walkFileTree(startingDir4, finder);
              //      Files.walkFileTree(startingDir5, finder);
                //    Files.walkFileTree(startingDir6, finder);
                } catch (IOException ex) {
                    Logger.getLogger(ScanFilesystem.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                //                startingDir = Paths.get(IdentifyPartition.caption.get(i) + "\\");
                //              Files.walkFileTree(startingDir, finder);

            }
        }

        double timetaken = (((double) (System.currentTimeMillis() - timeNow)) / (1000 * 60));
        // System.out.println("CAME");
        new WatchDir();

        System.out.println("Time taken for scanning in minutes: " + timetaken);
        System.out.println("No of files scanned: " + finder.numMatches);
        
        
        
        
        return true;

    }

}
