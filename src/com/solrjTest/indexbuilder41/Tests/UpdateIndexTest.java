/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Tests;

import indexbuilder41.FacetConfig;
import indexbuilder41.MurphyAnalyzer;
import static indexbuilder41.MultiMainThread.config;
import static indexbuilder41.MultiMainThread.taxowriter;
import static indexbuilder41.MultiMainThread.writer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author murphy
 */
public class UpdateIndexTest {
    public static void main(String[] args) throws IOException, Exception{
        String SETNAME = "offload";
     FacetsConfig config = FacetConfig.getconfig();
    MurphyAnalyzer anal = new MurphyAnalyzer();
    LimitTokenCountAnalyzer ltc = new LimitTokenCountAnalyzer(anal,Integer.MAX_VALUE);
                 IndexWriterConfig iwc = new IndexWriterConfig(ltc);
                 MergePolicy mc = iwc.getMergePolicy();
                 String DISKLOC = "/media/bigdrive/baseline_cwclaimanalyticsdbor/" ;
                    iwc.setRAMBufferSizeMB(512);
                    TaxonomyWriter taxonomyWriter = new DirectoryTaxonomyWriter(new MMapDirectory(Paths.get(DISKLOC + "taxo/")), IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                 IndexWriter writer = new IndexWriter(NIOFSDirectory.open(Paths.get(DISKLOC)), iwc);
                 IndexReader ireader = DirectoryReader.open(writer,true);
                 IndexSearcher search = new IndexSearcher(ireader);
                 TermQuery tq = new TermQuery(new Term("text",SETNAME));
                 Query query = new MatchAllDocsQuery();
                 TopScoreDocCollector tsc = TopScoreDocCollector.create(100000);
                 TopDocs td = search.search(query, 100000);
                 for(int i = 0; i < td.scoreDocs.length ; i++){
                           Document doc = ireader.document(td.scoreDocs[i].doc);
                           
                           String ke_id = doc.get("ke_id");
                           String val = doc.get("portfolioid");
                           System.out.println(val);
                           doc.removeField("portfolioid");
                           String newval;
                           if(!(val==null))
                                newval = SETNAME+ "\n" + val;
                           else
                               newval = SETNAME;
                           String[] list = newval.split("\n");
                           for(int k = 0; k < list.length; k++){
                               String protid = list[k].trim();
                               if(protid.length()>0)
                                    doc.add(new FacetField("tportfolioid",protid));
                           }
                           
                           doc.add(new Field("portfolioid",newval,Field.Store.YES,Field.Index.ANALYZED));
                           FacetConfig.update(doc);
                           writer.updateDocument(new Term("ke_id",ke_id), doc);
                           
                           
                           //System.out.println(val);
                           
                          
                 }
                 //writer.commit();
                 ireader = DirectoryReader.open(writer,true);
                  for(int i = 0; i < ireader.maxDoc() ; i++){
                           Document doc = ireader.document(i);
                           
                           String val = doc.get("portfolioid");
                           System.out.println(val);
                  }
                  writer.commit();
                  taxonomyWriter.commit();
                  writer.close();
                  taxonomyWriter.close();
    }
}
