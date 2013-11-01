package eu.scape_project.planning.xml;

import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class LocalURIResolver implements URIResolver {

    private Properties uriMappings = new Properties();
    
    public URIResolver addHrefBase(String href, String base) {
        if ((href == null) || (base == null) ) {
                throw new IllegalArgumentException("href and base must not be null");
        }
        uriMappings.put(href, base);
        return this;
}
    

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        String location = uriMappings.getProperty(href);
        if (location != null) {
                Source source = new StreamSource(getClass().getClassLoader().getResourceAsStream(location));
                // important: set the sytemId to the created InputSource, the parser will use it to complete relative URI's in the schema
                source.setSystemId(href);
                return source;
        } 
        return null;

    }

}
