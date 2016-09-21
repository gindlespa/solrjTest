/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest;
import java.util.*;
import java.io.*;

import com.solrjTest.MurphyAnalyzer;
import com.solrjTest.corrCalc;
import com.solrjTest.thrWriter;
import org.apache.lucene.search.IndexSearcher;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.document.Document;
//import org.apache.lucene.queries.mlt.MoreLikeThis;

import org.apache.lucene.util.BytesRef;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import highlight.GetHighlightsFromPdf;
import highlight.highlightLocation;
import indexbuilder.mainSearch;
import indexbuilder.readMSQ;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.solr.common.util.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import com.solrjTest.*;

/**
 *
 * @author murphy
 */
public class SimilarTread implements Runnable {
    private int nke_id;
    private String text;
    private corrCalc cc;
    private StringBuilder sb;
    private int mdocid;
    //private MoreLikeThis mlt;
    public static IndexSearcher is;
    public static IndexReader ir;
    public static String batesreg;
    public static String[] flds;
    public static MurphyAnalyzer anal;
    public static final String urlString = "https://solr.aseedge.com:8983/solr/baseline_avandiacommonssolrt";
    public static HttpSolrClient solrClient;

    private thrWriter thr;
    private void addToHash(HashMap<String, Integer> hash, String term, int count)
    {
        if(hash.containsKey(term)){
            int oldval = (int)hash.get(term);
            hash.put(term, oldval+count);
        }
        else{
            hash.put(term, count);
        }


    }
    public SimilarTread(int ke_id, int mdocid, thrWriter nthr) throws Exception {
        SystemDefaultHttpClient httpClient = new SystemDefaultHttpClient();
        HttpClient client = new httpclientinsecure(httpClient, "as33dg31nc", "as33dg31nc");
        solrClient = new HttpSolrClient(urlString, client);
        this.thr = nthr;
        this.nke_id = ke_id;
        this.mdocid = mdocid;
        sb = new StringBuilder();
        cc = new corrCalc();
//        mlt  = new MoreLikeThis(ir);
//
//        mlt.setFieldNames(flds);
//        mlt.setAnalyzer(anal);
//        mlt.setMinDocFreq(0);
//        mlt.setMinTermFreq(0);
//        mlt.setMaxWordLen(100);
//        mlt.setMinWordLen(0);
//        mlt.setMaxQueryTerms(Integer.MAX_VALUE);
//        mlt.setMaxDocFreqPct(30);

    }

