/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest;

import java.io.Reader;
//import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.AttributeFactory;

/**
 *
 * @author murphy
 */
public class AlphanumericTokenizer  extends CharTokenizer
{
    public AlphanumericTokenizer(){
        super();
    }
    public AlphanumericTokenizer(AttributeFactory in){
        super(in);
    }
    @Override
    protected boolean isTokenChar(int c)
    {
        return java.lang.Character.isLetterOrDigit(c);
    }
}
