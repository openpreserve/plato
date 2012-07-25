Taverna Server Java CLI Scripts
-------------------------------

These scripts provide examples of how to use the t2-server-client Java
library.

Building
--------

Simply use maven from the root directory:
$ mvn package

Usage
-----

The jar file produced by the above command contains all it needs to be
executed directly. The command you wish to run is provided as the first
parameter, with options to that command provided with the remaining
parameters.

Currently available commands are:
 * ServerInfo
 * RunWorkflow
 * DeleteRuns

All commands provide --help and -h options that gives detailed usage
instructions.

An example invocation would be:
$ java -jar t2-server-java-cli-0.0.1-jar-with-dependencies.jar ServerInfo -h
usage: ServerInfo [options] server-address

Where server-address is the full URI of the server to connect to, e.g.:
http://example.com:8080/taverna, and [options] can be:
 -h,--help      Show this help and exit
 -v,--version   Show the version and exit
