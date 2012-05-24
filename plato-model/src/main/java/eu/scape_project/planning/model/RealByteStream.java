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
package eu.scape_project.planning.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class RealByteStream implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5514647586201558155L;

    @Id
    @GeneratedValue
    private int id;    

    /**
     * shall be nullable, as a sample record could also be something without a
     * of a data stream (e.g. a record set somewhere)
     */
    @Lob
    @Column(length=125829120)
    private byte[] data = new byte[]{};
    
    public RealByteStream() {
        
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public RealByteStream clone() {
        RealByteStream clone = new RealByteStream();
        if (data != null) {
        	clone.setData(data.clone());
        }
        return clone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
