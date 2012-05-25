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
package at.tuwien.minimee.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can parse a file written by the Windows bat file 'topWin' and fit
 * the information into the data structure {@link ExecutionFootprintList}.
 * 
 * 'topWin' writes a log file with entries as can be seen below every x seconds.
 * 
 * Process memory detail for TU-YSI87UIFJM83:
 * 
 * Name   Pid   VM    WS   Priv Priv Pk  Faults NonP Page Tid Pri Cswtch State    User Time   Kernel Time Elapsed Time 
 * lame   2840  12168 2660 1568 1568     673    2    69   968 8   476    Running  0:00:00.140 0:00:00.000 0:00:00.171
 * 
 * @author gottardi
 */
public class PslistWinParser {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private String file;

    private WinExecutionFootprintList list = null;

    private RandomAccessFile input = null;
    
    private boolean dataLine;

    public PslistWinParser(String file) {

        this.file = file;
        dataLine=false;

    }

    public static void main(String[] args) {

        PslistWinParser p = new PslistWinParser("C:/Documents and Settings/riccardo/Desktop/topWinExample.txt");
        p.parse();

        System.out.println(p.getList().toString());

    }

    public void parse() {

        try {
            input = new RandomAccessFile(file, "r");
            list = new WinExecutionFootprintList();

            try {
                String line = null;
               
                while ((line = input.readLine()) != null && line!="") {
                    parseLine(line);
                }

            } finally {
                input.close();
            }

            // list.debugToConsole();

        } catch (Exception e) {
            log.error("Failed to parse PslistWin", e);
        }
    }

    private void parseLine(String line) throws IOException {

        StringTokenizer tokenizer = new StringTokenizer(line, " ");

        if (!tokenizer.hasMoreTokens()) {
            return;
        }

        String start = tokenizer.nextToken();

        if (start.compareTo("Name") == 0) {
            this.dataLine = true;
            return;
        } else if (!dataLine)
            return;

        // only if dataline is true can read the informations
        WinProcessExecutionFootprint pfp = parseProcessLine(tokenizer);
        line = input.readLine();
        line = input.readLine();
        tokenizer = new StringTokenizer(line, " ");
        
        if(line != null && line!="") 
            parseProcessTimeLine(tokenizer, pfp);
        
        list.add(pfp);
        
        dataLine=false;
        // }

    }
    private void parseProcessTimeLine(StringTokenizer tokenizer, WinProcessExecutionFootprint pfp){
       
        int column = 1;
        for (column = 1; tokenizer.hasMoreTokens(); column++) {

            String strColumn = tokenizer.nextToken(" ");

            switch (column) {

            case 1: // Tid
                break;

            case 2: // Pri
                break;

            case 3: // Cswtch 
                break;
                
            case 4: // State 
                break;
                
            case 5: // User Time 
                pfp.setUserTime(parseTimeColumn(strColumn));
                break;
                
            case 6: // Kernel Time 
                pfp.setKernelTime(parseTimeColumn(strColumn));
                break;

            case 7: // Running Time
                pfp.setElapsedTime(parseTimeColumn(strColumn));
                break;
            }
        }
  
    }
  
    private WinProcessExecutionFootprint parseProcessLine(StringTokenizer tokenizer) {

        WinProcessExecutionFootprint pfp = new WinProcessExecutionFootprint();

        int column = 1;
        for (column = 1; tokenizer.hasMoreTokens(); column++) {

            String strColumn = tokenizer.nextToken(" ");

            switch (column) {

            case 1: // Pid
                Double pid = parseSizeColumn(strColumn);
                pfp.setPid(pid.intValue());
                break;

            case 2: // VM
                Double virt = parseSizeColumn(strColumn);
                pfp.setVirtualMemory(virt);
                break;

            case 3: // WS: working set               
                break;

            case 4: // Private virtual memory
                Double pvmem = parseSizeColumn(strColumn);
                pfp.setPrivateVirtualMemory(pvmem);
                break;

            case 5: // Priv Pk
                Double privPeak = parseSizeColumn(strColumn);
                pfp.setPrivateVirtualMemoryPeak(privPeak);
                break;

            case 6: // Faults
                break;

            case 7: // NonP
                break;

            case 8: // Page
                break;

            default:
                continue;
            }
        }
        
        return pfp;
    }
    
   /**
    * Extract milliseconds from time String (H:mm:ss.S)
    * @param strColumn
    * @return
    */    
    private Double parseTimeColumn(String strColumn){
        try{
            StringTokenizer st=new StringTokenizer(strColumn,":");
            Double hour=Double.parseDouble(st.nextToken());
            Double minutes=Double.parseDouble(st.nextToken());
            String tmpToken=st.nextToken();
            Double seconds=Double.parseDouble(tmpToken.substring(0,tmpToken.indexOf(".")));
            Double milliSeconds=Double.parseDouble(tmpToken.substring(tmpToken.indexOf(".")+1));
           
            Double totalSeconds=hour*60*60*1000+
                                minutes*60*1000+
                                seconds*1000+
                                milliSeconds;
        return totalSeconds;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private Double parseSizeColumn(String strColumn) {
        strColumn = strColumn.trim();

        Double virt = null;

        if (strColumn.charAt(strColumn.length() - 1) == 'm') { // we have mega
                                                                // byte
            virt = new Double(strColumn.substring(0, strColumn.length() - 1));
            virt *= 1024;
        } else if (strColumn.charAt(strColumn.length() - 1) == 'k') { // we
                                                                        // have
                                                                        // kilo
                                                                        // bytes
            virt = new Double(strColumn.substring(0, strColumn.length() - 1));
        } else {
            virt = new Double(strColumn);
        }
        return virt;
    }

    public WinExecutionFootprintList getList() {
        return list;
    }
}
