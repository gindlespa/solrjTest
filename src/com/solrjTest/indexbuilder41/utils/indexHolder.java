/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;


import indexbuilder41.MurphyAnalyzer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.Version;

/**
 *
 * @author murphy
 */
public class indexHolder {
     private static HashMap<String,IndexReader> inds = null;
     private static HashMap<String,TaxonomyReader> taxomap = null;
     private static HashMap<String,Long> lastModifiedDateCollection = null;
    public static IndexSearcher getSearcher(String indexPath) throws CorruptIndexException, IOException, Exception
    {
        IndexReader  ireader = null;
        String slash;
        if(Constants.WINDOWS)
            slash = "\\";
        else
            slash = "/";
        if(!indexPath.endsWith(slash))
            indexPath += slash;
        File fl = new File(indexPath);
        String segPath = (indexPath + "segments.gen");
        
        long lastmod = fl.lastModified();
        if(inds==null){
            inds = new HashMap<String, IndexReader>();
            lastModifiedDateCollection = new HashMap<String,Long>();
        }
        if(!inds.containsKey(indexPath))
        {
            
            
             ireader = DirectoryReader.open(new NIOFSDirectory(Paths.get(indexPath)));
            inds.put(indexPath,ireader);
            lastModifiedDateCollection.put(indexPath, lastmod);
        }
        else
        {
            if(!lastModifiedDateCollection.get(indexPath).equals(lastmod)){
                inds.get(indexPath).close();
                inds.remove(indexPath);
                Directory dir  = NIOFSDirectory.open(Paths.get(indexPath));
                ireader = DirectoryReader.open(dir);
                
                inds.put(indexPath,ireader);
                lastModifiedDateCollection.remove(indexPath);
                lastModifiedDateCollection.put(indexPath, lastmod);
            }
                else
            {
               Directory dir  = NIOFSDirectory.open(Paths.get(indexPath));
                ireader = DirectoryReader.open(dir); 
                
            }
        }
        //inds.get(indexPath).getIndexReader().
        return new IndexSearcher(ireader);
    }
    public static TaxonomyReader getTaxoReader(String indexPath) throws IOException{
        return new DirectoryTaxonomyReader(new NIOFSDirectory(Paths.get(indexPath + "taxo/")));
    }
    
    private static IndexWriter makeWriter(String path) throws Exception{
            
            LimitTokenCountAnalyzer ltc = new LimitTokenCountAnalyzer(new MurphyAnalyzer(),Integer.MAX_VALUE);
            IndexWriterConfig iwc = new IndexWriterConfig(ltc);
            MergePolicy mc = iwc.getMergePolicy();
            iwc.setRAMBufferSizeMB(512);
            return new IndexWriter(NIOFSDirectory.open(Paths.get(path)), iwc);
    }
    
}
