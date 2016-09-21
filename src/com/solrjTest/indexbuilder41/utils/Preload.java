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
class Preload
        implements Runnable {
        private long ke_id;
        private GlobConfig gc;
        public Preload(long ke_id, GlobConfig gc){
            this.ke_id = ke_id;
            this.gc = gc;
        }
        //@override
        public void run() {
            
            try    
            {
                String filepath;
                filepath = LuceneUtil.getPathFromKEID(ke_id, gc);
                ImageData id = new ImageData(filepath);
            } 
            catch (Exception e) 
            {
                
            }
        }
        private void process()
        {
        
        }
    }
