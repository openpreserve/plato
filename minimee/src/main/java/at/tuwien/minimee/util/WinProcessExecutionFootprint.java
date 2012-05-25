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


public class WinProcessExecutionFootprint {

    private Integer pid;

    private String command;

    /**
     * The total amount of virtual memory used by the task.
     */
    private double virtualMemory;
    
    /**
     * Private Virtual Memory is as definition the memory not
     * backed by an image or data file on disk, which is considered sharable memory
     */
    private double privateVirtualMemory;
    
    /**
     * Private Maximale Virtual Memory
     */
    private double privateVirtualMemoryPeak;
    
    /**
     * Length of the time used by the CPU for executing user calls and functions (msec).
     */
    private Double userTime;
    
    /**
     * Length of time the thread used the CPU to execute system calls (msec)
     */
    private Double kernelTime;
    
    /**
     * Total length of time the thread has been running (msec)
     */
    private Double elapsedTime;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public double getPrivateVirtualMemoryPeak() {
        return privateVirtualMemoryPeak;
    }

    public void setPrivateVirtualMemoryPeak(double privateVirtualMemoryPeak) {
        this.privateVirtualMemoryPeak = privateVirtualMemoryPeak;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public double getPrivateVirtualMemory() {
        return privateVirtualMemory;
    }

    public void setPrivateVirtualMemory(double privateVirtualMemory) {
        this.privateVirtualMemory = privateVirtualMemory;
    }

    public double getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(double virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public Double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Double getKernelTime() {
        return kernelTime;
    }

    public void setKernelTime(Double kernelTime) {
        this.kernelTime = kernelTime;
    }

    public Double getUserTime() {
        return userTime;
    }

    public void setUserTime(Double userTime) {
        this.userTime = userTime;
    }

  
    

//    /**
//     * The non-swapped physical memory a task has used. RES = CODE + DATA.
//     */
//    private double residentSize;
//
//    /**
//     * The amount of shared memory used by a task. It simply reflects memory
//     * that could be potentially shared with other processes.
//     */
//    private double sharedMemory;
//
//    
//
//    /**
//     * A task's currently used share of available physical memory.
//     */
//    private double pMem;
//
//    /**
//     * The task's share of the elapsed CPU time since the last screen update,
//     * expressed as a percentage of total CPU time. In a true SMP environment,
//     * if 'Irix mode' is Off, top will operate in 'Solaris mode' where a task's
//     * cpu usage will be divided by the total number of CPUs. You toggle
//     * 'Irix/Solaris' modes with the 'I' interactive command.
//     */
//    private double cpu;

  

}
