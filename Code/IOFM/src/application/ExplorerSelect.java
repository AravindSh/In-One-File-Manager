package application;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aashish
 */
public class ExplorerSelect {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    void openInExplorer(String onlyPath) {
        
        
        String path = "/select,"+onlyPath;
        
        LinkedList<String> list= new LinkedList<>();
        StringBuilder sb=new StringBuilder();
        boolean jack = true;
        
        for(int i=0 ; i<path.length() ; i++)
        {
            if( i == 0)
            {
                sb.append(path.charAt(i));
                continue;
            }
            
            if( path.charAt(i) == ' '  && jack )
            {
                list.add( sb.toString() );
                sb.setLength(0);
                jack=false;
                continue;
            }
            
            if( !jack &&  path.charAt(i) != ' ')
            {
                jack = true;
            }
            
            sb.append(path.charAt(i));
        }
        
        list.add( sb.toString() );
                
        list.addFirst("explorer.exe");
        
   /*     for(String s:list)
        {
            System.out.println("list "+s);
        }
  */            
        try {
            Process p = new ProcessBuilder(list).start();
        } catch (IOException ex) {
            Logger.getLogger(ExplorerSelect.class.getName()).log(Level.SEVERE, null, ex);
        }
             
    }
    
}
