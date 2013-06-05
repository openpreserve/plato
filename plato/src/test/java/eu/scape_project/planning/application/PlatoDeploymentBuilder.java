package eu.scape_project.planning.application;

import java.io.File;
import java.util.Arrays;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import eu.scape_project.planning.application.MockAuthenticatedUserProvider;

public class PlatoDeploymentBuilder {

    private static MavenDependencyResolver resolver;
    
    
    static {
        resolver = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom(
            "pom.xml");
    }
    
    public static MavenDependencyResolver getResolver(){
        return resolver;
    }
    
    
    /**
     * Creates a basic {@link WebArchive} for module plato.
     * 
     * Additional classes from module plato have to be added as required.
     *   
     * @return
     */
    public static WebArchive createPlatoWebArchive() {
        
        // get plato-model and all of its dependencies
        File[] modelLibs = resolver.artifact("eu.scape-project.pw:plato-model").resolveAsFiles(); 

        // to add a persistence.xml to the model we have to modify this jar 
        JavaArchive modelLib = ShrinkWrap.createFromZipFile(JavaArchive.class, modelLibs[0]);
        modelLib.addAsResource("it_test-persistence.xml", "META-INF/persistence.xml");        
        
        // add additional libraries and their dependencies 
        File[] libs = resolver.artifacts(
            "eu.scape-project.pw:planning-core", 
            "eu.scape-project.pw:minimee",
            "pt.gov.dgarq.roda:roda-client"
            ).resolveAsFiles();

        
        // create the web archive
        WebArchive wa = ShrinkWrap
            .create(WebArchive.class, "plato.war")
            
            // MockAuthenticatedUserProvider
            .addAsWebInfResource("test-beans.xml", "beans.xml")
            .addAsLibrary(modelLib)
            .addAsLibraries( libs )
            .addAsLibraries( Arrays.copyOfRange(modelLibs, 1, modelLibs.length) )
            // NOTE: addPackages grabs all classes in a package, there is no restriction on the current module
            //       do NOT add classes which are already in one of the libs included above 
            .addPackages(true, "eu.scape_project.planning.services.pa")
            .addPackages(true, "eu.scape_project.planning.services.taverna")
            .addClass(MockAuthenticatedUserProvider.class)
            ;
        return wa;
    }
}
