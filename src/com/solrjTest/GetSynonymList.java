/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author murphy
 */
public class GetSynonymList {
    public static ArrayList<PrebuiltPairs> getWordLists(java.sql.Connection conn) throws Exception
    {
        ArrayList<PrebuiltPairs> tli = new ArrayList<PrebuiltPairs>();

        Statement stm = conn.createStatement();
        Statement stm2 = conn.createStatement();
        stm.setQueryTimeout(150);

        ResultSet rs;

        try{
            rs = stm.executeQuery("select distinct NEXUS_TSID, nexus_termid,term_query  from aspconnect.NEXUS_PREINDEX");

            while(rs.next())
            {

                String iterm = rs.getString("term_query").trim().toLowerCase();
                String query = iterm.replaceAll("[^a-zA-Z0-9*\\/]", " ").replace("[\\s]*", " ").trim();
                PrebuiltPairs pp = new PrebuiltPairs();
                pp.term = "nexustsid" + rs.getString("NEXUS_TSID");
                pp.query = query;
                tli.add(pp);
                pp = new PrebuiltPairs();
                pp.term = "nexustermid" + rs.getString("NEXUS_Termid");
                pp.query = query;
                tli.add(pp);

            }
            rs.close();
            /*
            rs = stm.executeQuery("select distinct NEXUS_TSID, nexus_termid,term_query  from aspconnect.SNEXUS_PREINDEX");
            
            while(rs.next())
            {
              
                    String iterm = rs.getString("term_query").trim().toLowerCase();
                    String query = iterm.replaceAll("[^a-zA-Z0-9*\\/]", " ").replace("[\\s]*", " ").trim();
                    PrebuiltPairs pp = new PrebuiltPairs();
                    pp.term = "snexustsid" + rs.getString("NEXUS_TSID");
                    pp.query = query;
                    tli.add(pp);
                    pp = new PrebuiltPairs();
                    pp.term = "snexustermid" + rs.getString("NEXUS_Termid");
                    pp.query = query;
                    tli.add(pp);
                    
            }
            rs.close();
            */
            conn.close();

        }
        catch(java.sql.SQLException sex){
            System.out.println(sex.getMessage());
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        return tli;
    }
}
