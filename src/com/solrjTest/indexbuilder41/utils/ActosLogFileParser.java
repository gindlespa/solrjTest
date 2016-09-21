/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest.indexbuilder41.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author murphy
 */
public class ActosLogFileParser {
    final static String DATE_FORMAT = "EEEE, MMMM dd, yyyy h:mm:ss aa";
    private static  DateFormat df;
    public static boolean isDateValid(String date) 
    {
            try {
                 
                df.setLenient(true);
                df.parse(date);
                return true;
            } catch (ParseException e) {
                return false;
            } catch (Exception e){
                return false;
            }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException{
        df = new SimpleDateFormat(DATE_FORMAT);
        File fl = new File(args[0]);
        FileReader fr = new FileReader(fl);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while(line!=null){
            if(isDateValid(line)){
                System.out.print(line);
                System.out.print('\t');
            }
            if(line.equals("User Name")){
                line = br.readLine();
                System.out.print(line);
                System.out.print("\r\n");
            }
            
            line = br.readLine();
        }
    }
}
