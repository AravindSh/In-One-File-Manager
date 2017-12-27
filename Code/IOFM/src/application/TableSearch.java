package application;

/**
 *
 * @author Aashish R.K, Aravind Sharma, Sanjeet Gaglani and Shashwath H.A
 */
 public class TableSearch {
    
    private String fname;
    private String extension;
    
    public TableSearch(){
        this.fname = "";
        this.extension = "";
        
    }

    public TableSearch(String fname, String extension) {
        this.fname = fname;
        this.extension = extension;
    }

    public String getFname() {
        return fname;
    }

    public String getExtension() {
        return extension;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
 

    
}
