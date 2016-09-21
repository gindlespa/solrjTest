/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.solrjTest;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.Date;
import java.util.concurrent.*;

//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.index.*;
//import org.apache.lucene.search.*;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.store.NIOFSDirectory;
//import org.apache.lucene.util.Constants;
//import org.apache.lucene.util.*;
import com.solrjTest.thrWriter;
import org.apache.lucene.util.Constants;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.http.client.HttpClient;

/**
 *java -Xmx6000M -Xms2000M  -jar indexbuilder.jar "Warehouse14" Baseline_forddieselmdl f:\build\
 * @author murphy
 */
public class Similars {
    private static final String urlString = "https://solr.aseedge.com:8983/solr/efDemo";
    private static HttpSolrClient solrClient;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set(HttpClientUtil.PROP_MAX_CONNECTIONS, 512);
        params.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, 256);
        params.set(HttpClientUtil.PROP_FOLLOW_REDIRECTS, false);
        HttpClient httpClient = HttpClientUtil.createClient(params);
        //solrServer = new HttpSolrServer(urlString, httpClient);
        solrClient =  new HttpSolrClient(urlString, httpClient);


        int threadCount = Integer.parseInt(args[4]);
        int processed = 0;
        String slash = "/";
        if(Constants.WINDOWS)
            slash = "\\";
        int count = 0;
        String DISKLOC = args[2] + slash +  args[1] + "dbor" + slash;
        int maxKeid = Integer.parseInt(args[3]);
        //Directory dir = new org.apache.lucene.store.Directory(DISKLOC);
        java.util.HashMap<Integer, Integer> mlist = makeList(DISKLOC);
        System.out.println("Import Done");
        //MurphyAnalyzer anal = new MurphyAnalyzer();
        //SimilarTread.anal = anal;
        File mxk = new File(DISKLOC);
        boolean doAppend = mxk.exists();
