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

package eu.scape_project.planning.xml.plan;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import sun.misc.BASE64Decoder;
import eu.scape_project.planning.model.ByteStream;
/**
 * Helper class for {@link eu.scape_project.planning.xml.ProjectImporter} to decode Base64 encoded strings.
 * Can set the decoded data to other objects which have a function setData(byte[] data) 
 * 
 * @author Michael Kraxner
 *
 */
public class BinaryDataWrapper implements Serializable{
    
    private static final long serialVersionUID = 2080538998419720006L;
    
    BASE64Decoder decoder = new BASE64Decoder();
    byte[] value = null;
    
    private String methodName= "setData";
    
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Decodes the Base64 encoded string <code>value</code> and keep this data for
     * the next call of {@link #setData(Object)}
     * 
     * @param value
     */
    public void setFromBase64Encoded(String value) {
        try {
            this.value = decoder.decodeBuffer(value.replaceAll("\\s", ""));
        } catch (IOException e) {
            this.value = null;
        }
    }
    
    /**
     * Invokes the function "setData" on <code>object</code> via reflection 
     * - with previously decoded data as parameter.  
     *      
     * @param object
     */
    public void setData(Object object) {
        try {
            ByteStream data = new ByteStream();
            data.setData(value);
            Method setData = object.getClass().getMethod(methodName, ByteStream.class);
            setData.invoke(object, new Object[]{data});
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    /**
     * Invokes the previously set function <methodName> on <code>object</code> via reflection 
     * - with previously decoded data as parameter.  
     *      
     * @param object
     */
    public void setString(Object object) {
        try {
            ByteStream data = new ByteStream();
            data.setData(value);
            Method setData = object.getClass().getMethod(methodName, String.class);
            String dataString = new String (value);                
            setData.invoke(object, new Object[]{dataString});
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }    
    
}
