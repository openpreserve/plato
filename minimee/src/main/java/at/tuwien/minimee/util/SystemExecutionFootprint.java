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
