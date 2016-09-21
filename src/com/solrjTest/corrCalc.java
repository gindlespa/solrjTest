/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest;
/**
 *
 * @author murphy
 */
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.highlight.TokenSources;

public class corrCalc {
    private Pattern alphanumeric;
    private Pattern nonalphanumeric;
    private Pattern whiteSpace;
    private Map htNoise;

    public corrCalc()
    {
        alphanumeric = Pattern.compile("[a-zA-Z0-9]");
        nonalphanumeric = Pattern.compile("[^a-zA-Z0-9]*");
        whiteSpace = Pattern.compile("[^\\S]");
        //setNoise();
    }

    public Map buildHashtable(String input)
    {
        Map ht = new HashMap();
        Analyzer anal=null;
        try {
            anal = new MurphyAnalyzer(null);
        } catch (Exception ex) {
            Logger.getLogger(corrCalc.class.getName()).log(Level.SEVERE, null, ex);
        }
        TokenStream stream = TokenSources.getTokenStream("text", input, anal);
        String[] arr = input.split("|");
        for (int i = 0; i < arr.length; i++)
        {
            String[] word = arr[i].split("-");
            ht.put(word[1], Integer.parseInt(word[0]));
        }
        return ht;
    }

    public Map parseString(String input)
    {

        Map ht = new HashMap();
        String tmpWord = "";
        int page = -1;
        int charPos = -1;
        boolean onWord = false;

        Matcher match;
        for (int i = 0; i < input.length() + 1; i++)
        {
            charPos++;

            String currentChar;
            if (i != input.length())
                currentChar = input.substring(i, i + 1).toLowerCase();
            else
                currentChar = " ";
            if (alphanumeric.matcher(currentChar).matches())
            {
                if (tmpWord.length() < 101)
                    tmpWord += currentChar;

                onWord = true;
            }
            else
            {
                if (onWord)
                {
                    if (!ht.containsKey(tmpWord))
                        ht.put(tmpWord, 1);
                    else
                    {
                        int val = ((Number)ht.get(tmpWord)).intValue()+1;
                        ht.remove(tmpWord);
                        ht.put(tmpWord,val);
                    }
                    tmpWord = "";
                    onWord = false;

                }
            }
        }
        if (onWord)
        {
            if (!ht.containsKey(tmpWord))
                ht.put(tmpWord, 1);
            else
            {
                int val = ((Number)ht.get(tmpWord)).intValue()+1;
                ht.remove(tmpWord);
                ht.put(tmpWord,val);
            }
            tmpWord = "";
            onWord = false;

        }
        ht.put("zjnftyslpnnm", 0);
        return ht;
    }
    public double getCorrelation(Map ht1, Map ht2)
    {
        Map htMain = new HashMap();
        ArrayList ar1 = new ArrayList<Integer>();
        ArrayList ar2 = new ArrayList<Integer>();
        Iterator it = ht1.keySet().iterator();
        while(it.hasNext())
            htMain.put(it.next(), 0);
        it = ht2.keySet().iterator();
        while(it.hasNext())
        {
            String key = it.next().toString();
            if(!htMain.containsKey(key))
                htMain.put(key, 0);
        }
        it = htMain.keySet().iterator();

        while(it.hasNext())
        {
            String key = it.next().toString();
            Object val;
            if (!ht1.containsKey(key))
                val = 0;
            else
                val = ((Number)ht1.get(key)).intValue();
            ar1.add(val);
            if (!ht2.containsKey(key))
                val = 0;
            else
                val = ((Number)ht2.get(key)).intValue();
            ar2.add(val);
        }
        return calcCorrelation(ar1, ar2);
    }
    private double calcCorrelation(ArrayList<Integer> ar1, ArrayList<Integer> ar2)
    {
        long size;
        if (ar1.size() != ar2.size())
        {
            new Exception("Invalid Array Lengths");
            return 0;
        }
        size = ar1.size();
        double xsd = standardDeviation(ar1);
        double ysd = standardDeviation(ar2);
        long Exy = 0;
        for (int i = 0; i < size; i++)
            Exy += ar1.get(i) * ar2.get(i);
        long sqsum = (long)getSum(ar1) * (long)getSum(ar2);
        double bot = (size - 1) * xsd * ysd;
        double score = (Exy - (sqsum / size)) / bot;

        return score;

    }
    private double standardDeviation(ArrayList<Integer> ar)
    {
        int n = ar.size();

        if (n == 0 || n == 1)
            return 0f;

        float E_x = (float)getSum(ar);
        float E_x2 = (float)getSumSq(ar);
        return Math.sqrt((E_x2 - ((E_x * E_x) / n)) / (n - 1));

    }
    private int getSum(ArrayList<Integer> ar)
    {
        int total = 0;
        Iterator it = ar.iterator();
        while(it.hasNext()){
            int val = (Integer)it.next();
            total += val;
        }
        return total;
    }
    private int getSumSq(ArrayList ar)
    {
        int total = 0;
        Iterator it = ar.iterator();
        while(it.hasNext()){
            int val = (Integer)it.next();
            total += val * val;
        }
        return total;
    }
}


