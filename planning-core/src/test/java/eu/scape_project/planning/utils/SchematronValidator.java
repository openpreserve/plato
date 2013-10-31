package eu.scape_project.planning.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import eu.scape_project.planning.xml.LocalURIResolver;

public class SchematronValidator {
    
    
    private static final String GEN_SCHEMATRON_XSLT = "schematron/iso_svrl_for_xslt1.xsl";
    
    public SchematronValidator(){
        
    }
    
    public void validate(Source instance, Source schematron, Result report, URIResolver uriResolver) throws Exception{
        
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setURIResolver(uriResolver);
        try {
            
            
            Transformer transformer = factory.newTransformer(
                new StreamSource(getClass().getClassLoader().getResourceAsStream(GEN_SCHEMATRON_XSLT)));
            
            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(schematron, streamResult);
            
            String schemaXSLT = writer.toString();
            System.out.println(schemaXSLT);
            
            Transformer validatorTransformer = factory.newTransformer(new StreamSource(new StringReader(schemaXSLT)));
            
            validatorTransformer.transform(instance, report);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
