/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

/**
 *
 * @author murphy
 */


import indexbuilder41.MurphyAnalyzer;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Constants;

/**
 *
 * @author murphy
 */
public class MakeCorpus 


{
    public static void main(String[] args) throws Exception{
        
        makeCorpus(args[0]);
    }
    public static void makeCorpus(String inpath) throws Exception
    {
        HashMap<String,Integer> hash = new HashMap<>();
        LinkedList<String> singles = new LinkedList<>();
        HashSet<String> sset = new HashSet<>();
        String slash = "/";
        if(Constants.WINDOWS)
            slash = "\\";
        else
            slash = "/";
        if(!inpath.endsWith(slash))
            inpath += slash;
        String outpath = inpath + "corpus.txt";
        IndexReader ireader = DirectoryReader.open(new NIOFSDirectory(Paths.get(inpath)));
        for(int i = 0; i < ireader.maxDoc(); i++)
        {
            //TermFreqVector tfv = ireader.getTermFreqVector(i, "text");
            System.out.println(i); //get the term frequency in the document
            Terms terms = ireader.getTermVector(i, "text"); //get terms vectors for one document and one field
            if (terms != null && terms.size() > 0) {
                TermsEnum termsEnum = terms.iterator(null); // access the terms for this field
                BytesRef term = null;
                while ((term = termsEnum.next()) != null) {// explore the terms for this field
                    DocsEnum docsEnum = termsEnum.docs(null, null); // enumerate through documents, in this case only one
                    int docIdEnum;
                    while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) 
                    {
                      
                      addToHash(hash,singles,sset,term.utf8ToString(),docsEnum.freq()) ;
                      
                    }
                }
            }
            /*
            AtomicReader leafReader = null;
            leafReader = SlowCompositeReaderWrapper.wrap(ireader);
            Map<Integer,Vector<String>> mymap = LuceneUtil.getTermPositionMap(leafReader,i, "text");
            Set<Integer> myset = new TreeSet<Integer>();

           
            for(int key:mymap.keySet()){
                Vector<String> vect = mymap.get(key);
               System.out.print(vect.firstElement());
               System.out.print(" ");
            }
            System.out.println();
                    */
            
        }
        ireader.close();
        
        String[] terms = hash.keySet().toArray(new String[0]);
        Arrays.sort(terms);
        
        PrintWriter writer = new PrintWriter(outpath, "UTF-8");
        writer.print("50.0");
            writer.print("\t");
            writer.println("50.00");
        for(int i=0;i<terms.length;i++){
            writer.print(terms[i]);
            writer.print("\t");
            writer.println(hash.get(terms[i]));
            if(i/100000 == i/100000.0)
                System.out.println(i);        
         }
        writer.flush();
        writer.close();
        
    }
    
    private static void addToHash(HashMap<String, Integer> hash,LinkedList<String> singles,HashSet<String> sset, String term, int count)
    {
        if(hash.containsKey(term)){
            int oldval = (int)hash.get(term);
            if(oldval+count > 4){
                hash.put(term, oldval+count);
                if(sset.contains(term) ){
                    singles.remove(term);
                    sset.remove(term);
                }
            }
        }
        else{
            if(count<4 && singles.size() < 100000){
                singles.add(term);
                sset.add(term);
            }
            hash.put(term, count);
        }
        /*
        while(singles.size() >= 1000000){
            String t = singles.removeFirst();
            sset.remove(t);
            hash.remove(t);
        }
                */
        while(hash.size() > 10000000 && singles.size()>0){
            String t = singles.removeFirst();
            sset.remove(t);
            hash.remove(t);
        }
        if(singles.size()==0 && hash.size() > 10000000)
            checkSingles(hash,singles,sset);
    }
    private static void checkSingles(HashMap<String, Integer> hash,LinkedList<String> singles,HashSet<String> sset)
    {
        Set<String> ms = hash.keySet();
        Iterator<String> iter = ms.iterator();
        while(singles.size()<100000 && iter.hasNext()){
            String t = iter.next();
            if(hash.containsKey(t) && hash.get(t) < 4)
            {
                singles.add(t);
                sset.add(t);
            }
        }
    }
    
}

