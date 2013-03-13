# JBoss utils
Contains utility functions specific to jboss.

* UTF8EncodingValve: Ensures that UTF-8 is used as default encoding for requests.

## Installation
1. Package the source

  `mvn package`

2. Copy the packaged jar to `${JBOSS_HOME}/modules/eu/scape_project/planning/util/main`

3. Create a file `module.xml` and add the following content

  <pre>
    &lt;?xml version="1.0" encoding="UTF-8"?&gt;
    &lt;module xmlns="urn:jboss:module:1.1" name="eu.scape_project.planning.util"&gt;
      &lt;resources&gt;
        &lt;resource-root path="jboss-util-0.0.1.jar"/&gt;
      &lt;/resources&gt;

      &lt;dependencies&gt;
        &lt;module name="javax.api"/&gt;
        &lt;module name="javax.servlet.api"/&gt;
        &lt;module name="org.jboss.common-core"/&gt;
        &lt;module name="org.jboss.as.web"/&gt;
    &lt;/dependencies&gt;
    &lt;/module&gt;
  </pre>

4. Add the module dependency to your deployment in file jboss-deployment-structure.xml

  <pre>
    &lt;jboss-deployment-structure&gt;
      &lt;!-- other stuff --&gt;
      &lt;dependencies&gt;
        &lt;module name="eu.scape_project.planning.util" export="true" /&gt;
      &lt;/dependencies&gt;
    &lt;/jboss-deployment-structure&gt;
  </pre>

### UTF8EncodingValve
This JBoss valve ensures that UTF-8 is used as default encoding for requests. To use it in your deployment, add the following to your jboss-web.xml. Note that the valve should be at the top to ensure that it is called before other valves. (This is a must if the application uses PicketLink which sets the encoding to ISO-8859-1)

<pre>
    &lt;?xml version="1.0" encoding="UTF-8"?&gt;
    &lt;jboss-web&gt;
      &lt;valve&gt;
        &lt;class-name>eu.scape_project.planning.jboss.util.UTF8EncodingValve&lt;/class-name&gt;
      &lt;/valve&gt;
      &lt;!-- other valves --&gt;
    &lt;/jboss-web&gt;
</pre>

