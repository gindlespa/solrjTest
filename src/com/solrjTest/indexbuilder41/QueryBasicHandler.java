/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;


import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

/**
 *
 * @author murphy
 */
public class QueryBasicHandler implements iQueryHandler 
{
    public Query getQuery(String qry, mainSearch m) throws Exception
    {
        MurphyAnalyzer anal = new MurphyAnalyzer();
        QueryParser qp = new QueryParser("text",anal);
        Query query = qp.parse(qry);
        return query;
    }
    public String getFilter()
    {
        return "";
    }
    public String getKeids(){
        
        return "";
    }
}
