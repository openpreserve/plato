package eu.scape_project.planning.utils;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import org.junit.Test;

public class CharacterisationReportGeneratorTest {
   
    @Test
    public void testSimpleFITSReport() throws Exception{
        Reader simpleFits = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("fits/simple-fits.xml"), "UTF8");
        Reader fitsToHtml = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("fits/fits-to-html.xsl"), "UTF8");
        StringBuilder buffer = new StringBuilder();
        
        CharacterisationReportGenerator reportGen = new CharacterisationReportGenerator();
        reportGen.generateReportFromXML(simpleFits, fitsToHtml, new OutputStreamWriter(System.out));
    }

}
