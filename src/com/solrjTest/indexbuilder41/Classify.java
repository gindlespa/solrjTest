/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.apache.lucene.classification.ClassificationResult;
import org.apache.lucene.classification.KNearestNeighborClassifier;
import org.apache.lucene.classification.SimpleNaiveBayesClassifier;
import org.apache.lucene.document.Document;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

/**
 *
 * @author murphy
 */
public class Classify {
    public static void main(String[] args) throws IOException, Exception{
        
     //TaxonomyWriter taxonomyWriter = new DirectoryTaxonomyWriter(new NIOFSDirectory(new File(TAXO)), IndexWriterConfig.OpenMode.);
     Directory dir  = NIOFSDirectory.open(Paths.get(args[0]));
     Query qry = new MatchAllDocsQuery();
     IndexReader ireader = DirectoryReader.open(dir);
     SimpleNaiveBayesClassifier bc = new SimpleNaiveBayesClassifier();
     LeafReader leafReader = null;
     leafReader = SlowCompositeReaderWrapper.wrap(ireader);
     
     bc.train(leafReader,"text","class",new MurphyAnalyzer(),qry);
     
     Directory dir2  = NIOFSDirectory.open(Paths.get(args[1]));
     IndexReader ireader2 = DirectoryReader.open(dir2);
     int right = 0;
     int wrong = 0;
     for(int i = 0; i < ireader2.maxDoc(); i++){
        
        Document doc = ireader2.document(i);
        if(doc.get("text").length() > 500){
            ClassificationResult<org.apache.lucene.util.BytesRef> result = bc.assignClass(doc.get("text"));
            String predicted = result.getAssignedClass().utf8ToString();
            String cls = doc.get("class");
            
            System.out.print(doc.get("ke_id"));
            System.out.print("\t");
            System.out.print(doc.get("class"));
            System.out.print("\t");
            System.out.print(predicted);
            System.out.print("\t");
            System.out.println(10000 * result.getScore());
            if (cls.equals(predicted))
                right++;
            else
                wrong++;
                
        }
        
      }
      System.out.println("right:" + right);
      System.out.println("wrong:" + wrong);
      System.out.println("right%:" + 100.0 * right / (right + wrong));
      System.out.println("wrong%:" + 100.0 * wrong / (right + wrong));
     ireader.close();
     dir.close();
      ireader2.close();
     dir2.close();
    }
}
