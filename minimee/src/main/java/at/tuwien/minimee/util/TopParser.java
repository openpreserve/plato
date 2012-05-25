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

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can parse a file written by the Linux program 'top' and fit 
 * the information into the data structure {@link ExecutionFootprintList}.
 * 
 * 'top' writes a log file with entries as can be seen below every x seconds.
 * 
 * top - 15:45:06 up 2 days, 23:43,  2 users,  load average: 0.19, 0.31, 0.26
 * Tasks:   1 total,   1 running,   0 sleeping,   0 stopped,   0 zombie
 * Cpu(s):  0.8%us,  0.2%sy,  0.0%ni, 98.8%id,  0.1%wa,  0.0%hi,  0.0%si,  0.0%st
 * Mem:   2036960k total,  1853784k used,   183176k free,    74072k buffers
 * Swap:  4192956k total,     5396k used,  4187560k free,   813216k cached
 *
 * PID USER      PR  NI  VIRT  RES  SHR S %CPU %MEM    TIME+  COMMAND            
 * 3758 kulovits  20   0 32400  20m 3452 R   93  1.0   0:00.46 gs                 
 * 
 * @author kulovits
 */
public class TopParser {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    private String file;
    
    private ExecutionFootprint currentExecutionFootprint = null;
    
    private ExecutionFootprintList list = new ExecutionFootprintList();
    
    private Integer monitoredPid = null;
    
    private RandomAccessFile input = null;
    
    public TopParser(String file) {
        
        this.file = file;
    }
    
    public static void main (String[] args) {
        
        TopParser p = new TopParser("/tmp/profile_1234906043659516000/top.log");
        p.parse();
        
        System.out.println(p.getList().toString());
       
    }

    public void parse() {
        
        try {
            
            input = new RandomAccessFile(file, "r");
            
            try {
                
                monitoredPid = findPid();
                
                // couldn't determine PID
                if (monitoredPid == null) {
                    return;
                }
                
                String line = null;
                while (( line = input.readLine()) != null){
	                parseLine(line);
                }
                
            } finally {
                input.close();
            }
            
            // list.debugToConsole();
            
        } catch (Exception e) {
            log.error("Failed to parse Top.",e);
        }
    }
    
    /**
     * The process ID is in the last line of the file and looks like follows:
     * monitored_pid= 6738 
     * 
     * @param input
     * @return
     * @throws Exception
     */
    private Integer findPid() throws Exception {
        
        Integer pid = new Integer(0);
        
        // we open the file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        
        try {
            
            long size = f.length();
    
            f.seek(size-2);
    
            // we search the file reverse for '='
            byte[] b = new byte[1];
            for (long i = size-2; i>= 0; i--) {
                f.seek(i);
                f.read(b);
                if (b[0] == '=') {
                    break;
                }
            }
    
            String line = f.readLine().trim();
    
            pid = new Integer(line);
            
        } finally {
            // this is important, RandomAccessFile doesn't close the file handle by default!
            // if close isn't called, you'll get very soon 'too many open files'
            f.close(); 
        }
        
        return pid;
    }
    
    private void parseLine(String line) {
        
        StringTokenizer tokenizer = new StringTokenizer(line, " :");
        
        if (!tokenizer.hasMoreTokens()) {
            return;
        }
        
        String start = tokenizer.nextToken();
        
        if (start.compareTo("top") == 0) {
            
            if (currentExecutionFootprint != null) {
                cleanUp(currentExecutionFootprint);
                list.add(currentExecutionFootprint);    
            }            
            currentExecutionFootprint = new ExecutionFootprint();
            
            parseTopLine(tokenizer);
            
        } else if (start.compareTo("Tasks") == 0) {
            
            parseTasksLine(tokenizer);
        } else if (start.compareTo("Cpu(s)") == 0) {
            
            parseCpusLine(line);
        } else if (start.compareTo("Mem") == 0) {
            
            parseMemLine(tokenizer);            
        } else if (start.compareTo("Swap") == 0) {
            
            parseSwapLine(tokenizer);
        } else if (start.compareTo("PID") == 0) {       // Header line
            
            parseHeaderLine(tokenizer);                 // we read the header line

            // ExecutionFootprintList fp = tempReadPerformanceData();
        } else if (start.startsWith("monitored_pid=")) {
            // do nothing
        } else { 
            try {
                Integer pid = new Integer(start.trim());
                
                // if (pid.equals(monitoredPid)) {
                    // currentExecutionFootprint.setPid(pid);
                ProcessExecutionFootprint pfp = parseProcessLine(tokenizer);
                
                pfp.setPid(pid);
                
                currentExecutionFootprint.getProcesses().add(pfp);
                // }
                
            } catch(NumberFormatException e) {
                log.error("Failed to parse line " + line,e);
            }
        }
    }
    
    private void cleanUp(ExecutionFootprint fp) {
        
        List<ProcessExecutionFootprint> processes = new ArrayList<ProcessExecutionFootprint>();
        
        ProcessExecutionFootprint root = null;
        
        for (ProcessExecutionFootprint pfp : fp.getProcesses()) {
            if (pfp.getPid() == monitoredPid.intValue()) {
                root = pfp;
                break;
            }
        }
        
        // we didn't find any process with PID 'monitoredPid'
        if (root == null) {
            // we remove all processes from list
            fp.getProcesses().clear();
            return;
        }
        
        processes.add(root);
        
        ProcessExecutionFootprint pfp = root;
        
        while (pfp.getPpid() != 1) {

            ProcessExecutionFootprint prev = pfp;
            for (ProcessExecutionFootprint iter : fp.getProcesses()) {
                if (iter.getPpid() == pfp.getPid()) {
                    pfp = iter;
                    break;
                }
            }
            
            // we didn't find a child
            if (prev == pfp) {
                break;
            }
            
            if (pfp.getPid() != 1) {
                processes.add(pfp);
            }
        }
        
        fp.setProcesses(processes);
    }
    
