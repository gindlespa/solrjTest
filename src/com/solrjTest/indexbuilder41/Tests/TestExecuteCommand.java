/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Tests;

import indexbuilder41.utils.DoLanguageDetect;
import indexbuilder41.utils.ExecuteCommand;
import indexbuilder41.utils.LanguageDetectionResult;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author murphy
 */
public class TestExecuteCommand {
    public static void main(String[] args) throws InterruptedException, IOException{
        
        LanguageDetectionResult[] output = DoLanguageDetect.getLanguage("This is a test to check engish language");
        for(int i = 0; i < output.length; i++)
            System.out.println(output[i].language + "\t" + output[i].percent + "%");
        
    }
    
}
