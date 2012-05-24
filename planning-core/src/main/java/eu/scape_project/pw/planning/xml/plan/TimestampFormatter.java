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
package eu.scape_project.pw.planning.xml.plan;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class to format the new timestamp format in plato's XML Files
 * Timestamps of changelog are now stored in the human readable format YYYY-MM-ddTHH:mm:ss
 *
 *    E.g.: 23 May 2008, 15:50:28 seconds = 2008-05-23T15:50:28
 *
 * @author Michael Kraxner
 *
 */
public class TimestampFormatter implements Serializable {
    private static final long serialVersionUID = -511907943817956306L;
    
    private SimpleDateFormat formatter;
    
    

    public TimestampFormatter() {
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }
        
    public long parseTimestamp(String s) throws java.text.ParseException{
        return formatter.parse(s).getTime();
    }
    
    public String formatTimestamp(long ts) {
        return formatter.format(ts);
    }
    
    public static void main(String[] args) {
        String date = "2008-02-23T13:01:45";
        long jetzt = System.currentTimeMillis();
        TimestampFormatter formatter = new TimestampFormatter();
        System.out.println("here and now : " + formatter.formatTimestamp(jetzt));
        try {
            long then = formatter.parseTimestamp(date);
            System.out.println("other time, unknown place: " + new Date(then));
        } catch (ParseException e) {
            System.out.println("Could not parse: " + date + " reason: " + e.getMessage());
        }
    }
}
