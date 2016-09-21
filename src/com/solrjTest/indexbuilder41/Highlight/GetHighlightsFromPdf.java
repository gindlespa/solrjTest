/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.solrjTest.indexbuilder41.Highlight;

import indexbuilder41.mainSearch;
import indexbuilder41.readMSQ;
import java.io.IOException;
import java.util.ArrayList;
import indexbuilder41.MurphyAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.highlight.WeightedSpanTerm;
import org.apache.lucene.search.highlight.WeightedSpanTermExtractor;
import org.apache.lucene.store.RAMDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author murphy
 */
public class GetHighlightsFromPdf {
    
    public String getLocations(PDDocument doc, String[] msqs, String[] color) throws IOException, Exception{
        MurphyAnalyzer anal = new MurphyAnalyzer(); 
        String text = "";
            PDFTextStripper pdf = new PDFTextStripper();
            pdf.setPageEnd("");
            pdf.setPageStart("\013");
            pdf.setWordSeparator("\t");
            pdf.setLineSeparator("\t");

            text = pdf.getText(doc).toLowerCase();
        
         RAMDirectory dir = new RAMDirectory();
         LimitTokenCountAnalyzer ltc;
        ltc = new LimitTokenCountAnalyzer(anal,Integer.MAX_VALUE);
        IndexWriterConfig iwc = new IndexWriterConfig(ltc);
        MergePolicy mc = iwc.getMergePolicy();
        iwc.setRAMBufferSizeMB(512);
        
        
        IndexWriter writer = new IndexWriter(dir, iwc);
        

         org.apache.lucene.document.Document memdoc = new org.apache.lucene.document.Document();
         FieldType ft = new FieldType();
        ft.setStored(true);
        ft.setTokenized(true);
        //ft.setDocValueType(FieldInfo.DocValuesType.);
        ft.setStoreTermVectors(true);
        ft.setStoreTermVectorPositions(true);
        ft.setStoreTermVectorOffsets(true);
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        memdoc.add(new Field("text", text, ft));
        writer.addDocument(memdoc);
        writer.close();
        IndexReader ireader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(ireader);
        ListExtractor le = new ListExtractor();
        le.text = text;
        MurphyAnalyzer anal2 = new MurphyAnalyzer(null);
         le.setTokenStream(TokenSources.getTokenStream("text",text,anal2));
         //Thread extractThread = new Thread(le);
         //extractThread.run();
         le.run();
        ArrayList<MyToken> al;
        al = le.getTokenStream();
        ArrayList<HighlightLocation> locs = new ArrayList();
         for(int imsq=0;imsq<msqs.length;imsq++)
         {
         
         mainSearch m = new mainSearch();    
         String msq = msqs[imsq];
         readMSQ rmsq = new readMSQ(msq);
         Query query = m.makeSpanQueries(rmsq,searcher); 
         
         
         
         TopScoreDocCollector tsc = TopScoreDocCollector.create(1);
            searcher.search(query, tsc);

            //end new
            ScoreDoc[] sd = tsc.topDocs().scoreDocs;
            int count = sd.length;
            int maxResults = count;
            while(!le.done())
                Thread.sleep(100);
            
            for (int i = 0; i < maxResults; i++)
            {

                //Document hitDoc = searcher.doc(sd[i].doc);
                TokenStream stream = TokenSources.getTokenStream(ireader.getTermVector(0, "text"));
                stream.reset();
                //Highlighter highlighter = new Highlighter(); //.getBestFragment()
                WeightedSpanTermExtractor ste = new WeightedSpanTermExtractor();
                
                ste.setExpandMultiTermQuery(true);
                //ste.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
                
                ste.setWrapIfNotCachingTokenFilter(true);
                ste.setUsePayloads(false);
                java.util.HashMap map = (java.util.HashMap) ste.getWeightedSpanTermsWithScores(query, stream, "text", ireader) ;
                stream.close();
                int size = map.size();
                java.util.Set set = map.keySet();
                Object[] keys = set.toArray();
                for (int k = 0; k < size; k += size)
                {
                    WeightedSpanTerm tt = (WeightedSpanTerm) map.get(keys[k]);
                    java.util.List ll = tt.getPositionSpans();
                    Object[] list = ll.toArray();
                    //for (int numSpan = 0; numSpan < 1; numSpan++) 
                    for (int numSpan = 0; numSpan < list.length; numSpan++)
                    {
                        org.apache.lucene.search.highlight.PositionSpan spanArray = (org.apache.lucene.search.highlight.PositionSpan)list[numSpan];
                        //for(int j=spanArray.start;j<spanArray.end + 1; j++)
                        {
                            HighlightLocation loc = new HighlightLocation();
                            locs.add(loc);
                            MyToken token = al.get(spanArray.start);
                            MyToken endtoken = al.get(spanArray.end);
                            int start = token.wordOffset-1;
                            int length = endtoken.term.length();
                            loc.start = start;
                            if(token.page==endtoken.page)
                                loc.length = length+endtoken.wordOffset-token.wordOffset;
                            else
                                loc.length = token.term.length();
                            loc.page = token.page -1;
                            loc.color = color[imsq];
                            loc.saStart = spanArray.start;
                            loc.saEnd = spanArray.end;
                           
                            
                        }

                    }
                    //HighlightLocationUtil.sortLocations(locs);
                   
                    

                }
                //}
            }
            
         }
         
         return GetPositionFromHighlights.getPositions(doc, locs);
    }
    protected String getgloss(ArrayList<MyToken> al,int location,int end, int length)
    {
        int size = end -location;
        StringBuilder sb = new StringBuilder();
        for(int i = location - length; i < location + size + length + 1; i++)
        {
            if(i<0 || i >= al.size())
                continue;
            if(i == location)
                sb.append("<token>");
            if(i > 1)
                sb.append(" ");
            sb.append(al.get(i).term);
            if(i == location+size)
                sb.append("</token>");
        }
        return sb.toString();
    }
    protected String getOffsets(String text, int startOffset, int length)
        {
            boolean onpage;
            int page = 0;
            int charCount = 0;
            char[] textChar;

            onpage = true;
            page = 0;
            charCount = 0;
            textChar = text.toCharArray();
            for (int i = startOffset; i >= 0; i--)
            {
                char a = textChar[i];
                if (a == '\013')
                {
                    onpage = false;
                    page++;
                }
                else if (a == '\t')
                {
                    //charCount++;
                }
                else if (onpage)
                {
                    charCount++;
                }

            }
            page--;
            return "<loc pg=" + page + " pos=" + charCount + " len=" + (length -1) + ">\r\n";
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
    class textExtractor{
        private String text;
        private Boolean isDone;
        private PDDocument pddoc;
        public void setPdfPath(PDDocument doc){
            pddoc = doc;
        }
        public Boolean done(){
            return isDone;
        }
        public void extract() throws Exception{
            if(isDone || pddoc == null)
                throw new Exception("Process not ready to start!");
            PDFTextStripper pdf = new PDFTextStripper();
            pdf.setPageEnd("");
            pdf.setPageStart("\013");
            pdf.setWordSeparator("\t");
            pdf.setLineSeparator("\t");

            text = pdf.getText(pddoc);
            isDone = true;
        }
    }
    
}
