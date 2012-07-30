package eu.scape_project.pw.planning.xml;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import eu.scape_project.planning.xml.C3POProfileParser;

public class C3POProfileParserTest {

	private static final String MISSING = "No format distribution provided";

	private C3POProfileParser parser;

	@Before
	public void setup() {
		parser = new C3POProfileParser();
		try {
			parser.read(
					new FileInputStream("src/test/resources/c3po/c3po.xml"),
					false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void shouldTestSuccessfulParsing() throws Exception {
		String id = this.parser.getCollectionId();
		Assert.assertEquals("roda", id);
		
		String count = this.parser.getObjectsCountInPartition();
		Assert.assertEquals("264", count);
		
		String desc = this.parser.getTypeOfObjects();
		Assert.assertNotSame(MISSING, desc);
		System.out.println(desc);
	}
}
