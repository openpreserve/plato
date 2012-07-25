/*
 * Copyright (c) 2011 The University of Manchester, UK.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the names of The University of Manchester nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package uk.org.taverna.server.client.cli;

import java.net.URI;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import uk.org.taverna.server.client.Server;

/**
 * @author Robert Haines
 * 
 */
public abstract class ConsoleApp {

	private static final int DEFAULT_WIDTH = 80;
	protected final int consoleWidth;

	// app info
	private final String name;
	private String usage = "[options] server-address";
	private final String header = "\nWhere server-address is the full URI of the server to connect to, e.g.: http://example.com:8080/taverna, and [options] can be:";
	private final String footer;

	// common options
	private final Options options;

	ConsoleApp(String name, String usage, String extraUsage) {
		String cols = System.getenv("COLUMNS");
		consoleWidth = cols == null ? DEFAULT_WIDTH : Integer.parseInt(cols);

		this.name = name;
		this.usage += usage != null ? " " + usage : "";

		this.footer = extraUsage != null ? extraUsage : "";

		options = new Options();
	}

	ConsoleApp(String name, String usage) {
		this(name, usage, null);
	}

	ConsoleApp(String name) {
		this(name, null, null);
	}

	public abstract void run(CommandLine line);

	public List<Option> registerOptions() {
		return null;
	}

	protected CommandLine parseOpts(List<Option> opts, String[] args) {
		// create common options
		options.addOption("h", "help", false, "Show this help and exit");
		options.addOption("v", "version", false, "Show the version and exit");

		// add program specific ones
		if (opts != null) {
			for (Option o : opts) {
				options.addOption(o);
			}
		}

		// parse and deal with common options
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);

			// help option
			if (line.hasOption("h")) {
				showHelpAndExit(0);
			}

			// version option
			if (line.hasOption("v")) {
				showVersionAndExit();
			}
		} catch (ParseException exp) {
			System.out.println("Unexpected exception: " + exp.getMessage());
		}

		return line;
	}

	protected CommandLine parseOpts(String[] args) {
		return parseOpts(null, args);
	}

	private void showVersionAndExit() {
		System.out.println("Taverna 2 Server Java Lib version: 0.0.3");
		System.out.println("Taverna 2 Server REST API version: 2.2a");
		System.exit(0);
	}

	protected void showHelpAndExit(int exitcode) {
		String usage = this.name + " " + this.usage;
		HelpFormatter help = new HelpFormatter();
		help.printHelp(usage, header, options, footer);
		System.exit(exitcode);
	}

	protected Server getServer(String[] args) {
		Server server = null;

		for (String arg : args) {
			try {
				server = Server.connect(new URI(arg));

				return server;
			} catch (Exception e) {
				// not a URI, or cannot connect, ignore
				server = null;
			}
		}

		if (server == null) {
			showHelpAndExit(1);
		}

		return server;
	}
}
