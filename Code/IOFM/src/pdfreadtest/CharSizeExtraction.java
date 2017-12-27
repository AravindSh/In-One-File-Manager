package pdfreadtest;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

/**
 *
 * @author Aravind Sharma
 */
public class CharSizeExtraction implements TextExtractionStrategy {

    private String text;

    @Override
    public void beginTextBlock() {
    }

    @Override
    public void renderText(TextRenderInfo renderInfo) {
        text = renderInfo.getText();
        
        //find font size
        Vector curBaseline = renderInfo.getBaseline().getStartPoint();
        Vector topRight = renderInfo.getAscentLine().getEndPoint();

        Rectangle rect = new Rectangle(curBaseline.get(0), curBaseline.get(1), topRight.get(0), topRight.get(1));
        int curFontSize = (int) rect.getHeight();
        
        //Split text into characters
        char[] textArr=new char[text.length()];
        textArr = text.toCharArray();
        
        
        //Add to the collection of CharInput class
        for(char c:textArr)
        {
        PDfTitleExtract.charInputs.add(new CharInput(c,curFontSize));
      //  System.out.print(c+" ");
        }
        
        
     //   System.out.println("size: "+curFontSize);
        PDfTitleExtract.count++;        
        
    }

    @Override
    public void endTextBlock() {
    }

    @Override
    public void renderImage(ImageRenderInfo renderInfo) {
    }

    @Override
    public String getResultantText() {
        return text;
    }
}
