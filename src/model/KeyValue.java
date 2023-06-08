/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author acer
 */
public class KeyValue {
    int key;
    String value;

    @Override
    public String toString(){
        return value;
    }
    
    public KeyValue(int key, String value){
        this.key = key;
        this.value = value;
    }
    
    public KeyValue(String value){
        this.key = 0;
        this.value = value;
    }
    
    public KeyValue(){
        key = 0;
        value = "";
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
