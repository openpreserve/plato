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

public class WinExecutionFootprintList {

    private List<WinProcessExecutionFootprint> list = new ArrayList<WinProcessExecutionFootprint>();

    public void add(WinProcessExecutionFootprint fp) {
        if (fp == null) {
            return;
        }

        // if pid is null no informations are available
        if (fp.getPid() == null) {
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

        for (WinProcessExecutionFootprint fp : list) {
            sum += fp.getVirtualMemory();
        }

        if (list.size() <= 0) {
            return 0.0;
        }
        BigDecimal d = new BigDecimal(sum / list.size());
        return (d.setScale(2, BigDecimal.ROUND_UP)).doubleValue();
    }

    /**
     * Calculates the average usage of shared memory by the task.
     * 
     * @return Average memory usage by the task (in kb)
     */
    public double getAveragePrivateMemory() {
        double sum = 0;
        for (WinProcessExecutionFootprint fp : list) {
            sum += fp.getPrivateVirtualMemory();
        }

        if (list.size() <= 0) {
            return 0.0;
        }

        BigDecimal d = new BigDecimal(sum / list.size());
        return (d.setScale(2, BigDecimal.ROUND_UP)).doubleValue()/1024;
    }

   public double getMaxVirtualMemory() {
        double max = 0.0;
        for (WinProcessExecutionFootprint fp : list) {
            max = Math.max(max, fp.getVirtualMemory());
        }

        return max;
    }
   
   public double getMaxPrivateMemory() {
       double max = 0.0;
       for (WinProcessExecutionFootprint fp : list) {
           max = Math.max(max, fp.getPrivateVirtualMemory());
       }

       return max;
   }
   
   public double getMaxPrivateVirtualMemoryPeak() {
       double max = 0.0;
       for (WinProcessExecutionFootprint fp : list) {
           max = Math.max(max, fp.getPrivateVirtualMemoryPeak());
       }

       return max;
   }

    public double getMaxUserTime() {
        double max = -999999999.0;
        for (WinProcessExecutionFootprint fp : list) {
            max = Math.max(max, fp.getUserTime());
        }

        return max;
    }

    public double getMaxKernelTime() {
        double max = -999999999.0;
        for (WinProcessExecutionFootprint fp : list) {
            max = Math.max(max, fp.getKernelTime());
        }

        return max;
    }

    public double getMaxElapsedTime() {
        double max = -999999999.0;
        for (WinProcessExecutionFootprint fp : list) {
            max = Math.max(max, fp.getElapsedTime());
        }

        return max;
    }
    
    public String toString() {
        //SimpleDateFormat sdf=new SimpleDateFormat( "H:mm:ss.S" );
        StringBuffer sb = new StringBuffer();
        sb.append("Execution footprint: \n");
        sb.append("Average Private VM: "+getAveragePrivateMemory()+"\n");
        sb.append("Average VM: "+getAverageVirtualMemory()+"\n");
        sb.append("Elapsed Time: "+getMaxElapsedTime()+"\n");
        sb.append("Kernel Time: "+getMaxKernelTime()+"\n");
        sb.append("User Time: "+getMaxUserTime()+"\n");
        return sb.toString();
    }
}
