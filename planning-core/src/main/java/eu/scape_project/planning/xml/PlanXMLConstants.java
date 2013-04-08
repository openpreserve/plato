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

import java.nio.charset.Charset;

/**
 * Provides constants for encoding, namespaces, schemas, and schema locations for XML representations of preservation plans.
 *  
 * @author Michael Kraxner
 *
 */
public class PlanXMLConstants {

    public static final String ENCODING = "UTF-8";
    public static final Charset ENCODING_CHARSET = Charset.forName(ENCODING);
    
    public static final int BASE64_LINE_LENGTH = 76;
    public static final byte[] BASE64_LINE_BREAK = {'\n'};

    
    private static final String SCHEMA_LOCATION = "data/schemas/";
    
    public static final String PLATO_NS = "http://ifs.tuwien.ac.at/dp/plato";
    public static final String PLATO_SCHEMA = "plato-V4.xsd";
    public static final String PLATO_SCHEMA_LOCATION = SCHEMA_LOCATION + PLATO_SCHEMA;
    public static final String PLATO_SCHEMA_URI = PLATO_NS + "/" + PLATO_SCHEMA;
    public static final String PLATO_SCHEMA_VERSION = "4.0.1";
    
    
    public static final String PAP_SCHEMA = "preservationActionPlan-V1.xsd";
    public static final String PAP_SCHEMA_LOCATION = SCHEMA_LOCATION + PAP_SCHEMA;     
    public static final String PAP_SCHEMA_URI = PLATO_NS + "/" + PAP_SCHEMA;
    
    public static final String TAVERNA_SCHEMA = "t2flow.xsd";
    public static final String TAVERNA_SCHEMA_LOCATION = SCHEMA_LOCATION + TAVERNA_SCHEMA;     
    public static final String TAVERNA_SCHEMA_URI = PLATO_NS + "/" + TAVERNA_SCHEMA;
    
    public static final String[] PLAN_SCHEMAS = {ProjectImporter.PLATO_SCHEMA_URI};
    

    
}
