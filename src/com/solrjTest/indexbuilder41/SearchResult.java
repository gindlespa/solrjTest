/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import java.util.List;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.search.ScoreDoc;

/**
 *
 * @author murphy
 */
public class SearchResult {
    public List<FacetResult> results;
    public int rStart;
    public int doccount;
    public int pagecount;
    public String[] highlights;
    public ScoreDoc[] scoredocs;
    public String tree;
}
