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

import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: No parent-child hierarchy because of performance reasons. If we wanted this data structure
 * we would have to jump back and forth in the file containing the 'top' performance values. As this
 * file can be very large we don't want to do this. 
 * 
 * @author hannes
 *
 */
public class ExecutionFootprint {
    
    private List<ProcessExecutionFootprint> processes = new ArrayList<ProcessExecutionFootprint>();
    
    private SystemExecutionFootprint systemFootprint = new SystemExecutionFootprint();
    
    public double getVirtualMemory() {
        double sum = 0;
        for (ProcessExecutionFootprint fp : processes) {
            sum += fp.getVirtualMemory();
        }
        
        return sum;
    }
    
    public double getSharedMemory() {
        double sum = 0;
        for (ProcessExecutionFootprint fp : processes) {
            sum += fp.getSharedMemory();
        }
        
        return sum;
    }
      
    public double getResidentSize() {
        double sum = 0;
        for (ProcessExecutionFootprint fp : processes) {
            sum += fp.getResidentSize();
        }
        
        return sum;
    }

    public double getPMem() {
        double sum = 0;
        for (ProcessExecutionFootprint fp : processes) {
            sum += fp.getPMem();
        }
        
        return sum;
    }
    
    public double getCpu() {
        double sum = 0;
        for (ProcessExecutionFootprint fp : processes) {
            sum += fp.getCpu();
        }
        
        return sum;
    }

    public long getMaxCpuTimeUsed() {
        long max = 0;
        
        for (ProcessExecutionFootprint fp : processes) {
            max = Math.max(max, fp.getCpuTimeUsed());
        }
        
        return max;
    }

    public double getMaxCpu() {
        double max = 0.0;
        
        for (ProcessExecutionFootprint fp : processes) {
            max = Math.max(max, fp.getCpu());
        }
        
        return max;
    }
    
    public List<ProcessExecutionFootprint> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessExecutionFootprint> processes) {
        this.processes = processes;
    }

    public SystemExecutionFootprint getSystemFootprint() {
        return systemFootprint;
    }

    public void setSystemFootprint(SystemExecutionFootprint systemFootprint) {
        this.systemFootprint = systemFootprint;
    }
}
