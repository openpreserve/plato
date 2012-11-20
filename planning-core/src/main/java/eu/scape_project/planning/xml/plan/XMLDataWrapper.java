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
package eu.scape_project.planning.xml.plan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.xml.PreservationActionPlanGenerator;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Element;

/**
 * Helper class for {@link eu.scape_project.planning.xml.ProjectImporter} to
 * decode Base64 encoded strings. Can set the decoded data to other objects
 * which have a function setData(byte[] data)
 * 
 * @author Michael Kraxner
 * 
 */
public class XMLDataWrapper implements Serializable {

    private static final long serialVersionUID = 2080538998419720006L;

    byte[] value = null;

    private String methodName = "setData";

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Decodes the Base64 encoded string <code>value</code> and keep this data
     * for the next call of {@link #setData(Object)}
     * 
     * @param value
     */
    public void setEncoded(Element value) {
        try {

            DOMReader reader = new DOMReader();
            Document doc = reader.read(value.getOwnerDocument());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLWriter writer = new XMLWriter(out, PreservationActionPlanGenerator.DEFAULT_OUTPUT_FORMAT);
            writer.write(doc);

            this.value = out.toByteArray();
        } catch (IOException e) {
            this.value = null;
        }
    }

    /**
     * Invokes the function "setData" on <code>object</code> via reflection -
     * with previously decoded data as parameter.
     * 
     * @param object
     */
    public void setData(Object object) {
        try {
            ByteStream data = new ByteStream();
            data.setData(value);
            Method setData = object.getClass().getMethod(methodName, ByteStream.class);
            setData.invoke(object, new Object[] {data});
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
     * Invokes the previously set function <methodName> on <code>object</code>
     * via reflection - with previously decoded data as parameter.
     * 
     * @param object
     */
    public void setString(Object object) {
        try {
            ByteStream data = new ByteStream();
            data.setData(value);
            Method setData = object.getClass().getMethod(methodName, String.class);
            String dataString = new String(value);
            setData.invoke(object, new Object[] {dataString});
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
