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
class HighlightLocation implements Comparable<HighlightLocation> {
        public  int start;
        public  int length;
        public  int end;
        public int compareTo(HighlightLocation hl)
        {
            if(hl.start < start)
                return 1;
            if(hl.start > start)
                return -1;
            return 0;
        }
    }
