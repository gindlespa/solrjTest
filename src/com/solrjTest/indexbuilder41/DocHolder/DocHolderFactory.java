/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.DocHolder;


import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author murphy
 */
public class DocHolderFactory {
    private static DocHolder holder = null;
    public static PDDocument getDoc(String path) throws IOException{
        if(holder==null)
            holder = new DocHolder();
        return holder.getDoc(path);
    }
    public static void destroyall() throws IOException{
        if(holder!=null)
            holder.destroyAll();
        
    }
    
}
