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
public class NFactory {
    private static HashMap<Integer,NBank> banklist;
    private static HashMap<Integer,NGroup> grouplist;
    public static HashMap<Integer,NBank> getNBankList() throws Exception{
        if(banklist == null)
            fillGroups();
        return banklist;
    }
    public static HashMap<Integer,NGroup> getNGroupList() throws Exception{
        if(grouplist == null)
            throw new Exception("Fill banks First!");
        return grouplist;
    }
    private static void fillGroups() throws Exception
    {
        DB db = new DB();
        banklist = new HashMap<>();
        grouplist = new HashMap<>();
        try{
            java.sql.Connection conn = db.dbConnect("jdbc:jtds:sqlserver://cw-wh-build:1433/LinguisticAnalysis","aspconnect","aspconnect");
            Statement stm = conn.createStatement(); 

            stm.setQueryTimeout(150);

            ResultSet rs;



            rs = stm.executeQuery("select distinct p.nexus_pbankid, p.bank_name, p.bank_desc  from aspconnect.NEXUS_PREINDEX i join aspconnect.NEXUS_PREINDEX_GROUPS t on t.NEXUS_TSID = i.NEXUS_TSID join aspconnect.NEXUS_PREINDEX_BANKS p on p.NEXUS_PBANKID = t.NEXUS_PBANKID where bank_active = 1");

            while(rs.next())
            {
                NBank bank=null;
                {
                    bank = new NBank();
                    banklist.put(rs.getInt("nexus_pbankid"), bank);
                    bank.code = rs.getInt("nexus_pbankid");
                    bank.name = rs.getString("bank_name");
                }
                
               

            }
            rs.close();
            rs = stm.executeQuery("select  nexus_pbankid, t.NEXUS_TSID,t.Group_Name, p.nexus_termid,p.term_query from aspconnect.NEXUS_PREINDEX p join aspconnect.NEXUS_PREINDEX_GROUPS t on t.NEXUS_TSID = p.NEXUS_TSID order by t.NEXUS_TSID");

            while(rs.next())
            {
                NGroup gr=null;
                if(!grouplist.containsKey(rs.getInt("NEXUS_TSID"))){
                    gr = new NGroup();
                    grouplist.put(rs.getInt("NEXUS_TSID"), gr);
                    int id = rs.getInt("NEXUS_TSID");
                    gr.code = "nexustsid" + String.valueOf(id);
                    gr.name = rs.getString("GROUP_NAME");
                    NBank bank = banklist.get(rs.getInt("nexus_pbankid"));
                    bank.add(gr.code, gr.name);
                    
                }
                else
                    gr = grouplist.get(rs.getInt("NEXUS_TSID"));
                gr.add("nexustermid"+ String.valueOf(rs.getInt("NEXUS_TSID")) , rs.getString("term_query"));
               

            }
            rs.close();
            conn.close();
        }
        catch(java.sql.SQLException sex){
            System.out.println(sex.getMessage());
            banklist = null;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            banklist = null;
        }
    }
}
