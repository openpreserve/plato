package eu.scape_project.planning.taverna.parser;

import org.jaxen.NamespaceContext;

public class T2FlowNamespaceContext implements NamespaceContext {

    @Override
    public String translateNamespacePrefixToUri(String prefix) {

        if (prefix == null) {
            return null;
        } else if ("t2f".equals(prefix)) {
            return "http://taverna.sf.net/2008/xml/t2flow";
        }
        return null;
    }

}
