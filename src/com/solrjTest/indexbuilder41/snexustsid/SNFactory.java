/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.snexustsid;

import indexbuilder41.utils.DB;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;


/**
 *
 * @author murphy
 */
public class SNFactory {
    private static HashMap<Integer,SNGroup> grouplist;
    
    public static HashMap<Integer,SNGroup> getSNGroupList() throws Exception{
        if(grouplist == null)
            fillGroups();
        return grouplist;
    }
    private static void fillGroups() throws Exception
    {
        DB db = new DB();
        grouplist = new HashMap();
        try{
            java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://cw-wh-build:1433/LinguisticAnalysis","aspconnect","aspconnect");
            Statement stm = conn.createStatement(); 

            stm.setQueryTimeout(150);

            ResultSet rs;



            rs = stm.executeQuery("select  t.NEXUS_TSID,t.NEXUS_TSNAME, p.nexus_termid,p.term_query  from aspconnect.sNEXUS_PREINDEX p\n" +
"join aspconnect.sNEXUS_TERM_SETS t on t.NEXUS_TSID = p.NEXUS_TSID order by t.NEXUS_TSNAME");

            while(rs.next())
            {
                SNGroup gr=null;
                if(!grouplist.containsKey(rs.getInt("NEXUS_TSID"))){
                    gr = new SNGroup();
                    grouplist.put(rs.getInt("NEXUS_TSID"), gr);
                    gr.code = "snexustsid" + String.valueOf(rs.getInt("NEXUS_TSID"));
                    gr.name = rs.getString("NEXUS_TSNAME");
                }
                else
                    gr = grouplist.get(rs.getInt("NEXUS_TSID"));
                gr.add("snexustermid"+ String.valueOf(rs.getInt("NEXUS_TSID")) , rs.getString("term_query"));
               

            }
            rs.close();

            conn.close();
        }
        catch(java.sql.SQLException sex){
            System.out.println(sex.getMessage());
            grouplist = null;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            grouplist = null;
        }
    }
}
