/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Constants;

/**
 *
 * @author murphy
 */
public class Corpus {
    private HashMap<String, Integer> corpusMap;
    private static final int MAXSIZE = 300;
    private static final int MINSIZE = 10;
    private static int totalcount = 0;
    private int largestcount = 0;
    private final HashSet<String> stopWords;
    
    private final String stopWordsString = "will|made|make|into|com|org|net|ccs|bccs|subject|i|me|my|myself|we|us|our|ours|ourselves|you|your|yours|yourself|yourselves|he|him|his|himself|she|her|hers|herself|it|its|itself|they|them|their|theirs|themselves|what|which|who|whom|whose|this|that|these|those|am|is|are|was|were|be|been|being|have|has|had|having|do|does|did|doing|will|would|should|can|could|ought|i'm|you're|he's|she's|it's|we're|they're|i've|you've|we've|they've|i'd|you'd|he'd|she'd|we'd|they'd|i'll|you'll|he'll|she'll|we'll|they'll|isn't|aren't|wasn't|weren't|hasn't|haven't|hadn't|doesn't|don't|didn't|won't|wouldn't|shan't|shouldn't|can't|cannot|couldn't|mustn't|let's|that's|who's|what's|here's|there's|when's|where's|why's|how's|a|an|the|and|but|if|or|because|as|until|while|of|at|by|for|with|about|against|between|into|through|during|before|after|above|below|to|from|up|upon|down|in|out|on|off|over|under|again|further|then|once|here|there|when|where|why|how|all|any|both|each|few|more|most|other|some|such|no|nor|not|only|own|same|so|than|too|very|say|says|said|shall";
    public Corpus(String path) throws FileNotFoundException, IOException, Exception{
        corpusMap = new HashMap<>();
        String slash = "/";
        if(Constants.WINDOWS)
            slash = "\\";
        
            
        if(!path.endsWith(slash))
            path += slash;
        path += "corpus.txt";
        FileReader fr = new FileReader(new File(path));
        BufferedReader br = new BufferedReader(fr);
        String tmp = br.readLine();
        if(tmp == null || tmp.length() == 0)
            throw new Exception("Nothing in corpus file!");
        String[] arrs = stopWordsString.split("\\|");
        stopWords = new HashSet<>();
        for(int i =0;i<arrs.length;i++)
            stopWords.add(arrs[i]);
        while((tmp = br.readLine())!= null){
            String[] arr = tmp.split("\t", 2);
            String word = arr[0];
            int count = Integer.parseInt(arr[1]);
            if(!stopWords.contains(word) && word.matches("[\\D]*")  && word.length()>3 &&count > 5){
            
                corpusMap.put(word,count);
                totalcount += count;
                if(count > largestcount)
                    largestcount = count;
            }
        }
        corpusMap = sortByValues(corpusMap);
        
    } 
    public HashMap<String,Integer> writeNonSearch(int maxdocs, PrintWriter pw)
    {
        Set set2 = corpusMap.entrySet();
        HashMap<String,Integer> curMap = new HashMap<>();
        Iterator iterator2 = set2.iterator();
        int high = 0;
        int low = Integer.MAX_VALUE;
        int icount = 0;
        while(iterator2.hasNext()) 
        {
            if (icount++ > maxdocs)
                break;
            Map.Entry me2 = (Map.Entry)iterator2.next();
            int cnt = (Integer)me2.getValue();
            curMap.put((String)me2.getKey(), cnt);
            if(cnt > high)
                high = cnt;
            if(cnt < low)
                low = cnt;
            
        }
        Iterator iter = curMap.entrySet().iterator();
        HashMap<String,Integer> returnMap = new HashMap<>();
        
        while(iter.hasNext()){
            Map.Entry me2 = (Map.Entry)iter.next();
            returnMap.put((String)me2.getKey(),getSize((Integer)me2.getValue(),high,low));
            
            
        }
        return returnMap;
        
    }
    public HashMap<String,Integer> writeSearch(ScoreDoc[] docs, int maxdocs, String DISKLOC) throws Exception
    {
        IndexSearcher searcher = indexHolder.getSearcher(DISKLOC);
        IndexReader ireader = searcher.getIndexReader();
        int doccount = docs.length;  //docs.length > maxdocs ? maxdocs : docs.length;
        HashMap<String, Integer> resultMap =  new HashMap<>();
        for(int i = 0; i < doccount;i++)
        {
            //TermFreqVector tfv = ireader.getTermFreqVector(i, "text");
            System.out.println(i); //get the term frequency in the document
            Terms terms = ireader.getTermVector(docs[i].doc, "text"); //get terms vectors for one document and one field
            if (terms != null && terms.size() > 0) {
                TermsEnum termsEnum = terms.iterator(null); // access the terms for this field
                BytesRef term = null;
                while ((term = termsEnum.next()) != null) {// explore the terms for this field
                    DocsEnum docsEnum = termsEnum.docs(null, null); // enumerate through documents, in this case only one
                    int docIdEnum;
                    while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) 
                    {
                      String word = term.utf8ToString();
                      int count = docsEnum.freq();
                      if(corpusMap.containsKey(word))
                        addToHash(resultMap,word,count) ;

                    }
                }
            }
            
        }
        
        Set set2 = resultMap.entrySet();
        HashMap<String,Double> curMap = new HashMap<>();
        Iterator iterator2 = set2.iterator();
        double high = 0;
        double low = Double.MAX_VALUE;
        int icount = 0;
        while(iterator2.hasNext()) 
        {
            
            Map.Entry me2 = (Map.Entry)iterator2.next();
            String word = (String)me2.getKey();
            int cnt = (Integer)me2.getValue();
            double value =  cnt * 1.0 / corpusMap.get(word) * Math.log(corpusMap.get(word)) ;
            curMap.put(word, value);
            
        }
        curMap = sortByValues(curMap);
        Iterator iter = curMap.entrySet().iterator();
        
        
        
        while(iter.hasNext()){
            
            Map.Entry me2 = (Map.Entry)iter.next();
            String word = (String)me2.getKey();
            double value = (double)me2.getValue();
            if(value > high)
                high = value;
            if(value < low)
                low = value;
            if(icount++ > maxdocs)
                break;
        }
        HashMap<String,Integer> returnMap = new HashMap<>();
        iter = curMap.entrySet().iterator();
        icount = 0;
        while(iter.hasNext()){
            if(icount++ > maxdocs)
                break;
            Map.Entry me2 = (Map.Entry)iter.next();
            double val = (Double)me2.getValue();
            double count = getSize(val,high,low);
            returnMap.put((String)me2.getKey(),((Double)count).intValue());
            
            
        }
        return returnMap;
        
    }
    private static void addToHash(HashMap<String, Integer> hash, String term, int count)
    {
        if(hash.containsKey(term)){
            int oldval = (int)hash.get(term);
            hash.put(term, oldval+count);
        }
        else{
            hash.put(term, count);
        }
        
        
    }
    private static int getSize(int count, int high, int low){
         float factor = (MAXSIZE-MINSIZE)*1.0f/(high-low);
         float size = (count - low) * factor + MINSIZE;
         return Math.round(size);
    }
    private static int getSize(double count, double high, double low){
         double factor = (MAXSIZE-MINSIZE)*1.0f/(high-low);
         double size = (count - low) * factor + MINSIZE;
         return (int) Math.round(size);
    }
    private static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o2)).getValue())
                  .compareTo(((Map.Entry) (o1)).getValue());
            }
       });

       // Here I am copying the sorted list in HashMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }
}
