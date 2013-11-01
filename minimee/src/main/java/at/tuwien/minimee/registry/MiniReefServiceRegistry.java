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
package at.tuwien.minimee.registry;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import at.tuwien.minireef.MiniREEFResolver;
import at.tuwien.minireef.ResultSet;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.services.IServiceInfo;
import eu.scape_project.planning.services.action.ActionInfo;
import eu.scape_project.planning.services.action.IPreservationActionRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * demonstration registry to query miniREEF (using the P2 knowledge base) for
 * available tools that have a (currently just any!) link with the input file
 * format
 * 
 * @author cb
 * 
 */
public class MiniReefServiceRegistry implements IPreservationActionRegistry {
    private static final Logger log = LoggerFactory.getLogger(MiniReefServiceRegistry.class);

    public void connect(String URL) throws ServiceException, MalformedURLException {

    }

    public String getToolIdentifier(String url) {
        return "";
    }

    public String getToolParameters(String url) {
        return "";
    }

    public List<IServiceInfo> getAvailableActions(FormatInfo sourceFormat) throws PlatoException {

        String statement = "SELECT distinct ?swname ?swversion ?formatname ?formatversion ?released ?vendorname "
            + "WHERE { ?sw ?link1 ?format . "
            +
            // "        ?link1 rdf:type <http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Other> . "
            // +
            "        ?format pronom:FileFormatIdentifier ?ident . " + "        ?ident  pronom:Identifier \"$PUID$\" ."
            + "        ?ident  pronom:IdentifierType \"PUID\" ." + "        ?sw ?link2 ?format2 . "
            + "        ?sw pronom:SoftwareName  ?swname . " + "        ?sw pronom:Version  ?swversion . "
            + "        ?format2 pronom:FormatName ?formatname . "
            + "        ?format2 pronom:FormatVersion ?formatversion . " + "        ?sw pronom:Vendor ?vendor . "
            + "        ?vendor pronom:VendorName ?vendorname . "
            + "       ?link2 rdf:type <http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Save> } ";

        // String s1 =
        // "SELECT distinct ?swname " +
        // "WHERE { ?sw ?link ?format . " +
        // //"        ?link rdf:type <http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Other> . "
        // +
        // "        ?format pronom:FileFormatIdentifier ?ident . " +
        // "        ?ident  pronom:Identifier \"$PUID$\" ." +
        // "        ?ident  pronom:IdentifierType \"PUID\" ." +
        // "        ?sw pronom:SoftwareName  ?swname } ";

        // prepare commonly used parameters
        Map<String, String> params = new HashMap<String, String>();

        String puid = "Target format PUID undefined for this action";
        if (sourceFormat != null && !"".equals(sourceFormat.getPuid())) {
            puid = sourceFormat.getPuid();
        }
        params.put("PUID", puid);

        ResultSet resultSet = MiniREEFResolver.getInstance().resolve(statement, params);
        ArrayList<IServiceInfo> result = new ArrayList<IServiceInfo>();

        if (resultSet == null) {
            // this should not happen if MiniREEF is properly configured
            log.error("querying miniREEF/P2 knowledge base failed for statement: " + statement);
            return result;
        }

        for (int i = 0; i < resultSet.size(); i++) {
            ActionInfo def = new ActionInfo();
            def.setShortname("Convert using " + resultSet.getRow(i).get(0) + " " + resultSet.getRow(i).get(1));
            def.setTargetFormat(resultSet.getRow(i).get(2) + " " + resultSet.getRow(i).get(3));
            def.setInfo("by " + resultSet.getRow(i).get(4));
            def.setActionIdentifier("P2");
            def.setExecutable(false);
            // if (service.getTargetFormat() != null) {
            // def.setTargetFormat(service.getTargetFormat().getDefaultExtension());
            // def.setTargetFormatInfo(service.getTargetFormat());
            // }
            // def.setInfo(service.getDescription());
            // def.setUrl(service.getUrl());
            // def.setDescriptor(service.getDescriptor());

            result.add(def);
        }
        return result;
    }

    public String getLastInfo() {
        return "";
    }

}
