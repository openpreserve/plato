package eu.scape_project.pw.planning.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SchemaValidationTest {
	
	private ValidatingParserFactory validatingParserFactory = new ValidatingParserFactory(); 


	protected SAXParser getNonValidatingParser() throws ParserConfigurationException, SAXException {
		SAXParserFactory f = SAXParserFactory.newInstance();
		return f.newSAXParser();
	}
	
	@Test
	public void parsePlanWithoutValidation() throws ParserConfigurationException, SAXException, IOException {
		InputStream inPlan = getClass().getClassLoader().getResourceAsStream("plans/Archiving_Digital_Photographs.xml");
		SAXParser parser = getNonValidatingParser();
		parser.parse(inPlan, new DefaultHandler());
	}
	
	@Test(expected=SAXException.class)
	public void parseNonWellformedXml() throws ParserConfigurationException, SAXException, IOException {
		SAXParser parser = getNonValidatingParser();
		
		parser.parse(new InputSource(new StringReader("<test><open></test>")), new DefaultHandler());
	}
	
	@Test(expected=SAXException.class)
	public void parseXmlInvalidOrder() throws ParserConfigurationException, SAXException, IOException {
		InputStream inPlan = getClass().getClassLoader().getResourceAsStream("simple/simpleInvalidOrder.xml");
		
		SAXParser parser = validatingParserFactory.getValidatingParser();
		//parser.setProperty(JAXP_SCHEMA_SOURCE, "file:///home/kraxner/workspace/planningsuite/planning-core/src/test/resources/simple/simple.xsd");
		
		parser.parse(inPlan, new StrictDefaultHandler(new SchemaResolver().addSchemaLocation("http://simple.org/simple/V1.0.0/simple.xsd", "simple/simple.xsd")));
	}
	
	@Test
	public void parseValidXml() throws ParserConfigurationException, SAXException, IOException {
		InputStream inPlan = getClass().getClassLoader().getResourceAsStream("simple/simple.xml");
		
		SAXParser parser = validatingParserFactory.getValidatingParser();
		parser.setProperty(ValidatingParserFactory.JAXP_SCHEMA_SOURCE, "http://simple.org/simple/V1.0.0/simple.xsd");
		parser.parse(inPlan, new StrictDefaultHandler(new SchemaResolver().addSchemaLocation("http://simple.org/simple/V1.0.0/simple.xsd", "simple/simple.xsd")));
	}
	

}
