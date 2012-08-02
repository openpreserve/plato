package eu.scape_project.planning.services.pa.taverna;

import java.net.MalformedURLException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.interfaces.actions.IPreservationActionRegistry;

public class ComponentRegistry implements IPreservationActionRegistry {

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolIdentifier(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolParameters(String url) {
		// TODO Auto-generated method stub
		return null;
	}

}
