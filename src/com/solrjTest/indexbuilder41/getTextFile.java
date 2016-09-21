/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import indexbuilder41.utils.FileRetriever;
import java.io.*;

/**
 *
 * @author murphy
 */
public class getTextFile implements iGetText 
{
    public String getText(String keid, String path) throws Exception {
        StringBuilder sb = new StringBuilder();
        InputStream fr = FileRetriever.getFile(path);
        DataInputStream ds = new DataInputStream(fr);
        BufferedReader br = new BufferedReader(new InputStreamReader(ds));
        
        String mx1 = br.readLine();
        while(mx1 != null){
            sb.append(mx1);
            sb.append("\r\n");
            mx1 = br.readLine();
        }
        br.close();
        ds.close();
        fr.close();;
        return sb.toString().toLowerCase();
    }
    
}