//        java.io.File fl = new File(DISKLOC);
//        IndexSearcher isearcher = null;
//
//        Directory dir = NIOFSDirectory.open(Paths.get(DISKLOC));
//        IndexReader ir = DirectoryReader.open(dir);
//        isearcher = new IndexSearcher(ir);
        
        //TODO  open a searcher for solr

        //writer.set
        DB db = new DB();
        int updated = 0;

        java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://" + args[0] + ":1433/" + args[1] ,"aspconnect","aspconnect");

        java.util.ArrayList<Integer> al = null;
        java.util.ArrayList<Integer> tl = null;

        String[] flds = new String[1];
        flds[0] = "text";
        SimilarTread.flds = flds;
        corrCalc cc = new corrCalc();
        String mquery = "select top 2000 e.ke_id from elements e join bates_check_new n on e.ke_id = n.ke_id where e.status_id = 'l' and e.ke_id > MAXKEID ORDER BY e.ke_id";
        if(args.length == 6)
            mquery = "select top 2000 e.ke_id from elements e join bates_check_new n on e.ke_id = n.ke_id join portfolioitem i on i.keid = e.ke_id where e.status_id = 'l' and e.ke_id > MAXKEID and i.portfolioid = '" + args[5] + "' ORDER BY e.ke_id";
        Statement stm = conn.createStatement();
        stm.setQueryTimeout(500);
        ResultSet rs;
        rs = stm.executeQuery("Select productionnumberprefixname from productionnumberprefixes");
        StringBuilder sb = new StringBuilder();
        while(rs.next()){
                if(sb.length() > 0)
                    sb.append("|");
                sb.append(rs.getString(1));
                sb.append("[0-9][0-9][0-9]*");
        }
        rs.close();
        SimilarTread.batesreg = sb.toString().toLowerCase();

        
        thrWriter thr = new thrWriter();
        Boolean hasRows = false;
                   
            //ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            ThreadPoolExecutor exService = new ThreadPoolExecutor(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5000));
            do{
                hasRows = false;
            if(al == null)
                {
                    
                    al = new ArrayList<Integer>();
                    tl = new ArrayList<Integer>();
                    try{
                    rs = stm.executeQuery(mquery.replace("MAXKEID", String.valueOf(maxKeid)));

                    while(rs.next())
                    {
                        hasRows = true;
                        int keid = rs.getInt("ke_id");
                        maxKeid = keid;
                        String ke_id = Integer.toString(keid);

                        SolrQuery query = new SolrQuery();
                        query.setQuery("ke_id:" + ke_id);
                        query.setFields("ke_id");
                        query.setRows(1);
                        QueryResponse response = solrClient.query(query);
                        SolrDocumentList list = response.getResults();
                        if(!mlist.containsKey(keid) && list.getNumFound() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                tl.add(Integer.valueOf(list.get(0).toString()));
                                al.add(keid);
                                //System.out.println(list.get(i));
                            }
                        }
                        
                        
                        //tl.add(rs.getString("Doc_Number"));
                    }

                    rs.close();
                    }
                    catch(SQLException sex){
                        System.out.print(sex.getMessage());
                    }
                }

                for(int i =0; i < al.size(); i++){
                count++;
                maxKeid = al.get(i);
                if(mlist.containsKey(al.get(i)))
                        continue;



                //if(Integer.parseInt(tl.get("textsize")) > 100){
                    Runnable worker = new SimilarTread(al.get(i), tl.get(i), thr);
                    exService.execute(worker);
                    processed++;
                    //System.out.println(maxKeid);
                    thr.addStackCount();
                //}



                }
                Date begin = new Date();
                while(exService.getQueue().size() > 200){
                    Thread.sleep(60000);
                    System.out.println(thr.getStackCount());
                }
                writeList(thr.read(), DISKLOC);
                System.out.println(maxKeid + " " + processed + " " + begin.toString());

            try{
                rs = stm.executeQuery(mquery.replace("MAXKEID", String.valueOf(maxKeid)));

                al = new ArrayList<Integer>();
                tl = new ArrayList<Integer>();
                while(rs.next())
                {
                    hasRows = true;
                    int keid = rs.getInt("ke_id");
                    String ke_id = String.format("%012d", keid);
                    maxKeid = keid;
                    SolrQuery query = new SolrQuery();
                    query.setQuery("ke_id:" + ke_id);
                    query.setFields("ke_id");
                    query.setRows(1);
                    QueryResponse response = solrClient.query(query);
                    SolrDocumentList list = response.getResults();
                    if(!mlist.containsKey(keid) && list.getNumFound() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            tl.add(Integer.valueOf(list.get(0).toString()));
                            al.add(keid);
                            //System.out.println(list.get(i));
                        }
                    }
                   
                }

                rs.close();
                }
                catch(SQLException sex){
                    System.out.print(sex.getMessage());
                }
            }


            while(exService.getQueue().size()>0 || hasRows == true);
            exService.shutdown();
            while (!exService.isTerminated()) 
            {
                Thread.sleep(1000);
            }
            writeList(thr.read(), DISKLOC);
            //isearcher.close();
            
    }

        public static java.util.HashMap<Integer, Integer> makeList(String path) throws Exception
        {

            java.util.HashMap<Integer, Integer> mlist = new java.util.HashMap<Integer, Integer>();
            if(new File(path + "sim.txt").exists())
            {
                BufferedReader br = new BufferedReader(
                new FileReader(path + "sim.txt"));
                String devstr;
                while ((devstr = br.readLine()) != null) 
                {
                    try{
                    String[] arr = devstr.split("\t");
                    int tint = Integer.parseInt(arr[0]);
                    if(!mlist.containsKey(tint))
                    {
                        mlist.put(tint, 0);
                    }
                    }
                    catch(Exception e){

                    }
                }
            }

            return mlist;
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

        System.out.println("Query Results: " + list.getNumFound());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }


    }

        public static void writeList(String output, String path) throws Exception
        {

                FileOutputStream fos = new FileOutputStream(path + "sim.txt",true);
                OutputStreamWriter out = new OutputStreamWriter(fos,"UTF-8");
                out.write(output);
                out.close();
                fos.close();


        }
            
    }




