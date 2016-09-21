/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;
import org.apache.lucene.search.IndexSearcher;
/**
 *
 * @author murphy
 */
public interface iQueryHandler {
    public org.apache.lucene.search.Query getQuery(String qry, com.solrjTest.indexbuilder41.mainSearch m) throws Exception;
    public String getFilter();
    public String getKeids();
}
