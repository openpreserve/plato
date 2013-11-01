package eu.scape_project.planning.xml;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.utils.ParserException;

public class C3POProfileParserTest {

    private static final String MISSING = "No format distribution provided";

    private C3POProfileParser parser;

    @Before
    public void setup() {
        parser = new C3POProfileParser();
        try {
            parser.read(new FileInputStream("src/test/resources/c3po/c3po.xml"), false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldTestSuccessfulParsing() throws Exception {
        String id = this.parser.getCollectionId();
        assertEquals("roda", id);

        String count = this.parser.getObjectsCountInPartition();
        assertEquals("264", count);

        String desc = this.parser.getTypeOfObjects();
        assertNotSame(MISSING, desc);
    }

    @Test
    public void shouldTestSampleRecordParsing() throws Exception {
        List<SampleObject> objects = this.parser.getSampleObjects();
        assertNotNull(objects);
        assertFalse(objects.isEmpty());

        boolean atLeastOnehasPuid = false;
        for (SampleObject o : objects) {
            assertNotNull(o.getFullname());
            assertNotSame("", o.getFullname());

            if (!atLeastOnehasPuid) {
                atLeastOnehasPuid = o.getFormatInfo().getPuid() != null;
            }
        }

        assertTrue(atLeastOnehasPuid);
    }
    
    @Test
    public void shouldTestObjectIdentifierRetrieval() throws Exception {
        List<String> ids = this.parser.getObjectIdentifiers();
        assertFalse(ids.isEmpty());
        assertEquals(264, ids.size());
    }
}
