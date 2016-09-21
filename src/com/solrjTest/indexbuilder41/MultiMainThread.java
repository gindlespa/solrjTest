/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;
import indexbuilder41.utils.DoLanguageDetect;
import indexbuilder41.utils.LanguageDetectionResult;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author murphy
 */
public class MultiMainThread  implements Runnable{
    public long keid;
    public Connection conn;
    
    private volatile static int activecount;
    public String kioskid;
    public String kioskname;
    private final int maxsize;
    public static TaxonomyWriter taxowriter;
    public static FacetsConfig config;
    public static MurphyAnalyzer anal;
    public static IndexWriter writer;
    private final thrWriter thr;
    private final int pid;
    public static int getCount(){
        
        
        return activecount;
    }
    public MultiMainThread(long ke_id, String kioskid, String kioskname, Connection conn ,  thrWriter thr, int pid, int maxsize){
        this.keid = ke_id;
        this.kioskid = kioskid;
        this.kioskname = kioskname;
        this.conn = conn;
        this.maxsize = maxsize;
        this.thr = thr;
        this.pid = pid;
    }
    @Override
    public void run() {
        
        
        java.util.Date start = new java.util.Date();
        String sqlCount = "select  e.trackmodification as etimestamp, b.trackmodification as btimestamp, ISNULL(m.submissionid,'') as submissionid, ISNULL(e.raw_link,'') as raw_link,ISNULL(e.pdf_link,'') as pdf_link, ISNULL(e.pdf_pages,0) as pdf_pages, ISNULL(Doc_Number,'') as Doc_Number, ISNULL(orderdate,'') as orderdate, ISNULL(e.short_description,'') as title, b.ke_id, ISNULL(productionnumberbeg,'') as productionnumberbeg, ISNULL(e.lsname,'') as lsname, ISNULL(lsid,0) as lsid, isnull(SourceFilePath,'') as SourceFilePath  from elements e join bates_check_new b on e.ke_id = b.ke_id left join profile p on b.ke_id = p.ke_id left join elementsmetadata m on m.ke_id = e.ke_id where e.ke_id = " + keid;
        FieldType ft = new FieldType();
        ft.setStored(false);
        ft.setTokenized(true);
        
        //ft.setDocValueType(FieldInfo.DocValuesType.);
        ft.setStoreTermVectors(true);
        ft.setStoreTermVectorPositions(true);
        ft.setStoreTermVectorOffsets(true);
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        FieldType tkeid = new FieldType();
        
        tkeid.setDocValuesType(DocValuesType.NUMERIC);
        tkeid.setNumericType(FieldType.NumericType.LONG);
        tkeid.setStored(true);
        tkeid.setTokenized(false);
        
        
        Statement stm = null;
        ResultSet rs = null;
        try{
            
        
            stm = conn.createStatement();
            stm.setQueryTimeout(300);
            rs = stm.executeQuery(sqlCount);
 
            if(rs.next())
            {
                String etimestamp = rs.getString("etimestamp");
                String btimestamp = rs.getString("btimestamp");
                String submissionid = rs.getString("submissionid");
                String text = "";
                if(rs.getString("Doc_Number").length()>maxsize)
                    text = rs.getString("Doc_Number").substring(0, maxsize);
                else
                    text = rs.getString("Doc_Number");
                String raw_link = rs.getString("raw_link");
                String pdf_link = rs.getString("pdf_link");
                int pdfpages = rs.getInt("pdf_pages");
                String odate = rs.getString("orderdate");
                String title = rs.getString("title");
                int lsid = rs.getInt("lsid");
                String lsname = rs.getString("lsname");
                String prodNumber = rs.getString("productionnumberbeg");
                String SourceFilePath = rs.getString("SourceFilePath");
                String orderdate = "";
                if(odate.length()>=8)
                    orderdate = odate.substring(0, 8);
                else
                    orderdate = "00000000";
                
                java.util.Date anl = new java.util.Date();
                String Language="";
                String Languages="";
                String LanguageReport="";
                //try {
                    //LanguageDetectionResult[] result = DoLanguageDetect.getLanguage(text);
                    /*if(result.length > 0){
                        Language = result[0].language;
                        
                    }
                    else {
                    */
                        Language = "Unknown";
                        Languages = "unknown\n";
                        LanguageReport = "Uknown";
                            
                    /*        }
                    for(int lan = 0;lan < result.length;lan++){
                        Languages += result[lan].language + "\n";
                        LanguageReport += result[lan].language + "\t" + result[lan].percent + "\n";
                    }
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(MultiMainThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MultiMainThread.class.getName()).log(Level.SEVERE, null, ex);
                }*/



                Document doc = new Document();
                
                String ke_id = String.format("%012d", keid);
                LongField fld = new LongField("ke_id",keid,Store.YES);
                fld.setLongValue(keid);
                doc.add(new LongField("ke_id",keid,Store.YES));
                //SortedNumericSortField sld = new SortedNumericSortField("ke_id",SortField.Type.LONG);
                doc.add(new SortedNumericDocValuesField("keid", keid));
                //doc.add(new Field("text",text.toLowerCase(),ft));
                doc.add(new Field("language",Language,Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("languages",Languages,Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("languagereport",LanguageReport,Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("etimestamp",etimestamp,Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("btimestamp",btimestamp,Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("text",text.toLowerCase(),ft));
                //doc.add(new BinaryDocValuesField("text",new BytesRef(text)));
                doc.add(new Field("kioskid",kioskid,Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("submissionid",submissionid.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("kioskname",kioskname.toLowerCase(),Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("raw_link",raw_link.toLowerCase(),Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("pdf_link",pdf_link.toLowerCase(),Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("lsid",String.valueOf(lsid) ,Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("textsize",String.format("%012d", text.length()),Field.Store.YES,Field.Index.ANALYZED));
                
                doc.add(new Field("lsname",lsname,Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("sourcefilepath",SourceFilePath.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                String isProduced = "Non-Produced";
                if(prodNumber.trim().length()>0)
                    isProduced = "Produced";
                doc.add(new Field("isproduced",isProduced,Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("prodnumber",prodNumber.toLowerCase(),Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new SortedDocValuesField("prodnumber", new BytesRef(prodNumber)));
                doc.add(new Field("title",title.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("orderdate",orderdate,Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new SortedDocValuesField("orderdate", new BytesRef(orderdate)));
                doc.add(new Field("pdf_pages",String.format("%012d", pdfpages),Field.Store.YES,Field.Index.NOT_ANALYZED));
                rs.close();
                
                rs = stm.executeQuery("select originator_name,originatortypeid from elementsoriginators e join originators o on e.originator_id = o.originator_id where ke_id = " + ke_id);
                String persons = "";
                String auths = "";
                String tos = "";
                String ccs = "";
                String bccs = "";
                int pcount = 0;
                while(rs.next())
                {
                    String o = rs.getString("originator_name");
                    if(o.trim().length()==0)
                        continue;
                    doc.add(new FacetField("toriginator_name",o));
                    persons+=rs.getString("originator_name") + "\n";
                    pcount++;
                    int id = rs.getInt("originatortypeid");
                    if(id == 1)
                        auths+=rs.getString("originator_name") + "\n";
                    if(id == 2)
                        tos+=rs.getString("originator_name") + "\n";
                    if(id == 3)
                        ccs+=rs.getString("originator_name") + "\n";
                    if(id == 5)
                        bccs+=rs.getString("originator_name") + "\n";

                }
                rs.close();
                String types = "";
                rs = stm.executeQuery("select type_name from types t join types_shape s on t.type_id = s.shape_id where ke_id =" + ke_id);
                while(rs.next())
                {
                    types+=rs.getString("type_name") + "\n";
                    
                }
                rs.close();
                String produceddate = "";
                StringBuilder sb = new StringBuilder();
                java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
                rs = stm.executeQuery("select dateproduced, productionnumberbeg, productionnumberend, paddingcount,char(paddingcharacterid) as pad, productionnumberprefixname from productionnumbers n join productionnumberprefixes p on n.productionnumberprefixid = p.productionnumberprefixid where ke_id =" + ke_id);
                if(rs.next()){
                    try
                    {
                        produceddate = dateFormat.format(rs.getDate(1));
                    }
                    catch(Exception e)
                    {
                        produceddate = "00000000";
                    }
                    try
                    {
                        int beg = rs.getInt(2);
                        int end = rs.getInt(3);
                        int count = rs.getInt(4);
                        String chr = rs.getString(5);
                        String prefix = rs.getString(6);
                        for(int j = beg; j <= end; j++)
                        {
                            sb.append(String.format(prefix + "%" + chr + count + "d\r\n", j));
                        }
                    }
                    catch(Exception e)
                    {
                         e.printStackTrace(System.out);
                    }
                }

                rs.close();
                
                String producingparty = "";
                String mattername = "";
                int matterid = 0;
                int producingpartyid = 0;
                rs = stm.executeQuery("Select DISTINCT df.DefendantName as producingparty ,m.matterid ,m.mattername, w.PartyId as producingpartyid from Elements E inner join [lsname] l on l.lsid = e.lsid inner join [witness] w on w.witnessid = l.witnessid inner join [matter] m on w.Matterid  = m.matterid inner join Defendantslist df on df.DefendantId = w.PartyID Where e.Status_id = 'L' and e.ke_id =" + ke_id);
                if(rs.next())
                {
                    producingparty = rs.getString("producingparty");
                    matterid = rs.getInt("matterid");
                    mattername = rs.getString("mattername");
                    producingpartyid = rs.getInt("producingpartyid");

                }	
                rs.close();
                
                // NEW MARKFIELD DATA FOR INDEX 01142015 wa
                String Author = "";
                String UniformTitle = "";
                String PublicationInformation = "";
                String LongSubject = "";  
                String ISBN = ""; 
                /*        
                rs = stm.executeQuery("SELECT isnull(Author,'') as Author, isnull(UniformTitle,'') as UniformTitle, "
                        + "isnull(PublicationInformation,'') as PublicationInformation, isnull(LongSubject,'') as LongSubject, isnull(ISBN,'') as ISBN from dbo.MARCRecord_Information where ke_id=" + ke_id);
                if(rs.next())
                {
                    Author = rs.getString("Author");
                    UniformTitle = rs.getString("UniformTitle");
                    PublicationInformation = rs.getString("PublicationInformation");
                    LongSubject = rs.getString("LongSubject");
                    ISBN = rs.getString("ISBN");
                    //new additional fields 01142015 wa
                    doc.add(new Field("Author",Author.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                    doc.add(new Field("UniformTitle",UniformTitle.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                    doc.add(new Field("PublicationInformation",PublicationInformation.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                    doc.add(new Field("LongSubject",LongSubject.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                    doc.add(new Field("ISBN",ISBN.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                    //end region

                }	
                rs.close();
                // end region
                */
                doc.add(new Field("producingparty",producingparty.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("productionnumbers",sb.toString().toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("productiondate",produceddate.toLowerCase(),Field.Store.YES,Field.Index.NOT_ANALYZED));

                doc.add(new Field("types",types.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("persons",persons.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("auths",auths.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("tos",tos.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("ccs",ccs.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("bccs",bccs.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                
                doc.add(new Field("matterid",Integer.toString(matterid),Field.Store.YES,Field.Index.NOT_ANALYZED));
                doc.add(new Field("mattername",mattername.toLowerCase(),Field.Store.YES,Field.Index.ANALYZED));
                doc.add(new Field("producingpartyid",Integer.toString(producingpartyid),Field.Store.YES,Field.Index.NOT_ANALYZED));
                
                
                
                FacetConfig.update(doc);
                adddoc(doc,sb,keid);
                java.util.Date done = new java.util.Date();
                //System.out.println(al.get(i));
                //System.out.println(sb.toString());
                System.out.println(pid + " " + keid + " start" + start.toString() + " analyze" + anl.toString() + " done" + done.toString() + " Language:" + Language);

            }
        else
            rs.close();
        
        }
        catch(java.lang.OutOfMemoryError err){
                    err.printStackTrace(System.out);
                    //System.out.print(keid + " " + "Out of Memory Error");
                }
        catch(java.sql.SQLException e)
        {
            e.printStackTrace(System.out);
        }
        /*
        catch(java.lang.Exception e)
        {
            System.out.println(keid + " " + e.getMessage());
        }
        * */
        finally{
            try{
            rs.close();
            }catch(SQLException ex){
                
            }catch(Exception ex){
                
            }
            try{
            stm.close();
            }catch(SQLException ex){
                
            }catch(Exception ex){
                
            }
         thr.subtractStackCount();   
        }
    }
    private  static void adddoc(Document doc, StringBuilder sb, long keid)
    {
        //synchronized(writer){
        try{
            writer.addDocument(config.build(taxowriter, doc));
        }
        catch(java.lang.OutOfMemoryError oom ){

            System.out.println(keid + " Out of memory!");
        }
        catch(org.apache.lucene.index.CorruptIndexException com ){

            System.out.println(keid + " Index Corrupt!");
        }
        catch(java.io.IOException ex){
            System.out.println(keid + " " + ex.getMessage());
        }
        //}
    }
    

}
    
