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
