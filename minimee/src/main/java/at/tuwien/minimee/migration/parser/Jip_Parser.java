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
package  at.tuwien.minimee.migration.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jip_Parser {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    final String LAST_LINE_MATCH_BEFORE_PARSING = "=====    =====";

	public static void main(String args[]) {
		Jip_Parser parser = new Jip_Parser();
                Double total_time = parser.getTotalTime("/home/riccardo/profilers/jip/jip_0_2Mb_Image_1");
		System.out.println("Total:" + total_time);
	}

	/**
	 * Reads the line matching the LAST_LINE_MATCH_BEFORE_PARSING bofore
	 * beginning to read and get the information
	 * 
	 * @param fileToRead
	 * @return
	 */
	public double getTotalTime(String fileToRead) {
            Double total_time = 0.0;
            try {
		/*
		 * Sets up a file reader to read the file passed on the command line
		 * one character at a time
		 */
		FileReader input = new FileReader(fileToRead);

                try {
                	/*
                	 * Filter FileReader through a Buffered read to read a line at a
                	 * time
                	 */
                	BufferedReader bufRead = new BufferedReader(input);
                
                	String line; // String that holds current file line
                
                	// Read first line
                	line = bufRead.readLine();
                
                	// Read through file one line at time. Print line # and line
                	while (line != null) {
                		if (line.contains(LAST_LINE_MATCH_BEFORE_PARSING))
                			break;
                		line = bufRead.readLine();
                	}
                
                	// read next line containing the first info
                	line = bufRead.readLine();
                
                	total_time = interpretLine(line);
                } finally {
                    input.close();
                }
            } catch (IOException e) {
		log.error("Failed to read total time from " + fileToRead, e);
	}

	return total_time;
	}

	private double interpretLine(String line) {
		char[] chars = new char[line.length()];
		line.getChars(0, line.length() - 1, chars, 0);

		String tot_time = "";

		int countWord = 0;
		int wordEntered = 0;

		for (char c : chars) {
			if (c == ' ') {
				wordEntered = 0;
				continue;
			}
			if (wordEntered == 0)
				countWord++;
			wordEntered = 1;
			if (countWord == 2)
				tot_time += c;
		}
		tot_time=tot_time.replace(',','.');
		return Double.parseDouble(tot_time);
	}
}