    @Override
    public void run()
    {

        java.util.Date start = new java.util.Date();
        //text = text.replaceAll(this.batesreg, "");

        String ke_id = Integer.toString(nke_id);

        java.util.Date begin = new java.util.Date();
        try{

            SolrQuery query = new SolrQuery();
            query.set("q","*:*");
            query.set("fq","ke_id:" + nke_id);
            query.set("tv.fl", "text");
            query.setRequestHandler("/tvrh");
            query.setRows(1);
            query.set("tv.tf","true");
            query.set("tv","true");
            query.set("fl","ke_id,textsize");
            query.set("omitHeader","true");
            QueryResponse response = solrClient.query(query);

            SolrDocumentList list = response.getResults();
            //Document mdoc = ir.document(mdocid);

            //Check textsize
            int mtextsize = Integer.parseInt((String) list.get(0).getFieldValue("textsize"));
            //int mtexthash = Integer.parseInt(mdoc.get("texthash"));
            thr.write(ke_id + "\t0\t0\r\n");
            if(mtextsize > 100){
                HashMap<String,Integer> ht1 = new HashMap();

                int skip = 0;

                //This works for iteration but there might be an easier way.
                NamedList<Object> solrResponse = solrClient.request(new QueryRequest(query));

                Iterator<Entry<String, Object>> termVectors =  ((NamedList) solrResponse.get("termVectors")).iterator();
                while(termVectors.hasNext()){
                    Entry<String, Object> docTermVector = termVectors.next();
                    //skip first row returned
                    skip++;
                    if(skip > 1){
                        for(Iterator<Entry<String, Object>> fi = ((NamedList)docTermVector.getValue()).iterator(); fi.hasNext(); ){
                            Entry<String, Object> fieldEntry = fi.next();
                            if(fieldEntry.getKey().equals("text")){
                                for(Iterator<Entry<String, Object>> tvInfoIt = ((NamedList)fieldEntry.getValue()).iterator(); tvInfoIt.hasNext(); ){
                                    Entry<String, Object> tvInfo = tvInfoIt.next();
                                    NamedList tv = (NamedList) tvInfo.getValue();
                                    if(ht1.containsKey(tvInfo.getKey()))
                                        addToHash(ht1,tvInfo.getKey(),(int)tv.get("tf")) ;

                                    //System.out.println("Vector Info: " + tvInfo.getKey() + " tf: " + tv.get("tf"));

                                }
                            }
                        }
                    }
//
                }

//            //add term and frequencies to hashmap
//            Terms terms = ir.getTermVector(mdocid, "text"); //get terms vectors for one document and one field
//            if (terms != null && terms.size() > 0) {
//                TermsEnum termsEnum = terms.iterator(null); // access the terms for this field
//                BytesRef term = null;
//                while ((term = termsEnum.next()) != null) {// explore the terms for this field
//                    DocsEnum docsEnum = termsEnum.docs(null, null); // enumerate through documents, in this case only one
//                    int docIdEnum;
//                    while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS)
//                    {
//                      String word = term.utf8ToString();
//                      int count = docsEnum.freq();
//                      if(ht1.containsKey(word))
//                        addToHash(ht1,word,count) ;
//
//                    }
//                }
//            }

                //System.out.println("Start " + ke_id + " " + begin.toString());
                int hits = 0;
                int misses = 0;
                //Query qry = null;
                //StringReader sr = new StringReader(text);

                //get 2500 documents like the main doc
                //qry = mlt.like(mdocid);
                long numDocs=0;
                try {

                    SolrQuery q = new SolrQuery("*:*");
                    q.setRows(0);  // don't actually request any data
                    numDocs = solrClient.query(q).getResults().getNumFound();



                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                numDocs = numDocs/3;


                query = new SolrQuery();


                query.setRequestHandler("/" + MoreLikeThisParams.MLT);
                query.set(MoreLikeThisParams.MATCH_INCLUDE, false);
                query.set(MoreLikeThisParams.MIN_DOC_FREQ, 0);
                query.set(MoreLikeThisParams.MIN_TERM_FREQ, 0);
                query.set(MoreLikeThisParams.MAX_WORD_LEN,100);
                query.set(MoreLikeThisParams.MIN_WORD_LEN,0);
                query.set(MoreLikeThisParams.BOOST, false);
                query.set(MoreLikeThisParams.MAX_DOC_FREQ,(int)numDocs);
                query.set(MoreLikeThisParams.MAX_QUERY_TERMS, Integer.MAX_VALUE);
                query.set(MoreLikeThisParams.SIMILARITY_FIELDS,"text");
                query.setQuery("ke_id:" + nke_id);
                query.set("fl", "ke_id,score");
                int maxResults = 2500;
                query.setRows(maxResults);

                QueryResponse response2 = solrClient.query(query);

                SolrDocumentList list2 = response2.getResults();

                //TopScoreDocCollector tsc = TopScoreDocCollector.create(2500);
                //is.search(qry, tsc);


                //starting score
                float lastScore = 1.5f;
                //TopDocs td = tsc.topDocs();
                int currenthit = 0;
                //go through each hit until you get below a certain score
                while(currenthit < list2.getNumFound() && ( lastScore > .9f))
                {
                    //set lastScore to last docs score
                    lastScore = (float)list2.get(currenthit).getFieldValue("score");
                    double pscore = 0d;
                    //int = td.scoreDocs[currenthit].doc;
                    String nkeid = "";
                    int textsize = 0;

                    nkeid = (String)list2.get(currenthit).getFieldValue("ke_id");
                    textsize = Integer.parseInt((String)list2.get(currenthit).getFieldValue("textsize"));

                    //int texthash = Integer.parseInt(ir.document(docid).get("texthash"));
                    //if(nsize>50000)
                    //    nsize=50000;

                    float factor = 0f;

                    //compare text sizes
                    if(mtextsize > textsize)
                        factor = (textsize + 50000) * 1.0f / (mtextsize + 50000);
                    else
                        factor = (mtextsize + 50000) * 1.0f / (textsize + 50000);
                    if(factor > .50 && textsize > 50){


                        //int texthash = Integer.parseInt(ir.document(docid).get("texthash"));


                        //NO TEXT EXTRACTED MESSAGE ELIMINATE
                        if(textsize<100){
                            currenthit++;
                            continue;
                        }


                        //if(texthash==(mtexthash))
                        //    pscore = 1;
                        //if(ntext.length() > 250000)
                        //    ntext = ntext.substring(0,250000);
                        else{
                            HashMap<String,Integer> ht2 = new HashMap();

                            skip = 0;

                            //This works for iteration but there might be an easier way.
                            NamedList<Object> solrResponse2 = solrClient.request(new QueryRequest(query));

                            Iterator<Entry<String, Object>> termVectors2 =  ((NamedList) solrResponse2.get("termVectors")).iterator();
                            while(termVectors2.hasNext()){
                                Entry<String, Object> docTermVector = termVectors2.next();
                                //skip first row returned
                                skip++;
                                if(skip > 1){
                                    for(Iterator<Entry<String, Object>> fi = ((NamedList)docTermVector.getValue()).iterator(); fi.hasNext(); ){
                                        Entry<String, Object> fieldEntry = fi.next();
                                        if(fieldEntry.getKey().equals("text")){
                                            for(Iterator<Entry<String, Object>> tvInfoIt = ((NamedList)fieldEntry.getValue()).iterator(); tvInfoIt.hasNext(); ){
                                                Entry<String, Object> tvInfo = tvInfoIt.next();
                                                NamedList tv = (NamedList) tvInfo.getValue();
                                                if(ht1.containsKey(tvInfo.getKey()))
                                                    addToHash(ht2,tvInfo.getKey(),(int)tv.get("tf")) ;

                                                //System.out.println("Vector Info: " + tvInfo.getKey() + " tf: " + tv.get("tf"));

                                            }
                                        }
                                    }
                                }
//
                            }



                            pscore = cc.getCorrelation(ht1, ht2);

                            pscore = pscore > 1 ? 1 / pscore : pscore;
                        }

                        //pscore = pscore * factor;
                        if(pscore > 0.90 && !ke_id.equals(nkeid)){
                            thr.write(ke_id + "\t" + nkeid + "\t" /*+ td.scoreDocs[currenthit].score + " "*/ + pscore + "\r\n");
                            if(misses > -100)
                                misses--;
                        }
                        else
                            misses++;
                    }
                    else
                        misses++;


                    currenthit++;
                }

            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally{
            thr.subtractStackCount();

        }
        Date end = new Date();
        long diff = end.getTime() - start.getTime();
        long diffSeconds = diff / 1000;
        System.out.println("End " + ke_id + " " + diffSeconds);

    }

    public SolrQuery buildUpMoreLikeThisQuery(String originalId) {

        long numDocs=0;
        try {

            SolrQuery q = new SolrQuery("*:*");
            q.setRows(0);  // don't actually request any data
            numDocs = solrClient.query(q).getResults().getNumFound();



        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        numDocs = numDocs/3;


        SolrQuery query = new SolrQuery();


        query.setRequestHandler("/" + MoreLikeThisParams.MLT);
        query.set(MoreLikeThisParams.MATCH_INCLUDE, false);
        query.set(MoreLikeThisParams.MIN_DOC_FREQ, 0);
        query.set(MoreLikeThisParams.MIN_TERM_FREQ, 0);
        query.set(MoreLikeThisParams.MAX_WORD_LEN,100);
        query.set(MoreLikeThisParams.MIN_WORD_LEN,0);
        query.set(MoreLikeThisParams.BOOST, false);
        query.set(MoreLikeThisParams.MAX_DOC_FREQ,(int)numDocs);
        query.set(MoreLikeThisParams.MAX_QUERY_TERMS, Integer.MAX_VALUE);
        query.set(MoreLikeThisParams.SIMILARITY_FIELDS,"text");
        query.setQuery("ke_id:" + originalId);
        query.set("fl", "ke_id,score");
        int maxResults = 2500;
        query.setRows(maxResults);
        return query;
    }

}
