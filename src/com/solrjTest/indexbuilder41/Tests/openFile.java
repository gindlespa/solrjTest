/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Tests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import jcifs.Config;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import static jcifs.smb.SmbFile.FILE_SHARE_READ;
import jcifs.smb.SmbFileInputStream;

/**
 *
 * @author murphy
 */
public class openFile {
    public static void main(String[] args) throws IOException{
        String path = "\\\\10.180.191.223\\g\\GettysburgFoundation\\docs\\Fisher Collection\\Fisher_024.pdf";
        path = "smb:" + path.replace("\\", "/");
        
        Config.setProperty("jcifs.smb.client.domain", "CWFILESRV1");
        Config.setProperty("jcifs.smb.client.username", "Samba");
        Config.setProperty("jcifs.smb.client.password", "Samba");
        

        SmbFile file = new SmbFile(path);
        System.out.println(file.exists());
        SmbFileInputStream in = new SmbFileInputStream(path);
        BufferedInputStream buf = new BufferedInputStream(in); 
        
    }
}
