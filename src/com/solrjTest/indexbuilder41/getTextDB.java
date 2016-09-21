/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41;

import com.solrjTest.indexbuilder41.utils.DB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author murphy
 */
public class getTextDB implements iGetText
{
    private Connection conn;
    public void setConnection(String connstr,String username, String password) throws Exception
    {
        DB db = new DB();
        conn = db.dbConnect(connstr,username,password);
    
    }
    @Override
    public String getText(String keid, String path) throws Exception
    {
        
        String sqlCount = "Select doc_number from bates_check_new where ke_id = " + keid;
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sqlCount);
        String text = "";
        if(rs.next())
            text = rs.getString("Doc_Number").toLowerCase(); 
        else
            throw new Exception("No results from db!");
        return text;
    }
}
