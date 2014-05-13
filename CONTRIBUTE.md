# Plato: The Preservation Planning Tool

Efficient and trustworthy preservation planning.

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
	This will continuously check the code as you type and mark the bad spots with yellow and will provide
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

	1. Build planningsuite.ear with the additional parameter: <pre> -Ddatabase.schema.generation=create </pre>
	   and deploy it to JBoss (like described above)
	2. Stop JBoss
	3. Build planningsuite.ear once more without the additional parameter and deploy it again.
	   This is **important**, otherwise the database gets re-created with each start - and your data gets lost

	To run tests:

	* Instead of parameter -DskipTests you have to provide the path to your local JBoss server (the one you have prepared for testing):
	  <pre> -DjbossHomeTest=&lt; path to local jboss server &gt; </pre>


After successful compile the generated artifacts are available in the _target_ sub-folders of planningsuite-ear and idp

### Deploy

You have to use the war and ear files, you cannot deploy the exploded archives, because there is a bug in the maven-war plugin.

1. Copy planningsuite-ear/target/planningsuite-ear.ear to your JBoss deployments folder
2. Copy idp/target/idp.war to your JBoss deployments folder

### Contribute

1. [Fork the GitHub project](https://help.github.com/articles/fork-a-repo)
2. Change the code and push into the forked project
3. [Submit a pull request](https://help.github.com/articles/using-pull-requests)

To increase the chances of your changes being accepted and merged into the official source here's a checklist of things to go over before submitting a contribution. For example:

* Has unit tests (that covers at least 80% of the code)
* Has documentation (at least 80% of public API)
* Agrees to contributor license agreement, certifying that any contributed code is original work and that the copyright is turned over to the project
