/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Highlight;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.json.simple.JSONObject;

/**
 *
 * @author murphy
 */
public class GetPositionFromHighlights {
    public static String getPositions(PDDocument doc, ArrayList<HighlightLocation> al) throws IOException{
        StringBuilder sb = new StringBuilder();
        java.util.List<PDPage> allPages = doc.getDocumentCatalog().getAllPages();
        sb.append("{\"hits\":[");
        int count = 0;
        for(HighlightLocation hl:al){
            
            PdfPositionFinder pdfp = new PdfPositionFinder();
            ArrayList<HighlightLocation> alm = new ArrayList<>();
            alm.add(hl);
            PDPage pdpage = allPages.get(hl.page);
            pdfp.getPositions(hl.page, pdpage, alm);
            for(int i =0; i < hl.boxes.size();i++){
            if(count++>0)
                sb.append(",");
            sb.append("{");
            sb.append("\"page\":"); 
            sb.append(hl.page);
            
            sb.append(",\"x\":"); 
            sb.append(hl.boxes.get(i).x);
            
            sb.append(",\"y\":"); 
            sb.append(hl.boxes.get(i).y);
            
            sb.append(",\"width\":"); 
            sb.append(hl.boxes.get(i).width);
            
             sb.append(",\"height\":"); 
            sb.append(hl.boxes.get(i).height);
            
            sb.append(",\"color\":\""); 
            sb.append(hl.boxes.get(i).color);
            sb.append("\""); 
            
            sb.append(",\"hit\":\""); 
            sb.append(hl.token);
            sb.append("\""); 
            
            sb.append("}");
            }
        }
        
        
        
        
        sb.append("]}");
        return sb.toString();
    }
    
}
