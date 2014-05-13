# Plato: The Preservation Planning Tool

Efficient and trustworthy preservation planning.

## How to install and use

### Requirements

To install you need:

* Java 7
* MySQL 5 Server
* JBoss AS 7.1.0.Final

### Download

At the moment there are no precompiled versions available, please refer to the instructions for building Plato on the contribute page.

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


### Licence

Plato is released under [Apache version 2.0 license](LICENSE.txt).

### Acknowledgements

This work was partially supported by the [SCAPE project](http://scape-project.eu/). The SCAPE project is co-funded by the European Union under FP7 ICT-2009.4.1 (Grant Agreement number 270137)
### Support

This tool is supported by the [Open Planets Foundation](http://www.openplanetsfoundation.org). If you require support, feel free to contact plato [at] ifs.tuwien.ac.at.

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
