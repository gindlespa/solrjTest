///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.solrjTest.indexbuilder41.utils;
//
//import com.solrjTest.indexbuilder41.MurphyAnalyzer;
//import com.solrjTest.indexbuilder41.mainSearch;
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
////import org.apache.commons.lang3.mutable.MutableInt;
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.LeafReader;
////import org.apache.lucene.index.SlowCompositeReaderWrapper;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.highlight.TokenSources;
//import org.apache.lucene.search.highlight.WeightedSpanTerm;
//import org.apache.lucene.search.highlight.WeightedSpanTermExtractor;
//
///**
// *
// * @author murphy
// */
//public class OutputPositions {
//     final private  ArrayList<String> outList= new ArrayList<>();
////     private  ArrayList<termOffset> getList(TokenStream ts, HashMap<String,String> map) throws Exception
////        {
////            int size = 0;
////            ArrayList<termOffset> lst = new ArrayList<>();
////            boolean hasToken;
////            termOffset to = new termOffset();
////            hasToken = ts.incrementToken( );
////            while (hasToken)
////            {
////                String nexus;
////                String str = ts.getAttribute(CharTermAttribute.class).toString();
////                if(str.length()>8 && (str.substring(0, 9).equals("snexuster") || str.substring(0, 9).equals("nexusterm") ||str.substring(0, 9).equals("nexustsid") || str.substring(0, 9).equals("snexustsi"))){
////                    if(map.containsKey(str))
////                        to.startoffset = map.get(str).trim().split("\\s").length - 1;
////                }
////                else{
////
////
////                   to = new termOffset();
////                   to.startoffset = 0;
////                   to.endoffset = 0;
////                   to.term = str;
////                   lst.add(to);
////                }
////
////
////                hasToken = ts.incrementToken( );
////            }
////
////            return lst;
////        }
////    private String getSpan(int start, int end, ArrayList<termOffset> lst, int size)
////    {
////        return getSpan(start, end, lst, size, "<b>", "</b>");
////    }
////    private  String getSpan(int start, int end, ArrayList<termOffset> lst, int size, String startTag, String endTag)
////    {
////        MutableInt tstart = new MutableInt(start);
////        MutableInt tend = new MutableInt(end);
////        String mid = startTag + addString(tstart,tend,lst,true) + endTag;
////        MutableInt prebeg = new MutableInt(tstart.getValue() - size >= 0 ? tstart.getValue() -size : 0);
////        MutableInt postend = new MutableInt(tend.getValue() + size > (lst.size() - 1) ? (lst.size() -1) : tend.getValue() + size);
////        String begin="";
////        String finish="";
////        tstart.subtract(1);
////        tend.add(1);
////        try {
////            begin = addString(prebeg, tstart, lst);
////            finish = addString(tend, postend, lst);
////        } catch (Exception e) {
////            Logger.getLogger(OutputPositions.class.getName()).log(Level.SEVERE, null, e);
////        }
////
////        return "<td align='right'>" + begin + "</td><td align='center'>" + mid + "</td><td align='left'>" + finish + "</td>";
////    }
////     private  String addString(MutableInt start, MutableInt end, ArrayList<termOffset> lst)
////    {
////        return addString(start, end, lst, false);
////    }
////    private  String addString(MutableInt start, MutableInt end, ArrayList<termOffset> lst, Boolean addoff)
////    {
////        StringBuilder sb = new StringBuilder();
////        int len = 0;
////        for(int i = start.getValue(); i <= end.getValue(); i++ ){
////            termOffset to = lst.get(i);
////            sb.append(to.term);
////            if(addoff  && i==end.getValue()){
////                 end.add(to.startoffset);
////                addoff = false;
////            }
////            if(i != end.getValue())
////                sb.append(" ");
////
////        }
////        return sb.toString();
////    }
////    public  String[] outputResultsWithPosition(Query query, ScoreDoc[] hits, IndexReader reader,int prox, GlobConfig gc){
////        return outputResultsWithPosition(query, hits, reader,prox, "<b>","</b>", gc);
////
////    }
////    public  String[] outputResultsWithPosition(Query query, ScoreDoc[] hits, IndexReader reader,int prox, String startTag, String endTag, GlobConfig gc)
////    {
////        MurphyAnalyzer anal=null;
////         try {
////             anal = new MurphyAnalyzer();
////         } catch (Exception ex) {
////             Logger.getLogger(OutputPositions.class.getName()).log(Level.SEVERE, null, ex);
////         }
////        //QueryParser qp = new QueryParser("text",new MurphyAnalyzer());
////        DocumentPreLoader dpl = new DocumentPreLoader();
////        for (int i = 0; i <hits.length ; i++)
////        {
////            try{
////                org.apache.lucene.document.Document hitDoc = null;
////                int docid = hits[i].doc;
////                try{
////
////                    hitDoc = reader.document( docid);
////                }
////                catch(org.apache.lucene.index.CorruptIndexException ex){
////                    System.out.println(ex.getMessage());
////                    return null;
////                }
////                catch(java.io.IOException ex){
////                    System.out.println(ex.getMessage());
////                    return null;
////                }
////                LeafReader r = SlowCompositeReaderWrapper.wrap(reader);;
////                //String textpath = hitDoc.get("textpath");
////                long ke_id = Long.parseLong(hitDoc.get("ke_id"));
////                dpl.add(ke_id);
////                //String orderdate = hitDoc.get("orderdate");
////                //String text;
////
////
////                TokenStream stream = null;
////
////                ArrayList<termOffset> lst = null;
////                try{
////
////                    stream = TokenSources.getTokenStreamWithOffsets(reader, docid, "text");
////
////                    stream.reset();
////
////
////                    lst = getList(stream, anal.getTermMap());
////
////                    stream.end();
////                    stream.close();
////                    stream = TokenSources.getTokenStreamWithOffsets(reader, docid, "text");
////                    stream.reset();
////                }
////                catch(IOException ex)
////                {
////                    System.out.print("FAILTURE STREAM");
////                    ex.printStackTrace(System.out);
////                }
////                catch(Exception ex)
////                {
////                    System.out.print("FAILTURE STREAM");
////                    ex.printStackTrace(System.out);
////                }
////
////                WeightedSpanTermExtractor ste = new WeightedSpanTermExtractor();
////                java.util.HashMap map = null;
////                try
////                {
////                    //ste.setExpandMultiTermQuery(true);
////
////                    map = (java.util.HashMap)ste.getWeightedSpanTerms(query, stream,"text");
////                }
////                catch(java.io.IOException ex)
////                {
////                    ex.printStackTrace(System.out);
////                    return null;
////
////                }
////                catch(Exception ex)
////                {
////                    ex.printStackTrace(System.out);
////                    return null;
////
////                }
////                stream.end();
////                stream.close();
////                int size = map.size();
////
////                java.util.Set set = map.keySet();
////                Object[] keys = set.toArray();
////
////
////                StringBuilder sb = new StringBuilder();
////                List<HighlightLocation> locs = new ArrayList<HighlightLocation>();
////                for (int k = 0; k < size; k += size)
////                {
////                    WeightedSpanTerm tt = (WeightedSpanTerm)map.get(keys[k]);
////                    java.util.List ll = tt.getPositionSpans();
////                    Object[] list = ll.toArray();
////                    //for (int numSpan = 0; numSpan < 1; numSpan++)
////                    for (int numSpan = 0; numSpan < list.length; numSpan++)
////                    {
////                        HighlightLocation loc = new HighlightLocation();
////
////
////                        org.apache.lucene.search.highlight.PositionSpan spanArray = (org.apache.lucene.search.highlight.PositionSpan)list[numSpan];
////                        loc.start = spanArray.start;
////                        loc.end =  spanArray.end;
////                        loc.length = loc.end - loc.start + 1;
////                        locs.add(loc);
////                    }
////                }
////                String docViewNum = String.valueOf(ke_id);
////                if(hitDoc.get("docnumber")!=null && hitDoc.get("docnumber").trim().length()>0)
////                    docViewNum = hitDoc.get("docnumber");
////                if(hitDoc.get("prodnumber")!=null && hitDoc.get("prodnumber").trim().length()>0)
////                    docViewNum = hitDoc.get("prodnumber");
////                String pdf_pages = hitDoc.get("pdf_pages");
////                DecimalFormat df = new DecimalFormat("0.00##");
////                Iterator<HighlightLocation> itt = locs.iterator();
////                //if(itt.hasNext()){
////                    sb.append("<tr><td>");
////                    sb.append(startTag.replace("#keid", String.valueOf(ke_id)));
////                    sb.append(docViewNum);
////                    sb.append(endTag);
////                    sb.append("</td><td/><td>");
////                    sb.append("Page Count:");
////                    int pages = 1;
////                    try{
////                    pages = Integer.parseInt(pdf_pages);
////                    }
////                    catch(Exception e){}
////                    sb.append(pages);
////                    sb.append("</td></tr>");
////                //}
////                while (itt.hasNext()){
////                    HighlightLocation loc = itt.next();
////
////                    //sb.append(df.format(loc.start*100.0/lst.size()));
////                    //sb.append("%");
////
////                    sb.append("<tr>");
////                    sb.append(getSpan(loc.start,loc.end,lst,prox,startTag.replaceAll("#keid", String.valueOf(ke_id)), endTag));
////                    sb.append("</tr>");
////                }
////
////                outList.add(sb.toString());
////            }
////            catch(IOException ex){
////                Logger.getLogger(mainSearch.class.getName()).log(Level.SEVERE, null, ex);
////            }
////        }
////        if(gc.getDocumentPreLoad())
////            dpl.start(reader, gc);
////        String[] s = new String[outList.size()];
////        return outList.toArray(s);
////    }
////    class termOffset{
////        int startoffset = 0;
////        int endoffset = 0;
////        String term = "";
////
////    }
//}
