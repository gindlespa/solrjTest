/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.solrjTest;

/**
 *
 * @author murphy
 */
public class thrWriter {
    private volatile int stackCount;
    private volatile int tCount;
    private volatile int sCount;
    private StringBuilder sb;
    public thrWriter(){
        sb = new StringBuilder();
        stackCount = 0;
        tCount=0;
        tCount=0;
    }
    public synchronized int getStackCount(){
        return stackCount;
    }
    public synchronized void addStackCount(){
        stackCount++;
        tCount++;
    }
    public synchronized void subtractStackCount(){
        stackCount--;
        sCount++;
    }
    
    public void write(String txt){
        synchronized(this){
            sb.append(txt);
        }
        //System.out.print(txt);
    }
    public String read(){
        String out;
        synchronized(this){
            out = sb.toString();
            sb = new StringBuilder();
        }
        return out;
    }
}
