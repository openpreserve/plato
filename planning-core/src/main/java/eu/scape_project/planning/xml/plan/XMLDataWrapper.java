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
import java.text.ParseException;

import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.model.ChangeLog;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Element;

/**
 * Helper class for {@link eu.scape_project.planning.xml.PlanParser} to read an
 * XML element. Can set the decoded data to other objects which have a function
 * setData(byte[] data)
 */
public class XMLDataWrapper implements Serializable {

    private static final long serialVersionUID = 2080538998419720006L;

    /**
     * Encoding used for writing data.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The default output format used for this class.
     */
    public static final OutputFormat DEFAULT_OUTPUT_FORMAT;

    private OutputFormat outputFormat = DEFAULT_OUTPUT_FORMAT;

    private String methodName = "setData";

    private String changeLogMethodName = null;

    private byte[] data = null;

    private ChangeLog changeLog = null;

    static {
        DEFAULT_OUTPUT_FORMAT = OutputFormat.createPrettyPrint();
        DEFAULT_OUTPUT_FORMAT.setEncoding(ENCODING);
    }

    /**
     * Reads an XML element and <code>value</code> and keeps this data for the
     * next call of {@link #setData(Object)}.
     * 
     * Additionally checks the <code>value</code> if a <code>changelog</code>
     * element is present and stores it for the next call of
     * {@link #setChangeLog(Object)}.
     * 
     * @param value
     *            the value to create
     * @throws IOException
     *             if the data could not be written
     * @throws ParseException
     *             if the data could not be parsed
     */
    public void setEncoded(Element value) throws IOException, ParseException {

        DOMReader reader = new DOMReader();
        org.w3c.dom.Document w3cDocument = value.getOwnerDocument();
        w3cDocument.appendChild(value);
        Document doc = reader.read(w3cDocument);

        org.dom4j.Element changeLogElement = (org.dom4j.Element) doc.selectSingleNode("//*[local-name()='changelog']");
        createChangeLog(changeLogElement);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLWriter writer = new XMLWriter(out, outputFormat);
        writer.write(doc);

        this.data = out.toByteArray();

    }

    /**
     * Invokes the previously set method <methodName> on <code>object</code> via
     * reflection - with previously decoded data as parameter.
     * 
     * @param object
     *            the object where the data should be set
     * @throws NoSuchMethodException
     *             if the method could not be invoked
     * @throws IllegalAccessException
     *             if the method could not be invoked
     * @throws InvocationTargetException
     *             if the method could not be invoked
     */
    public void setData(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ByteStream bs = new ByteStream();
        bs.setData(data);
        Method setDataMethod = object.getClass().getMethod(methodName, ByteStream.class);
        setDataMethod.invoke(object, new Object[] {bs});
    }

    /**
     * Invokes the previously set method <methodName> on <code>object</code> via
     * reflection - with previously decoded data as parameter.
     * 
     * @param object
     *            the object where the data should be set
     * @throws NoSuchMethodException
     *             if the method could not be invoked
     * @throws IllegalAccessException
     *             if the method could not be invoked
     * @throws InvocationTargetException
     *             if the method could not be invoked
     */
    public void setString(Object object) throws NoSuchMethodException, IllegalAccessException,
        InvocationTargetException {
        ByteStream bs = new ByteStream();
        bs.setData(data);
        Method setDataMethod = object.getClass().getMethod(methodName, String.class);
        String dataString = new String(data);
        setDataMethod.invoke(object, new Object[] {dataString});
    }

    /**
     * Invokes the previously set method <changeLogMethodName> on
     * <code>object</code> via reflection - with previously decoded change log
     * data as parameter.
     * 
     * @param object
     *            the object where the change log should be set
     * @throws NoSuchMethodException
     *             if the method could not be invoked
     * @throws IllegalAccessException
     *             if the method could not be invoked
     * @throws InvocationTargetException
     *             if the method could not be invoked
     */
    public void setChangeLog(Object object) throws NoSuchMethodException, IllegalAccessException,
        InvocationTargetException {
        Method setData = object.getClass().getMethod(changeLogMethodName, ChangeLog.class);
        setData.invoke(object, new Object[] {changeLog});
    }

    /**
     * Creates a change log from the provided changeLogElement.
     * 
     * @param changeLogElement
     *            the element containing the change log
     * @throws ParseException
     *             if the date could not be parsed
     */
    private void createChangeLog(org.dom4j.Element changeLogElement) throws ParseException {
        changeLog = new ChangeLog();
        if (changeLogElement == null) {
            return;
        }
        TimestampFormatter formatter = new TimestampFormatter();
        changeLog.setChangedBy(changeLogElement.attributeValue("changedBy"));
        changeLog.setCreatedBy(changeLogElement.attributeValue("createdBy"));
        String changed = changeLogElement.attributeValue("changed");
        String created = changeLogElement.attributeValue("created");
        changeLog.setChanged(formatter.parseTimestamp(changed));
        changeLog.setCreated(formatter.parseTimestamp(created));
    }

    // ---------- getter/setter ----------
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getChangeLogMethodName() {
        return changeLogMethodName;
    }

    public void setChangeLogMethodName(String changeLogMethodName) {
        this.changeLogMethodName = changeLogMethodName;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

}
