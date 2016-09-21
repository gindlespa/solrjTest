/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.DocHolder;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author murphy
 */
class DocHolder {
    private volatile static  LinkedHashMap<String, PDDocument> lm;
    private static int MAXSIZE = 25;
    DocHolder(){
        System.setProperty("org.apache.pdfbox.baseParser.pushBackSize", "999000");
        lm = new LinkedHashMap<>();
        
    }
    public static PDDocument getDoc(String path) throws IOException{
        
        PDDocument doc = null;
        if(lm.containsKey(path)){
            synchronized(lm){
                doc = lm.get(path);
                lm.remove(path);
                lm.put(path, doc);
            }
            
        }
        else
        {
            if(lm.size()>=MAXSIZE){
                PDDocument doccheck = lm.get(lm.keySet().toArray()[0].toString());
                while(doccheck==null)
                    try {
                        Thread.sleep(1000);
                        doccheck = lm.get(lm.keySet().toArray()[0].toString());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DocHolder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                try
                {
                    doccheck.close();
                }  catch(Exception ex)
                        {
                      Logger.getLogger(DocHolder.class.getName()).log(Level.SEVERE, null, ex);  
                        }
                    synchronized(lm){
                        lm.remove(lm.get(lm.keySet().toArray()[0].toString()));
                    }
                
                
                
            }
            doc = PDDocument.load(indexbuilder41.utils.FileRetriever.getFile(path));
            synchronized(lm){
                if(!lm.containsKey(path))
                lm.put(path, doc);
            }
        }
        return doc;
    }
     public static synchronized void destroyAll() throws IOException{
        Set<String> set = lm.keySet();
        Iterator<String> iter = set.iterator();
        while(iter.hasNext()){
            String curr = iter.next();
            PDDocument doc = lm.get(curr);
            try{
                doc.close();
            }
            catch(Exception e){}
            }
        }
}
