/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Tests;

import indexbuilder41.snexustsid.SNFactory;
import indexbuilder41.snexustsid.SNGroup;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author murphy
 */
public class buildSNexus {
    public static void main(String[] args) throws Exception{
        HashMap<Integer,SNGroup> gl = SNFactory.getSNGroupList();
        Set<Integer> s = gl.keySet();
        Iterator<Integer> iter = s.iterator();
        while(iter.hasNext()){
            int i = iter.next();
            SNGroup gr = gl.get(i);
            String p = "/home/murphy/sample/" + gr.name + ".msq";
            FileOutputStream fos = new FileOutputStream(new File(p));
            PrintWriter pw = new PrintWriter(fos);
            BufferedWriter br = new BufferedWriter(pw);
            br.write("xxList1");
            br.write("\r\n");
            br.write(gr.code);
            br.write("\r\n");
            br.write("xxList2");
            br.write("\r\n");
            System.out.println(gr.name);
            br.close();
            pw.close();
            fos.close();
        }
    }
}
