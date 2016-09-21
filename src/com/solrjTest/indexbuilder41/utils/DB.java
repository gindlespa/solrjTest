/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;
import java.sql.*;
/**
 *
 * @author murphy
 */
public class DB{

    public DB() {}

    public Connection dbConnect(String db_connect_string,
  String db_userid, String db_password)  throws Exception
    {
       
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);

            //System.out.println("connected");
            return conn;

       
    }
};
