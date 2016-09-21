/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;
/**
 *
 * @author murphy
 */
public class QueryMSQHandler implements iQueryHandler
{
    private IndexSearcher isearcher;
    private readMSQ msq;
    private String keids;
    public void setSearcher(IndexSearcher is){
        
        isearcher = is;
    }
    public void setKeids(String keids){
        this.keids = keids;
    
    }
    
    public Query getQuery(String qry, mainSearch m) throws Exception
    {
//        if(isearcher == null)
//            throw new Exception("IndexSearcher not set in class QueryMSQHandler!");
//        msq = new readMSQ(qry);
//        Query query = m.makeSpanQueries(msq,isearcher);
//        return query;
    }
    public String getFilter(){
        
        return msq.xxFilter;
    }
    public String getKeids(){
        return keids;
    }
}
