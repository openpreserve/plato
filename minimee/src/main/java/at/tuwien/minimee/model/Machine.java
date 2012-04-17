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
package at.tuwien.minimee.model;

/**
 * very basic description of a machine environment on which an engine is deployed.
 * @author cbu
 */
public class Machine {
    public static final String MACHINE_NAME = "machine:name";
    public static final String MACHINE_CPUS = "machine:cpus";
    public static final String MACHINE_CPUTYPE = "machine:cputype";
    public static final String MACHINE_CPUCLOCK = "machine:cpuclock";
    public static final String MACHINE_MEMORY = "machine:memory";
    public static final String MACHINE_OS = "machine:os";
    
    private String id;
    private String cpus;
    private String cpuType; 
    private String cpuClock; 
    private String memory; 
    private String operatingSystem;
    public String getCpuClock() {
        return cpuClock;
    }
    public void setCpuClock(String cpuClock) {
        this.cpuClock = cpuClock;
    }
    public String getCpus() {
        return cpus;
    }
    public void setCpus(String cpus) {
        this.cpus = cpus;
    }
    public String getCpuType() {
        return cpuType;
    }
    public void setCpuType(String cpuType) {
        this.cpuType = cpuType;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMemory() {
        return memory;
    }
    public void setMemory(String memory) {
        this.memory = memory;
    }
    public String getOperatingSystem() {
        return operatingSystem;
    }
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
}
