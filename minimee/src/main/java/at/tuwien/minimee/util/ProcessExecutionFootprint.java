/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package at.tuwien.minimee.util;

public class ProcessExecutionFootprint {

    private int pid;
    
    /**
     * parent's process id;
     */
    private int ppid;
    
    private String command;
    
    /**
     * The  total  amount  of virtual memory used by the task.  It includes
     * all code, data and  shared  libraries  plus  pages  that  have  been
     * swapped out.
     * VIRT = SWAP + RES.
     */
    private double virtualMemory;
    
    /**
     * The non-swapped physical memory a task has used.
     * RES = CODE + DATA.
     */
    private double residentSize;
    
    /**
     * The amount of shared memory used by a task.  It simply reflects memory 
     * that could be potentially shared with other processes.
     */
    private double sharedMemory;
    
    /**
     * Total CPU time the task has used since it started.
     */
    private long cpuTimeUsed;
    
    /**
     * A task's currently used share of available physical memory.
     */
    private double pMem;
    
    /**
     * The task's share of the elapsed  CPU  time  since  the  last  screen
     * update,  expressed as a percentage of total CPU time.  In a true SMP
     * environment, if 'Irix mode' is Off, top  will  operate  in  'Solaris
     * mode'  where  a task's cpu usage will be divided by the total number
     * of CPUs.  You toggle 'Irix/Solaris' modes with the  'I'  interactive
     * command.
     */
    private double cpu;
   


    public long getCpuTimeUsed() {
        return cpuTimeUsed;
    }

    public void setCpuTimeUsed(long cpuTimeUsed) {
        this.cpuTimeUsed = cpuTimeUsed;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPpid() {
        return ppid;
    }

    public void setPpid(int ppid) {
        this.ppid = ppid;
    }

    public double getResidentSize() {
        return residentSize;
    }

    public void setResidentSize(double residentSize) {
        this.residentSize = residentSize;
    }

    public double getSharedMemory() {
        return sharedMemory;
    }

    public void setSharedMemory(double sharedMemory) {
        this.sharedMemory = sharedMemory;
    }

    public double getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(double virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public double getPMem() {
        return pMem;
    }

    public void setPMem(double mem) {
        pMem = mem;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
