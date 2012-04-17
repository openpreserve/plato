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
package at.tuwien.minimee.migration.testprogram;

import javax.imageio.ImageIO;

public class ImageIOInformations {
    public static void main(String args[]) {
        String readerNames[] = ImageIO.getReaderFormatNames();
        printlist(readerNames, "Reader names:");
        String readerMimes[] = ImageIO.getReaderMIMETypes();
        printlist(readerMimes, "Reader MIME types:");
        String writerNames[] = ImageIO.getWriterFormatNames();
        printlist(writerNames, "Writer names:");
        String writerMimes[] = ImageIO.getWriterMIMETypes();
        printlist(writerMimes, "Writer MIME types:");
    }

    private static void printlist(String names[], String title) {
        System.out.println(title);
        for (int i = 0, n = names.length; i < n; i++) {
            System.out.println("\t" + names[i]);
        }
    }
}
