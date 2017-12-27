package pdfreadtest;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aravind Sharma
 */
public class PDfTitleExtract {

    static int count;
    static ArrayList<CharInput> charInputs;

    /**
     * @param path
     * @return
     */
    //return string[] or arraylist
    public static ArrayList<String> ExtractTitle(String path) {
        //path = "D:\\AASS\\PDF\\Research papers\\tested ok\\bitcoin.pdf";

        count = 0;
        charInputs = new ArrayList<>();

        PdfReader reader = null;
        try {
            reader = new PdfReader(path);
            CharSizeExtraction charSizeExtObj = new CharSizeExtraction();
            PdfTextExtractor.getTextFromPage(reader, 1, (TextExtractionStrategy) charSizeExtObj);
        } catch (IOException ex) {
            Logger.getLogger(PDfTitleExtract.class.getName()).log(Level.SEVERE, null, ex);
        }
        // reader.selectPages("4");

        System.out.println("done");
        reader.close();

        Comparator<Integer> descOrd
                = (Integer e1, Integer e2) -> e2.compareTo(e1);

        //set of fonts (among extracted characters) in desc order
        SortedSet<Integer> setOfSizes = new TreeSet<>(descOrd);

        for (CharInput c : charInputs) {
            setOfSizes.add(c.fSize);
        }

     //   System.out.println("\nsos" + setOfSizes);
        StringBuilder sb = new StringBuilder();

        // size ->[arraylist of words belonging to that size]
        TreeMap<Integer, ArrayList<String>> map = new TreeMap<>(descOrd);

        int tcount = 0;
        boolean endflag = false;
        ArrayList<String> tAl = null;
        CharInput c;
        int m;

        //Obtain Map
        for (Integer tempSize : setOfSizes) {

            //for every size in setOfSizes loop through all characters
            for (m = 0, endflag = false; m < charInputs.size(); m++) {
                c = charInputs.get(m);

                if (c.fSize.intValue() == tempSize.intValue()) {
                    //characters that belong to tempsize
                    if (tcount < 100) {

                        //no of character sequence appended to sb restriced to 100 characters
                        sb.append(c.character);

                        tcount++;
                    }
                    endflag = true;
                } else {
                    //if a sequence (of characters that belong to tempsize) ends 
                    if (endflag) {

                        if (map.containsKey(tempSize)) {
                            tAl = map.get(tempSize);
                            tAl.add(sb.toString());
                            map.put(tempSize, tAl);

                        } else {
                            tAl = new ArrayList<>();
                            tAl.add(sb.toString());

                            map.put(tempSize, tAl);
                        }

                        endflag = false;
                        //empty sb to make room for new sequence of characters
                        sb.delete(0, sb.length());
                        tcount = 0;
                    }
                }
            }
        }

        //       System.out.println("MOP" + map);
        int ctitles = 0;

        final int maxSuggestion = 6;

        //title that map to particular size(temporary variable)
        ArrayList<String> tempTitles = new ArrayList();

        //arraylist of titles based on maxSuggestion
        ArrayList<String> titles = new ArrayList();

        //Get maxSuggestion titles from keyset
        for (int k : map.keySet()) {

            tempTitles = map.get(k);
            titles.addAll(tempTitles);

            ctitles += tempTitles.size();       //ctitles is essentially titles.size()

            if (ctitles > maxSuggestion) {
                break;
            }

        }
        System.out.println("titles.size: " + titles.size());
//        System.out.println("titles: " + titles);

        //Array of titles
        ArrayList<String> cutTitles = new ArrayList<>();

        //write code for situations where titles are less than 3
        for (int i = 0; i < Math.min(maxSuggestion, titles.size()); i++) {

            // System.out.println("i value" + i);
            cutTitles.add(titles.get(i));
        }
        
        count = 0;
        charInputs = new ArrayList<>();

        return cutTitles;
    }

}
