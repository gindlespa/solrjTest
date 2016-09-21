package com.solrjTest;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 *
 * @author murphy
 */
class DocHolder {
    private static LinkedHashMap<String, PDDocument> lm;
    private static int MAXSIZE = 100;
    DocHolder(){

        lm = new LinkedHashMap<>();
    }
    public static synchronized PDDocument getDoc(String path) throws IOException{
        PDDocument doc = null;
        if(lm.containsKey(path)){
            doc = lm.get(path);
            lm.remove(path);
            lm.put(path, doc);

        }
        else
        {
            if(lm.size()>=MAXSIZE){
                Set<Map.Entry<String,PDDocument>> s = lm.entrySet();
                Iterator<Map.Entry<String,PDDocument>> iter = s.iterator();
                Map.Entry<String,PDDocument> e = iter.next();
                PDDocument odoc = e.getValue();
                try{
                    odoc.close();
                }
                catch(Exception ex){}

                lm.remove(e.getKey());
            }
            doc = PDDocument.load(util.FileRetriever.getFile(path));
            lm.put(path, doc);
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

