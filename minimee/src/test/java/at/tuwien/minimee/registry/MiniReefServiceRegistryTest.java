package at.tuwien.minimee.registry;

import java.util.List;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.services.IServiceInfo;

public class MiniReefServiceRegistryTest {
    
    private MiniReefServiceRegistry registry; 
    
    @Before
    public void setUp() {
        registry = new MiniReefServiceRegistry();
    }
    
    @Test(expected=PlatoException.class)
    public void testRetrieveWithNullFormatInfo() throws PlatoException{
        registry.getAvailableActions(null);
    }
    
    @Test
    public void testRetrieveAction() throws PlatoException{
        FormatInfo formatInfo = new FormatInfo();
        formatInfo.setPuid("fmt/16");
        List<IServiceInfo> services = registry.getAvailableActions(formatInfo);
        assertNotNull(services);
        assertTrue(services.size() > 0);
    }

    @Test
    public void testRetrieveActionForTemporalPuid() throws PlatoException{
        FormatInfo formatInfo = new FormatInfo();
        formatInfo.setPuid("x-fmt/35");
        List<IServiceInfo> services = registry.getAvailableActions(formatInfo);
        assertNotNull(services);
        assertTrue(services.size() > 0);
    }

    @Test(expected=PlatoException.class)
    public void testMalformedPuid() throws PlatoException{
        FormatInfo formatInfo = new FormatInfo();
        formatInfo.setPuid("G:\\tmp\\DSC_3161.NEF");
        registry.getAvailableActions(formatInfo);
    }
}
