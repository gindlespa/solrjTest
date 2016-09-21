/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.snexustsid;

import java.util.ArrayList;

/**
 *
 * @author murphy
 */
public class SNGroup {
    public String name;
    public String code;
    public ArrayList<SNItem> items;
    public SNGroup(){
        items = new ArrayList<>();
        
    }
    public void add(String code, String term){
        SNItem item = new SNItem();
        item.code = code;
        item.term = term;
    }
}
