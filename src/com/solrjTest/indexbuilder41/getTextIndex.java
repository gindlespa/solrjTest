/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import indexbuilder41.utils.DB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;

/**
 *
 * @author murphy
 */
public class getTextIndex implements com.solrjTest.indexbuilder41.iGetText
{
    private IndexSearcher isearcher;
    
    public void setIndex(IndexSearcher isearcher) throws Exception
    {
        this.isearcher = isearcher;
    
    }
    @Override
    public String getText(String keid, String path) throws Exception
    {
        
        TermQuery tq = new TermQuery(new Term("ke_id",keid));
        TopScoreDocCollector tdc= TopScoreDocCollector.create(1);
        isearcher.search(tq, tdc);
        int docid = tdc.topDocs().scoreDocs[0].doc;
        Document doc = isearcher.getIndexReader().document(docid);
        return doc.get("text");
    }
}
