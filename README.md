# Plato: The Preservation Planning Tool

Efficient and trustworthy preservation planning.

### What does Plato do?

Plato is a decision support tool which guides you through the preservation planning workflow.
To do this efficiently it integrates information from external sources like control policies, content-profiles, and component registries; it can run these components to automate tool evaluation, and connects to repositories via open interfaces.
During this process Plato collects all the information to enable the planner to take an informed decision, and finally generates an evidence-based preservation plan which can be executed on suitable platforms.

### What are the benefits for the end user?

Plato brings you the following benefits:

* Guidance through the preservation planning process
* Trustability through controlled experiments and documentation
* Policy aware planning
* Using [standardized measures](purl.org/DP/quality/measures)
* [myExperiment](http://www.myexperiment.org/) and Taverna integration: Share migration, quality assurance, and characterisation components with the community
* Connects to repositories using [open interfaces](https://github.com/openplanets/scape-apis)
* Provides a plan executable on your content
* Enables on-going monitoring by creating triggers for [Scout](https://github.com/openplanets/scout)

### Who is the intended audience?

Plato is for:

* Content holders
* Preservation experts

## Features and roadmap

### Version 4.4

* Policy aware
* Reads content profiles
* Integration of myExperiment
* Connects to repositories via open APIs (Data Connector API and Plan Management API)
* Read only view for public plans
* Deploy of executable Preservation Action Plan

### Roadmap

* Improved template creation for Executable Plan (based on used migration and quality assurance components)
* Smarter triggers for monitoring
* Implementation of Notification API to recieve planning requests
* Policy-based improvements to increase efficiency
* Implementation of Reassessment API


## How to install and use

### Requirements

To install you need:

* Java 7
* MySQL 5 Server
* JBoss AS 7.1.0.Final

### Download

At the moment there are no precompiled versions available, please refer to section [Build](#build).

### Install instructions

To install follow these steps:

1. Install and setup MySQL server 
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

2. Setup Database for Planning Suite
	If you run Planning Suite and IDP on the same domain, you can use a predefined script:
	
	1. Switch to the _tools_ directory, there you will find the script setup-database.sh.
	   It creates DB users and databases for IDP and Plato, and prepares a config file for your JBoss AS 7 server. 
	2. Run it via
	   `./setup-database.sh <MySQL root password> <Plato DB password> <IDP DB password>`
	3. You will find two new files in this directory: standalone.xml for your production environment, adjust it to your needs, and additionally standalone-test.xml - the configuration for your test server.

3. Install and setup JBoss AS 7
	1. Download and install JBoss AS 7.1.0.Final. 
	   You can use JBoss AS 7.1.1.Final, but there are [issues with the included JSF implementation] (https://issues.jboss.org/browse/AS7-4366), so you have to replace the corresponding modules yourself.
	2. Copy the file standalone.xml you have generated during database setup to  [JBOSS_HOME]/standalone/configuration/standalone.xml


4. Install MySQL Drivers
	1. Download [MySQL Connector/J] (http://dev.mysql.com/downloads/connector/j)
	2. Create a driver module as described in [Installing a JDBC driver as a module] (https://community.jboss.org/wiki/DataSourceConfigurationInAS7#Installing_a_JDBC_driver_as_a_module)

5. Install and setup PicketLink 
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

6. Install UTF8EncodingValve

	Please refer to the [readme of jboss-utils] (https://github.com/openplanets/plato/blob/integration/jboss-util/README.md)


8. Configure Plato
Some aspects of Plato can be configured using configuration files. See [Plato configuration](https://github.com/openplanets/plato/wiki/Plato-configuration) for further information.

9. Optional: FITS
	* Install FITS Tool from [http://code.google.com/p/fits](http://code.google.com/p/fits) .
	* Set environment variable FITS_HOME to install directory. 

### Use

To use the tool, start up JBoss 7 and navigate with your browser to e.g. at http://localhost:8080/plato


### Troubleshooting

If you encounter problems please use the built in feedback functionality in Plato (on the right side of the page),
or refer to [github](https://github.com/openplanets/plato/issues)

## More information

### Publications

* Christoph Becker, Hannes Kulovits, and Andreas Rauber: [Trustworthy Preservation Planning with Plato](http://ercim-news.ercim.eu/images/stories/EN80/EN80-web.pdf) ERCIM News 80,p.24-25, January 2010.
* Kraxner, Plangg, Duretec, Becker, Faria: [The SCAPE planning and watch suite: supporting the preservation lifecycle in repositories.](http://hdl.handle.net/1822/25215) In: iPRES 2013, Lisbon, Portugal.
* Christoph Becker, Hannes Kulovits, Mark Guttenbrunner, Stephan Strodl, Andreas Rauber, and Hans Hofman: [Systematic planning for digital preservation: Evaluating potential strategies and building preservation plans](http://www.ifs.tuwien.ac.at/~becker/pubs/becker-ijdl2009.pdf) International Journal on Digital Libraries (IJDL), December 2009.

Check out the [Plato website](http://ifs.tuwien.ac.at/dp/plato/intro_documentation.html) for more publications.

### Licence

Plato is released under [Apache version 2.0 license](LICENSE.txt).

### Acknowledgements

Part of this work was supported by the European Union in the 7th Framework Program, IST, through the SCAPE project, Contract 270137.

### Support

This tool is supported by the [Open Planets Foundation](http://www.openplanetsfoundation.org).

## Develop

[![Build Status](https://travis-ci.org/openplanets/plato.png)](https://travis-ci.org/openplanets/plato)

### Requirements

To build you require:

* Git client
* Apache Maven 3
* Java Developers Kit 7 (e.g. OpenJDK 7)

For using the recommended IDE you require:

* [Eclipse for Java(Eclipse Indigo)](http://www.eclipse.org/downloads/index-developer.php)
* [Eclipse checkstyle plugin](http://marketplace.eclipse.org/node/150)
* [Eclipse m2e plugin](http://marketplace.eclipse.org/content/maven-integration-eclipse)

### Setup IDE

1. Start Eclipse
2. Install the m2e plugin
3. Install the checkstyle plugin
4. Import the projects
	1. Select "File > Import". Then, select "Maven > Existing Maven Projects" and click "Next"
	2. In the "Root Directory", browse to Plato source code directory on your filesystem and select "Open"
	3. Optionally, you can add it to a "Working set"
	4. Click "Finish"
5. Setting up Checkstyle
If you are planning to contribute please setup the provided eclipse_formatter, cleanup_profile and checkstyle config file
(in the build-tools-config project). To do this for all projects in this eclipse instance follow these steps:

	1. Select Window > Preferences. In the new window select Java > Code Style > Clean Up
	and import the cleanup_profile.xml (mentioned above). Do the same for the Formatter in
	Java > Code Style > Formatter 
	
	2. For Checkstyle open again the preferences window and select Checkstyle. Click on the New button and select
	Project Relative Configuration. Afterwards give a name and browse to the checkstyle.xml file provided in the build-tools-config
	maven module. At the end select this to be the default checkstyle config.
	
	3. To activate checkstyle for a certain project just right click on it select checkstyle > activate checkstyle.
	This will continouosly check the code as you type and mark the bad spots with yellow and will provide
	warnings.

### Build

1. Setup Test Server
To setup the test server make a copy of the already configured main instance, replace the standalone.xml with the generated standalone-test.xml, and rename it to standalone.xml.

2. Build
	1. Install maven version 3
	2. Install git client
	3. Clone Plato source from the Github: <pre>git clone git@github.com:openplanets/plato.git</pre>
	4. Go into the folder plato and start the build process: 
	   <pre>mvn clean install -Dps.port=80 -Dsp.domain=your.planningsuite.domain.org -Didp.domain=your.idp.domain.org -DskipTests</pre>
	  Parameters:
	  * ps.port=80: port on which the web-applications will be available (defaults to _8080_)
	  * sp.domain: specifies the domain of the service provider - where Planning Suite will be available (defaults to _localhost_)
	  * idp.domain: the domain where your identity provider will be available(defaults to _localhost_)
	
	NOTE: If your database is set up for the first time, you have to:

	1. Build plannginsuite.ear with the additional parameter: <pre> -Ddatabase.schema.generation=create </pre>
	   and deploy it to JBoss (like described above)
	2. Stop JBoss
	3. Build plannginsuite.ear once more without the additional parameter and deploy it again.
	   This is **important**, otherwise the database gets re-created with each start - and your data gets lost

	To run tests:

	* Instead of parameter -DskipTests you have to provide the path to your local JBoss server (the one you have prepared for testing):
	  <pre> -DjbossHomeTest=&lt; path to local jboss server &gt; </pre>


After successful compile the generated artifacts are available in the _target_ sub-folders of planningsuite-ear and idp

### Deploy

You have to use the war and ear files. (you cannot deploy the exploded archives, because there is a bug in the maven-war plugin)

1. Copy planningsuite-ear/target/planningsuite-ear.ear to your JBoss deployments folder
2. Copy idp/target/idp.war to your JBoss deployments folder.

### Contribute

1. [Fork the GitHub project](https://help.github.com/articles/fork-a-repo)
2. Change the code and push into the forked project
3. [Submit a pull request](https://help.github.com/articles/using-pull-requests)

To increase the changes of you code being accepted and merged into the official source here's a checklist of things to go over before submitting a contribution. For example:

* Has unit tests (that covers at least 80% of the code)
* Has documentation (at least 80% of public API)
* Agrees to contributor license agreement, certifying that any contributed code is original work and that the copyright is turned over to the project
