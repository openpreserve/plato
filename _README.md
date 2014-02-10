# Plato: The Preservation Planning Tool

Efficient and trustworthy preservation planning.

### What does Plato do?

Plato is a decision support tool which guides you through the preservation planning workflow.
To do this efficiently it integrates information from external sources like control policies, content-profiles, and component registries; it can run these components to automate tool evaluation, and connects to repositories via open interfaces.
During this process Plato collects all the information to enable the planner to take an informed decision, and finally generates an evidence-based preservation plan which can be executed on suitable platforms.

### What are the benefits for the end user?

* Trustability through controlled experiments and documentation
* Policy aware
* Using standardized measures
* myExperiment and Taverna integration: Share components with the community
* Connects to repositories using open interfaces
* Provides an executable plan
* Creates triggers for monitoring via Scout

### Who is the intended audience?

Plato is for:

* Content holders? (specify if possible)
* Preservation experts? (specify if possible)
* Institutions that would like to ... (be as specific as possible)

## Features and roadmap

### Version 4.4

* Generation of Preservation Action Plan template
* Read only view for public plans

### Roadmap

* Feature 3
* Feature 4

## How to install and use

### Requirements

To install you need:

* Java 7
* MySQL 5 Server
* JBoss AS 7.1.0.Final

### Download

At the moment there are no precompiled versions available, please refer to section Build.

### Install instructions

To install follow these steps:

1. Setup Database for Planning Suite
	If you run Planning Suite and IDP on the same domain, you can use a predefined script:

	1. Switch to the _tools_ directory, there you will find the script setup-database.sh.
	   It creates DB users and databases for IDP and Plato, and prepares a config file for your JBoss AS 7 server. 
	2. Run it via
	   `./setup-database.sh <MySQL root password> <Plato DB password> <IDP DB password>`
	3. You will find two new files in this directory: standalone.xml for your production environment, adjust it to your needs, and additionally standalone-test.xml - the configuration for your test server.
2. Second step

### Use

To use the tool, open it with your browser, e.g. at http://localhost:8080/plato


refer to help page

### Troubleshooting

Problems and workarounds will be here when needed.

## More information

### Publications

* Publication 1
* Publication 2

### Licence

Plato is released under [Apache version 2.0 license](LICENSE.txt).

### Acknowledgements

Part of this work was supported by the European Union in the 7th Framework Program, IST, through the SCAPE project, Contract 270137.

### Support

This tool is supported by the [Open Planets Foundation](http://www.openplanetsfoundation.org). Commercial support is provided by company X.

## Develop

[![Build Status](https://travis-ci.org/openplanets/plato.png)](https://travis-ci.org/openplanets/plato)

### Requirements

To build you require:

* Git client
* Apache Maven
* Java Developers Kit (e.g. OpenJDK 6)

For using the recommended IDE you require:

* Eclipse of Java

### Setup IDE

1. Start Eclipse
2. Select "File > Import". Then, select "Maven > Existing Maven Projects" and click "Next"
3. In the "Root Directory", browse to RODA source code directory on your filesystem and select "Open"
4. Optionally, you can add it to a "Working set"
5. Click "Finish"

### Build

To compile go to the sources folder and execute the command:

```bash
$ mvn clean install
```

After successful compile the binary will be available at `target/binary.jar`.

### Deploy

To deploy do ...

### Contribute

1. [Fork the GitHub project](https://help.github.com/articles/fork-a-repo)
2. Change the code and push into the forked project
3. [Submit a pull request](https://help.github.com/articles/using-pull-requests)

To increase the changes of you code being accepted and merged into the official source here's a checklist of things to go over before submitting a contribution. For example:

* Has unit tests (that covers at least 80% of the code)
* Has documentation (at least 80% of public API)
* Agrees to contributor license agreement, certifying that any contributed code is original work and that the copyright is turned over to the project
