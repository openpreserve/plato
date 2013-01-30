Plato: the preservation planning tool
=====================================

Plato is a preservation planning tool, it originates from the [Planets Project](http://planets-project.eu/) and is now further developed within the [SCAPE project](http://www.scape-project.eu). 


## Prepare the Database

### Install and setup MySQL server 
1. Install MySQL 5 Server (tested with version 5.1.54)
2. Set UTF8 as a default encoding/collation. 
   Check appropriate MySQL 5.X reference manual for instruction on how to do this. There were some changes so it might not be the same for all versions. 
3. Configuration
   Make the following changes in your MySQL config file (on Linux it is my.cnf and on Windows my.ini)
    <pre>
     #in the Server Section [mysqld] 
     max_allowed_packet = 128M
     max_sp_recursion_depth = 255
     thread_stack = 512K
    </pre> 
4. Restart MySQL server 

### Setup Database for Planning Suite
If you run Planning Suite and IDP on the same domain, you can use a predefined script:

1. Switch to the _tools_ directory, there you will find the script setup-database.sh.
   It creates DB users and databases for IDP and Plato, and prepares a config file for your JBoss AS 7 server. 
2. Run it via
   `./setup-database.sh <MySQL root password> <Plato DB password> <IDP DB password>`
3. You will find two new files in this directory: standalone.xml for your production environment, adjust it to your needs, and additionally standalone-test.xml - the configuration for your test server.

***
## Install and setup JBoss AS 7
1. Download and install JBoss AS 7.1.0.Final. 
   You can use JBoss AS 7.1.1.Final, but there are [issues with the included JSF implementation] (https://issues.jboss.org/browse/AS7-4366), so you have to replace the corresponding modules yourself.
2. Copy the file standalone.xml you have generated during database setup to  [JBOSS_HOME]/standalone/configuration/standalone.xml

***
### Install MySQL Drivers
1. Download [MySQL Connector/J] (http://dev.mysql.com/downloads/connector/j)
2. Create a driver module as described in [Installing a JDBC driver as a module] (https://community.jboss.org/wiki/DataSourceConfigurationInAS7#Installing_a_JDBC_driver_as_a_module)

### Install and setup PicketLink 
1. Go to the modules/org/picketlink/main directory and delete all jar files in it.
2. Download Picketlink 2.1.4 jars for JBoss AS 7.1.x here: [picketlink-core-2.1.4.Final.jar](https://repository.jboss.org/nexus/content/groups/public/org/picketlink/picketlink-core/2.1.4.Final/picketlink-core-2.1.4.Final.jar) and 
[picketlink-jbas7-2.1.4.Final.jar](https://repository.jboss.org/nexus/content/groups/public/org/picketlink/distribution/picketlink-jbas7/2.1.4.Final/picketlink-jbas7-2.1.4.Final.jar)
3. Copy both Picketlink 2.1.4 jars into modules/org/picketlink/main directory
4. In modules/org/picketlink/main do the following changes to the module.xml file :
   <pre> 
&lt;module xmlns="urn:jboss:module:1.1" name="org.picketlink"&gt;
    &lt;resources&gt;
        &lt;resource-root path="picketlink-core-2.1.4.Final.jar"/&gt;
        &lt;resource-root path="picketlink-jbas7-2.1.4.Final.jar"/&gt;
    &lt;/resources&gt;
    &lt;dependencies&gt;
        &lt;module name="javax.api"/&gt;
        &lt;module name="javax.security.auth.message.api"/&gt;
        &lt;module name="javax.security.jacc.api"/&gt;
        &lt;module name="javax.transaction.api"/&gt;
        &lt;module name="javax.xml.bind.api"/&gt;
        &lt;module name="javax.xml.stream.api"/&gt;
        &lt;module name="javax.servlet.api"/&gt;
        &lt;module name="org.jboss.common-core"/&gt;
        &lt;module name="org.jboss.logging"/&gt;
        &lt;module name="org.jboss.as.web"/&gt;
        &lt;module name="org.jboss.security.xacml"/&gt;
        &lt;module name="org.picketbox"/&gt;
        &lt;module name="javax.xml.ws.api"/&gt;
        &lt;module name="org.apache.log4j"/&gt;
        &lt;module name="org.apache.santuario.xmlsec"/&gt;
    &lt;/dependencies&gt;
&lt;/module&gt;
  </pre>

***

## Install required programs 
### FITS
* Install FITS Tool from [http://code.google.com/p/fits](http://code.google.com/p/fits) .
* Set environment variable FITS_HOME to install directory. 

### Optional: Minimee
Only necessary if you want to use Minimee services

1. Install all tools you want to use on your server.
2. Configure tool and services in the files tool-configs.xml and actions-config.xml.
   You can find examples in `minimee/src/main/resources/data/services/`

### Configure Plato
Some aspects of Plato can be configured using configuration files. See [Plato configuration](https://github.com/openplanets/plato/wiki/Plato-configuration) for further information.

***
## Build and Deploy

### Setup Test Server
To setup the test server make a copy of the already configured main instance, replace the standalone.xml with the generated standalone-test.xml, and rename it to standalone.xml.

### Build and Deploy
1. Install maven version 3 .
2. Install git client
3. Clone Plato source from the Github: <pre>git clone git@github.com:openplanets/plato.git</pre>
4. Go into the folder plato and start the build process: 
   <pre>mvn clean install -Dsp.domain=your.planningsuite.domain.org -Didp.domain=your.idp.domain.org -DjbossHomeTest=&lt;JBOSS_HOME_TEST&gt;</pre>
  Parameters:
  * sp.domain: the domain where Planning Suite will be available (defaults to _localhost_)
  * idp.domain: the domain where your identity provider will be available(defaults to _localhost_)
  * jbossHomeTest: the path to your local JBoss server  used for testing
    Note: you can skip this parameter if you also skip all tests via _-DskipTests_
  The generated artifacts are in the _target_ sub-folders of planningsuite-ear and idp
  You have to use the war and ear files. (you cannot deploy the exploded archives, because there is a bug in the maven-war plugin)

5. Copy planningsuite-ear/target/planningsuite-ear.ear to your JBoss deployments folder
6. Copy idp/target/idp.war to your JBoss deployments folder.

NOTE: If your database is set up for the first time, you have to:

1. Edit the file planningsuite-ear/src/main/application/META-INF/persistence.xml and set hibernate.hbm2dll.auto to _create_
2. Generate plannginsuite-ear.ear and deploy it to JBoss (like described above)
3. Change hibernate.hbm2dll.auto back to _update_ 
   This is **important**, otherwise the database gets re-created with each start - and your data gets lost
4. Generate and deploy once again plannginsuite-ear.ear 

## Development

### Requirements
 - Eclipse Indigo: http://www.eclipse.org/downloads/index-developer.php
 - Eclipse checkstyle plugin: http://marketplace.eclipse.org/node/150
 - Eclipse m2eclipse plugin: http://marketplace.eclipse.org/content/maven-integration-eclipse
 - Maven 3: http://maven.apache.org/
 - clone this repo (if you haven't)

### Setup IDE
After you install eclipse and clone the repo, install the following
plugins listed above. To install a plugin click on Help > Eclipse Market Place
and search them or just use the nice drag and drop feature and drag them from the links above.

As soon as you are ready import the maven modules by selecting File > Import > Maven > Existing Maven Projects.
Maven will fetch the whole internet (this is normal) and will import the projects for you.

If you are planning to contribute please setup the provided eclipse_formatter, cleanup_profile and checkstyle config file
(in the build-tools-config project). To do this for all projects in this eclipse instance follow these steps:

Select Window > Preferences. In the new window select Java > Code Style > Clean Up
and import the cleanup_profile.xml (mentioned above). Do the same for the Formatter in
Java > Code Style > Formatter 

For Checkstyle open again the preferences window and select Checkstyle. Click on the New button and select
Project Relative Configuration. Afterwards give a name and browse to the checkstyle.xml file provided in the build-tools-config
maven module. At the end select this to be the default checkstyle config.

To activate checkstyle for a certain project just right click on it select checkstyle > activate checkstyle.
This will continouosly check the code as you type and mark the bad spots with yellow and will provide
warnings.

Acknowledgements
----------------

Part of this work was supported by the European Union in the 7th Framework Program, IST, through the SCAPE project, Contract 270137.