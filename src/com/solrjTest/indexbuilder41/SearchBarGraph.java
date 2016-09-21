/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import indexbuilder41.utils.BarResult;
import indexbuilder41.utils.indexHolder;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;

/**
 *
 * @author murphy
 */
public class SearchBarGraph {
    private static FacetResult masterResult;
    public static SearchResult search(String DISKLOC, String term, PrintWriter out, String[] prebuilt) throws IOException, Exception
    {
     
     FacetsConfig config = FacetConfig.getconfig();
    int a = 1;
    
    String TAXO = DISKLOC + "/taxo/";
    
    //Directory dir = new org.apache.lucene.store.Directory(DISKLOC);
     MurphyAnalyzer anal = new MurphyAnalyzer(null);
     //MurphyAnalyzer anal2 = new MurphyAnalyzer();
     File mxk = new File(DISKLOC);
     java.io.File fl;
     
     IndexSearcher searcher = indexHolder.getSearcher(DISKLOC);
     IndexReader ireader = searcher.getIndexReader();
     TaxonomyReader taxoReader = indexHolder.getTaxoReader(DISKLOC);

     FacetsCollector fc = new FacetsCollector();

    // MatchAllDocsQuery is for "browsing" (counts facets
    // for all non-deleted docs in the index); normally
    // you'd use a "normal" query:
     Query query;
     
     QueryParser fparser;

    fparser = new QueryParser("text", anal);
    String newterm = "";


     if(prebuilt!=null && prebuilt.length > 0)
        for(int i = 0; i < prebuilt.length; i++)
            newterm += "\"" + term + " " + prebuilt[i] + "\"~25 " ;
     else
        newterm = term;
     if(newterm == null || newterm.trim().equals(""))
        query = new MatchAllDocsQuery();
     else
        query = fparser.parse(newterm);
     SortField sf = new SortField("ke_id",SortField.Type.STRING,false);
     Sort srt = new Sort(sf); 
    
    TopFieldCollector tfc;
        tfc = TopFieldCollector.create(srt, 25, false, false, false);
        Collector  coll = MultiCollector.wrap(fc,tfc);
        searcher.search(query, coll);
    // Retrieve results
    
    SearchResult sr = new SearchResult();
    sr.results = new ArrayList<FacetResult>();
    ArrayList<BarResult> br = new ArrayList<>();
        try {
            Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
            FacetResult pages = facets.getTopChildren(1000000, "tpdf_pages");
            int pdf_pages = 0;
            int pdf_docs = 0;
            for (int i = 0; i < pages.labelValues.length; i++) {
                if (pages.labelValues[i].label != null) {
                    pdf_pages += pages.labelValues[i].value.intValue() * Integer.parseInt(pages.labelValues[i].label);
                    pdf_docs += pages.labelValues[i].value.intValue();
                }
            }
            sr.pagecount = pdf_pages;
            sr.doccount = pdf_docs;
    // Count both "Publish Date" and "Author" dimensions
            
            sr.results.add(facets.getTopChildren(100, "tmattername"));
            sr.results.add(facets.getTopChildren(100, "torderdate"));
            sr.results.add(facets.getTopChildren(100, "tproducingparty"));
            sr.results.add(facets.getTopChildren(100, "tpersons"));
            sr.results.add(facets.getTopChildren(100, "tlanguages"));
            sr.results.add(facets.getTopChildren(100, "ttypes"));
            
            /*
            if(term==null)
            {
                masterResult = sr.results.get(0);

            }
            else
            {
                for(int i=0;i<sr.results.get(0).labelValues.length;i++){
                    BarResult bar = new BarResult();
                    bar.label = sr.results.get(0).labelValues[i].label;
                    bar.num = 
                    br.add(null)
                }
            }
            */
        } catch (IOException iOException) {
            return null;
        } catch (NumberFormatException numberFormatException) {
            return null;
         } catch (Exception iOException) {
            return null;
         }
        ScoreDoc[] docs = tfc.topDocs().scoreDocs;
        
        /*BaseFragmentsBuilder hfb = new BaseFragmentsBuilder()
        {
            @Override
            protected String GetFilteredFieldText(Field field)
            {

            MemoryStream theStream = new MemoryStream(UTF8.GetBytes(field.StringValue()));
            CharReader reader = CharReader.Get(new StreamReader(theStream));
            reader = new HTMLStripCharFilter(reader);
            int r;
            StringBuilder sb = new StringBuilder();
            while ((r = reader.Read()) != -1)
                sb.append((char)r);
            return sb.toString();
            }
            @Override
            public List<WeightedFragInfo> getWeightedFragInfoList( List<WeightedFragInfo> src )
            {
                return src;
            }
            
        };
        */
        
        FastVectorHighlighter highlighter = new FastVectorHighlighter();
        ArrayList<String> slist = new ArrayList<>();
        int len = docs.length > 25 ? 25 : docs.length;
        for(int i =0;i<len;i++)
        {
            int docid = docs[i].doc;
            String keid = ireader.document(docid).get("ke_id");
            
            String[] result = highlighter.getBestFragments(highlighter.getFieldQuery(query), ireader, docid, "text", 500, 3);
            
            for(int k=0; k< result.length;k++)
                slist.add(keid + "\r\n" + result[k]);
        }
        sr.scoredocs = docs;
        sr.highlights = new String[slist.size()];
        slist.toArray(sr.highlights);
    
    return sr;
    /*
    
       out.println("Person: " + results.get(0).toString().replace("\n", "<br/>"));
    out.println("Document Date: " + results.get(1).toString().replace("\n", "<br/>"));
    out.println("Producing Party: " + results.get(2).toString().replace("\n", "<br/>"));
    out.println("Persons Count: " + results.get(3).toString().replace("\n", "<br/>"));
    out.println("Types: " + results.get(4).toString().replace("\n", "<br/>"));
    
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
