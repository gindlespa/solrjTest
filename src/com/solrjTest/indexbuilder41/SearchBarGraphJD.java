///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.solrjTest.indexbuilder41;
//
//import indexbuilder41.utils.BarResult;
//import indexbuilder41.utils.GlobConfig;
//import indexbuilder41.utils.OutputPositions;
//import indexbuilder41.utils.indexHolder;
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import org.apache.lucene.facet.FacetResult;
//import org.apache.lucene.facet.Facets;
//import org.apache.lucene.facet.FacetsCollector;
//import org.apache.lucene.facet.FacetsConfig;
//import org.apache.lucene.facet.LabelAndValue;
//import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
//import org.apache.lucene.facet.taxonomy.TaxonomyReader;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.search.BooleanClause.Occur;
//import org.apache.lucene.search.BooleanQuery;
//import org.apache.lucene.search.Collector;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.MatchAllDocsQuery;
//import org.apache.lucene.search.MultiCollector;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.QueryWrapperFilter;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.Sort;
//import org.apache.lucene.search.SortField;
//import org.apache.lucene.search.TopFieldCollector;
//import org.apache.lucene.search.spans.SpanNearQuery;
//import org.apache.lucene.search.spans.SpanQuery;
//import org.apache.lucene.search.spans.SpanTermQuery;
//import org.apache.lucene.util.Constants;
//
///**
// *
// * @author murphy
// */
//public class SearchBarGraphJD {
//
//    public static SearchResult search(GlobConfig gc, readMSQ msq, PrintWriter out, String[] prebuilt, HttpServletRequest request) throws IOException, Exception
//    {
//     int rStart;
//     try{
//         rStart = Integer.parseInt(request.getParameter("rStart"));
//     }
//     catch(Exception e){
//         rStart = 0;
//     }
//
//
//     String   DISKLOC = gc.getIndexPath();
//     mainSearch m = new mainSearch();
//     ArrayList<SpanQuery> spans = new ArrayList<>();
//
//     FacetsConfig config = FacetConfig.getconfig();
//    int a = 1;
//    String slash = "/";
//        if(Constants.WINDOWS)
//            slash = "\\";
//    String TAXO = DISKLOC + slash + "taxo" + slash;
//
//    //Directory dir = new org.apache.lucene.store.Directory(DISKLOC);
//     MurphyAnalyzer anal = new MurphyAnalyzer(null);
//     //MurphyAnalyzer anal2 = new MurphyAnalyzer();
//     File mxk = new File(DISKLOC);
//     java.io.File fl;
//
//     IndexSearcher searcher = indexHolder.getSearcher(DISKLOC);
//     IndexReader ireader = searcher.getIndexReader();
//     TaxonomyReader taxoReader = indexHolder.getTaxoReader(DISKLOC);
//     for(int i = 0; i < msq.xxList.size();i++)
//         spans.add(m.formulateSpan(msq.xxList.get(i), searcher));
//     FacetsCollector fc = new FacetsCollector();
//
//    // MatchAllDocsQuery is for "browsing" (counts facets
//    // for all non-deleted docs in the index); normally
//    // you'd use a "normal" query:
//     Query query;
//
//     Boolean doHighlight = true;
//
//     msq.xxRange1 = 50;
//     if(prebuilt!=null)
//     {
//         for(int i =0;i<prebuilt.length; i++)
//             msq.xxList.add(prebuilt[i]);
//     }
//     query = m.makeSpanQueries(msq, searcher);
//     if(query==null){
//        query = new MatchAllDocsQuery();
//        doHighlight = false;
//     }
//     SortField sf = new SortField("ke_id",SortField.Type.LONG,false);
//     Sort srt = new Sort(sf);
//     BooleanQuery bqm = new BooleanQuery();
//     BooleanQuery bq = null;
//     Map<String,String[]> map = request.getParameterMap();
//     for(int i = 1; i < BarTypesLegal.types.length; i++){
//
//         bq = new BooleanQuery();
//         String sFieldName = BarTypesLegal.sfields[i];
//         QueryParser fparser = new QueryParser(sFieldName, anal);
//         Set<String> fields = map.keySet();
//         String grp = String.valueOf(i);
//         Iterator<String> fiter = fields.iterator();
//         while(fiter.hasNext()){
//             String fname = fiter.next();
//             if(fname.startsWith(grp))
//             {
//                 String value = "\"" + fname.substring(2,fname.length()).replaceAll("_", " ").trim() + "\"";
//                 Query q = fparser.parse(value);
//
//                 //ddq.add(fieldName, value);
//
//                 bq.add(q,Occur.SHOULD);
//             }
//
//         }
//         if(bq.getClauses().length > 0)
//            bqm.add(bq,Occur.MUST);
//     }
//     HttpSession session = request.getSession(true);
//     TopFieldCollector tfc;
//     Query wquery;
//
//     if(bqm.clauses().size()>0){
//        BooleanQuery bbq = new BooleanQuery();
//        bbq.add(query,Occur.MUST);
//        bbq.add(bqm,Occur.MUST);
//        wquery = bbq;
//
//
//     }
//     else{
//        wquery = query;
//
//     }
//    // Retrieve results
//
//
//    if(!wquery.equals(session.getAttribute("wquery")))
//        rStart = 0;
//    session.setAttribute("wquery", wquery);
//    tfc = TopFieldCollector.create(Sort.INDEXORDER, 10 + rStart, false, false, false);
//    Collector  coll = MultiCollector.wrap(fc,tfc);
//    searcher.search(wquery, coll);
//    session.setAttribute("query", query);
//    SearchResult sr = new SearchResult();
//    sr.rStart = rStart;
//    sr.results = new ArrayList<FacetResult>();
//    ArrayList<BarResult> br = new ArrayList<>();
//        try {
//            Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);
//            StringBuilder sb = new StringBuilder();
//            expand(sb,facets,10,"ttree");
//            sr.doccount = tfc.getTotalHits();
//            sr.tree = sb.toString();
//
//            FacetResult pages = facets.getTopChildren(Integer.MAX_VALUE, "tpdf_pages");
//            int pdf_pages = 0;
//            int pdf_docs = 0;
//            for (int i = 0; i < pages.labelValues.length; i++) {
//                if (pages.labelValues[i].label != null) {
//                    pdf_pages += pages.labelValues[i].value.intValue() * Integer.parseInt(pages.labelValues[i].label);
//                    pdf_docs += pages.labelValues[i].value.intValue();
//                }
//            }
//            sr.pagecount = pdf_pages;
//    // Count both "Publish Date" and "Author" dimensions
//            for(int i=1; i<BarTypesLegal.fields.length; i++)
//                sr.results.add(facets.getTopChildren(100, BarTypesLegal.fields[i]));
//
//        } catch (IOException iOException) {
//            return null;
//        } catch (NumberFormatException numberFormatException) {
//            return null;
//         } catch (Exception iOException) {
//            return null;
//         }
//        ScoreDoc[] docs = tfc.topDocs(rStart,10).scoreDocs;
//
//        //if(doHighlight)
//        //{
//        OutputPositions op = new OutputPositions();
//        if(gc.getUseLink())
//            sr.highlights = op.outputResultsWithPosition(query, docs, ireader, 5, "<a class='doclink' target='new' href='" + gc.getDocumentPath()+ "?ke_id=#keid&page=1'>", "</a>", gc);
//        else
//            sr.highlights = op.outputResultsWithPosition(query, docs, ireader, 20, "<b>", "</b>", gc);
//
//        sr.scoredocs = docs;
//        //}
//
//
//    return sr;
//     }
//
//    public static void expand(StringBuilder sb, Facets facets, int count,String cat, String... path) throws IOException{
//      String[] arr = new String[path.length+1];
//      for(int i = 0;i<path.length; i++)
//          arr[i] = path[i];
//      FacetResult result = facets.getTopChildren(count, cat ,path);
//      if(result==null)
//          return;
//      for(LabelAndValue lav: result.labelValues){
//            for(int i=0;i<path.length;i++)
//                sb.append("--");
//            sb.append(lav.label + " " + lav.value.toString());
//            sb.append("<br/>");
//            arr[path.length] = lav.label;
//            expand(sb, facets,count,cat,arr);
//      }
//
//  }
//
//}
