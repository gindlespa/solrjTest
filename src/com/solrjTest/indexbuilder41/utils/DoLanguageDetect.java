/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author murphy
 */
public class DoLanguageDetect {
    public static LanguageDetectionResult[] getLanguage(String text) throws InterruptedException, IOException{
        Pattern pw = Pattern.compile("[\\S]*%");
        if(text.length()>10000000)
            text=text.substring(0,10000000);
        byte[] utf8 = text.getBytes("UTF-8");
        String path = "/tmp/" + java.util.UUID.randomUUID();
        File fl = new File(path);
        writefile(utf8,fl);
        String output = ExecuteCommand.execute("cat \"" + path + "\" | cld2");
        fl.delete();
        Matcher mt = pw.matcher(output);
        int start = 0;
        ArrayList<LanguageDetectionResult> result = new ArrayList<>();
        while(mt.find(start)){
            String grp = mt.group();
            start = mt.end();
            String arr[] = grp.split("\\(");
            LanguageDetectionResult lrt = new LanguageDetectionResult();
            lrt.language = arr[0];
            lrt.percent = Integer.parseInt(arr[1].replace("%", ""));
            result.add(lrt);
        
        }
        LanguageDetectionResult[] ra = new LanguageDetectionResult[result.size()];
        result.toArray(ra);
        return ra;
        
    }
    private static void writefile(byte[] text, File fl)
    {
        try {
 
			
 
			
 
			// if file doesnt exists, then create it
			if (!fl.exists()) {
				fl.createNewFile();
			}
 
			FileWriter fw = new FileWriter(fl.getAbsoluteFile());
                        IOUtils.write(text,fw);
			fw.close();
			//System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
    
    }
}
