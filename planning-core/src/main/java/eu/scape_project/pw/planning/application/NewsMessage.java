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

package eu.scape_project.pw.planning.application;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Used to display news to the user on the welcome page, like server down-times due to maintenance 
 * 
 * @author Michael Kraxner
 *
 */
public class NewsMessage implements Serializable  {

    private static final long serialVersionUID = -8014290341694796740L;

    /**
     * News message.
     */
    private String news = "";

    /**
     * Date/Time the news masseage was posted.
     */
    private String timestamp = "";

    /**
     * Importance level of the message. Can be chosen arbitrarily.
     */
    private String importance = "";

    /**
     * Author of the news message.
     */
    private String author = "";

    public String getAuthor() {
        return author;
    }

    public NewsMessage(){

    }

    /**
     *
     * @param news news message
     * @param importance importance level of the message. Can be chosen arbitrarily.
     * @param author author of the news message.
     */
    public NewsMessage(String news, String importance, String author){
        this.news = news;
        this.author = author;
        this.importance = importance;
        SimpleDateFormat format = new SimpleDateFormat("dd.MMMM.yyyy kk:mm:ss");
        this.timestamp = format.format(new Date(System.currentTimeMillis()));
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImportance() {
        return importance;
    }

    public String getNews() {
        return news;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public void setNews(String news) {
        this.news = news;
    }
}
