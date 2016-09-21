/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.lucene.document.Document;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocValuesRangeQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;

/**
 *
 * @author murphy
 */
public final class LuceneUtil {
/**
* Private constructor to prevent instantiation.
*/
private LuceneUtil() {}

/**
* 
* @param reader the index reader
* @param fieldName name of the field of interest
* @param docId internal doc ID of the document of interest
* @return all Terms present in the requested field
* @throws IOException on IndexReader error
*/
public static Terms getTerms(final LeafReader reader, final String fieldName, 
final int docId) throws IOException{
return reader.getTermVector(docId, fieldName);
}

/**
* Returns a map of the terms and their token positions for a field in a 
* document. The map may be empty because vector information is not available 
* for the requested field, or because the analyzer assigned to it found no 
* terms in the original document field at index time.
* 
* @param reader Lucene index reader (for access to term vector info)
* @param docId the internal Lucene ID of the document of interest
* @param fieldName name of the field of interest
* @return a map of term/positions pairs; the map may be empty.
* @throws IOException on IndexReader error
*/
public static Map<Integer, Vector<String>> getTermPositionMap(final LeafReader reader,
final int docId, final String fieldName) throws IOException {
    Map<Integer,Vector<String>> termPosMap = new HashMap<>();
    Terms terms = LuceneUtil.getTerms(reader, fieldName, docId);
    if(terms!=null) {
        TermsEnum termsEnum = terms.iterator(TermsEnum.EMPTY);
        BytesRef term;
        while ((term=termsEnum.next())!=null) 
        {
            String docTerm = term.utf8ToString();
            DocsAndPositionsEnum docPosEnum = 
            termsEnum.docsAndPositions(reader.getLiveDocs(),null,DocsAndPositionsEnum.FLAG_OFFSETS);
            docPosEnum.nextDoc();
            int freq = docPosEnum.freq();
            int[] posArray = new int[freq];
            for (int i = 0; i < freq; i++) 
            {
                int position = docPosEnum.nextPosition();
                Vector<String> vect;
                if(termPosMap.containsKey(position)){
                    vect= termPosMap.get(position);
                    vect.add(docTerm);
                }
                else{
                    vect = new Vector<>();
                    vect.add(docTerm);
                    termPosMap.put(position, vect);
                }
                
            }
            
        }
    }
return termPosMap;
}
public static String getPathFromKEID(long ke_id, GlobConfig gc) throws Exception{
    IndexSearcher searcher = indexHolder.getSearcher(gc.getIndexPath());
    return getPathFromKEID(ke_id,searcher);
    }
public static String getPathFromKEID(long ke_id, IndexReader reader) throws Exception
{
    IndexSearcher searcher = new IndexSearcher(reader);
    return getPathFromKEID(ke_id,searcher);
}
public static String getPathFromKEID(String ke_id, String indexpath, GlobConfig gc) throws Exception{
    IndexSearcher searcher = indexHolder.getSearcher(indexpath);
    return getPathFromKEID(ke_id,searcher, gc);
    }
public static String getPathFromKEID(String ke_id, IndexReader reader, GlobConfig gc) throws Exception
{
    IndexSearcher searcher = new IndexSearcher(reader);
    return getPathFromKEID(ke_id,searcher, gc);
}
private static String getPathFromKEID(long ke_id, IndexSearcher searcher) throws IOException
{
    final BytesRefBuilder builder = new BytesRefBuilder();
                NumericUtils.longToPrefixCoded(ke_id, 0, builder);
                Query qr = new TermQuery(new Term("ke_id",builder.toBytesRef()));
    TopDocsCollector tfc = TopScoreDocCollector.create(1);
    searcher.search(qr, tfc);
    TopDocs td = tfc.topDocs();
    if(td.totalHits>0){
        int docid = td.scoreDocs[0].doc;
        Document doc = searcher.getIndexReader().document(docid);
        String filepath = doc.get("raw_link");
        return  filepath;
    }
    else
        return null;
    }
private static String getPathFromKEID(String ke_id, IndexSearcher searcher, GlobConfig gc) throws IOException
{
    TermQuery tq = new TermQuery( new Term("ke_id",ke_id));
    TopDocsCollector tfc = TopScoreDocCollector.create(1);
    searcher.search(tq, tfc);
    TopDocs td = tfc.topDocs();
    if(td.totalHits>0){
        int docid = td.scoreDocs[0].doc;
        Document doc = searcher.getIndexReader().document(docid);
        String filepath = doc.get(gc.filePath);
        return  filepath;
    }
    else
        return null;
    }    
}

