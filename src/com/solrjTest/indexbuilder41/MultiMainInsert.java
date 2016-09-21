package com.solrjTest.indexbuilder41;
import indexbuilder41.utils.DB;
import indexbuilder41.utils.MakeCorpus;
import java.io.File;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.*;
import java.sql.*;
import java.util.concurrent.*;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;

import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.Constants;
import org.apache.lucene.util.NumericUtils;


/**
 *
 * @author murphy john
 */
public class MultiMainInsert {
     /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
   //try{
        int maxsize=0;
        if(InetAddress.getLocalHost().getHostName().equals("Mike-DT"))
            maxsize = 250000;
        else
            maxsize = Integer.MAX_VALUE;
        FacetsConfig config = FacetConfig.getconfig();
        String slash = "/";
        if(Constants.WINDOWS)
            slash = "\\";
        System.out.print(args.length);
        System.out.flush();
        thrWriter thr = new thrWriter();
        String msqpath = args[3];

        int processNumber = Integer.parseInt(args[4]);
        int processCount = Integer.parseInt(args[5]);
        int maxmerge = 8;
        if(args.length == 7)
            maxmerge = Integer.parseInt(args[6]);
        int a = 1;
        int updated = 0;
        String DISKLOC = args[2] + slash +  args[1] + "dbor" + slash;
        long maxKeid = 0;
        //Directory dir = new org.apache.lucene.store.NIOFSDirectory(DISKLOC);
        DB db = new DB();
        java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://" + args[0] + ":1433/" + args[1] ,"aspconnect","aspconnect");

        
        String LOCK = "lock";
        MurphyAnalyzer anal = new MurphyAnalyzer();
        File mxk = new File(DISKLOC);
        if(mxk.isDirectory())
            deleteTimestamp.deleteDocs(conn, DISKLOC);
        LimitTokenCountAnalyzer ltc;
        ltc = new LimitTokenCountAnalyzer(anal,maxsize);
        IndexWriterConfig iwc = new IndexWriterConfig(ltc);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        MergePolicy mc = iwc.getMergePolicy();
        iwc.setRAMBufferSizeMB(512);
        Directory dir = NIOFSDirectory.open(Paths.get(DISKLOC));
        
        IndexWriter writer = new IndexWriter(dir, iwc);
        IndexReader ireader = DirectoryReader.open(writer,true);
        IndexSearcher isearcher = new IndexSearcher(ireader);
         Connection[] conns = new Connection[processCount];
         TaxonomyWriter taxonomyWriter = new DirectoryTaxonomyWriter(new MMapDirectory(Paths.get(DISKLOC + "taxo\\")), IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
         for(int i=0;i<processNumber;i++)
             conns[i] = db.dbConnect("jdbc:jtds:sqlserver://" + args[0] + ":1433/" + args[1] ,"aspconnect","aspconnect");

        
        Statement stm = conn.createStatement();
        stm.setQueryTimeout(500);
        ResultSet rs;

         rs = stm.executeQuery("select k.KioskID , k.KIoskName from Kioskdb..[Kiosk] K inner join Kioskdb..[KioskDB]  DB on K.KioskDBID = DB.KioskDBID Where db.KioskDBName = DB_Name()");
         rs.next();
         String kioskid = String.valueOf(rs.getInt(1));
         String kioskname = rs.getString(2);
         rs.close();
         java.util.ArrayList<Long> al;
         MultiMainThread.writer = writer;
         MultiMainThread.taxowriter = taxonomyWriter;
         MultiMainThread.config = config;
         MultiMainThread.anal = anal;
         //ExecutorService  executor = Executors.newFixedThreadPool(processCount);
         int rssize;
         ThreadPoolExecutor exService = new ThreadPoolExecutor(processCount, processCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5000));
         do{

             rs = stm.executeQuery("Select distinct top 1000 e.ke_id from elements e join bates_check_new b on b.ke_id = e.ke_id join profile p on e.ke_id = p.ke_id where e.ke_id > " + maxKeid +  " AND e.status_id = 'l' ORDER BY e.ke_id");
             rssize = 0;
             al = new ArrayList<>();
             while(rs.next()){
                     long keid = rs.getLong("ke_id");
                     al.add(keid);
                     rssize++;
             }
             rs.close();
             writer.prepareCommit();
             writer.commit();
             taxonomyWriter.prepareCommit();;
             taxonomyWriter.commit();
             System.out.println(maxKeid);
             for(int i =0; i < al.size(); i++)
             {

                long keid = al.get(i);
                String ke_id = String.format("%012d", keid);
                if(keid > maxKeid)
                    maxKeid = keid;
                //if(doAppend){
                final BytesRefBuilder builder = new BytesRefBuilder();
                NumericUtils.longToPrefixCoded(keid, 0, builder);
                Query qr = new TermQuery(new Term("ke_id",builder.toBytesRef()));
                TotalHitCountCollector collector = new TotalHitCountCollector();
                isearcher.search(qr,collector);
                if(collector.getTotalHits() > 0)
                    continue;
                 //   } 
                updated++;
                while(exService.getQueue().size()>500)
                    Thread.sleep(1000);
                Runnable worker = null;
                for(int k=0;k<processNumber;k++)
                    if(keid % processNumber == k)
                    {
                        while(conns[k].isClosed())
                        {
                            Thread.sleep(5000);
                            conns[k] = db.dbConnect("jdbc:jtds:sqlserver://" + args[0] + ":1433/" + args[1] ,"aspconnect","aspconnect");
                        }
                        worker  = new MultiMainThread(al.get(i), kioskid, kioskname, conns[k], thr, k, maxsize);
                        break;
                    }


                exService.execute(worker);
                thr.addStackCount();
                //worker.run();
             }
             
         }
         while(rssize > 0000);

         exService.shutdown();
         while(!exService.isTerminated())
             Thread.sleep(1000);

         ireader.close();
         
         writer.commit();
         writer.forceMerge(maxmerge);
         writer.deleteUnusedFiles();
         writer.close();
         
         taxonomyWriter.commit();
         taxonomyWriter.close();
         MakeCorpus.makeCorpus(DISKLOC);
    }
    
};

