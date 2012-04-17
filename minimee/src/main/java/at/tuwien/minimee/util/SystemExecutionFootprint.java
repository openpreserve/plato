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

public class SystemExecutionFootprint {
    private double totalMemoryAvailable;
    
    private double totalMemoryUsed;
    
    /**
     * The time the CPU has spent running user's processes that are not niced.
     */
    private double totalCpusUser;
    
    /**
     * The time the CPU has spent running the kernel and its processes
     */
    private double totalCpusSystem;
    
    /**
     * The time the CPU has spent idle.
     */
    private double totalCpusIdle;
    
    
    public double getTotalCpusIdle() {
        return totalCpusIdle;
    }
    public void setTotalCpusIdle(double totalCpusIdle) {
        this.totalCpusIdle = totalCpusIdle;
    }
    public double getTotalCpusSystem() {
        return totalCpusSystem;
    }
    public void setTotalCpusSystem(double totalCpusSystem) {
        this.totalCpusSystem = totalCpusSystem;
    }
    public double getTotalCpusUser() {
        return totalCpusUser;
    }
    public void setTotalCpusUser(double totalCpusUser) {
        this.totalCpusUser = totalCpusUser;
    }
    public double getTotalMemoryAvailable() {
        return totalMemoryAvailable;
    }
    public void setTotalMemoryAvailable(double totalMemoryAvailable) {
        this.totalMemoryAvailable = totalMemoryAvailable;
    }
    public double getTotalMemoryUsed() {
        return totalMemoryUsed;
    }
    public void setTotalMemoryUsed(double totalMemoryUsed) {
        this.totalMemoryUsed = totalMemoryUsed;
    }
}
