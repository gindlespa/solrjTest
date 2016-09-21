/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import java.io.*;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Constants;

/**
 *
 * @author murphy
 */
public class IndexFromSimFile2 {
    
    public static void main(String[] args) throws Exception
    {}
    private static void deleteFolder(String path){
        File fl = new File(path);
        File[] paths = fl.listFiles();
        for(File flpath : paths)
            flpath.delete();
        fl.delete();;
    }
    
    private static String makeString(ArrayList<IndexFromSimFile2.docScore> docs)
    {
        StringBuilder sb = new StringBuilder();
        Boolean isFirst = true;
        for(IndexFromSimFile2.docScore doc :docs)
        {
            if(isFirst)
                isFirst = false;
            else
                sb.append("|");
            sb.append(doc.keid+";"+doc.score);
            
        }
        return sb.toString();
    }
    private static class docScore{
        public String keid;
        public String score;
        
    }
    
}
