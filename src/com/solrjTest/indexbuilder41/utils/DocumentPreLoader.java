/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;



import java.util.ArrayList;
import org.apache.lucene.index.IndexReader;

/**
 *
 * @author murphy
 */
public class DocumentPreLoader {
    private ArrayList<Long> lst = new ArrayList<>();
    
    public void add(long keid){
        lst.add(keid);
    }
    public void add(String keid){
        try{
        lst.add(Long.parseLong(keid));
        }
        catch(Exception e){}
    }
    /*
    public void start(String indexpath, GlobConfig gc){
        for(int i = 0; i< lst.size(); i++){
            Preload p = new Preload(lst.get(i), gc);
            
            Thread thr = new Thread();
            
            
        }
        
    }
    */
    public void start(IndexReader reader, GlobConfig gc){
        for(int i = 0; i< lst.size(); i++){
            Preload p = new Preload(lst.get(i), gc);
            //p.run();
            Thread thr = new Thread(p);
            thr.start();
            
        }
        
    }
    
    
     
}