    private void parseTopLine(StringTokenizer tokenizer) {
        
    }
    
    private void parseTasksLine(StringTokenizer tokenizer) {
        
    }
    
    private void parseCpusLine(String line) {
                        
        int index;
        
        index = line.indexOf("%us");
        if (index != -1) {
            int lastIndex = line.lastIndexOf(' ', index);
            // this is the case when 'Cpu(s):100.0%us,'
            if (lastIndex == -1) {
                lastIndex = line.lastIndexOf(':', index) + 1;
            }
            String s = line.substring(lastIndex, index);
            currentExecutionFootprint.getSystemFootprint().setTotalCpusUser(new Double(s));
        }
        
        index = line.indexOf("%sy");
        if (index != -1) {
            String s = line.substring(line.lastIndexOf(',', index)+1, index);
            currentExecutionFootprint.getSystemFootprint().setTotalCpusSystem(new Double(s));
        }

        index = line.indexOf("%id");
        if (index != -1) {
            String s = line.substring(line.lastIndexOf(',', index)+1, index);
            currentExecutionFootprint.getSystemFootprint().setTotalCpusIdle(new Double(s));
        }
        
    }
    
    private void parseMemLine(StringTokenizer tokenizer) {
        
        int column = 1;
        Double d = null;
        
        for (column = 1; tokenizer.hasMoreTokens() && column < 4; column++) {
            
            String strColumn = tokenizer.nextToken().trim();
            
            switch(column) {
            case 1:     // total mem available (e.g. 2036960k)
                
                d = parseSizeColumn(strColumn);               
                currentExecutionFootprint.getSystemFootprint().setTotalMemoryAvailable(d);
                
                break;
                
            case 2:     // this is just the text: 'total,'
                break;
                
            case 3:
                d = parseSizeColumn(strColumn);               
                currentExecutionFootprint.getSystemFootprint().setTotalMemoryAvailable(d);
                
                break;
            }

        }
    }
    
    private void parseSwapLine(StringTokenizer tokenizer) {
 
    }
    
    /**
     * Doesn't do anything right now, maybe do some validation
     * @param tokenizer
     */
    private void parseHeaderLine(StringTokenizer tokenizer) {
        
    }
    
    private ProcessExecutionFootprint parseProcessLine(StringTokenizer tokenizer) {
        
        ProcessExecutionFootprint pfp = new ProcessExecutionFootprint();
        
        int column = 1;
        for (column = 1; tokenizer.hasMoreTokens(); column++) {
            
            String strColumn = tokenizer.nextToken(" ");
            
            switch(column) {
            
            case 1:             // USER
                break;
                
            case 2:             // PR
                break;
                
            case 3:             // NI
                break;
                
            case 4:             // VIRT
                
                Double virt = parseSizeColumn(strColumn);
                pfp.setVirtualMemory(virt);
                
                break;
                
            case 5:             // RES

                Double res = parseSizeColumn(strColumn);
                pfp.setResidentSize(res);
                
                break;
                
            case 6:             // SHR
                
                Double shr = parseSizeColumn(strColumn);
                pfp.setSharedMemory(shr);
                
                break;
                
            case 7:             // S
                break;
                
            case 8:             // %CPU
                
                Double pCPU = new Double(strColumn);
                pfp.setCpu(pCPU);
                
                break;
                
            case 9:             // %MEM
                
                Double pMem = new Double(strColumn);
                pfp.setPMem(pMem);
                
                break;
                
            case 10:            // TIME+
                
                // mm:ss.hh
                
                String[] tokens = strColumn.split("[:|.]");
                
                if (tokens.length == 3) {
                    long cpuTimeUsed = (new Long(tokens[0]))*60*1000;
                    cpuTimeUsed += (new Long(tokens[1]))*1000;
                    cpuTimeUsed += (new Long(tokens[2]))*10;
                    pfp.setCpuTimeUsed(cpuTimeUsed);
                }
                
                break;
                
            case 11:            // PPID
                
                pfp.setPpid(new Integer(strColumn));
                
                break;
                
            case 12:
                
                pfp.setCommand(strColumn);
                break;
                
            default:
                continue;
            }
        }
        
        return pfp;
    }

    private Double parseSizeColumn(String strColumn) {
        strColumn = strColumn.trim();
        
        Double virt = null;
        
        if (strColumn.charAt(strColumn.length()-1) == 'g') {    // we have giga byte
            virt = new Double(strColumn.substring(0, strColumn.length()-1));
            virt *= (1024 * 1024);
        } else if (strColumn.charAt(strColumn.length()-1) == 'm') {    // we have mega byte
            virt = new Double(strColumn.substring(0, strColumn.length()-1));
            virt *= 1024;
        } else if (strColumn.charAt(strColumn.length()-1) == 'k') { // we have kilo bytes
            virt = new Double(strColumn.substring(0, strColumn.length()-1));
        } else {
            virt = new Double(strColumn);
        }
        return virt;
    }

    public ExecutionFootprintList getList() {
        return list;
    }
}
