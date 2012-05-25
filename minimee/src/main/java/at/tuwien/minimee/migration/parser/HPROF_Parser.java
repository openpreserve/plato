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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HPROF_Parser {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    long total_virtual = 0;

    long total_allocated = 0;

    public HPROF_Parser() {
        super();
    }

    public static void main(String args[]) {
        HPROF_Parser hp_parser = new HPROF_Parser();
        hp_parser
                .parse("/home/riccardo/profilers/hprof/hprof_1_Image_0_2Mb.hprof");
        System.out.println("Virtual:" + hp_parser.getTotal_virtual()
                + "MB Allocated: " + hp_parser.getTotal_allocated() + "MB");
    }

    /**
     * Returns the Total Allocated Memory in MB
     * 
     * @return
     */
    public double getTotal_allocated() {
        return (((double)  total_allocated) / (1024 * 1024));
    }

    /**
     * Returns the Total Virtual Memory in MB
     * 
     * @return
     */
    public double getTotal_virtual() {
        return (((double) total_virtual) / (1024 * 1024));
    }

    public void parse(String fileToRead) {
        
        try {
        
            total_virtual = 0;
            total_allocated = 0;
    
            /*
             * Sets up a file reader to read the file passed on the command line
             * one character at a time
             */
            FileReader input = new FileReader(fileToRead);
    
            /*
             * Filter FileReader through a Buffered read to read a line at a
             * time
             */
            try {
                BufferedReader bufRead = new BufferedReader(input);
                String line; // String that holds current file line
    
                // Read first line
                line = bufRead.readLine();
    
                // Read through file one line at time. Print line # and line
                while (line != null) {
                    if (line.contains(" rank   self"))
                        break;
                    line = bufRead.readLine();
                }
    
                // read next line containing the first info
                line = bufRead.readLine();
    
                // begin parsing
                while (line != null && line.compareTo("SITES END") != 0) {
                    interpretline(line);
                    line = bufRead.readLine();
                }
    
            } finally {
                input.close();
            } 
        } catch (IOException e) {
            // If another exception is generated, print a stack trace
           log.error("Failed to parse HPROF output " + fileToRead,  e);
        } 

    }// end main

    private void interpretline(String line) {
        char[] chars = new char[line.length()];
        line.getChars(0, line.length() - 1, chars, 0);

        String live_mem = "";
        String alloc_mem = "";

        int countWord = 0;
        int wordEntered = 0;

        for (char c : chars) {
            if (c == ' ') {
                wordEntered = 0;
                continue;
            }
            if (wordEntered == 0)
                countWord++;
            wordEntered = 1;
            if (countWord == 4)
                live_mem += c;
            else if (countWord == 6)
                alloc_mem += c;
        }

        total_virtual = total_virtual + Long.parseLong(live_mem);
        total_allocated = total_allocated + Long.parseLong(alloc_mem);
    }
}
