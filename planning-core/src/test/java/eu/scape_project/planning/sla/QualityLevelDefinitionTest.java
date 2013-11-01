package eu.scape_project.planning.sla;

import java.io.StringWriter;

import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import eu.scape_project.planning.utils.SchematronValidator;
import eu.scape_project.planning.xml.LocalURIResolver;

public class QualityLevelDefinitionTest {
    
    //@Test
    public void testValidSample1() throws Exception{
        SchematronValidator validator = new SchematronValidator();
        
        StringWriter reportWriter = new StringWriter();

        URIResolver resolver = new LocalURIResolver().addHrefBase("iso_schematron_skeleton_for_xslt1.xsl", "schematron/iso_schematron_skeleton_for_xslt1.xsl");
        
        validator.validate(new StreamSource(getClass().getClassLoader().getResourceAsStream("qld/sample_measures1.xml")), 
            new StreamSource(getClass().getClassLoader().getResourceAsStream("qld/sample_qld1.xml")), new StreamResult(reportWriter), resolver);
        
        
    }

}
