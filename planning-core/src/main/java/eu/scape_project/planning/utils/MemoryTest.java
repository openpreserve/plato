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
package eu.scape_project.planning.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MemoryTest  implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2263585209064893834L;
    List<byte[]> bytestreams = new ArrayList<byte[]>();

    public MemoryTest() {
        
    }
    
    public void munchMem(int mb) {
        int toAllocate = mb;
        while (toAllocate > 100) {
            byte[] bytearray = new byte[100 * 1024 * 1024];
            bytestreams.add(bytearray);
            toAllocate -= 100;
        }
        byte[] bytearray = new byte[toAllocate * 1024 * 1024];
        bytestreams.add(bytearray);
    }
    public void releaseMem() {
        bytestreams.clear();
        System.gc();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        MemoryTest memtest = new MemoryTest();
        
        int input = '0';
        showCommands();
        do {
            try {
                input = System.in.read();
                if (input == 'a') {
                    System.out.println("adding 500MB");
                    memtest.munchMem(500);
                } else if (input == 'c') {
                    System.out.println("start clean up ...");
                    memtest.releaseMem();
                }
            } catch (IOException e) {
                showCommands();
            }
        }
        while (input != 'q');

    }
    private static void showCommands() {
        System.out.println("COMMANDS: a ... add 500MB, c ... clean up, i ... show allocated mem, q ... quit");
    }

}
