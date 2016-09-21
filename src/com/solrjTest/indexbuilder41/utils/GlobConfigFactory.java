/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author murphy
 */
public class GlobConfigFactory {
    public static GlobConfig gc = null;
    public static GlobConfig makeconfig() throws UnknownHostException{
      if(gc!=null)  
        return gc;
      return makeconfig("lib");
    }
    public static GlobConfig makeconfig(String code) throws UnknownHostException{
        gc = new GlobConfig();
        //code = "/index/share/baseline_jonesdaydbor";
        /*if(code.equals("ge")){
            gc.DocumentPreLoad = true;
            gc.useLink = true;
            if(InetAddress.getLocalHost().getHostName().equals("ubuntu-murphy"))
                gc.indexpath = "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_geseegriddbor/";
            else
                gc.indexpath = "/index/share/class/baseline_geseegriddbor/";
            gc.documentPath ="viewDoc.jsp";
            gc.filePath = "raw_link";
            
        }
        else 
            if(code.equals("lib"))
                {
            gc.DocumentPreLoad = true;
            gc.useLink = true;
            if(InetAddress.getLocalHost().getHostName().equals("ubuntu-murphy"))
                gc.indexpath = "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_cwlibraryresearchcenterdbor/";
            else
                gc.indexpath = "/index/share/class/baseline_cwlibraryresearchcenterdbor/";
            gc.documentPath ="viewDoc.jsp";
            gc.filePath = "raw_link";
        }
            else if(code.equals("cr"))
                {
            gc.DocumentPreLoad = true;
            gc.useLink = true;
            if(InetAddress.getLocalHost().getHostName().equals("ubuntu-murphy"))
                gc.indexpath = "/media/murphy/1fc1a27f-20c6-4e1d-a9ef-e4107c04911d/index/class/baseline_crestordbor/";
            else
                gc.indexpath = "/index/share/class/baseline_crestordbor/";
            gc.documentPath ="viewDoc.jsp";
            gc.filePath = "raw_link";
        }
        else{*/
            gc.DocumentPreLoad = true;
            gc.useLink = true;
            if(InetAddress.getLocalHost().getHostName().equals("Mike-DT") )
                gc.indexpath = "\\\\mike-dt\\indicies\\baseline_jonesdaydbor\\";
            else
                gc.indexpath = code.toLowerCase();
            gc.documentPath ="viewDoc.jsp";
            gc.filePath = "raw_link";
        //}
        return gc;
    }
}
