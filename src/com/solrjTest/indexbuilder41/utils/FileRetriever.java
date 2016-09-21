/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;
/**
 *
 * @author murphy
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import jcifs.Config;
import org.apache.lucene.util.Constants;
import jcifs.smb.*;

public class FileRetriever
{
    public static InputStream getFile(String path) throws FileNotFoundException, SmbException, MalformedURLException, UnknownHostException
    {
        InputStream fis = null;
        if(Constants.WINDOWS || path.startsWith("/"))
        {
            fis = new FileInputStream(path);
            
        }
        else
        {
            NtlmPasswordAuthentication auth = setConfig(path);
            //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("ase", "murphy", "mu77in");
            
            path = "smb:" + path.replace("\\", "/");
            SmbFile file = new SmbFile(path,auth);
            SmbFileInputStream in = new SmbFileInputStream(file);
            fis = new BufferedInputStream(in);
            
        }
        return fis;
    }
    public static Boolean exists(String path) throws FileNotFoundException, SmbException, MalformedURLException, UnknownHostException
    {
        File fis = null;
        if(Constants.WINDOWS || path.startsWith("/"))
        {
            fis = new File(path);
            return fis.exists();
            
        }
        else
        {
            
            NtlmPasswordAuthentication auth = setConfig(path);
            
            path = "smb:" + path.replace("\\", "/");
            SmbFile in = new SmbFile(path,auth);
            return in.exists();
            
        }
        
    }
    public static Boolean isDirectory(String path) throws FileNotFoundException, SmbException, MalformedURLException, UnknownHostException
    {
        File fis = null;
        if(Constants.WINDOWS || path.startsWith("/"))
        {
            fis = new File(path);
            return fis.isDirectory();
            
        }
        else
        {
            NtlmPasswordAuthentication auth = setConfig(path);
            //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("ase", "murphy", "mu77in");
            
            path = "smb:" + path.replace("\\", "/");
            SmbFile in = new SmbFile(path,auth);
            return in.isDirectory();
            
        }
        
    }
    private static NtlmPasswordAuthentication setConfig(String path){
            NtlmPasswordAuthentication auth;
            if(path.contains("10.180.191.223")){
                
                auth = new NtlmPasswordAuthentication("CWFILESRV1", "Samba", "Samba");
                
            }
            else
            {
                auth = new NtlmPasswordAuthentication("ase", "murphy", "mu77in");
            
            }
            Config.setProperty("jcifs.resolveOrder","DNS");
            Config.setProperty( "jcifs.netbios.wins", "10.80.160.11");
            return auth;
    }
    
}
