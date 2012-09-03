package eu.scape_project.planning.taverna.parser;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class T2FlowNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            throw new NullPointerException("Null prefix");
        else if ("t2f".equals(prefix))
            return "http://taverna.sf.net/2008/xml/t2flow";
        else if ("xml".equals(prefix))
            return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}
