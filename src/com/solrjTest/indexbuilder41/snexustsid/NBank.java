/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.snexustsid;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author murphy
 */
public class NBank {
    public String name;
    public int code;
    public HashSet<NGroup> items;
    public NBank(){
        items = new HashSet<>();
        
    }
    public void add(String code, String groupName){
        NGroup group = new NGroup();
        group.code = code;
        group.name = groupName;
        items.add(group);
    }
}
