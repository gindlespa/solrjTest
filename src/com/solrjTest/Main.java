package com.solrjTest;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

//import org.apache.solr.client.solrj.impl.HttpSolrServer;






public class Main {

    public static final String urlString = "https://solr.aseedge.com:8983/solr/baseline_avandiacommonssolrt";
    public static HttpSolrClient solrClient;


    public Main() throws Exception {


        SystemDefaultHttpClient httpClient = new SystemDefaultHttpClient();
        HttpClient client = new httpclientinsecure(httpClient, "as33dg31nc", "as33dg31nc");
        solrClient = new HttpSolrClient(urlString, client);

        //httpClient = new httpclientinsecure(httpClient, "as33dg31nc", "as33dg31nc");
        //SolrServer solrServer = new HttpSolrServer(url, httpClient));
        //HttpSolrServer solrServer = new HttpSolrServer(urlString, httpClient);

        //solrServer = new HttpSolrServer(urlString, httpClient);
        //solrClient =  new HttpSolrClient(urlString, httpClient);

    }

    public static void main(String[] args) throws Exception {

	// write your code here
        Main main = new Main();
        //main.queryMoreLikeThis("3896-7560");
        //main.addDocumentTest(3000);
        //Thread.sleep(3000);
        //main.querySolr("text:*");
        //main.deleteByQuery("qID:24");
        //Thread.sleep(3000);
        //main.querySolr("qID:24");
        //main.highlightLocations();
        main.queryMoreLikeThis("1");
    }

    public void querySolr(String queryString) throws Exception {

        //ModifiableSolrParams params = new ModifiableSolrParams();
        //params.set(HttpClientUtil.PROP_MAX_CONNECTIONS, 128);
        //params.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, 32);
        //params.set(HttpClientUtil.PROP_FOLLOW_REDIRECTS, false);
        //HttpClient httpClient = HttpClientUtil.createClient(params);
        //httpClient = new InsecureHttpClient(httpClient, "as33dg31nc", "as33dg31nc");
        //HttpSolrServer solrServer = new HttpSolrServer(urlString, httpClient);





        SolrQuery query = new SolrQuery();
        query.set("fl", "qID");
        query.setQuery(queryString);

        QueryResponse response = solrClient.query(query);

        SolrDocumentList list = response.getResults();



//        TokenStream stream = TokenSources.getTermVectorTokenStreamOrNull("text",null,0);
//        WeightedSpanTermExtractor ste = new WeightedSpanTermExtractor();
//        ste.getWeightedSpanTerms(query,1.0f,stream);



        System.out.println("Query Results: " + list.getNumFound());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }


    }

    public void queryMoreLikeThis(String ke_id) throws Exception {



        //SolrQuery query = buildUpMoreLikeThisQuery(ke_id);
        SolrQuery query = new SolrQuery();
        query.set("q","*:*");
        query.set("fq","ke_id:" + ke_id);
        query.set("tv.fl", "text");
        query.setRequestHandler("/tvrh");
        query.setRows(1);
        query.set("tv.tf","true");
        query.set("tv","true");
        query.set("fl","ke_id");
        query.set("omitHeader","true");


//        //This works for Terms on the entire index level.
//        QueryResponse response = solrClient.query(query);
//        TermsResponse tr = response.getTermsResponse();
//        List<TermsResponse.Term> items = null;
//        items = tr.getTerms("text");
//
//        if (items != null) {
//            for (TermsResponse.Term term : items) {
//                System.out.println(term.getTerm());
//                System.out.println(term.getFrequency());
//            }
//        }

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
                            System.out.println("Vector Info: " + tvInfo.getKey() + " tf: " + tv.get("tf"));

                        }
                    }
                }
            }
//
        }

        //org.apache.solr.handler.component.TermVectorComponent tvc = new TermVectorComponent();






    }

    public void deleteByQuery(String queryString) {
        try {
            solrClient.deleteByQuery(queryString);
            System.out.println("Delete Successful");
            //Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteByQuery(String queryString, int commitWithin) {
        try {
            solrClient.deleteByQuery(queryString,commitWithin);
            System.out.println("Delete Successful");
            //Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDocumentTest(int commitWithin) {

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("qID", 24, 1.0f);
        doc.addField( "status", "testing", 1.0f );
        doc.addField( "text", "This is a test to see how special characters are escaped.  ^_!@#$%&*()[]{}':;?><]abynt^%#@$%&^[" );
        try {
            solrClient.add(doc,commitWithin);
            System.out.println("Document Added");
            //Thread.sleep(commitWithin);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addDocumentTest() {

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("qID", 24, 1.0f);
        doc.addField( "status", "testing", 1.0f );
        doc.addField( "text", "This is a test to see how special characters are escaped.  ^_!@#$%&*()[]{}':;?><]abynt^%#@$%&^[" );
        try {
            solrClient.add(doc);
            System.out.println("Document Added");
            //Thread.sleep(commitWithin);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void commitDocs() throws IOException, SolrServerException {



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

//    private void highlightLocations() throws Exception {
//
//        String[] color = new String[]{"green"};
//        String[] msq = new String[]{"\\\\xxList1\\r\\nfollow up\\r\\nxxList2\\r\\n\\r\\nxxList3\\r\\n\\r\\nxxFilter\\r\\n"};
//        StringWriter writer = new StringWriter();
//        PrintWriter pw = new PrintWriter(writer);
//        GetHighlightsFromPdf gh = new GetHighlightsFromPdf();
//        PDDocument document = PDDocument.load(util.FileRetriever.getFile("\\\\cw-fs2\\E_drive\\BaselineCWFS2_Edrive\\FS11_Fdrive\\BaselineFileServer-11\\GiantEagleV6\\docs\\IncomingProductions\\GE0000001\\PDF\\189\\378530.pdf"));
//        String highlights = gh.getLocations(document ,msq , pw, null ,color);
//        System.out.print(highlights);
//        System.out.print(writer.toString());
//    }

}
