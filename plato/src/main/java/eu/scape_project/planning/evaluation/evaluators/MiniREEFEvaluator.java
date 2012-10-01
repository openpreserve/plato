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
package eu.scape_project.planning.evaluation.evaluators;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minireef.ResultSet;
import eu.scape_project.planning.evaluation.EvaluatorException;
import eu.scape_project.planning.evaluation.IActionEvaluator;
import eu.scape_project.planning.evaluation.IStatusListener;
import eu.scape_project.planning.evaluation.MeasureConstants;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.values.Value;

public class MiniREEFEvaluator implements IActionEvaluator {

    private static Logger log = LoggerFactory.getLogger(MiniREEFEvaluator.class);

    private static final String P2_RESOURCE_FORMAT_LICENSE_RIGHTS_OPEN = "http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights/open";
    private static final String P2_RESOURCE_FORMAT_LICENSE_RIGHTS_IPR_PROTECTED = "http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights/ipr_protected";
    private static final String P2_RESOURCE_FORMAT_LICENSE_RIGHTS_PROPRIETARY = "http://p2-registry.ecs.soton.ac.uk/pronom/risk_categories/rights/proprietary";

    private Map<String, String> statements = new HashMap<String, String>();

    public MiniREEFEvaluator() {
        addStatements();
    }

    public HashMap<String, Value> evaluate(Alternative alternative, List<String> measureUris, IStatusListener listener)
        throws EvaluatorException {

        HashMap<String, Value> results = new HashMap<String, Value>();

        if (alternative.getAction() == null) {
            return results;
        }

        // prepare commonly used parameters
        Map<String, String> params = new HashMap<String, String>();

        // PUID: use the PUID from action.targetformat
        String puid = "Target format PUID undefined for this action";
        FormatInfo targetInfo = alternative.getAction().getTargetFormatInfo();
        if (targetInfo != null && !"".equals(targetInfo.getPuid())) {
            puid = targetInfo.getPuid();
        }
        params.put("PUID", puid);

        for (String measureId : measureUris) {

            Scale scale = null; // FIXME
            Value value = null; // FIXME scale.createValue();

            if (MeasureConstants.ACTION_BATCH_SUPPORT.equals(measureId)) {
                if (!alternative.getAction().isEmulated() && alternative.getAction().isExecutable()) {
                    // this alternative is wrapped as service and therefore
                    // provides batch support
                    value.parse("Yes");
                    value.setComment("this alternative is wrapped as service and therefore provides batch support");
                }
            }

            String statement = statements.get(measureId);
            if (statement == null) {
                // this leaf cannot be evaluated by MiniREEF - skip it
                continue;
            }

            String result = null;

            // add additional params if necessary
            // ...
            ResultSet resultSet = MiniREEFResolver.getInstance().resolve(statement, params);
            listener.updateStatus("MiniREEF is attempting to evaluate " + measureId);

            if (resultSet == null) {
                // this should not happen, if MiniREEF is properly configured
                listener.updateStatus("querying MiniREEF/P2 knowledge base failed for statement: " + statement);
                // skip this leaf
                continue;
            }

            // evaluation was successful!
            if (measureId.startsWith(MeasureConstants.FORMAT_NUMBEROFTOOLS)) {
                // _measure_ is the number of tools found
                result = "" + resultSet.size();
                value.parse(result);
                // add names of tools as comment
                value.setComment(toCommaSeparated(resultSet.getColResults("swname"))
                    + "; - according to miniREEF/P2 knowledge base");
                listener.updateStatus("MiniREEF evaluated " + measureId);
            } else if (MeasureConstants.FORMAT_SUSTAINABILITY_RIGHTS.equals(measureId)) {
                if (resultSet.size() > 0) {
                    // e.g. open = false, comment: "Format is encumbered by IPR"
                    String comment = "";
                    String valueStr = "";
                    for (int i = 0; i < resultSet.size(); i++) {
                        List<String> vals = resultSet.getRow(i);
                        comment = comment + vals.get(0) + "\n";
                        String type = vals.get(1);
                        if (P2_RESOURCE_FORMAT_LICENSE_RIGHTS_IPR_PROTECTED.equals(type)) {
                            valueStr = "ipr_protected";
                            comment = comment + valueStr;
                        } else if (P2_RESOURCE_FORMAT_LICENSE_RIGHTS_PROPRIETARY.equals(type)) {
                            valueStr = "proprietary";
                            comment = comment + valueStr;
                        } else if (P2_RESOURCE_FORMAT_LICENSE_RIGHTS_OPEN.equals(type)) {
                            valueStr = "open";
                            comment = comment + valueStr;
                        }
                    }
                    if (resultSet.size() > 1) {
                        comment = comment
                            + ": more than one right category applies to this format, check for reason of this conflict.\n";
                    }
                    value = scale.createValue();
                    value.parse(valueStr);
                    value.setComment(comment + ": according to MiniREEF/P2 knowledge base");
                    listener.updateStatus("MiniREEF evaluated " + measureId);
                }
                listener.updateStatus("P2 does not contain enough information to evaluate " + measureId
                    + " for this format.");
            } else if ((MeasureConstants.FORMAT_COMPLEXITY.equals(measureId))
                || (MeasureConstants.FORMAT_DISCLOSURE.equals(measureId))
                || (MeasureConstants.FORMAT_UBIQUITY.equals(measureId))
                || (MeasureConstants.FORMAT_DOCUMENTATION_QUALITY.equals(measureId))
                || (MeasureConstants.FORMAT_STABILITY.equals(measureId))
                || (MeasureConstants.FORMAT_LICENSE.equals(measureId))) {
                if (resultSet.size() > 0) {
                    String text = resultSet.getRow(0).get(0);
                    if (text.trim().length() > 0) {
                        value = scale.createValue();
                        value.parse(text);
                        value.setComment("according to miniREEF/P2 knowledge base");
                    }
                    listener.updateStatus("MiniREEF evaluated " + measureId);
                } else {
                    listener.updateStatus("P2 does not contain enough information to evaluate " + measureId
                        + " for this format.");
                }

            }
            // put measure to result map
            results.put(measureId, value);
        }

        return results;
    }

