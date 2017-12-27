/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdfreadtest;

/**
 *
 * @author Aravind Sharma
 */
public class CharInput {
    Character character;    //characters in pdf file
    Integer fSize;          //font size of respective character

    public CharInput(Character text, int fSize) {
        this.character = text;
        this.fSize = fSize;
    }
    
    
    
}
