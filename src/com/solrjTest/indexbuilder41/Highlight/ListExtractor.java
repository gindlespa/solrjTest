package com.solrjTest.indexbuilder41.Highlight;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

class ListExtractor implements Runnable
     {
        String text;
        private Boolean isDone = false;
        private ArrayList<MyToken> tokens;
        
        private TokenStream ts;
        public void setText(String txt){
            text = txt;
        }
        public void setTokenStream(TokenStream tokenStream){
            ts = tokenStream;
        }
         public ArrayList<MyToken> getTokenStream(){
            return tokens;
        }
        public Boolean done(){
            return isDone;
        }
        
        public void run(){
            try {
                ts.reset();
            } catch (IOException ex) {
                Logger.getLogger(GetHighlightsFromPdf.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(isDone || text == null || ts == null)
                System.out.println("Process not ready to start!");
            try {
                tokens = getList(ts);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            char[] textChar;
            textChar = text.toCharArray();
            int charOffset = 0;
            int page = 0;
            Iterator<MyToken> iter = tokens.iterator();
            MyToken lasttoken=null;
            if(iter.hasNext()){
                MyToken token = iter.next();
                for (int i = 0; i < textChar.length; i++)
                {
                    
                if(token.position==0){
                    if(iter.hasNext()){
                        token.page = lasttoken.page;
                        token.wordOffset = lasttoken.wordOffset;
                        token = iter.next();
                        i--;
                        continue;
                    }
                }    
                
                lasttoken = token;
                    if(token==null)
                        break;
                    char a = textChar[i];
                    if (a == '\013')
                    {
                        charOffset = 0;
                        page++;
                    }
                    else if (a != '\t')
                    {
                        charOffset++;
                    }
                    if(i==token.beginOffset)
                    {
                        
                        
                        token.page = page;
                        token.wordOffset = charOffset;
                        if(iter.hasNext())
                            token = iter.next();
                        else
                            break;
                        
                    }
                }
            }
            isDone = true;
            try {
                ts.close();
            } catch (IOException ex) {
                Logger.getLogger(GetHighlightsFromPdf.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        private ArrayList<MyToken> getList(TokenStream ts) throws IOException
        {
            ArrayList<MyToken> al = new ArrayList<MyToken>();
            boolean hasToken = false;
            hasToken = ts.incrementToken( );
            while (hasToken) 
            {
                
                    MyToken mt = new MyToken();
                    mt.term = ts.getAttribute(CharTermAttribute.class).toString( );
                    OffsetAttribute offsetAttribute = ts.getAttribute(OffsetAttribute.class);
                    mt.beginOffset = offsetAttribute.startOffset();
                    mt.endOffset = offsetAttribute.endOffset();
                    PositionIncrementAttribute poatt = ts.getAttribute(PositionIncrementAttribute.class);
                    mt.position = poatt.getPositionIncrement();
                    al.add(mt);
                    hasToken = ts.incrementToken( );
                
            }

            return al;
            
        }
    } 