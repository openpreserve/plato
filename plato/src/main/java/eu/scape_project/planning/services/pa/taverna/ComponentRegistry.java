package eu.scape_project.planning.services.pa.taverna;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.RegexRules;
import org.apache.commons.digester3.SimpleRegexMatcher;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.interfaces.actions.IPreservationActionRegistry;
import eu.scape_project.planning.utils.JGet;

public class ComponentRegistry implements IPreservationActionRegistry {
	private static Logger log = Logger.getLogger(ComponentRegistry.class);

	private static final String ME_SPARQL_ENDPOINT = "http://rdf.myexperiment.org/sparql";
	private static final String ENCODING_UTF8 = "UTF-8";
	
	private List<PreservationActionDefinition>  preservationActions;

	@Override
	public void connect(String URL) throws ServiceException,
			MalformedURLException {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * http://rdf.myexperiment.org/sparql

PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX dcterms: <http://purl.org/dc/terms/>
PREFIX meannot: <http://rdf.myexperiment.org/ontologies/annotations/>
PREFIX mecontrib: <http://rdf.myexperiment.org/ontologies/contributions/>

SELECT ?w ?wt ?wdesc
WHERE {
  ?w a mecontrib:Workflow ;
     dcterms:title ?wt ;
     dcterms:description ?wdesc ;
     meannot:has-tagging ?tscape ;
     meannot:has-tagging ?tmigration .
  ?tscape meannot:uses-tag <http://www.myexperiment.org/tags/2681> .
  ?tmigration meannot:uses-tag <http://www.myexperiment.org/tags/3108> .
  
  
}
ORDER BY ?w ?wt
	 */
	@Override
	public List<PreservationActionDefinition> getAvailableActions(
			FormatInfo sourceFormat) throws PlatoException {
		preservationActions = new ArrayList<PreservationActionDefinition>();
		
		StringBuilder query = new StringBuilder();
		query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append("\n")
			 .append("PREFIX dcterms: <http://purl.org/dc/terms/>").append("\n")
			 .append("PREFIX meannot: <http://rdf.myexperiment.org/ontologies/annotations/>").append("\n")
			 .append("PREFIX mecontrib: <http://rdf.myexperiment.org/ontologies/contributions/>").append("\n")
			 .append("SELECT ?w ?wt ?wdesc").append("\n")
			 .append("WHERE {").append("\n")
			 .append("  ?w a mecontrib:Workflow ;").append("\n")
			 .append("     dcterms:title ?wt ;").append("\n")
			 .append("     dcterms:description ?wdesc ;").append("\n")
			 .append("     meannot:has-tagging ?tscape ;").append("\n")
			 .append("     meannot:has-tagging ?tmigration .").append("\n")
			 .append("  ?tscape meannot:uses-tag <http://www.myexperiment.org/tags/2681> .").append("\n")
			 .append("  ?tmigration meannot:uses-tag <http://www.myexperiment.org/tags/3108> .").append("\n")
//			 .append("  ?tmigration meannot:uses-tag <http://www.myexperiment.org/tags/3214> .").append("\n")
			 .append("}").append("\n")
			 .append("ORDER BY ?w ?wt").append("\n");
		try {
			String url = ME_SPARQL_ENDPOINT + "?query=" + URLEncoder.encode(query.toString(), ENCODING_UTF8) + "&formatting=XML&reasoning=1";
			String response = JGet.wget(url);
			log.debug(response);
			
			new SparqlResultComponentsParser().addComponentsFromSparqlResult(preservationActions, new StringReader(response));
			
			for (PreservationActionDefinition def : preservationActions) {
				def.setActionIdentifier("myExperiment");
			}

			return preservationActions;
			
		} catch (MalformedURLException e) {
			throw new PlatoException("Component registry is not configured properly.",e);
		} catch (IOException e) {
			throw new PlatoException("Failed to retrieve list of components.",e);
		}
	}
	




	@Override
	public String getLastInfo() {
		return null;
	}

	@Override
	public String getToolIdentifier(String url) {
		return "myExperiment";
	}

	@Override
	public String getToolParameters(String url) {
		return null;
	}

}
