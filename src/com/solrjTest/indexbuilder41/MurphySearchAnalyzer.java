package com.solrjTest.indexbuilder41;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import indexbuilder41.utils.DB;
import indexbuilder41.utils.GetSynonymList;
import indexbuilder41.utils.PrebuiltPairs;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.Version;

/** Filters {@link LetterTokenizer} with {@link
 * LowerCaseFilter} and {@link StopFilter}.
 *
 * <a name="version"/>
 * <p>You must specify the required {@link Version}
 * compatibility when creating StopAnalyzer:
 * <ul>
 *   <li> As of 2.9, position increments are preserved
 * </ul>
*/


  public class MurphySearchAnalyzer extends Analyzer {
    private List<PrebuiltPairs> phrases;
     public MurphySearchAnalyzer() throws Exception 
    {
        this.phrases = null;
        DB db = new DB();
        java.sql.Connection tconn = db.dbConnect("jdbc:jtds:sqlserver://cw-wh-build:1433/LinguisticAnalysis","aspconnect","aspconnect");
        this.phrases  = GetSynonymList.getWordLists(tconn);
        tconn.close();
        
    }
    public MurphySearchAnalyzer(List<PrebuiltPairs> phrases) 
    {
        this.phrases = phrases;
    }
    
    
     @Override
    public Analyzer.TokenStreamComponents createComponents(String fieldName) 
    {
        
        AlphanumericTokenizer wt = new AlphanumericTokenizer();
        LowerCaseFilter filter = new LowerCaseFilter(wt);
        if(phrases==null)
            return new Analyzer.TokenStreamComponents(wt,filter);
        /*
         try {
            filter.reset();
        } catch (IOException ex) {
            Logger.getLogger(MurphyAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
        SynonymMap.Builder sb = new SynonymMap.Builder(true); 
        for(int i =0;i<phrases.size();i++){
            PrebuiltPairs pp = phrases.get(i);
            String phrase = pp.query.replace(' ', SynonymMap.WORD_SEPARATOR);
            sb.add(new CharsRef(phrase) , new CharsRef(pp.term), true); 
            
        }
        SynonymMap smap = null;
        try { 
            smap = sb.build();
            
        } catch (IOException ex) {
            Logger.getLogger(MurphyAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        SynonymFilter sf = new  SynonymFilter(filter,smap,true);
        /*
        try {
            sf.reset();
                   } catch (IOException ex) {
            Logger.getLogger(MurphyAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        return new Analyzer.TokenStreamComponents(wt,sf);
    }
}
