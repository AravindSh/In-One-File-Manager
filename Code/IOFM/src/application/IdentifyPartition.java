package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
public class IdentifyPartition {

    public static ArrayList<String> caption = new ArrayList<>();   //volume caption
    public static ArrayList<Boolean> ext = new ArrayList<>();      //entry corresponding to caption is true if external drive

    void identifyPartitionFunc() {

        String line;

        Process p = null;
        try {
            p = Runtime.getRuntime().exec("wmic logicaldisk get description,volumedirty,Caption");
        } catch (IOException ex) {
            Logger.getLogger(IdentifyPartition.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(IdentifyPartition.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
        );

        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    //   System.out.println(line +":::"+ line.charAt(9));
                    if (line.startsWith("Caption")) {
                        continue;
                    }

                    if (line.substring(9, 11).compareToIgnoreCase("CD") == 0) {
                        continue;
                    }

                    boolean extFlag = line.contains("TRUE") || line.contains("FALSE");
                    if (!extFlag) {
                        caption.add(line.substring(0, 2));
                        //    System.out.println(line.substring(0, 2));

                        ext.add(extFlag);
                    }
                    //   else                     
                    //        ext.add(false);

                    //    System.out.println(line.substring(0, 2) + "  " + (line.contains("TRUE") || line.contains("FALSE")));
                }
//                System.out.println("\n\nCaptions"+caption);
            }

               System.out.println(caption);
                System.out.println(ext);
        } catch (IOException ex) {
            Logger.getLogger(IdentifyPartition.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
