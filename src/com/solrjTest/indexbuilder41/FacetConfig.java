/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;

/**
 *
 * @author murphy
 */
public class FacetConfig {
    public static void update(Document doc){
        String[] ttype = doc.get("types").split("\n");
        for(int i = 0;i<ttype.length; i++)
            if(ttype[i].length()>0)
                doc.add(new FacetField("ttypes",ttype[i]));
        if(doc.get("persons")!=null && doc.get("persons").trim().length()>0){
            String[] torig = doc.get("persons").split("\n");
            for(int i = 0;i<torig.length; i++)
                if(torig[i].length()>0)
                    doc.add(new FacetField("tpersons",torig[i]));
        }
        if(doc.get("language")!=null && doc.get("language").trim().length()>0){
            String[] torig = doc.get("language").split("\n");
            for(int i = 0;i<torig.length; i++)
                if(torig[i].length()>0)
                    doc.add(new FacetField("tlanguage",torig[i]));
        }
         if(doc.get("languages")!=null && doc.get("languages").trim().length()>0){
            String[] torig = doc.get("languages").split("\n");
            for(int i = 0;i<torig.length; i++)
                if(torig[i].length()>0)
                    doc.add(new FacetField("tlanguages",torig[i]));
        }
        if(doc.get("portfolioid")!=null && doc.get("portfolioid").trim().length()>0){
            String[] torig = doc.get("portfolioid").split("\n");
            for(int i = 0;i<torig.length; i++)
                if(torig[i].length()>0)
                    doc.add(new FacetField("tportfolioid",torig[i]));
        }
        String orderdate = doc.get("orderdate");
         if(orderdate.length()>=8)
            orderdate = orderdate.substring(0, 8);
        else
            orderdate = "00000000";
        doc.add(new Field("year",orderdate.substring(0,4),Field.Store.YES,Field.Index.NOT_ANALYZED));
        doc.add(new Field("month",orderdate.substring(4,6),Field.Store.YES,Field.Index.NOT_ANALYZED));
        doc.add(new Field("day",orderdate.substring(6,8),Field.Store.YES,Field.Index.NOT_ANALYZED));
        doc.add(new FacetField("torderdate", orderdate.substring(0,4),orderdate.substring(4,6),orderdate.substring(6,8) ));
        doc.add(new FacetField("tyear", orderdate.substring(0,4)));
        doc.add(new FacetField("tmonth", orderdate.substring(4,6)));
        doc.add(new FacetField("tday", orderdate.substring(6,8)));
        if(doc.get("lsname") != null && doc.get("lsname").length() > 0)
            doc.add(new FacetField("tlsname",doc.get("lsname")));
        if(doc.get("mattername") != null && doc.get("mattername").length() > 0)
            doc.add(new FacetField("tmattername",doc.get("mattername")));
        if(doc.get("producingparty") != null && doc.get("producingparty").length() > 0)
            doc.add(new FacetField("tproducingparty",doc.get("producingparty")));
        if(doc.get("isproduced") != null && doc.get("isproduced").length() > 0)
            doc.add(new FacetField("tproduced",doc.get("isproduced")));
        if(doc.get("pdf_pages") != null && doc.get("pdf_pages").length() > 0)
            doc.add(new FacetField("tpdf_pages",doc.get("pdf_pages")));
        doc.add(new FacetField(isNone("ttree"),isNone(doc.get("mattername")),isNone(doc.get("producingparty")),isNone(doc.get("isproduced")),isNone(doc.get("lsname")) ));       
    }
    public static FacetsConfig getconfig(){
        FacetsConfig config = new FacetsConfig();
        config.setHierarchical("torderdate", true);
        config.setHierarchical("ttree", true);
        config.setMultiValued("toriginator_name", true);
        config.setMultiValued("tpersons", true);
        config.setMultiValued("ttypes", true);      
        config.setMultiValued("tportfolioid", true);
        config.setMultiValued("tlanguages", true);
        return config;
    }
    
    private static String isNone(String tmp){
        if(tmp==null || tmp.length() == 0)
            return "None Set";
        else
            return tmp;
    
    }
}
