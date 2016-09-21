/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import jcifs.smb.SmbException;
import org.apache.lucene.util.Constants;

/**
 *
 * @author murphy
 */
public class CheckFileExt {
    public static String checkPath(String path) throws FileNotFoundException, SmbException, MalformedURLException, UnknownHostException{
        String slash = "/";
        if(Constants.WINDOWS)
        slash = "\\";
        if(!path.endsWith(slash))
            path += slash;
        if(FileRetriever.exists(path+"1.jpg"))
            return "jpg";
        if(FileRetriever.exists(path+"1.png"))
            return "png";
        return "null";
    }
}
