/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.Tests;

import indexbuilder41.MurphyAnalyzer;
import indexbuilder41.utils.DB;
import indexbuilder41.utils.GetSynonymList;
import indexbuilder41.utils.PrebuiltPairs;
import java.io.File;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author murphy
 */
public class InsertTester {
    public static void main(String[] args){
        DB db = new DB();
        IndexReader ireader = null;
     IndexSearcher isearcher = null;
     String path = "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/baseline_incretinproductsdbor";
     
     //TaxonomyWriter taxonomyWriter = new DirectoryTaxonomyWriter(new NIOFSDirectory(new File(TAXO)), IndexWriterConfig.OpenMode.);
     
     
     
        
        try{
            Directory dir  = NIOFSDirectory.open(Paths.get(path));
            ireader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(ireader);
            //TopFieldCollector tfc;
            //tfc = TopFieldCollector.create(srt, 100000, false, false, false, false);
            java.sql.Connection tconn = db.dbConnect("jdbc:jtds:sqlserver://cw-wh-build:1433/LinguisticAnalysis","aspconnect","aspconnect");
            Statement stm = tconn.createStatement();
            ResultSet rs = stm.executeQuery("select distinct NEXUS_TSID, nexus_termid,term_query  from aspconnect.SNEXUS_PREINDEX");
            Analyzer anal = new MurphyAnalyzer(null);
            QueryParser fparser = new QueryParser("text", anal);
            while(rs.next())
            {
              
                    String iterm = rs.getString("term_query").trim().toLowerCase();
                    String query = iterm.replaceAll("[^a-zA-Z0-9*\\/]", " ").replace("[\\s]*", " ").trim();
                    PrebuiltPairs pp = new PrebuiltPairs();
                    String termgroup = "nexustsid" + rs.getString("NEXUS_TSID");
                    
                    
                    String term = "nexustermid" + rs.getString("NEXUS_Termid");
                    Query q = fparser.parse("\"" + query + "\"");
                    TopDocs td  = searcher.search(q,1);
                    //if(td.totalHits>0){
                    System.out.print(termgroup);
                    System.out.print("\t");
                    System.out.print(term);
                    System.out.print("\t");
                    System.out.print(query);
                    System.out.print("\t");
                    System.out.print(td.totalHits);
                    System.out.println();
                    //}
                    
                    
                    
            }
            ireader.close();
            dir.close();
            rs.close();
            tconn.close();
            }
            catch(java.sql.SQLException sex){
                System.out.println(sex.getMessage());
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
       
    }
}
