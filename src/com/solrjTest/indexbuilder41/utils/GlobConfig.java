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
public class GlobConfig {
    // CRESTOR
    /*
    public static boolean DocumentPreLoad = false;
    public static String filePath = "filepath";
    public static String getIndexPath()
    {
        
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_geseegriddbor/";
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_jonesdaydbor/";
        //return "/index/share/class/baseline_jonesdaydbor/";
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_cwlibraryresearchcenterdbor/";
        //return "/index/share/class/baseline_cwlibraryresearchcenterdbor/";
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/culler/";
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/crestor/";
        return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/crestor/";
    }
     public static String getDocumentPath()
    {
        
        //return "viewDoc.jsp";
        return "ViewGeneratedDoc";
        
    }
    */
    // Jones Day
    GlobConfig(){}
    boolean DocumentPreLoad = false;
    String filePath = "raw_link";
    String indexpath = "";
    String documentPath = "viewDoc.jsp";
    Boolean useLink = true;
    public Boolean getDocumentPreLoad(){
        
        return DocumentPreLoad;
    }
    public Boolean getUseLink(){
        
        return useLink;
    }
    public String getIndexPath()
    {
        
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_geseegriddbor/";
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_jonesdaydbor/";
        return indexpath;
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_cwlibraryresearchcenterdbor/";
        //return "/index/share/class/baseline_cwlibraryresearchcenterdbor/";
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/culler/";
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/crestor/";
        //return "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/crestor/";
    }
     public String getDocumentPath()
    {
        
        return documentPath;
        //return "ViewGeneratedDoc";
        
    }
    
    
}
