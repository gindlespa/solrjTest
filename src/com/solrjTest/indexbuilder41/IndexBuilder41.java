///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.solrjTest.indexbuilder41;
//
//import com.solrjTest.indexbuilder41.utils.*;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;
//import java.nio.file.Paths;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.ArrayList;
//
//import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.TextField;
//import org.apache.lucene.facet.FacetField;
//import org.apache.lucene.facet.FacetsConfig;
//import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
//import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
//import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.index.IndexWriterConfig.OpenMode;
//import org.apache.lucene.index.MergePolicy;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.TermQuery;
//import org.apache.lucene.search.TopDocs;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.store.MMapDirectory;
//import org.apache.lucene.store.NIOFSDirectory;
//import org.apache.lucene.util.Version;
//import org.apache.lucene.document.FieldType;
///**
// *
// * @author murphy
// */
//public class IndexBuilder41 {
//
//    /**
//     * @param args the command line arguments
//     */
//    private final static FacetsConfig config = new FacetsConfig();
//
//        public static void main(String[] args) {
//        // TODO code application logic here
//     try{
//                config.setHierarchical("torderdate", true);
//                config.setMultiValued("toriginator_name", true);
//                config.setMultiValued("ttypes", true);
//                int a = 1;
//                String DISKLOC = args[2] + args[1] + "dbor/";
//                String TAXO = DISKLOC + "/taxo/";
//                int maxKeid = 0;
//                //Directory dir = new org.apache.lucene.store.Directory(DISKLOC);
//                java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
//                 MurphyAnalyzer anal = new MurphyAnalyzer();
//                 File mxk = new File(DISKLOC);
//                 boolean doAppend = mxk.exists();
//                 java.io.File fl;
//                 IndexReader ireader = null;
//                 IndexSearcher isearcher = null;
//                 TaxonomyWriter taxonomyWriter = new DirectoryTaxonomyWriter(new MMapDirectory(Paths.get(TAXO)), OpenMode.CREATE);
//                  //CategoryDocumentBuilder categoryDocumentBuilder = new CategoryDocumentBuilder(taxonomyWriter, new DefaultFacetIndexingParams());
//
//
//                     LimitTokenCountAnalyzer ltc = new LimitTokenCountAnalyzer(anal,Integer.MAX_VALUE);
//                     IndexWriterConfig iwc = new IndexWriterConfig(ltc);
//                     MergePolicy mc = iwc.getMergePolicy();
//                     iwc.setRAMBufferSizeMB(512);
//                     IndexWriter writer = new IndexWriter(NIOFSDirectory.open(Paths.get(DISKLOC)), iwc);
//
//                  //writer.sset
//                 DB db = new DB();
//                 int updated = 0;
//                 String kioskid;
//                 String kioskname;
//                 java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://" + args[0] + ":1433/" + args[1] ,"aspconnect","aspconnect");
//
//                 java.util.ArrayList<Integer> al;
//                 boolean didUpdate = false;
//                 do{
//                      java.util.Date start = new java.util.Date();
//
//                     Statement stm = conn.createStatement();
//                     stm.setQueryTimeout(500);
//                     ResultSet rs;
//                     if(args.length == 5){
//
//                        rs = stm.executeQuery("Select top 1000 e.ke_id from elements e join bates_check_new b on b.ke_id = e.ke_id join portfolioitem pi on e.ke_id = pi.keid where e.ke_id > " + maxKeid + " AND e.status_id = 'l' and portfolioid in ('" + args[3] + "')ORDER BY e.ke_id");
//                     }
//                     else
//                        rs = stm.executeQuery("Select top 1000 e.ke_id from elements e join bates_check_new b on b.ke_id = e.ke_id where e.ke_id > " + maxKeid + " AND e.status_id = 'l' ORDER BY e.ke_id");
//                        //rs = stm.executeQuery("select top 500 ke_id  from ElementsMetadata Where ke_id in (select keid From portfolioItem        Where portfolioID  = '41F39BBB-BBE4-4690-901E-5B9A74D7D30D' ) and ke_id > " + maxKeid + "Order by 1");
//
//                     al = new ArrayList<Integer>();
//                     while(rs.next()){
//                             int keid = rs.getInt("ke_id");
//                             al.add(keid);
//                     }
//                     rs.close();
//
//                     System.out.println(maxKeid + " " + updated);
//                     for(int i =0; i < al.size(); i++){
//
//                            String ke_id = String.format("%012d", al.get(i));
//                            System.out.println(ke_id);
//                            int keid = Integer.parseInt(ke_id);
//                                if(keid > maxKeid)
//                                    maxKeid = keid;
//
////                                    Query qr = new TermQuery(new Term("ke_id",ke_id));
////                                    TopDocs hts = isearcher.search(qr,1);
////                                    if(hts.totalHits > 0)
////                                        continue;
//
//                            didUpdate = true;
//                            String sqlCount = "select  e.trackmodification as etimestamp, b.trackmodification as btimestamp, ISNULL(m.submissionid,'') as submissionid, ISNULL(e.raw_link,'') as raw_link,ISNULL(e.pdf_link,'') as pdf_link, ISNULL(e.pdf_pages,0) as pdf_pages, ISNULL(Doc_Number,'') as Doc_Number, ISNULL(orderdate,'') as orderdate, ISNULL(e.short_description,'') as title, b.ke_id, ISNULL(productionnumberbeg,'') as productionnumberbeg, ISNULL(e.lsname,'') as lsname, ISNULL(lsid,0) as lsid, isnull(SourceFilePath,'') as SourceFilePath  from elements e join bates_check_new b on e.ke_id = b.ke_id left join profile p on b.ke_id = p.ke_id left join elementsmetadata m on m.ke_id = e.ke_id where e.ke_id = " + keid;
//                            stm = conn.createStatement();
//                            rs = stm.executeQuery(sqlCount);
//                            if(rs.next())
//                            {
//                                String text = rs.getString("Doc_Number");
//                                String raw_link = rs.getString("raw_link");
//                                String pdf_link = rs.getString("pdf_link");
//                                String odate = rs.getString("orderdate");
//                                String title = rs.getString("title");
//                                String lsname = rs.getString("lsname");
//                                String prodNumber = rs.getString("productionnumberbeg");
//                                String orderdate = "";
//				int lsid = rs.getInt("lsid");
//                                int pdfpages = rs.getInt("pdf_pages");
//
//                                if(odate.length()>8)
//                                    orderdate = odate.substring(0, 8);
//                                else
//                                    orderdate = "00000000";
//                                java.util.Date anl = new java.util.Date();
//
//
//                                Document doc = new Document();
//                                FieldType ft = new FieldType(TextField.TYPE_STORED);
//                                ft.setStoreTermVectors(true);
//                                ft.setStoreTermVectorOffsets(true);
//                                ft.setStoreTermVectorPositions(true);
//                                doc.add(new Field("text",text.toLowerCase(),ft));
//                                doc.add(new Field("class",args[4],ft));
//                                doc.add(new Field("ke_id",ke_id,ft));
//                                writer.addDocument(doc);
//
//
//
//                                //System.out.println(al.get(i));
//                                //System.out.println(sb.toString());
//
//
//                                }
//                            else
//                                rs.close();
//
//
//                        }
//                     java.util.Date done = new java.util.Date();
//                     System.out.println("start" + start.toString()  + " done" + done.toString());
//                 }
//                 while(1==2);//(al.size()>0);
//                 writer.commit();
//                 writer.close();
//                 taxonomyWriter.close();
//                 System.out.println(maxKeid + " " + updated);
//
//                 FileOutputStream fos = new FileOutputStream(DISKLOC + "log.txt",true);
//                 OutputStreamWriter out = new OutputStreamWriter(fos,"UTF-8");
//                 java.util.Date date = new java.util.Date();
//                System.out.println();
//                 out.write(dateFormat.format(date) + "\t" + maxKeid + "\t " + updated + "\r\n");
//                 out.close();
//                 fos.close();
//
//            }
//        catch(Exception e){
//                e.printStackTrace();
//                System.err.println("here");
//        }
//
//    }
//}
