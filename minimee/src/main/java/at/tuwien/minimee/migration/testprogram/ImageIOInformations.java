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
