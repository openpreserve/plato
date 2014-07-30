package eu.scape_project.planning.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FileUtilsTest {

    @Test
    public void testConvertUri() {
        String filename = FileUtils
            .makeFilename("http://roda.scape.keep.pt/roda-core/rest/file/roda:79/2013-04-16T07:59:16.39Z/F5");
        assertTrue(filename.indexOf('/') == -1);
    }
}
