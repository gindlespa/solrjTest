/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;


import com.solrjTest.indexbuilder41.Highlight.HighlightLocation;
import com.solrjTest.indexbuilder41.utils.DB;
import com.solrjTest.indexbuilder41.utils.indexHolder;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.CompressionTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
//import org.apache.lucene.queryParser.QueryParser;
        

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
//import org.apache.lucene.document.*;
import org.apache.lucene.search.spans.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.util.BytesRef;
/**
 *
 * @author murphy
 */



public class mainSearch {
    public mainSearch(){
    
        int a = 0;
    }
    public class searchResult{
        public ScoreDoc[] hits;
        public java.util.ArrayList<Integer> arl;
        public Query query;
        public int max;
    }
    
    
     protected void sortLocations(List<HighlightLocation> locs)
    {
        // Make sure of no overlap and in order
        Collections.sort(locs);
        for(int i = 0; i < locs.size(); i++)
            locs.get(i).end = locs.get(i).start + locs.get(i).length - 1;
        for(int i = 1; i < locs.size(); i++)
        {
            HighlightLocation hl1 = locs.get(i-1);
            HighlightLocation hl2 = locs.get(i);
            if(hl2.start <= hl1.end)
            {
                hl1.end = hl2.end;
                locs.remove(i);
                i--;
            }
            
        }
    }
    public class customComparator {
    public boolean compare(Object object1, Object object2) {
        return (((Number)object1).intValue() > ((Number)object2).intValue());
    }
}
    public Query sQuery;
    private TopFieldCollector frontGateCalc(HashMap<String, String> map, PrintWriter pw, IndexSearcher isearcher, boolean[] keidsonly, FacetsCollector fc) throws CorruptIndexException, IOException, Exception{
        String SortOrder = map.get("sortby");
        BooleanQuery qry = new BooleanQuery();
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        int start = 0;
        int end = 0;
          // new IndexSearcher(FSDirectory.open(fl),true);
        
        spanMaker sm = new spanMaker(isearcher);
        boolean reverse = false;
            if(map.get("Reverse") != null)
                reverse = Boolean.parseBoolean(map.get("Reverse"));
            if(map.get("ResultsFrom") != null)
                start = Integer.parseInt(map.get("ResultsFrom"));
            if(map.get("ResultsTo") != null)
                end = Integer.parseInt(map.get("ResultsTo"));
            
            
                if(!map.get("text").equals(""))
                {
                    readMSQ msq = new readMSQ();
                    if(!map.get("text").trim().equals(""))
                    msq.xxList.add(map.get("text").replace("|", "\r\n"));
                    if(!map.get("text2").trim().equals(""))
                    msq.xxList.add(map.get("text2").replace("|", "\r\n"));
                    if(!map.get("text3").trim().equals(""))
                    msq.xxList.add(map.get("text3").replace("|", "\r\n"));
                    msq.xxListExclude = map.get("textexclude").replace("|", "\r\n");
                    msq.xxRange1 = Integer.parseInt(map.get("range1"));
                    msq.xxRange2 = Integer.parseInt(map.get("range2"));
                    BooleanQuery spn = makeSpanQueries(msq, isearcher);
                    qry.add(spn,Occur.MUST);
                }
                if(!map.get("lsid").equals(""))
                {
                    String text = map.get("lsid").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "lsid");
                    qry.add(spn,Occur.MUST);
                }
                 if(!map.get("type").equals(""))
                {
                    String text = map.get("type").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "types");
                    qry.add(spn,Occur.MUST);
                }
                if(!map.get("title").equals(""))
                {
                    String text = map.get("title").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "title");
                    qry.add(spn,Occur.MUST);
                }
                if(!map.get("auths").equals("") && map.get("auths").equals(map.get("tos"))){
                    String text = map.get("auths").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "persons");
                    qry.add(spn,Occur.MUST);
                }
                else{
                    if(!map.get("auths").equals(""))
                    {
                        String text = map.get("auths").replace("|", "\r\n");
                        SpanQuery spn = sm.formulateSpan(text, "auths");
                        qry.add(spn,Occur.MUST);
                    }
                      if(!map.get("tos").equals(""))
                    {
                        BooleanQuery tbq = new BooleanQuery();
                        String text = map.get("tos").replace("|", "\r\n");
                        SpanQuery spna = sm.formulateSpan(text, "tos");
                        SpanQuery spnb = sm.formulateSpan(text, "ccs");
                        SpanQuery spnc = sm.formulateSpan(text, "bccs");
                        tbq.add(spna,Occur.SHOULD);
                        tbq.add(spnb,Occur.SHOULD);
                        tbq.add(spnc,Occur.SHOULD);
                        qry.add(tbq,Occur.MUST);
                    }
                       if(!map.get("ccs").equals(""))
                    {
                        String text = map.get("ccs").replace("|", "\r\n");
                        SpanQuery spn = sm.formulateSpan(text, "ccs");
                        qry.add(spn,Occur.MUST);
                    }
                        if(!map.get("bccs").equals(""))
                    {
                        String text = map.get("bccs").replace("|", "\r\n");
                        SpanQuery spn = sm.formulateSpan(text, "bccs");
                        qry.add(spn,Occur.MUST);
                    }
               }
               if(!map.get("productionnumbers").equals(""))
                {
                    String text = map.get("productionnumbers").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "productionnumbers");
                    qry.add(spn,Occur.MUST);
                }
             if(map.get("query")!=null)  
             if(!map.get("query").trim().equals(""))
                {
                    DB db = new DB();
                    String query = map.get("query");
                    java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://" + map.get("server") + ":1433/" + map.get("database") ,map.get("username"),map.get("password"));    
                    BooleanQuery bq = new BooleanQuery();
                    
                    ResultSet rs;
                    Statement stm = conn.createStatement();
                    stm.setQueryTimeout(500);
                    rs = stm.executeQuery(query);
                    {
                        int keid = 0;
                        String term = String.format("%012d", keid);
                        Term t = new Term("ke_id",term);
                        TermQuery tq = new TermQuery(t);
                        bq.add(tq,Occur.SHOULD);
                    }
                    while(rs.next()){
                        int keid = rs.getInt(1);
                        String term = String.format("%012d", keid);
                        Term t = new Term("ke_id",term);
                        TermQuery tq = new TermQuery(t);
                        bq.add(tq,Occur.SHOULD);
                    }
                    rs.close();
                    stm.close();
                    conn.close();
                    qry.add(bq,Occur.MUST);
                }   
             
             if(map.get("equery")!=null)  
             if(!map.get("equery").trim().equals(""))
                {
                    DB db = new DB();
                    String query = map.get("equery");
                    java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://" + map.get("server") + ":1433/" + map.get("database") ,map.get("username"),map.get("password"));    
                    BooleanQuery bq = new BooleanQuery();
                    
                    ResultSet rs;
                    Statement stm = conn.createStatement();
                    stm.setQueryTimeout(500);
                    rs = stm.executeQuery(query);
                    {
                        int keid = 0;
                        String term = String.format("%012d", keid);
                        Term t = new Term("ke_id",term);
                        TermQuery tq = new TermQuery(t);
                        bq.add(tq,Occur.SHOULD);
                    }
                    while(rs.next()){
                        int keid = rs.getInt(1);
                        String term = String.format("%012d", keid);
                        Term t = new Term("ke_id",term);
                        TermQuery tq = new TermQuery(t);
                        bq.add(tq,Occur.SHOULD);
                    }
                    rs.close();
                    stm.close();
                    conn.close();
                    qry.add(bq,Occur.MUST_NOT);
                }   
             if(!map.get("keidlimit").equals(""))
                {
                    String text = map.get("keidlimit").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "ke_id");
                    qry.add(spn,Occur.MUST);
                }
             if(!map.get("kioskid").equals(""))
                {
                    String text = map.get("kioskid").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "kioskid");
                    qry.add(spn,Occur.MUST);
                }
            if(!map.get("matterid").equals(""))
                {
                    String text = map.get("matterid").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "matterid");
                    qry.add(spn,Occur.MUST);
                }
             if(!map.get("producingpartyid").equals(""))
                {
                    String text = map.get("producingpartyid").replace("|", "\r\n");
                    SpanQuery spn = sm.formulateSpan(text, "producingpartyid");
                    qry.add(spn,Occur.MUST);
                }
             if(map.containsKey("keidsonly"))
                {
                    keidsonly[0] = true;
                }
             
             if(!map.get("orderdate").equals(""))
                {
                    String[] arr = map.get("orderdate").split("[|]");
                    
                    TermRangeQuery spn = new TermRangeQuery("orderdate",new BytesRef(arr[0]),new BytesRef(arr[1]),true,true);
                    qry.add(spn,Occur.MUST);
                }    
             {
                 TermRangeQuery spn = new TermRangeQuery("matterid",new BytesRef("1"),new BytesRef("zzzzzzzzzz"),true,true);
                    qry.add(spn,Occur.MUST);
             }
              {
                 TermRangeQuery spn = new TermRangeQuery("producingpartyid",new BytesRef("1"),new BytesRef("zzzzzzzzzz"),true,true);
                    qry.add(spn,Occur.MUST);
             }
             if(map.get("iquery")!=null)  
             if(!map.get("iquery").trim().equals(""))
                {
                    DB db = new DB();
                    String query = map.get("iquery");
                    java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://" + map.get("server") + ":1433/" + map.get("database") ,map.get("username"),map.get("password"));    
                    BooleanQuery bq = new BooleanQuery();
                    
                    ResultSet rs;
                    Statement stm = conn.createStatement();
                    stm.setQueryTimeout(500);
                    rs = stm.executeQuery(query);
                    {
                        int keid = 0;
                        String term = String.format("%012d", keid);
                        Term t = new Term("ke_id",term);
                        TermQuery tq = new TermQuery(t);
                        bq.add(tq,Occur.SHOULD);
                    }
                    while(rs.next()){
                        int keid = rs.getInt(1);
                        String term = String.format("%012d", keid);
                        Term t = new Term("ke_id",term);
                        TermQuery tq = new TermQuery(t);
                        bq.add(tq,Occur.SHOULD);
                    }
                    rs.close();
                    stm.close();
                    conn.close();
                    BooleanQuery qry1 = new BooleanQuery();
                    
                    qry1.add(bq,Occur.SHOULD);
                    qry1.add(qry,Occur.SHOULD);
                    qry = qry1;
                }   
        SortField sf = new SortField(SortOrder,SortField.Type.STRING,reverse);
        Sort srt = new Sort(sf);      
        TopFieldCollector tfc;
        tfc = TopFieldCollector.create(srt, start + end, false, false, false);
       Collector  coll = MultiCollector.wrap(fc,tfc);
        isearcher.search(qry, coll);
        
        
        return tfc;
        
    }
    public void frontGateData(HashMap<String, String> map, PrintWriter pw) throws Exception
    {
        TopDocs td=null;
        String indexPath = map.get("IndexPath");
        
        File fl = new File(indexPath);
        
        IndexSearcher isearcher = indexHolder.getSearcher(indexPath);
        
        boolean[] keidsonly = new boolean[]{false};
        HashMap ht = new java.util.HashMap();
        TopFieldCollector tfc=null;
        FacetsCollector fc=new FacetsCollector();
        tfc = frontGateCalc(map,pw,isearcher,keidsonly, fc);
        td = tfc.topDocs();
        pw.println(td.scoreDocs.length);
        for(int i = 0; i < td.scoreDocs.length; i++)
        {
           int num = td.scoreDocs[i].doc;
           Document hitDoc = null;
            try
            {
                hitDoc = isearcher.getIndexReader().document(num);
            }
            catch(Exception e)
            {
                e.printStackTrace(pw);

            }
            
            String lsname = hitDoc.get("lsname");
            String isProduced = "no";
            if(!hitDoc.get("prodnumber").trim().equals(""))
                    isProduced = "yes";
            String matterName = hitDoc.get("mattername");
            String producingparty = hitDoc.get("producingparty");
            String type;
            if(hitDoc.get("types") != null && hitDoc.get("types").split("\r\n").length >0)
                type = hitDoc.get("types").split("\r\n")[0];
            else
                type="none";
            addHashData(ht, isBlank(matterName) + "|" + isBlank(producingparty) + "|" + isBlank(isProduced)+ "|" +isBlank(lsname) + "|" + isBlank(type), hitDoc.get("ke_id") + "~" + hitDoc.get("orderdate"));
           

        }

        printHashData(ht,"splitdata",pw);
       
        pw.flush(); 
        
        //isearcher.close();
    }
    
    private String isBlank(String tmp){
        
        if(tmp.trim().equals("")){
            tmp = "No Data";
        }
        return tmp;
    }
    public void frontGateSearch(HashMap<String, String> map, PrintWriter pw) throws Exception
    {
         
        String indexPath = map.get("IndexPath");
        String[] faces = new String[]{"tpersons","torderdate","tproducingparty","tpersonscount","ttypes","tlsname","tproduced","tmattername"};
                
        File fl = new File(indexPath);
        
        IndexSearcher isearcher = indexHolder.getSearcher(indexPath);
        TaxonomyReader taxoReader = indexHolder.getTaxoReader(indexPath);
        boolean[] keidsonly = new boolean[]{false};
        TopFieldCollector tfc=null;
        FacetsCollector fc=null;
        
        fc = new FacetsCollector();
        tfc = frontGateCalc(map,pw,isearcher,keidsonly, fc);
        Facets facets = new FastTaxonomyFacetCounts(taxoReader, FacetConfig.getconfig(), fc);
        List<FacetResult> results = new ArrayList<>();
        FacetResult pages = facets.getTopChildren(10000,  "tpdf_pages");
        int pdf_pages = 0;
        for(int i = 0; i < pages.labelValues.length;i++)
            if(pages.labelValues[i].label!=null)
                pdf_pages += pages.labelValues[i].value.intValue() * Integer.parseInt(pages.labelValues[i].label);
        for(int i = 0 ;i < faces.length; i++)
        {
            results.add(facets.getTopChildren(10000, faces[i]));
        }
        pw.println(tfc.getTotalHits());
        pw.println(pdf_pages);
        TopDocs td = tfc.topDocs();
        for(int i = 0; i < results.size();i++){
            if(results.get(i) != null){
                FacetResult fr = results.get(i);
                pw.println(faces[i]);
                for(int k=0; k<results.get(i).labelValues.length;k++){
                    pw.print(results.get(i).labelValues[k].label);
                    pw.print("\t");
                    pw.print(results.get(i).labelValues[k].value);
                    pw.println();
                }
            }
        }
        //ScoreDoc[] hits = td.scoreDocs;
        pw.println("ttext");
        for(int i = 0; i < td.scoreDocs.length; i++)
        {
            Document doc = isearcher.doc(td.scoreDocs[i].doc);
            pw.print(doc.get("ke_id"));
            if(!keidsonly[0]){
                pw.print("\t");
                pw.print(getField(doc,("title")));
                pw.print("\t");
                pw.print(getField(doc,("lsname")));
                pw.print("\t");
                pw.print(getField(doc,("prodnumber")));
                pw.print("\t");
                pw.print(getField(doc,("orderdate")));
                pw.print("\t");
                pw.print(getField(doc,("mattername")));
                pw.print("\t");
                pw.print(getField(doc,("pdf_link")));
                pw.print("\t");
                pw.print(doc.get("kioskid"));
                pw.print("\t");
                pw.print(getField(doc,("producingparty")));
                /*
                pw.print("\t");
                pw.print(getField(doc,("Author")));
                pw.print("\t");
                pw.print(getField(doc,("UniformTitle")));
                pw.print("\t");
                pw.print(getField(doc,("PublicationInformation")));
                pw.print("\t");
                pw.print(getField(doc,("LongSubject")));
                pw.print("\t");
                pw.print(getField(doc,("ISBN")));
                */
            }
            pw.println();
            
        }
        //isearcher.close();
    }
    private String getField(Document doc, String field){
        String fld = doc.get(field);
        if(fld == null || fld.equals(""))
            return "Empty";
        return fld.replace("\r","").replace("\n","");
    }
    
    public void msearch(String indexPath, String qry,int maxresults, PrintWriter pw, String keids) throws IOException, ParseException, Exception
    {
        IndexSearcher isearcher = null;
        File indexFile = new File(indexPath);
        try
        {        
            isearcher     = indexHolder.getSearcher(indexPath);//new IndexSearcher(FSDirectory.open(indexFile),true);
            
        }
        catch(Exception ex){
            pw.print(ex);  
            return;
        }
        IndexReader reader = isearcher.getIndexReader();
        searchResult sr = new searchResult();
        QueryMSQHandler qmh = new QueryMSQHandler();
        qmh.setKeids(keids);
        qmh.setSearcher(isearcher);
        
        hitsFromMSQ(isearcher,qry,maxresults,pw,sr,qmh);
        iGetText tgf = new getTextFile();
        outputDataDump(sr, pw, reader, tgf);
        /*
        try
        {
        isearcher.close();
        }
        catch(java.io.IOException ex)
        {
            pw.println(ex.getMessage());
            return;
        }
        */
    } 
    private void outputDataDump(searchResult sr, PrintWriter pw, IndexReader reader,iGetText tg)
    {
        ArrayList<Integer> arl = sr.arl;
        //Note:  Can not be enforced, does not work with QueryBasicHandler.
        Query query = sr.query;
        ScoreDoc[] hits = sr.hits;
        pw.print(sr.max);
        pw.print("\r\n");
        pw.flush();
        float score = 0f;
        for (int i = 0; i < hits.length; i++)
        {
            org.apache.lucene.document.Document hitDoc = null;
            try{
                hitDoc = reader.document(hits[i].doc);
                score = hits[i].score;
            }
            catch(org.apache.lucene.index.CorruptIndexException ex){
                pw.println(ex.getMessage());
                return;
            }
            catch(java.io.IOException ex){
                pw.println(ex.getMessage());
                return;
            }
            String ke_id = hitDoc.get("ke_id");
            pw.print(ke_id);
            pw.print("\t");
            pw.print(hitDoc.get("title"));
            pw.print("\t");
            pw.print(hitDoc.get("orderdate"));
            pw.print("\t");
            pw.print(score);
            pw.print("\r\n");
            //}
        }
        
    }
    private void outputResultsAsSplitString(searchResult sr, PrintWriter pw, IndexReader reader,iGetText tg)
    {
        ArrayList<Integer> arl = sr.arl;
        //Note:  Can not be enforced, does not work with QueryBasicHandler.
        Query query = sr.query;
        ScoreDoc[] hits = sr.hits;
        pw.print(sr.max);
        pw.print("\r\n");
        pw.flush();
        for (int i = 0; i < arl.size(); i++)
        {
            org.apache.lucene.document.Document hitDoc = null;
            try{
                int num = arl.get(i).intValue();
                hitDoc = reader.document( hits[num].doc);
            }
            catch(org.apache.lucene.index.CorruptIndexException ex){
                pw.println(ex.getMessage());
                return;
            }
            catch(java.io.IOException ex){
                pw.println(ex.getMessage());
                return;
            }
            String ke_id = hitDoc.get("ke_id");
            if(i>0)
                pw.print(";");
             pw.print(ke_id);
            //}
        }
        
    }
    private String outputResultsAsSplitString(searchResult sr, IndexReader reader,iGetText tg)
    {
        ArrayList<Integer> arl = sr.arl;
        //Note:  Can not be enforced, does not work with QueryBasicHandler.
        Query query = sr.query;
        ScoreDoc[] hits = sr.hits;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arl.size(); i++)
        {
            org.apache.lucene.document.Document hitDoc = null;
            try{
                int num = arl.get(i).intValue();
                hitDoc = reader.document( hits[num].doc);
            }
            catch(org.apache.lucene.index.CorruptIndexException ex){
                System.out.println(ex.getMessage());
                return "";
            }
            catch(java.io.IOException ex){
                System.out.println(ex.getMessage());
                return "";
            }
            String ke_id = hitDoc.get("ke_id");
            if(i>0)
                sb.append(";");
             sb.append(ke_id);
            //}
        }
        return sb.toString();
    }
    private void outputResultsAsIntList(searchResult sr, PrintWriter pw, IndexReader reader,iGetText tg)
    {
        ArrayList<Integer> arl = sr.arl;
        //Note:  Can not be enforced, does not work with QueryBasicHandler.
        Query query = sr.query;
        ScoreDoc[] hits = sr.hits;
        pw.print(sr.max);
        pw.print("\r\n");
        pw.flush();
        for (int i = 0; i < arl.size(); i++)
        {
            org.apache.lucene.document.Document hitDoc = null;
            try{
                int num = arl.get(i).intValue();
                hitDoc = reader.document( hits[num].doc);
            }
            catch(org.apache.lucene.index.CorruptIndexException ex){
                pw.println(ex.getMessage());
                return;
            }
            catch(java.io.IOException ex){
                pw.println(ex.getMessage());
                return;
            }
            String ke_id = hitDoc.get("ke_id");
            
             pw.print(ke_id);
             pw.print("\r\n");
            //}
        }
        
    }
     private void outputResultsAsData(searchResult sr, PrintWriter pw, IndexReader reader,iGetText tg)
    {
        ArrayList<Integer> arl = sr.arl;
        //Note:  Can not be enforced, does not work with QueryBasicHandler.
        Query query = sr.query;
        ScoreDoc[] hits = sr.hits;
        pw.print(sr.max);
        pw.print("\r\n");
        pw.flush();
        HashMap dht = new java.util.HashMap();
        HashMap aht = new java.util.HashMap();
        HashMap lht = new java.util.HashMap();
        HashMap cht = new java.util.HashMap();
        HashMap tht = new java.util.HashMap();

        org.apache.lucene.document.Document hitDoc = null;

        for (int i = 0; i < hits.length; i++)
        {

            int num = arl.get(i).intValue();

            try
            {
                hitDoc = reader.document( hits[num].doc);
            }
            catch(Exception e)
            {
                e.printStackTrace(pw);

            }
            String orderdate = hitDoc.get("orderdate");
            if(orderdate.length()<6)
                orderdate = "00000000";
            orderdate = orderdate.substring(0,6);
            String lsname = hitDoc.get("lsname");
            String auths = hitDoc.get("auths");
            String types = hitDoc.get("types");
            String personscount = hitDoc.get("personscount");
            addHash(dht,orderdate);
            addHash(lht,lsname);
            addHash(aht,auths);
            addHash(cht,personscount);
            addHash(tht,types);

        }

        printHash(dht,"orderdate",pw);
        printHash(lht,"lsname",pw);
        printHash(aht,"auths",pw);
        printHash(cht,"personscount",pw);
        printHash(tht,"types",pw);
        pw.flush();        

            
        
    }
     
    public void hitsFromMSQNY(IndexSearcher isearcher, String msqPath,int maxresults, PrintWriter pw, searchResult sr, iQueryHandler qh) throws IOException, Exception
    {
        MurphyAnalyzer anal = new MurphyAnalyzer();
        Query query = null;
        
        try{
            query = qh.getQuery(msqPath,this); 
        }
        catch(Exception ex){
            pw.print(ex);    
        }
        ScoreDoc[] hits = null;
        if (query == null)
            return;
        SortField sf = new SortField("orderdate",SortField.Type.STRING,true);
        Sort srt = new Sort(sf);
        TopScoreDocCollector collector = TopScoreDocCollector.create(2000000);
        
        
        SpanOrQuery soq = null;
        if(!qh.getKeids().equals("") && qh.getKeids() != null){
            String[] keids = qh.getKeids().split(";");
            SpanTermQuery[] stq = new SpanTermQuery[keids.length];
            for(int i = 0;i<keids.length;i++)
            {
                
                String term = keids[i];
                stq[i] = new SpanTermQuery(new Term("ke_id", term));
                
            }
            soq = new SpanOrQuery(stq);
        }
        if(qh.getFilter().trim().equals("")){
            try{
            if(soq==null){
                isearcher.search(query,collector);
                hits = collector.topDocs().scoreDocs;
            }
            else{
                QueryWrapperFilter qf = new QueryWrapperFilter(soq);
                isearcher.search(query,qf,collector);
                hits = collector.topDocs().scoreDocs;
                }
            }
            catch(IOException ex){
                pw.println(ex.getMessage());
                return;
            }

        }
        else{

            QueryParser fparser = new QueryParser( "text", anal);
            Query fquery = null;
            if(soq==null){
                try
                {
                    fquery = fparser.parse(qh.getFilter().replace("\r\n", "").trim() + " +ke_id:{000000000000 TO 999999999999}");
                }
                catch(ParseException ex){
                    pw.println(ex.getMessage());
                }
                QueryWrapperFilter qf = new QueryWrapperFilter(fquery);
                try{
                    isearcher.search(query,qf,collector);
                    hits = collector.topDocs().scoreDocs;
                }
                catch(IOException ex){
                    pw.println(ex.getMessage());
                    return;
                }
            }
            else
            {
                 try
                {
                    Query xxf = fparser.parse(qh.getFilter().replace("\r\n", "").trim() + " +ke_id:{000000000000 TO 999999999999}");
                    BooleanQuery bq = new BooleanQuery();
                    bq.add(xxf,BooleanClause.Occur.MUST);
                    bq.add(soq,BooleanClause.Occur.MUST);
                    fquery = bq;
                }
                catch(ParseException ex){
                    pw.println(ex.getMessage());
                    return;
                }
                QueryWrapperFilter qf = new QueryWrapperFilter(fquery);
                try{
                isearcher.search(query,qf,collector);
                hits = collector.topDocs().scoreDocs;
                
                }
                catch(IOException ex){
                    pw.println(ex.getMessage());
                    return;
                }
                
            }
        }
        sr.max = collector.getTotalHits();
        //end new
        int maxResults = hits.length > maxresults ? maxresults : hits.length;
            
        java.util.Random r = new java.util.Random();
        java.util.ArrayList<Integer> arl = new java.util.ArrayList<Integer>();
        if (hits.length > maxResults)
        {

            for (int i = 0; i < maxResults; i++)
            {
                //arl.add(i);
                
                Boolean indone = false;
                while (!indone)
                {
                    int dnum = r.nextInt(hits.length);
                    Boolean iscool = true;
                    for (int k = 0; k < arl.size(); k++)
                    {
                        if (((Number)arl.get(k)).intValue() == dnum)
                            iscool = false;
                    }
                    if (iscool)
                    {
                        arl.add(dnum);
                        indone = true;
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < hits.length; i++)
                arl.add(i);
        }

        Collections.sort(arl);


        
        sr.hits = hits;
        sr.arl = arl;
        sr.query = query;
    } 
    
    public void hitsFromMSQ(IndexSearcher isearcher, String msqPath,int maxresults, PrintWriter pw, searchResult sr, iQueryHandler qh) throws IOException, ParseException, Exception
    {
        MurphyAnalyzer anal = new MurphyAnalyzer(null);
        Query query = null;
        
        try{
            query = qh.getQuery(msqPath,this); 
        }
        catch(Exception ex){
            ex.printStackTrace(System.err);
            return;
        }
        ScoreDoc[] hits = null;
        if (query == null)
            return;
        SortField sf = new SortField("orderdate",SortField.Type.STRING,true);
        Sort srt = new Sort(sf);
        TopScoreDocCollector collector = TopScoreDocCollector.create(2000000);
        
        
        SpanOrQuery soq = null;
        if(qh.getKeids() != null && !qh.getKeids().equals("")){
            String[] keids = qh.getKeids().split(";");
            SpanTermQuery[] stq = new SpanTermQuery[keids.length];
            for(int i = 0;i<keids.length;i++)
            {
                
                String term = String.format("%012d", Integer.parseInt(keids[i]));
                stq[i] = new SpanTermQuery(new Term("ke_id", term));
                
            }
            soq = new SpanOrQuery(stq);
        }
        if(qh.getFilter().trim().equals("")){
            try{
            if(soq==null){
                isearcher.search(query,collector);
                //hits = collector.topDocs().scoreDocs;
            }
            else{
                QueryWrapperFilter qf = new QueryWrapperFilter(soq);
                isearcher.search(query,qf,collector);
                //hits = collector.topDocs().scoreDocs;
                }
            }
            catch(IOException ex){
                pw.println(ex.getMessage());
                return;
            }

        }
        else{

            QueryParser fparser = new QueryParser("text", anal);
            Query fquery = null;
            if(soq==null){
                try
                {
                    fquery = fparser.parse(qh.getFilter().replace("\r\n", "").trim() + " +ke_id:{000000000000 TO 999999999999}");
                }
                catch(ParseException ex){
                    pw.println(ex.getMessage());
                }
                QueryWrapperFilter qf = new QueryWrapperFilter(fquery);
                try{
                    isearcher.search(query,qf,collector);
                    //hits = collector.topDocs().scoreDocs;
                }
                catch(IOException ex){
                    pw.println(ex.getMessage());
                    return;
                }
            }
            else
            {
                 try
                {
                    Query xxf = fparser.parse(qh.getFilter().replace("\r\n", "").trim() + " +ke_id:{000000000000 TO 999999999999}");
                    BooleanQuery bq = new BooleanQuery();
                    bq.add(xxf,BooleanClause.Occur.MUST);
                    bq.add(soq,BooleanClause.Occur.MUST);
                    fquery = bq;
                }
                catch(ParseException ex){
                    pw.println(ex.getMessage());
                    return;
                }
                QueryWrapperFilter qf = new QueryWrapperFilter(fquery);
                try{
                isearcher.search(query,qf,collector);
                //hits = collector.topDocs().scoreDocs;
                
                }
                catch(IOException ex){
                    pw.println(ex.getMessage());
                    return;
                }
                
            }
        }
        getSearchResults(sr,collector,query,maxresults);
    }
    
    private void getSearchResults(searchResult sr, TopScoreDocCollector collector, Query query, int maxresults){
        sr.max = collector.getTotalHits();
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        
            
        java.util.Random r = new java.util.Random();
        java.util.ArrayList<Integer> arl = new java.util.ArrayList<Integer>();
        if (hits.length > maxresults)
        {

            for (int i = 0; i < maxresults; i++)
            {
                //arl.add(i);
                
                Boolean indone = false;
                while (!indone)
                {
                    int dnum = r.nextInt(hits.length);
                    Boolean iscool = true;
                    for (int k = 0; k < arl.size(); k++)
                    {
                        if (((Number)arl.get(k)).intValue() == dnum)
                            iscool = false;
                    }
                    if (iscool)
                    {
                        arl.add(dnum);
                        indone = true;
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < hits.length; i++)
                arl.add(i);
        }

        Collections.sort(arl);


        
        sr.hits = hits;
        sr.arl = arl;
        sr.query = query;
    
    }
     private void outputResultsWithPositionPrebuilt(searchResult sr, PrintWriter pw, IndexReader reader,int prox,iGetText tg) throws IOException, Exception
    {
        MurphyAnalyzer anal = new MurphyAnalyzer();
        ArrayList<Integer> arl = sr.arl;
        //Note:  Can not be enforced, does not work with QueryBasicHandler.
        Query query = (Query)sr.query;
        ScoreDoc[] hits = sr.hits;
        pw.print(sr.max);
        pw.print("\r\n");
         pw.flush();
        //QueryParser qp = new QueryParser("text",new MurphyAnalyzer());
        for (int i = 0; i < arl.size(); i++)
        {
            ArrayList<String> lst;
            org.apache.lucene.document.Document hitDoc = null;
            try{
                int num = arl.get(i).intValue();
                hitDoc = reader.document( hits[num].doc);
            }
            catch(org.apache.lucene.index.CorruptIndexException ex){
                pw.println(ex.getMessage());
                return;
            }
            catch(java.io.IOException ex){
                pw.println(ex.getMessage());
                return;
            }
            String textpath = hitDoc.get("textpath");
            String ke_id = hitDoc.get("ke_id");
            String orderdate = hitDoc.get("orderdate");
            String text;
            try{
                text = tg.getText(ke_id, textpath);
            }
            catch(java.io.IOException ex)
            {
                pw.println(ex.getMessage());
                return;    

            }
            catch(Exception ex)
            {
                pw.println(ex.getMessage());
                return;    

            }
            
                    
            java.lang.StringBuilder sbj = new java.lang.StringBuilder();
            try{
                BytesRef bunit = hitDoc.getBinaryValue("units");
                String units = CompressionTools.decompressString(bunit);
                sbj.append(units);
            }
            catch(java.lang.Exception e){
                sbj.append(hitDoc.get("units"));
            }
            
            TokenStream stream = TokenSources.getTokenStream("text", text, anal);
            
            //stream.reset();
            try{
                lst = getList(stream);
            }
            catch(Exception ex)
            {
                pw.print(ex.getMessage());
                return;
                
            }
            stream.reset();
            WeightedSpanTermExtractor ste = new WeightedSpanTermExtractor();
            java.util.HashMap map = null;
            try
            {
                map = (java.util.HashMap)ste.getWeightedSpanTerms(sQuery, stream);
            }
            catch(java.io.IOException ex)
            {
                pw.println(ex.getMessage());
                return;    

            }
            int size = map.size();
            java.util.Set set = map.keySet();
            Object[] keys = set.toArray();
            //stream = TokenSources.getTokenStream("text", text, new MurphyAnalyzer());
            
            StringBuilder sb = new StringBuilder();
            List<HighlightLocation> locs = new ArrayList<HighlightLocation>();
            for (int k = 0; k < size; k += size)
            {
                WeightedSpanTerm tt = (WeightedSpanTerm)map.get(keys[k]);
                java.util.List ll = tt.getPositionSpans();
                Object[] list = ll.toArray();
                //for (int numSpan = 0; numSpan < 1; numSpan++) 
                for (int numSpan = 0; numSpan < list.length; numSpan++)
                {
                    HighlightLocation loc = new HighlightLocation();
                    
                    
                    org.apache.lucene.search.highlight.PositionSpan spanArray = (org.apache.lucene.search.highlight.PositionSpan)list[numSpan];
                    loc.start = spanArray.start;
                    loc.end =  spanArray.end;
                    loc.length = loc.end - loc.start + 1;
                    locs.add(loc);
                    //PositionSpan ps = ll.get(k) as PositionSpan;
                    
                    //pw.flush();

                    //mainList.Add(new hitHolder(spanArray,  keid, term.Replace("\t","   ")));
                }
                
                
                //reader.close();
                //searcher.close();

            }
            sortLocations(locs);
            Iterator<HighlightLocation> itt = locs.iterator();
                while (itt.hasNext()){
                    HighlightLocation loc = itt.next();
                    sb.append(ke_id);
                    sb.append("\t");
                    sb.append(getSpan(loc.start,loc.end,lst,prox));
                    sb.append("\t");
                    sb.append(loc.start);
                    sb.append("\t");
                    sb.append(loc.end);
                    sb.append("\t");
                    sb.append(orderdate);
                    sb.append("\t");
                    float score = 0f;
                        float ds = hits[i].score;
                        score = ds;
                    
                    sb.append(score);
                    sb.append("\r\n");
                }
                pw.print(sb.toString());
        }
    }
        private void outputResultsWithPosition(searchResult sr, PrintWriter pw, IndexReader reader,int prox,iGetText tg)
    {
        MurphyAnalyzer anal = new MurphyAnalyzer(null);
        ArrayList<Integer> arl = sr.arl;
        //Note:  Can not be enforced, does not work with QueryBasicHandler.
        ScoreDoc[] hits = sr.hits;
        pw.print(sr.max);
        pw.print("\r\n");
        
         
        //QueryParser qp = new QueryParser("text",new MurphyAnalyzer());
        for (int i = 0; i <arl.size() ; i++)
        {
            try{
                org.apache.lucene.document.Document hitDoc = null;
                int num = arl.get(i).intValue();
                int docid = hits[num].doc;
                try{
                    
                    hitDoc = reader.document( docid);
                }
                catch(org.apache.lucene.index.CorruptIndexException ex){
                    pw.println(ex.getMessage());
                    return;
                }
                catch(java.io.IOException ex){
                    pw.println(ex.getMessage());
                    return;
                }
                String textpath = hitDoc.get("textpath");
                String ke_id = hitDoc.get("ke_id");
                String orderdate = hitDoc.get("orderdate");
                String text;
                
                
                TokenStream stream = null;
                ArrayList<String> lst = null;
                try{
                    
                    stream = TokenSources.getTokenStream(reader, docid, "text", anal);
                    
                    stream.reset();
                    
                    
                    lst = getList(stream);
                    
                    stream.end();
                    stream.close();
                    stream = TokenSources.getTokenStreamWithOffsets(reader, docid, "text");
                    stream.reset();
                }
                catch(IOException ex)
                {
                    pw.print("FAILTURE STREAM");
                    ex.printStackTrace(pw);
                }
                catch(Exception ex)
                {
                    pw.print("FAILTURE STREAM");
                    ex.printStackTrace(pw);
                }
                
                WeightedSpanTermExtractor ste = new WeightedSpanTermExtractor();
                java.util.HashMap map = null;
                try
                {
                    //ste.setExpandMultiTermQuery(true);
                    
                    map = (java.util.HashMap)ste.getWeightedSpanTerms(sQuery, stream,"text");
                }
                catch(java.io.IOException ex)
                {
                    ex.printStackTrace(pw);
                    return;
                    
                }
                catch(Exception ex)
                {
                    ex.printStackTrace(pw);
                    return;
                    
                }
                stream.end();
                stream.close();
                int size = map.size();
                
                java.util.Set set = map.keySet();
                Object[] keys = set.toArray();
                
                
                StringBuilder sb = new StringBuilder();
                List<HighlightLocation> locs = new ArrayList<HighlightLocation>();
                for (int k = 0; k < size; k += size)
                {
                    WeightedSpanTerm tt = (WeightedSpanTerm)map.get(keys[k]);
                    java.util.List ll = tt.getPositionSpans();
                    Object[] list = ll.toArray();
                    //for (int numSpan = 0; numSpan < 1; numSpan++)
                    for (int numSpan = 0; numSpan < list.length; numSpan++)
                    {
                        HighlightLocation loc = new HighlightLocation();
                        
                        
                        org.apache.lucene.search.highlight.PositionSpan spanArray = (org.apache.lucene.search.highlight.PositionSpan)list[numSpan];
                        loc.start = spanArray.start;
                        loc.end =  spanArray.end;
                        loc.length = loc.end - loc.start + 1;
                        locs.add(loc);
                        //PositionSpan ps = ll.get(k) as PositionSpan;
                        
                        //pw.flush();
                        
                        //mainList.Add(new hitHolder(spanArray,  keid, term.Replace("\t","   ")));
                    }
                    
                    
                    //reader.close();
                    //searcher.close();
                    
                }
                sortLocations(locs);
                Iterator<HighlightLocation> itt = locs.iterator();
                while (itt.hasNext()){
                    HighlightLocation loc = itt.next();
                    sb.append(ke_id);
                    sb.append("\t");
                    sb.append(getSpan(loc.start,loc.end,lst,prox));
                    sb.append("\t");
                    sb.append(loc.start);
                    sb.append("\t");
                    sb.append(loc.end);
                    sb.append("\t");
                    sb.append(orderdate);
                    sb.append("\t");
                    float score = 0f;
                    float ds = hits[i].score;
                    score = ds;
                    
                    sb.append(score);
                    sb.append("\r\n");
                }
                pw.print(sb.toString());
                pw.flush();
            }
            catch(IOException ex){
                Logger.getLogger(mainSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void getList(String indexPath, String msqPath,int maxresults, PrintWriter pw, String keids){
        IndexSearcher isearcher = null;
        File indexFile = new File(indexPath);
        try
        {        
            isearcher     = indexHolder.getSearcher(indexPath); //new IndexSearcher(FSDirectory.open(indexFile),true);
            
        }
        catch(Exception ex){
            pw.print(ex);   
            return;
        }
        
        try
        {
            getList(isearcher,msqPath,maxresults,pw,keids);
        }
        catch(Exception ex)
        {
            pw.println(ex.getMessage());
            return;
        }finally{
            /*
            try{
            isearcher.close();
            }
                catch(IOException ex)
            {
                pw.println(ex.getMessage());
                return;
            }
            */
        }
    } 
    
    
    public String  getListResults(IndexSearcher isearcher, String msqPath,int maxresults, PrintWriter pw, String keids) throws IOException, ParseException, Exception
    
    {
        
        
        IndexReader reader = isearcher.getIndexReader();
        searchResult sr = new searchResult();
        QueryMSQHandler qmh = new QueryMSQHandler();
        qmh.setKeids(keids);
        qmh.setSearcher(isearcher);
        hitsFromMSQ(isearcher,msqPath,maxresults,pw,sr,qmh);
        iGetText tgf = new getTextFile();
        return outputResultsAsSplitString(sr, reader, tgf);
        
            
            
    }
    public void getList(IndexSearcher isearcher, String msqPath,int maxresults, PrintWriter pw, String keids) throws IOException, ParseException, Exception
    {
        
        
        IndexReader reader = isearcher.getIndexReader();
        searchResult sr = new searchResult();
        QueryMSQHandler qmh = new QueryMSQHandler();
        qmh.setKeids(keids);
        qmh.setSearcher(isearcher);
        hitsFromMSQ(isearcher,msqPath,maxresults,pw,sr,qmh);
        iGetText tgf = new getTextFile();
        outputResultsAsIntList(sr, pw, reader, tgf);
        
            
            
    }
    
    public void main(String indexPath, String server, String database, String msqPath,int maxresults, int prox, PrintWriter pw, String keids) throws IOException, Exception
    //   throws Exception      
    {
        
        IndexSearcher isearcher = null;
        File indexFile = new File(indexPath);
        try
        {        
            isearcher     = indexHolder.getSearcher(indexPath);//new IndexSearcher(FSDirectory.open(indexFile),true);
            
        }
        catch(Exception ex)
        {
            pw.print(ex);    
        }
        StringBuilder sb = new StringBuilder();
        if(keids.trim() != ""){
         DB db = new DB();
                 int updated = 0;
         
                 java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://" + server + ":1433/" + database ,"aspconnect","aspconnect");
                 Statement stm = conn.createStatement();
                 stm.setQueryTimeout(500);
                 ResultSet rs;
                 rs = stm.executeQuery("Select i.keid from portfolioitem i join portfolio p on p.portfolioid = i.portfolioid where p.clusteredindexid in (" + keids + ")");
                 Boolean isFirst = true;
                 while(rs.next()){
                     if(isFirst){
                         isFirst = false;
                         
                     }
                     else
                         sb.append(";");
                     sb.append(String.valueOf(rs.getInt("keid")));
                 }
                 rs.close();
        }
        
        
        
        IndexReader reader;
        reader = isearcher.getIndexReader();
        searchResult sr = new searchResult();
        QueryMSQHandler qmh = new QueryMSQHandler();
        qmh.setKeids(sb.toString());
        qmh.setSearcher(isearcher);
        hitsFromMSQNY(isearcher,msqPath,maxresults,pw,sr,qmh);
        iGetText tgf = new getTextFile();
        
        try
        {
            //if(msqPath.contains("nexustsid"))
            //    outputResultsWithPositionPrebuilt(sr, pw, reader, prox, tgf);
            //else
                outputResultsWithPosition(sr, pw, reader, prox, tgf);
            //isearcher.close();
        }
        catch(Exception ex)
        {
            pw.println(ex.getMessage());
            
        }
            
            
    }
    public void nydata(String indexPath, String server, String database, String msqPath,int maxresults, PrintWriter pw, String keids) throws IOException, Exception
    //   throws Exception      
    {
        
        IndexSearcher isearcher = null;
        File indexFile = new File(indexPath);
        try
        {        
            isearcher     = indexHolder.getSearcher(indexPath);//new IndexSearcher(FSDirectory.open(indexFile),true);
            
        }
        catch(Exception ex){
            pw.print(ex);    
        }
        
        StringBuilder sb = new StringBuilder();
        if(keids.trim() != ""){
         DB db = new DB();
            int updated = 0;

            java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://" + server + ":1433/" + database ,"aspconnect","aspconnect");
            Statement stm = conn.createStatement();
            stm.setQueryTimeout(500);
            ResultSet rs;
            rs = stm.executeQuery("Select i.keid from portfolioitem i join portfolio p on p.portfolioid = i.portfolioid where p.clusteredindexid in (" + keids + ")");
            Boolean isFirst = true;
            while(rs.next()){
                if(isFirst){
                    isFirst = false;

                }
                else
                    sb.append(";");
                sb.append(String.valueOf(rs.getInt("keid")));
            }
            rs.close();
        }
        
        IndexReader reader = isearcher.getIndexReader();
        searchResult sr = new searchResult();
        
        QueryMSQHandler qmh = new QueryMSQHandler();
        qmh.setKeids(sb.toString());
        qmh.setSearcher(isearcher);
        hitsFromMSQNY(isearcher,msqPath,maxresults,pw,sr,qmh);
        iGetText tgf = new getTextFile();
        try
        {
            outputResultsAsData(sr, pw, reader, tgf);
        }
        catch(Exception ex)
        {
            pw.println(ex.getMessage());
            return;
        }
        /*
        try
        {
        isearcher.close();
        }
        catch(java.io.IOException ex)
        {
            pw.println(ex.getMessage());
            return;
        }
          */  
            
    }
     public void data(String indexPath, String msqPath,int maxresults, PrintWriter pw, String keids) throws IOException, ParseException, Exception
    //   throws Exception      
    {
        
        IndexSearcher isearcher = null;
        File indexFile = new File(indexPath);
        try
        {        
            isearcher     = indexHolder.getSearcher(indexPath);//new IndexSearcher(FSDirectory.open(indexFile),true);
            
        }
        catch(Exception ex){
            pw.print(ex);    
        }
        IndexReader reader = isearcher.getIndexReader();
        searchResult sr = new searchResult();
        
        QueryMSQHandler qmh = new QueryMSQHandler();
        qmh.setKeids(keids);
        qmh.setSearcher(isearcher);
        hitsFromMSQ(isearcher,msqPath,maxresults,pw,sr,qmh);
        iGetText tgf = new getTextFile();
        try
        {
            outputResultsAsData(sr, pw, reader, tgf);
        }
        catch(Exception ex)
        {
            pw.println(ex.getMessage());
            return;
        }
        /*
        try
        {
        isearcher.close();
        }
        catch(java.io.IOException ex)
        {
            pw.println(ex.getMessage());
            return;
        }
          */  
            
    }
   public void searchdb(String server,String database, String indexPath, String msqPath,int maxresults, int prox, PrintWriter pw,String keids) throws IOException, ParseException, Exception
   {
       IndexSearcher isearcher = null;
        File indexFile = new File(indexPath);
        try
        {        
            isearcher     = indexHolder.getSearcher(indexPath);//new IndexSearcher(FSDirectory.open(indexFile),true);
            
        }
        catch(IOException ex){
            pw.print(ex); 
            return;
        }
        IndexReader reader = isearcher.getIndexReader();
        searchResult sr = new searchResult();
        QueryMSQHandler qmh = new QueryMSQHandler();
        qmh.setKeids(keids);
        qmh.setSearcher(isearcher);
        hitsFromMSQ(isearcher,msqPath.replace("??", "\r\n"),maxresults,pw,sr,qmh);
        getTextIndex tgf = new getTextIndex();
        tgf.setIndex(isearcher);
        
        try
        {
            //if(msqPath.contains("nexustsid"))
            //    outputResultsWithPositionPrebuilt(sr, pw, reader, prox, tgf);
            //else
                outputResultsWithPosition(sr, pw, reader, prox, tgf);
            //isearcher.close();
        }
        catch(Exception ex)
        {
            pw.println("FAILURE");
            pw.println(ex.getMessage());
            
        }
   }
    
    
    
    public ArrayList<String> getList(TokenStream ts) throws Exception
        {
            int size = 0;
            ArrayList<String> lst = new ArrayList<String>();
            boolean hasToken = false;
    
            hasToken = ts.incrementToken( );
            while (hasToken) {

                lst.add( ts.getAttribute(CharTermAttribute.class).toString( ));
                hasToken = ts.incrementToken( );
               
            }
            return lst;
        }

    public String addString(int start, int end, ArrayList<String> lst)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i <= end; i++ ){
            sb.append(lst.get(i));
            if(i != end)
                sb.append(" ");
            
        }
        return sb.toString();
    }
    
    protected class hashResult{
        public int count;
        public ArrayList<String> list;
        public hashResult(){
            count++;
            list = new ArrayList<String>();
        }
    }
    
    protected void addHashData(HashMap ht, String key, String ke_id){
        String[] arr = key.split("\r\n");
        for(int i = 0;i< arr.length;i++){
            
            if(!arr[i].equals(""))
                if(ht.containsKey(arr[i])){
                    hashResult hr = (hashResult)ht.get(arr[i]);
                    hr.list.add(ke_id);
                    hr.count++;
                    
                }
                else{
                    hashResult hr = new hashResult();
                    hr.list.add(ke_id);
                    ht.put(arr[i],hr);
                }
        }
    }
    protected void printHashData(HashMap ht, String name, PrintWriter pw){
        pw.print("XHashMapX\t"+name);
        pw.print("\r\n");
        for (Iterator e = ht.keySet().iterator() ; e.hasNext();) {
            String val = e.next().toString();
            hashResult hr = (hashResult)ht.get(val);
            Object[] lst = (Object[])hr.list.toArray();
            pw.print(val + "\t" + hr.count + "\t" + printlist(lst));
            pw.print("\r\n");
     }
 
    }
    protected String printlist(Object[] lst){
        StringBuilder sb = new StringBuilder();
        for(int i =0;i<lst.length;i++){
            if(i> 0)
                sb.append(";");
            sb.append(lst[i].toString());
        }
        return sb.toString();
    }
    protected void addHash(HashMap ht, String key){
        String[] arr = key.split("\r\n");
        for(int i = 0;i< arr.length;i++){
            if(!arr[i].equals(""))
                if(ht.containsKey(arr[i]))
                    ht.put(arr[i], ((Number)ht.get(arr[i])).intValue() + 1);
                else
                    ht.put(arr[i],1);
        }
    }
    protected void printHash(HashMap ht, String name, PrintWriter pw){
        pw.print("XHashMapX\t"+name);
        pw.print("\r\n");
        for (Iterator e = ht.keySet().iterator() ; e.hasNext();) {
            String val = e.next().toString();
            pw.print(val + "\t" + ht.get(val));
            pw.print("\r\n");
     }
 
    }
     
    public BooleanQuery  makeSpanQueries(readMSQ msq,
           IndexSearcher irs) throws Exception
    {
        if(msq.xxList.size()==0)
            return null;
        BooleanQuery bq = new BooleanQuery();
        bq.setMaxClauseCount(Integer.MAX_VALUE);
        SpanQuery[] tql = new SpanQuery[msq.xxList.size()];
        for(int i = 0; i < msq.xxList.size(); i++)
            tql[i] = formulateSpan(msq.xxList.get(i),irs);
        SpanQuery sqNot = formulateSpan(msq.xxListExclude,irs);

        SpanQuery sq;
        if(tql.length==1)
            sq = tql[0];
        else
            sq = new SpanNearQuery(tql, msq.xxRange1, false);
        
        if (sqNot != null)
        {
            SpanNearQuery snq = new SpanNearQuery(new SpanQuery[] { sq, sqNot }, msq.xxRange2, false, true);
            bq.add((Query)snq,Occur.MUST_NOT);
        }
        //if(sqNear1 != null || sqNear2 != null || sqNot != null)
            bq.add(sq,Occur.MUST);
        sQuery = (Query)sq.clone();

        return bq;
    }

    public SpanQuery formulateSpan(String qry, IndexSearcher irs) 
    {
        qry = qry.replaceAll("[^0-9a-zA-Z/*\\r\\n]", " ").trim();
        if (qry.length() == 0)
            return null;
        String[] qrys = qry.split("\r\n");
        SpanQuery[] sqa = new SpanQuery[qrys.length];
        for (int i = 0; i < qrys.length; i++)
        {

            String tmpquery = qrys[i].toLowerCase().trim();
            try {
                sqa[i] = makeSpanQuery(tmpquery, irs);
            } catch (Exception ex) {
                Logger.getLogger(mainSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
                if(tmpquery.trim().length()==0)
                    try 
                    {
                        throw new Exception("Bad query=" + qrys[i]);
                    } 
                    catch (Exception ex) 
                    {
                        Logger.getLogger(mainSearch.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace(System.err);
                        return null;
                    }
                
            //bq.add(sqa[i],Occur.SHOULD);
        }
        SpanQuery sq = new SpanOrQuery(sqa);

        return  sq;

    }        
    protected SpanQuery makeSpanQuery(String qry, IndexSearcher irs) throws Exception
    {
        Pattern pw = Pattern.compile("\\{-?[0-9.]*\\}");
        Pattern patt = Pattern.compile("\\/-?[0-9]*");
        Matcher mat= pw.matcher(qry);
        float weight = 1.0f;
        if(mat.find())
        {
            String within = mat.group();
            String s = within.substring(1, within.length()-1);
            weight = Float.parseFloat(s);
            qry = qry.replaceAll("\\{-?[0-9.]*\\}", "");

        }
        mat= patt.matcher(qry);
        if (mat.find())
        {
            //String[] mats = qry.;
            String within = mat.group();
            String[] subs = patt.split(qry, 2);
            SpanQuery spn1 = makeSpanQuery(subs[0], irs);
            SpanQuery spn2 = makeSpanQuery(subs[1], irs);
            String s = within.substring(1, within.length());
            int count = Integer.parseInt(s);
            SpanNearQuery spn = new SpanNearQuery(new SpanQuery[] { spn1, spn2 }, count, false);
            spn.setBoost(weight);
            return spn;
        }

        SpanQuery sq = null;
        List<String> lst = new LinkedList<String>(Arrays.asList(qry.split("\\s")));
            while(lst.remove(""));
        String[] qrys = lst.toArray(new String[lst.size()]);
        if (qrys.length == 1)
        {
            if (qrys[0].contains("*"))
            {
                sq = makeWildCardSpanQuery(qrys[0], irs); //.substring(0, qrys[0].length() - 1)
                //if (sq.toString().equals("spanOr([])"))
                //    sq = new SpanTermQuery(new Term("text", qrys[0].substring(0, qrys[0].length() - 1)));
            }
            else
                sq = new SpanTermQuery(new Term("text", qrys[0]));

        }
        else
        {
            SpanQuery[] stq = new SpanQuery[qrys.length];
            for (int i = 0; i < qrys.length; i++)
            {
                stq[i] = makeSpanQuery(qrys[i], irs);
            }
            sq = new SpanNearQuery(stq, 0, true);
        }
        if(weight != 1.0)
            sq.setBoost(weight);
        return sq;
    }
    protected SpanQuery makeWildCardSpanQuery(String qry, IndexSearcher irs) throws Exception
    {
        return spanMaker.makeWildCardSpanQuery("text", qry, irs);
        
        
    }
        public String getSpan(int start, int end, ArrayList<String> lst, int size)
            
    {
        String mid = "*" + addString(start,end,lst) + "*";
        int prebeg = start - size >= 0 ? start -size : 0;
        int postend = end + size > (lst.size() - 1) ? (lst.size() -1) : end + size;
        String begin = "";
        String finish = "";
        try{
        begin = addString(prebeg,start-1,lst);
        finish = addString(end + 1, postend, lst);
        }
        catch(Exception e)
        {
            
            return "";
        }
        return begin + mid + finish;
    }
     
}
