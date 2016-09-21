/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 *
 * @author murphy
 */
public class ExecuteCommand {
    public static String execute(String cmd) throws InterruptedException, IOException{
        String[] cmds = {"/bin/sh","-c",cmd};
        StringBuilder sb = new StringBuilder();
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(cmds);
        InputStream stdin = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);

        String line = null;
        

        while ( (line = br.readLine()) != null)
        {
            sb.append(line);
            sb.append('\n');
        }

        int exitVal = proc.waitFor();
        //Debug.println("Process exitValue: " + exitVal, "Debug");
        return sb.toString();
        
    }
    
}
