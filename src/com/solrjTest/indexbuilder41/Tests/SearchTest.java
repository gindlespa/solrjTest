/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Tests;

import indexbuilder41.MurphyAnalyzer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

/**
 *
 * @author murphy
 */
public class SearchTest {
    private final static FacetsConfig config = new FacetsConfig();
     public static void main(String[] args) throws IOException, Exception {
    // TODO code application logic here

    config.setHierarchical("torderdate", true);
    config.setMultiValued("toriginator_name", true);
    config.setMultiValued("ttypes", true);
    int a = 1;
    String DISKLOC = args[2] + args[1] + "dbor/";
    String TAXO = DISKLOC + "/taxo/";
    int maxKeid = 0;
    //Directory dir = new org.apache.lucene.store.Directory(DISKLOC);
    java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
     MurphyAnalyzer anal = new MurphyAnalyzer();
     File mxk = new File(DISKLOC);
     boolean doAppend = mxk.exists();
     java.io.File fl;
     IndexReader ireader = null;
     IndexSearcher isearcher = null;
     File fl1 = new File(DISKLOC);
     //TaxonomyWriter taxonomyWriter = new DirectoryTaxonomyWriter(new NIOFSDirectory(new File(TAXO)), IndexWriterConfig.OpenMode.);
     Directory dir  = NIOFSDirectory.open(Paths.get(DISKLOC));
     ireader = DirectoryReader.open(dir);
     IndexSearcher searcher = new IndexSearcher(ireader);
     TaxonomyReader taxoReader = new DirectoryTaxonomyReader(new NIOFSDirectory(Paths.get(TAXO)));

     FacetsCollector fc = new FacetsCollector();

    // MatchAllDocsQuery is for "browsing" (counts facets
    // for all non-deleted docs in the index); normally
    // you'd use a "normal" query:
    TermQuery  query = new TermQuery(new Term("text","date"));
    TermQuery fquery = new TermQuery(new Term("persons","egan"));
    TermQuery f1query = new TermQuery(new Term("persons","art"));
    TermQuery  pquery = new TermQuery(new Term("personscount","4"));
    WildcardQuery wcq = new WildcardQuery(new Term("text","nexustermid13095*"));
    BooleanQuery bquery = new BooleanQuery();
    bquery.add(query,Occur.MUST);
    bquery.add(fquery,Occur.MUST);
    //bquery.add(f1query,Occur.MUST);
    bquery.add(pquery,Occur.MUST);
    //bquery.add(wcq,Occur.MUST);
    FacetsCollector.search(searcher,  bquery, 1, fc);//new MatchAllDocsQuery()
    
    // Retrieve results
    List<FacetResult> results = new ArrayList<>();

    // Count both "Publish Date" and "Author" dimensions
    Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
    results.add(facets.getTopChildren(25, "tpersons"));
    results.add(facets.getTopChildren(10, "torderdate"));
    results.add(facets.getTopChildren(10, "tproducingparty"));
    results.add(facets.getTopChildren(10, "tpersonscount"));
    results.add(facets.getTopChildren(10, "ttypes"));
    ireader.close();
    taxoReader.close();
    
    System.out.println("Person: " + results.get(0));
    System.out.println("Document Date: " + results.get(1));
    System.out.println("Producing Party: " + results.get(2));
    System.out.println("Persons Count: " + results.get(3));
    System.out.println("Types: " + results.get(4));
    /*
    System.out.println("\n");
    System.out.println("Facet drill-down example (Publish Date/2010):");
    System.out.println("---------------------------------------------");
    System.out.println("Author: " + example.runDrillDown());

    System.out.println("\n");
    System.out.println("Facet drill-sideways example (Publish Date/2010):");
    System.out.println("---------------------------------------------");
    for(FacetResult result : example.runDrillSideways()) {
      System.out.println(result);
     }
     */
     }
}
