/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.CompiledAutomaton;

/**
 *
 * @author murphy
 */
public class spanMaker {
    private IndexSearcher irs;
    public spanMaker(IndexSearcher isr){
        irs = isr;
    }
     public SpanQuery formulateSpan(String qry,String field) throws Exception
        {
            MurphyAnalyzer sa = new MurphyAnalyzer(null);
            qry = qry.replaceAll("[^0-9a-zA-z/*\\r\\n]", " ").trim();
            if (qry.length() == 0)
                return null;
            List<String> lst = new LinkedList<String>(Arrays.asList(qry.split("\r\n")));
            while(lst.remove(""));
            
            
            SpanQuery[] sqa = new SpanQuery[lst.size()];
            for (int i = 0; i < lst.size(); i++)
            {
                String tmpquery = lst.get(i).toLowerCase();
                //tmpquery = sa.
                sqa[i] = makeSpanQuery(tmpquery, field, irs);
            }
            SpanOrQuery sq = new SpanOrQuery(sqa);
            return sq;
        }
    protected SpanQuery makeSpanQuery(String qry,String field,  IndexSearcher irs) throws Exception
        {
            Pattern patt = Pattern.compile("\\/[0-9][0-9]*");
            Matcher mat= patt.matcher(qry);
            if (mat.find())
            {
                //String[] mats = qry.;
                String within = mat.group();
                String[] subs = patt.split(qry, 2);
                SpanQuery spn1 = makeSpanQuery(subs[0],field, irs);
                SpanQuery spn2 = makeSpanQuery(subs[1],field, irs);
                String s = within.substring(1, within.length());
                int count = 0;
                try{
                     count = Integer.parseInt(s);
                }        
                catch(Exception e){
                    int a = 1;
                
                }
                SpanNearQuery spn = new SpanNearQuery(new SpanQuery[] { spn1, spn2 }, count, false);
                return spn;
            }

            Pattern Murphy = Pattern.compile("[^a-z0-9]*");
            String qryanal = qry.replaceAll("[^a-z0-9*]", " ").trim();
            SpanQuery sq = null;
            List<String> lst = new LinkedList<String>(Arrays.asList(qry.split("\\s")));
                while(lst.remove(""));
            String[] qrys = lst.toArray(new String[lst.size()]);
            if (qrys.length == 1)
            {
                if (qrys[0].contains("*"))
                {
                    sq = makeWildCardSpanQuery(qrys[0], field, irs); //.substring(0, qrys[0].length() - 1)
                    //if (sq.toString().equals("spanOr([])"))
                    //    sq = new SpanTermQuery(new Term("text", qrys[0].substring(0, qrys[0].length() - 1)));
                }
                else
                    sq = new SpanTermQuery(new Term(field, qrys[0]));

            }
            else
            {
                SpanQuery[] stq = new SpanQuery[qrys.length];
                for (int i = 0; i < qrys.length; i++)
                {
                    stq[i] = makeSpanQuery(qrys[i],field, irs);
                }
                sq = new SpanNearQuery(stq, 0, true);
            }
            return sq;
        }
    
        public static SpanQuery makeWildCardSpanQuery(String field,String qry, IndexSearcher irs) throws Exception
        {
            IndexReader reader = irs.getIndexReader();
            HashSet<String> hs = new HashSet<>();
            //IndexReader reader = IndexReader.open(indexpath);
            SpanOrQuery soq;
            //if(qry.endsWith("*"))
            //    qry = qry.substring(0,qry.length()-1);
            List<SpanTermQuery> stq = new ArrayList<SpanTermQuery>();
            List<LeafReaderContext> leaves = reader.leaves();
            for (LeafReaderContext context : leaves) {
                LeafReader leafReader = context.reader();
                    Terms terms = leafReader.terms(field);
                    Automaton automaton = WildcardQuery.toAutomaton(new Term(field,qry)); // this transforms the wildcard syntax with ? and * to a state machine 
                    CompiledAutomaton compiled = new CompiledAutomaton(automaton); // copiles the state machine 
                    TermsEnum tenum = compiled.getTermsEnum(terms); // "terms" can be retrieved from AtomicReader 
                    BytesRef term;
                    while ((term = tenum.next()) != null )
                    {
                        String sterm = term.utf8ToString();
                        if(!hs.contains(sterm)){
                            stq.add(new SpanTermQuery(new Term(field, sterm)));
                            hs.add(sterm);
                        }

                    }    
                    
            }
            
            SpanQuery[] sqnn = new SpanQuery[stq.size()];
            for(int k = 0;k< stq.size(); k++){
                sqnn[k] = stq.get(k);
            }
            soq = new SpanOrQuery(sqnn);
                    
            //reader.close();
            return soq;
        }
    
}
