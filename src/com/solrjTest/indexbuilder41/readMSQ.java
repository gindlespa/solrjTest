/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author murphy
 */
public class readMSQ {
    protected ArrayList<String> xxList = new ArrayList<>();;
    public String xxListExclude="";
    public String xxFilter="";
    public int xxRange1=10;
    public int xxRange2=10;
    private Boolean dropnexus = false;
    
    public readMSQ()
    {
        
        xxListExclude = "\r\n";
        xxRange1 = 10;
        xxRange2 = 10;
        xxFilter="\r\n"; 
    }
    public void addList(String list){
        if(list.trim().length()>0)
            xxList.add(list);
    }
    
    public readMSQ(File msq) throws Exception
    {
        String s;
        java.io.FileReader rdr = new java.io.FileReader(msq);
        readMSQReader(rdr);
    }
    
    public readMSQ(String msq, Boolean dropnexus) throws Exception
    {
        String s;
        java.io.StringReader rdr = new java.io.StringReader(msq.replace("\\r\\n", "\r\n"));
        readMSQReader(rdr);
        this.dropnexus = dropnexus;
    }
    
     public readMSQ(String msq) throws Exception
    {
        String s;
        java.io.StringReader rdr = new java.io.StringReader(msq.replace("\\r\\n", "\r\n"));
        readMSQReader(rdr);
    }
    public String writeMSQ(){
        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < xxList.size(); i++){
            
            sb.append("xxList");
            sb.append("\r\n");
            sb.append(xxList.get(i));
            sb.append("\r\n");
            
        }
        sb.append("xxListExclude");
        sb.append("\r\n");
        sb.append(xxListExclude);
        sb.append("\r\n");
        sb.append("xxRange1");
        sb.append("\r\n");
        sb.append(xxRange1);
        sb.append("\r\n");
        sb.append("xxRange2");
        sb.append("\r\n");
        sb.append(xxRange2);
        sb.append("\r\n");
        sb.append("xxFilter");
        sb.append("\r\n");
        sb.append(xxFilter);
        sb.append("\r\n");
        return sb.toString();
    }
    
    
    public void readMSQReader(Reader rd) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        String cats = "xxList1|xxList2|xxList3|xxListExclude|xxRange1|xxRange2|xxFilter";
        String[] catarray = cats.split("|");
        String s = "";
       
        xxListExclude = "";
        xxRange1 = 10;
        xxRange2 = 10;
        xxFilter="";
                
        BufferedReader br = new BufferedReader(rd);
        while((s = br.readLine()) != null)
        {
            //System.out.println(s);
            
            if(s.equals("xxList"))
            {
                if(sb.toString().trim().length()>0){
                    xxList.add(sb.toString());
                    sb = new StringBuilder();
                }
                continue;
            }
            if(s.equals("xxListExclude"))
            {
                xxListExclude="";
                if(sb.toString().trim().length()>0){
                    xxList.add(sb.toString());
                    sb = new StringBuilder();
                }
                continue;
            }
            if(s.equals("xxRange1"))
            {
                
                xxListExclude = sb.toString();
                sb = new StringBuilder();
                continue;
            }
            if(s.equals("xxRange2"))
            {
                
                try{
                    xxRange1 = Integer.parseInt(sb.toString().replace("\r\n", ""));
                }
                catch(Exception e){
                    xxRange1 = 10;
                }
                sb = new StringBuilder();
                continue;
            }
            if(s.equals("xxFilter"))
            {
                
                try{
                    xxRange2 = Integer.parseInt(sb.toString().replace("\r\n", ""));
                }
                catch(Exception e){
                    xxRange2 = 10;
                }
                sb = new StringBuilder();
                continue;
            }
            //System.out.println(s);
            sb.append(s);
            sb.append("\r\n");
        }
        xxFilter = sb.toString();
        br.close();
       
    }
    
}
