/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

/**
 *
 * @author ARAVIND
 */
public class TableExtensions {    
   
    
    private String ext;
    private String desc;
    
    public TableExtensions(){
        this.ext = "";
        this.desc = "";
        
    }

    public TableExtensions(String ext, String desc) {
        this.ext = ext;
        this.desc = desc;
    }

 
    public String getExt() {
        return ext;
    }

    public String getDesc() {
        return desc;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }   


    
}
