/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.xml;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.utils.FileUtils;

public class PlanMigrator {

    private static final Logger log = LoggerFactory.getLogger(PlanMigrator.class);
    
    /**
     * Used by digester
     */
    private String fileVersion;
    
    /**
     * Detect the version of the given XML representation of plans. If the
     * version of the XML representation is not up to date, necessary
     * transformations are applied.
     * 
     * @param importData
     * @return null if the transformation fails, otherwise an up to date XML
     *         representation
     * @throws IOException
     *             if parsing the XML representation fails
     * @throws SAXException
     *             if parsing the XML representation fails
     */
    public String getCurrentVersionData(final InputStream in, final String tempPath, final List<String> appliedTransformations) throws PlatoException {
        String originalFile = tempPath + "_original.xml";
        try {
            FileUtils.writeToFile(in, new FileOutputStream(originalFile));

            /** check for the version of the file **/

            // The version of the read xml file is unknown, so it is not possible to
            // validate it
            // moreover, in old plans the version attribute was on different
            // nodes(project, projects),
            // with a different name (fileVersion)
            // to be backwards compatible we create rules for all these attributes
            fileVersion = "xxx";
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(false);
            Digester d = new Digester(factory.newSAXParser());
            
            d.setValidating(false);
            // StrictErrorHandler errorHandler = new StrictErrorHandler();
            // d.setErrorHandler(errorHandler);
            d.push(this);
            // to read the version we have to support all versions:
            d.addSetProperties("*/projects", "version", "fileVersion");
            // manually migrated projects may have the file version in the node
            // projects/project
            d.addSetProperties("*/projects/project", "version", "fileVersion");
            // pre V1.3 version info was stored in the project node
            d.addSetProperties("*/project", "version", "fileVersion");
            // since V1.9 the root node is plans:
            d.addSetProperties("plans", "version", "fileVersion");

            InputStream inV = new FileInputStream(originalFile);
            d.parse(inV);
            inV.close();
            /** this could be more sophisticated, but for now this is enough **/
            String version = "1.0";
            if (fileVersion != null) {
                version = fileVersion;
            }

            String fileTo = originalFile;
            String fileFrom = originalFile;

            boolean success = true;
            if ("xxx".equals(version)) {
                fileFrom = fileTo;
                fileTo = fileFrom + "_V1.3.xml";
                /** this is an old export file, transform it to the 1.3 schema **/
                success = transformXmlData(fileFrom, fileTo, "data/xslt/Vxxx-to-V1.3.xsl");
                appliedTransformations.add("Vxxx-to-V1.3.xsl");
                version = "1.3";
            }
            if (success && "1.3".equals(version)) {
                fileFrom = fileTo;
                fileTo = fileFrom + "_V1.9.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V1.3-to-V1.9.xsl");
                appliedTransformations.add("V1.3-to-V1.9.xsl");
                version = "1.9";
            }
            // with release of Plato 2.0 and its schema ProjectExporter creates
            // documents with version 2.0
            if (success && "1.9".equals(version)) {
                version = "2.0";
            }
            if (success && "2.0".equals(version)) {
                // transform the document to version 2.1
                fileFrom = fileTo;
                fileTo = fileFrom + "_V2.1.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V2.0-to-V2.1.xsl");
                appliedTransformations.add("V2.0-to-V2.1.xsl");
                version = "2.1";
            }
            if (success && "2.1".equals(version)) {
                // transform the document to version 2.1.2
                fileFrom = fileTo;
                fileTo = fileFrom + "_V2.1.2.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V2.1-to-V2.1.2.xsl");
                appliedTransformations.add("V2.1-to-V2.1.2.xsl");
                version = "2.1.2";
            }
            if (success && "2.1.1".equals(version)) {
                // transform the document to version 2.1.2
                fileFrom = fileTo;
                fileTo = fileFrom + "_V2.1.2.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V2.1.1-to-V2.1.2.xsl");
                appliedTransformations.add("V2.1.1-to-V2.1.2.xsl");
                version = "2.1.2";
            }

            if (success && "2.1.2".equals(version)) {
                // transform the document to version 3.0.0
                fileFrom = fileTo;
                fileTo = fileFrom + "_V3.0.0.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V2.1.2-to-V3.0.0.xsl");
                appliedTransformations.add("V2.1.2-to-V3.0.0.xsl");
                version = "3.0.0";
            }
            if (success && "3.0.0".equals(version)) {
                // transform the document to version 3.0.1
                fileFrom = fileTo;
                fileTo = fileFrom + "_V3.0.1.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V3.0.0-to-V3.0.1.xsl");
                appliedTransformations.add("V3.0.0-to-V3.0.1.xsl");
                version = "3.0.1";
            }
            if (success && "3.0.1".equals(version)) {
                // transform the document to version 3.9.0
                fileFrom = fileTo;
                fileTo = fileFrom + "_V3.9.0.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V3.0.1-to-V3.9.0.xsl");
                appliedTransformations.add("V3.0.1-to-V3.9.0.xsl");
                version = "3.9.0";
            }
            if (success && "3.9.0".equals(version)) {
                // transform the document to version 3.9.9
                fileFrom = fileTo;
                fileTo = fileFrom + "_V3.9.9.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V3.9.0-to-V3.9.9.xsl");
                appliedTransformations.add("V3.9.0-to-V3.9.9.xsl");
                version = "3.9.9";
            }
            if (success && "3.9.9".equals(version)) {
                // transform the document to version 4.0.0
                fileFrom = fileTo;
                fileTo = fileFrom + "_V4.0.1.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V3.9.9-to-V4.0.1.xsl");
                appliedTransformations.add("V3.9.9-to-V4.0.1.xsl");
                version = "4.0.1";
            }
            if (success && "4.0.1".equals(version)) {
                // transform the document to version 4.0.0
                fileFrom = fileTo;
                fileTo = fileFrom + "_V4.0.2.xml";
                success = transformXmlData(fileFrom, fileTo, "data/xslt/V4.0.1-to-V4.0.2.xsl");
                appliedTransformations.add("V4.0.1-to-V4.0.2.xsl");
                version = "4.0.2";
            }

            if (success) {
                return fileTo;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new PlatoException("Failed to update plan to current version.", e);
        }
    }

    public boolean transformXmlData(final String fromFile, final String toFile, final String xslFile)
        throws IOException {
        try {
            InputStream xsl = Thread.currentThread().getContextClassLoader().getResourceAsStream(xslFile);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xsl));

            OutputStream transformedOut = new FileOutputStream(toFile);
            Result outputTarget = new StreamResult(transformedOut);

            Source xmlSource = new StreamSource(new FileInputStream(fromFile));
            transformer.transform(xmlSource, outputTarget);
            transformedOut.close();
            return true;

        } catch (TransformerConfigurationException e) {
            log.debug(e.getMessage(), e);
        } catch (TransformerFactoryConfigurationError e) {
            log.debug(e.getMessage(), e);
        } catch (TransformerException e) {
            log.debug(e.getMessage(), e);
        }
        return false;

    }    

    public void setFileVersion(final String fileVersion) {
        this.fileVersion = fileVersion;
    }

}
