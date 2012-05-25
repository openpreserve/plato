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
