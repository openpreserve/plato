package eu.scape_project.planning.utils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.DigitalObject;

public class CharacterisationReportGenerator {

    private static Logger log = LoggerFactory.getLogger(CharacterisationReportGenerator.class);

    public String generateHTMLReport(final DigitalObject object) {
        StringWriter writer = new StringWriter();
        try {
            appendFITSReport(object, writer);
        } catch (Exception e) {
            log.error("Failed to generate FITS report. " + e);
        }
        return writer.toString();
    }

    private void appendFITSReport(final DigitalObject object, final Writer writer) throws TransformerException, UnsupportedEncodingException {
        generateReportFromXML(new StringReader(object.getFitsXMLString()), new InputStreamReader(Thread.currentThread()
            .getContextClassLoader().getResourceAsStream("fits/fits-to-html.xsl"), "UTF8"), writer);
    }

    public void generateReportFromXML(final Reader xmlInput, final Reader xslInput, final Writer writer)
        throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslInput));

        transformer.transform(new StreamSource(xmlInput), new StreamResult(writer));
    }
}
