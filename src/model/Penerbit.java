/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

public class Penerbit {
    private int id;
    private String penerbit;
    private String keyword;

    public Penerbit(int id, String penerbit) {
        this.id = id;
        this.penerbit = penerbit;
    }

    public Penerbit() {
        
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setPenerbit(String penerbit) {
        this.penerbit = penerbit;
    }

    
    public int getId() {
        return id;
    }

    public String getPenerbit() {
        return penerbit;
    }
    
    
    }

