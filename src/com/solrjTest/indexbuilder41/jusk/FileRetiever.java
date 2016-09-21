package indexbuilder41.jusk;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author murphy
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import jcifs.Config;
import org.apache.lucene.util.Constants;
import jcifs.smb.*;
import org.jpedal.exception.PdfException;
import indexbuilder41.utils.FileRetriever;

public class FileRetiever
{
    public static InputStream getFile(String path, ServletContext cont) throws FileNotFoundException, SmbException, MalformedURLException, UnknownHostException
    {
        InputStream fis = null;
        if(Constants.WINDOWS || cont == null)
        {
            fis = new FileInputStream(path);
            
        }
        else
        {
            Config.registerSmbURLHandler();
            Config.setProperty("jcifs.smb.client.domain", cont.getInitParameter("smbdomain"));
            Config.setProperty("jcifs.smb.client.username", cont.getInitParameter("smbuser"));
            Config.setProperty("jcifs.smb.client.password", cont.getInitParameter("smbpassword"));
            Config.setProperty("jcifs.resolveOrder","DNS");
            Config.setProperty( "jcifs.netbios.wins", cont.getInitParameter("smbwins") );
            //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("ase", "murphy", "mu77in");
            
            path = "smb:" + path.replace("\\", "/");
            SmbFileInputStream in = new SmbFileInputStream(path);
            fis = in;
            
        }
        
        return fis;
    }
    public static boolean isDirectory(String path, ServletContext cont) throws MalformedURLException, SmbException
    {
        boolean isDir = false;
        if(Constants.WINDOWS)
        {
            File fl = new File(path);
            isDir = fl.isDirectory();
            
        }
        else
        {
            Config.registerSmbURLHandler();
            Config.setProperty("jcifs.smb.client.domain", cont.getInitParameter("smbdomain"));
            Config.setProperty("jcifs.smb.client.username", cont.getInitParameter("smbuser"));
            Config.setProperty("jcifs.smb.client.password", cont.getInitParameter("smbpassword"));
            Config.setProperty("jcifs.resolveOrder","DNS");
            Config.setProperty( "jcifs.netbios.wins", cont.getInitParameter("smbwins") );
            //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("ase", "murphy", "mu77in");
            
            path = "smb:" + path.replace("\\", "/");
            SmbFile fl = new SmbFile(path);
            isDir = fl.isDirectory();
            
        }
        return isDir;
    
    }
    public static long getSize(String pdfUrl, ServletContext cont) throws MalformedURLException
    {
        //InputStream fis = null;
        if(Constants.WINDOWS)
        {
            File fis = new File(pdfUrl);
            return fis.length();
        }
        else
        {
            Config.registerSmbURLHandler();
            Config.setProperty("jcifs.smb.client.domain", cont.getInitParameter("smbdomain"));
            Config.setProperty("jcifs.smb.client.username", cont.getInitParameter("smbuser"));
            Config.setProperty("jcifs.smb.client.password", cont.getInitParameter("smbpassword"));
            Config.setProperty("jcifs.resolveOrder","DNS");
            Config.setProperty( "jcifs.netbios.wins", cont.getInitParameter("smbwins") );
            //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("ase", "murphy", "mu77in");
            
            pdfUrl= "smb:" + pdfUrl.replace("\\", "/");
            SmbFile sfile = new SmbFile(pdfUrl);
            return (long)sfile.getContentLength();
            
        }
        
    }
    public static int getFilesLength(String filepath, ServletContext cont) throws SmbException, MalformedURLException{
        
        if(Constants.WINDOWS)
        {
            File fis = new File(filepath);
            return fis.listFiles().length;
        }
        else
        {
            Config.registerSmbURLHandler();
            Config.setProperty("jcifs.smb.client.domain", cont.getInitParameter("smbdomain"));
            Config.setProperty("jcifs.smb.client.username", cont.getInitParameter("smbuser"));
            Config.setProperty("jcifs.smb.client.password", cont.getInitParameter("smbpassword"));
            Config.setProperty("jcifs.resolveOrder","DNS");
            Config.setProperty( "jcifs.netbios.wins", cont.getInitParameter("smbwins") );
            //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("ase", "murphy", "mu77in");
            
            filepath= "smb:" + filepath.replace("\\", "/");
            SmbFile sfile = new SmbFile(filepath);
            return sfile.listFiles().length;
            
        }
    }
    static public boolean isPDFLinearized(String pdfUrl) throws PdfException {
        
        //if (pdfUrl.startsWith("jar"))
        //    return false;
        
        boolean isLinear=false;
        //read first few bytes
       
        final InputStream is;
        
        try {
            
            is = FileRetriever.getFile(pdfUrl);
            //final String filename = url.getPath().substring(url.getPath().lastIndexOf('/')+1);
            
            // Download buffer
            byte[] buffer = new byte[128];
            is.read(buffer);
            is.close();
            
            //test if linearized
            
            //scan for Linearized in text
            int len=buffer.length;
            for(int i=0;i<len;i++ ){
                
                if(buffer[i]=='/' && buffer[i+1]=='L' && buffer[i+2]=='i' && buffer[i+3]=='n' && buffer[i+4]=='e' && buffer[i+5]=='a' && buffer[i+6]=='r'){
                    isLinear=true;
                    i=len;
                }
            }
            
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
        return isLinear;
        
    }
}
