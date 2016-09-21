package com.solrjTest;

/**
 * Created by MGindlesperger on 4/7/2016.
 */

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import org.apache.pdfbox.pdmodel.PDDocument;

public class DocHolderFactory {
    private static DocHolder holder = null;
    public static PDDocument getDoc(String path) throws IOException{
        if(holder==null)
            holder = new DocHolder();
        return holder.getDoc(path);
    }
    public static void destroyall() throws IOException{
        if(holder!=null)
            holder.destroyAll();

    }

}
