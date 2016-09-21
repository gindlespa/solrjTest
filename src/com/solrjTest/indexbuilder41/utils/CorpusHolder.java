/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

import java.util.HashMap;

/**
 *
 * @author murphy
 */
public class CorpusHolder {
    private static HashMap<String, Corpus> maps;
    public static Corpus getmap(String DISKLOC) throws Exception{
        if(maps==null)
            maps = new HashMap<String, Corpus>();
        if(!maps.containsKey(DISKLOC))
            maps.put(DISKLOC, new Corpus(DISKLOC));
        return maps.get(DISKLOC);
    }
    
}
