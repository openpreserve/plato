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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExecutionFootprintList {
    
    private List<ExecutionFootprint> list = new ArrayList<ExecutionFootprint>();
    
    public void add(ExecutionFootprint fp) {
         if (fp == null) {
            return;
        }
        
        // if the list doesn't contain any processes, we don't add fp
        if (fp.getProcesses() == null || fp.getProcesses().size() <= 0) {
            return;
        }
        
        list.add(fp);
    }
    
    /**
     * Calculates the average usage of virtual memory b< the task.
     * 
     * @return Average memory usage by the task (in kb)
     */
    public double getAverageVirtualMemory() {
        double sum = 0;
        
        for (ExecutionFootprint fp : list) {
            sum += fp.getVirtualMemory();
        }
        
        if (list.size()<=0) {
            return 0.0;
        }
        BigDecimal d = new BigDecimal(sum/list.size());
        return (d.setScale(2, BigDecimal.ROUND_UP)).doubleValue();
    }

    /**
     * Calculates the average usage of shared memory by the task.
     * 
     * @return Average memory usage by the task (in kb)
     */
    public double getAverageSharedMemory() {
        double sum = 0;
        for (ExecutionFootprint fp : list) {
            sum += fp.getSharedMemory();
        }
        
        if (list.size()<=0) {
            return 0.0;
        }
        
        BigDecimal d = new BigDecimal(sum/list.size());
        return (d.setScale(2, BigDecimal.ROUND_UP)).doubleValue();
    }

    /**
     * Calculates the average resident size.
     * 
     * @return Average memory usage by the task (in kb)
     */
    public double getAverageResidentSize() {
        double sum = 0;
        for (ExecutionFootprint fp : list) {
            sum += fp.getResidentSize();
        }
        
        if (list.size()<=0) {
            return 0.0;
        }
        
        BigDecimal d = new BigDecimal(sum/list.size());
        return (d.setScale(2, BigDecimal.ROUND_UP)).doubleValue();
    }
    
    public double getTotalCpuTimeUsed() {
        long max = 0;
        for (ExecutionFootprint fp : list) {
            max = Math.max(max, fp.getMaxCpuTimeUsed());
        }               
                
        return max;
    }
    
    public double getMaxVirtualMemory() {
        double max = 0.0;
        for (ExecutionFootprint fp : list) {
            max = Math.max(max, fp.getVirtualMemory());
        }
        
        return max;
    }
    
    public double getMaxResidentSize() {
        double max = 0.0;
        for (ExecutionFootprint fp : list) {
            max = Math.max(max, fp.getResidentSize());
        }
        
        return max;
    }
    
    public double getMaxSharedMemory() {
        double max = 0.0;
        for (ExecutionFootprint fp : list) {
            max = Math.max(max, fp.getSharedMemory());
        }
        
        return max;
    }
    
    public double getAverageTotalCPUUser() {
        double sum = 0.0;
        for (ExecutionFootprint fp : list) {
            sum += fp.getSystemFootprint().getTotalCpusUser();
        }
        
        if (list.size()<=0) {
            return 0.0;
        }
        BigDecimal d = new BigDecimal(sum/list.size());
        return (d.setScale(2, BigDecimal.ROUND_UP)).doubleValue();
    }

    public double getAverageTotalCPUSystem() {
        double sum = 0.0;
        for (ExecutionFootprint fp : list) {
            sum += fp.getSystemFootprint().getTotalCpusSystem();
        }
        
        if (list.size()<=0) {
            return 0.0;
        }
        BigDecimal d = new BigDecimal(sum/list.size());
        return (d.setScale(2, BigDecimal.ROUND_UP)).doubleValue();
    }

    public double getAverageTotalCPUIdle() {
        double sum = 0.0;
        for (ExecutionFootprint fp : list) {
            sum += fp.getSystemFootprint().getTotalCpusIdle();
        }
        
        if (list.size()<=0) {
            return 0.0;
        }
        BigDecimal d = new BigDecimal(sum/list.size());
        return (d.setScale(2, BigDecimal.ROUND_UP)).doubleValue();
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Execution footprint: ");
        //System.out.println("--- Start");
        for (ExecutionFootprint fp : list) {
            
            if (fp == null) {
                continue;
            }
            
            for (ProcessExecutionFootprint pfp : fp.getProcesses()) {
                String pid = Integer.toString(pfp.getPid());
                String ppid = Integer.toString(pfp.getPpid());

                String virt = Double.toString(pfp.getVirtualMemory());
                String res = Double.toString(pfp.getResidentSize());
                String shr = Double.toString(pfp.getSharedMemory());
                String cpu = Double.toString(pfp.getCpu());
                String cpuTimeUsed = Double.toString(pfp.getCpuTimeUsed());
                String pmem = Double.toString(pfp.getPMem());
                
                sb.append("PID: " + pid + " PPID: " + ppid + "virt=" + virt + " res=" + res + " shr=" + shr + " cpu=" + cpu + " cpuTimeUsed=" + cpuTimeUsed + " pmem=" + pmem + " " + pfp.getCommand()+"\n");
            }
            
            String virt = Double.toString(fp.getVirtualMemory());
            String res = Double.toString(fp.getResidentSize());
            String shr = Double.toString(fp.getSharedMemory());
            String cpu = Double.toString(fp.getCpu());
            String cpuTimeUsed = Double.toString(fp.getMaxCpuTimeUsed());
            String pmem = Double.toString(fp.getPMem());
            
            String cpuUser = Double.toString(fp.getSystemFootprint().getTotalCpusUser());
            String cpuIdle = Double.toString(fp.getSystemFootprint().getTotalCpusIdle());
            String cpuSys = Double.toString(fp.getSystemFootprint().getTotalCpusSystem());
            
            sb.append("VIRT | RES | SHR | CPU | CPU time used | PMEM | CPU user | CPU idle | CPU system\n");
            sb.append(virt + " " + res + " " + shr + " " + cpu + " " + cpuTimeUsed + " " + pmem + " " + cpuUser + " " + cpuIdle + " " + cpuSys);
            sb.append("\n - ");
            
        }
        
        String avgResidentSize = Double.toString(this.getAverageResidentSize());
        String avgSharedMemory = Double.toString(this.getAverageSharedMemory());
        String avgVirtualMemory = Double.toString(this.getAverageVirtualMemory());
        String totalCpuTimeUsed = Double.toString(this.getTotalCpuTimeUsed());
        String maxRes = Double.toString(this.getMaxResidentSize());
        String maxShr = Double.toString(this.getMaxSharedMemory());
        String maxVirt = Double.toString(this.getMaxVirtualMemory());
        
        sb.append("\n").append("Overall performance:");
        sb.append("\n").append("average resident size: " + avgResidentSize);
        sb.append("\n").append("average shared memory: " + avgSharedMemory);
        sb.append("\n").append("average virtual memory: " + avgVirtualMemory);
        
        sb.append("\n").append("average total cpu system: " + this.getAverageTotalCPUSystem());
        sb.append("\n").append("average total cpu user: " + this.getAverageTotalCPUUser());
        sb.append("\n").append("average total cpu idle: " + this.getAverageTotalCPUIdle());
        
        sb.append("\n").append("max resident size: " + maxRes);
        sb.append("\n").append("max shared memory: " + maxShr);
        sb.append("\n").append("max virtual memory: " + maxVirt);
        sb.append("\n").append("total cpu time used: " + totalCpuTimeUsed);
        return sb.toString();
    }
}
