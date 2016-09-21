package com.solrjTest.indexbuilder41.utils;


import indexbuilder41.DocHolder.DocHolderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.json.simple.JSONObject;

public class ImageData{
    private int pageCount = 0;
    private ArrayList<PageInfo> pl = new ArrayList<>();
    public int getPageCount(){
        
        return pageCount;
    }
    public String writeJSON(){
        JSONObject json = new JSONObject();
        json.put("pages", pl);
        StringBuilder sb = new StringBuilder();
        sb.append("{\"pages\":[");
        
        for(int i = 0;i<pl.size();i++){
            if(i>0)
                sb.append(",");
            sb.append("{");
            sb.append("\"height\":"); 
            sb.append(pl.get(i).height);
            
            sb.append(",\"width\":"); 
            sb.append(pl.get(i).width);
            
            sb.append(",\"oheight\":"); 
            sb.append(pl.get(i).oheight);
            
            sb.append(",\"owidth\":"); 
            sb.append(pl.get(i).owidth);
            
            sb.append("}");
            
        }
        sb.append("]}");
        return sb.toString();
    }

    public ImageData(String filepath) throws IOException{
                PDDocument document = DocHolderFactory.getDoc(filepath);
                List allPages = document.getDocumentCatalog().getAllPages();
                pageCount = allPages.size();
                for(int i = 0; i < pageCount; i++){
                PageInfo pi = new PageInfo();
                PDPage ppage = (PDPage) allPages.get(i);

                PDRectangle prect = ppage.findCropBox();
                if(ppage.findRotation() == -90 || ppage.findRotation() == 90){
                pi.width = prect.getHeight();
                pi.height = prect.getWidth();
            
                pi.oheight = prect.getLowerLeftX();
                pi.owidth = prect.getLowerLeftY();    
                    
                }else{
                pi.height = prect.getHeight();
                pi.width = prect.getWidth();
            
                pi.owidth = prect.getLowerLeftX();
                pi.oheight = prect.getLowerLeftY();
                }
                pl.add(pi);
            }
    }
    class PageInfo{
        public float width = 0;
        public float height = 0;
        public float owidth = 0;
        public float oheight = 0;
        
    }

}