/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author murphy
 */
public class deleteTimestamp {
    public static void deleteDocs(java.sql.Connection conn, String path) throws SQLException, LockObtainFailedException, CorruptIndexException, IOException, Exception
    {
        int maxkeid = 0;
        MurphyAnalyzer anal = new MurphyAnalyzer();
        Directory dir = NIOFSDirectory.open(Paths.get(path));
        LimitTokenCountAnalyzer ltc = new LimitTokenCountAnalyzer(anal,Integer.MAX_VALUE);
        IndexWriterConfig iwc = new IndexWriterConfig(ltc);
        MergePolicy mc = iwc.getMergePolicy();
        iwc.setRAMBufferSizeMB(512);
        
        IndexWriter writer = new IndexWriter(dir, iwc);
        IndexReader ireader =DirectoryReader.open(writer.getDirectory());
        IndexSearcher isearcher = new IndexSearcher(ireader);
        boolean isempty;
        ResultSet rs;
        Statement stm = conn.createStatement();
        do{
            isempty = true;
            rs = stm.executeQuery("Select top 1000 e.ke_id, e.trackmodification as etimestamp, b.trackmodification as btimestamp from elements e join bates_check_new b on e.ke_id = b.ke_id where  e.ke_id > " + String.valueOf(maxkeid) + " order by e.ke_id");
            while(rs.next())
            {
                isempty = false;
                maxkeid = rs.getInt("ke_id");
                String keid = String.format("%012d", rs.getInt("ke_id"));
                String etimestamp = rs.getString("etimestamp");
                String btimestamp = rs.getString("btimestamp");
                Term t = new Term("ke_id",keid) ;
                TermQuery tq = new TermQuery(t);
                TopScoreDocCollector collector = TopScoreDocCollector.create(1);
                isearcher.search(tq, collector);
                if(collector.getTotalHits() > 0)
                {
                    int docid = collector.topDocs().scoreDocs[0].doc;
                    Document doc = ireader.document(docid);
                    if(doc.get("etimestamp") == null || doc.get("btimestamp") == null){
                        writer.deleteDocuments(t);
                        continue;
                    }
                    if(!doc.get("etimestamp").equals(etimestamp) || !doc.get("btimestamp").equals(btimestamp))
                    {
                    System.out.print("deleting ");    
                    System.out.print(doc.get("ke_id"));
                    System.out.print("\t");    
                    System.out.print(doc.get("etimestamp"));
                    System.out.print(" ");    
                    System.out.print(etimestamp);
                    System.out.print("\t");    
                    System.out.print(doc.get("btimestamp"));
                    System.out.print(" ");    
                    System.out.println(btimestamp);
                    writer.deleteDocuments(t);
                    }
                }
            }
            rs.close();
        }while(!isempty);
        stm.close();
        writer.commit();
        writer.close();
        
    }
}
