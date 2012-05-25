/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.tuwien.minimee.migration.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses the output file of the linux 'time' command (/usr/bin/time)
 * 
 * @author kulovits
 */
public class TIME_Parser {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Percentage of the CPU that this job got.  This is just user + system times  divided  by  the
     * total running time. It also prints a percentage sign.
     */
    private double pCpu;
    /**
     * Total number of CPU-seconds used by the system on behalf of the process 
     * (in kernel mode), in seconds.
     */
    private double sys;
    /**
     * Total number of CPU-seconds that the process used directly (in user mode), in seconds.
     */
    private double user;
    /**
     * Elapsed real (wall clock) time used by the process, in seconds.
     */
    private double real;
    
    /**
     *  Exit status of the command.
     */
    private int exitCode;

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        TIME_Parser p = new TIME_Parser();
        p.parse("/home/kulovits/time-out.txt");

    }

    public void parse(String fileName) {

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(fileName);
            try {
                BufferedReader input = new BufferedReader(fileReader);
            
                // the output file shall only have one line
                String line;
                while ((line = input.readLine())!=null) {
                    parseLine(line);
                }
            } finally {
                fileReader.close();
            }
        } catch (IOException e) {
            log.error("Failed to parse TIME output " + fileName, e);
        }
    }
    
    /**
     * The line is supposed to look like that: 
     * pCpu:0%,sys:0.00,user:0.00,real:1.00,exit:0
     * @param line
     */
    private void parseLine(String line) {
        
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            
            int colon = token.indexOf(':');
            if (colon == -1) {
                continue;
            }
            
            if ("pCpu".compareTo(token.substring(0, colon)) == 0) {
                
                int pSign = token.indexOf('%');
                if (pSign == -1) {
                    continue;
                }
                
                pCpu = (new Double(token.substring(colon+1, pSign)));
            } else if ("sys".compareTo(token.substring(0, colon)) == 0) {
                
                sys = (new Double(token.substring(colon+1)));
                
            } else if ("user".compareTo(token.substring(0, colon)) == 0) {
                
                user = (new Double(token.substring(colon+1)));
                
            } else if ("real".compareTo(token.substring(0, colon)) == 0) {
                
                real = (new Double(token.substring(colon+1)));
                
            } else if ("exit".compareTo(token.substring(0, colon)) == 0) {
                
                exitCode = (new Integer(token.substring(colon+1)));
            }
        }
    }

    public int getExitCode() {
        return exitCode;
    }

    public double getPCpu() {
        return pCpu;
    }

    public double getReal() {
        return real;
    }

    public double getSys() {
        return sys;
    }

    public double getUser() {
        return user;
    }
    
}
