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
package eu.scape_project.pw.planning.utils;

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
