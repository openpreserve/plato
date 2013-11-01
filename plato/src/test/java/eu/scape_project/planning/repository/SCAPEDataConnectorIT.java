package eu.scape_project.planning.repository;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import eu.scape_project.planning.annotation.ManualTest;
import eu.scape_project.planning.utils.RepositoryConnectorException;

@ManualTest
public class SCAPEDataConnectorIT {

    /**
     * Downloads a test file from a locally running fedora4 instance.
     * 
     * @throws IOException
     * @throws RepositoryConnectorException
     */
    @Test
    public void downloadFile() throws IOException, RepositoryConnectorException {
        SCAPEDataConnectorClient connector = new SCAPEDataConnectorClient("http://localhost:6080/fcrepo/rest/scape/", "", "");
        
        InputStream in = connector.downloadFile("entity-1/representation-1/file-1");
        assertNotNull(in);
        in.close();
    }
    

}