    private String toCommaSeparated(List<String> list) {
        if (list == null) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            b.append(iter.next());
            if (iter.hasNext()) {
                b.append(", ");
            }
        }
        return b.toString();
    }

    /**
     * add a set of available ARQ - SPARQL statements at the moment only results
     * with one column can be handled
     * 
     * You can define parameters by wrapping an identifier with '$' - they will
     * be replaced later on with supplied values
     */
    private void addStatements() {
        // action://format/numberOfTools
        String statement = "SELECT distinct ?swname " + "WHERE { ?sw ?link ?format . "
            + "        ?link rdf:type <http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink> . "
            + "        ?format pronom:FileFormatIdentifier ?ident . "
            + "        ?ident  pronom:Identifier \"$PUID$\" ." + "        ?ident  pronom:IdentifierType \"PUID\" ."
            + "        ?sw pronom:SoftwareName  ?swname } ";

        statements.put(MeasureConstants.FORMAT_NUMBEROFTOOLS, statement);

        // action://format/numberOfTools/save :
        // "http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Save"
        statement = "SELECT distinct ?swname " + "WHERE { ?sw ?link ?format . "
            + "        ?link rdf:type <http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Save> . "
            + "        ?format pronom:FileFormatIdentifier ?ident . "
            + "        ?ident  pronom:Identifier \"$PUID$\" ." + "        ?ident  pronom:IdentifierType \"PUID\" ."
            + "        ?sw pronom:SoftwareName  ?swname } ";

        statements.put(MeasureConstants.FORMAT_NUMBEROFTOOLS_SAVE, statement);

        // action://format/numberOfTools/open :
        // "http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Save"
        statement = "SELECT distinct ?swname " + "WHERE { ?sw ?link ?format . "
            + "        ?link rdf:type <http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Open> . "
            + "        ?format pronom:FileFormatIdentifier ?ident . "
            + "        ?ident  pronom:Identifier \"$PUID$\" ." + "        ?ident  pronom:IdentifierType \"PUID\" ."
            + "        ?sw pronom:SoftwareName  ?swname } ";

        statements.put(MeasureConstants.FORMAT_NUMBEROFTOOLS_OPEN, statement);

        // action://format/numberOfTools/other :
        // "http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Other"
        statement = "SELECT distinct ?swname " + "WHERE { ?sw ?link ?format . "
            + "        ?link rdf:type <http://p2-registry.ecs.soton.ac.uk/pronom/SoftwareLink/Other> . "
            + "        ?format pronom:FileFormatIdentifier ?ident . "
            + "        ?ident  pronom:Identifier \"$PUID$\" ." + "        ?ident  pronom:IdentifierType \"PUID\" ."
            + "        ?sw pronom:SoftwareName  ?swname } ";

        statements.put(MeasureConstants.FORMAT_NUMBEROFTOOLS_OTHERS, statement);

        statement = "SELECT ?d WHERE { " + " ?format pronom:FormatDisclosure ?d . "
            + " ?format pronom:FileFormatIdentifier ?ident . " + " ?ident pronom:IdentifierType \"PUID\" . "
            + " ?ident pronom:Identifier \"$PUID$\" }";
        statements.put(MeasureConstants.FORMAT_DISCLOSURE, statement);

        // p2 is used to add information about ubiquity
        statement = "SELECT ?d  WHERE {  " + "?format p2-additional:ubiquity ?u . " + "?u rdfs:comment ?d . "
            + "?format pronom:IsSupertypeOf ?pronomformat . " + "?pronomformat pronom:FileFormatIdentifier ?ident ."
            + "?ident pronom:IdentifierType \"PUID\" ." + "?ident pronom:Identifier \"$PUID$\" }";
        statements.put(MeasureConstants.FORMAT_UBIQUITY, statement);

        // p2 is used to add information about ubiquity
        statement = "SELECT ?d  WHERE {  " + "?format p2-additional:complexity ?u . " + "?u rdfs:comment ?d . "
            + "?format pronom:IsSupertypeOf ?pronomformat . " + "?pronomformat pronom:FileFormatIdentifier ?ident ."
            + "?ident pronom:IdentifierType \"PUID\" ." + "?ident pronom:Identifier \"$PUID$\" }";
        statements.put(MeasureConstants.FORMAT_COMPLEXITY, statement);

        statement = "SELECT ?d  WHERE {  " + "?format p2-additional:documentation_quality ?q . "
            + "?q rdfs:comment ?d . " + "?format  pronom:FileFormatIdentifier ?ident ."
            + "?ident pronom:IdentifierType \"PUID\" ." + "?ident pronom:Identifier \"$PUID$\" " + " }";
        statements.put(MeasureConstants.FORMAT_DOCUMENTATION_QUALITY, statement);

        // pronom(!) is used to add information about stability
        statement = "SELECT ?d  WHERE {  " + "?format pronom:stability ?u . " + "?u rdfs:comment ?d . "
            + "?format pronom:IsSupertypeOf ?pronomformat . " + "?pronomformat pronom:FileFormatIdentifier ?ident ."
            + "?ident pronom:IdentifierType \"PUID\" ." + "?ident pronom:Identifier \"$PUID$\" }";
        statements.put(MeasureConstants.FORMAT_STABILITY, statement);

        /**
         * we use the same query for information on rights, and select the
         * comment and rdf:resource, this way we can provide more detailed
         * information, if a right model does not apply to the format
         */
        // pronom(!) is used to add information about rights!
        String selectRights = "SELECT DISTINCT ?d ?u WHERE {  " + "?format pronom:rights ?u . "
            + "?u rdfs:comment ?d . " + "?format pronom:IsSupertypeOf ?pronomformat . "
            + "?pronomformat pronom:FileFormatIdentifier ?ident ." + "?ident pronom:IdentifierType \"PUID\" ."
            + "?ident pronom:Identifier \"$PUID$\" }";
        statements.put(MeasureConstants.FORMAT_LICENSE, selectRights);

        // pronom(!) is used to add information about rights!
        statements.put(MeasureConstants.FORMAT_SUSTAINABILITY_RIGHTS, selectRights);
    }

}
