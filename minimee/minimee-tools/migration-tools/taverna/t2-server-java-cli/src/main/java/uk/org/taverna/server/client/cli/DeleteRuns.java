/*
 * Copyright (c) 2010, 2011 The University of Manchester, UK.
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import uk.org.taverna.server.client.RunNotFoundException;
import uk.org.taverna.server.client.Server;

/**
 * 
 * @author Robert Haines
 * 
 */
public final class DeleteRuns extends ConsoleApp {

	private static final String NAME = "DeleteRuns";
	private static final String USAGE = "[run-ids...]";
	private static final String EXTRA_USAGE = "run-ids are the id numbers of the runs you want to delete.";

	public DeleteRuns() {
		super(NAME, USAGE, EXTRA_USAGE);
	}

	@Override
	public void run(CommandLine line) {

		boolean deleteAll = false;
		if (line.hasOption("all")) {
			deleteAll = true;
		}

		// get server address and run ids from left over arguments
		String[] args = line.getArgs();
		Server server = getServer(args);

		ArrayList<UUID> runs = new ArrayList<UUID>();
		for (String arg : args) {
			try {
				UUID run = UUID.fromString(arg);
				runs.add(run);
			} catch (IllegalArgumentException e) {
				// not a UUID, ignore
			}
		}

		// delete things
		if (deleteAll) {
			server.deleteAllRuns();
		} else {
			if (runs.size() == 0) {
				showHelpAndExit(1);
			}

			for (UUID u : runs) {
				try {
					server.deleteRun(u);
				} catch (RunNotFoundException e) {
					System.out.println("Run '" + u + "' not found - skipping.");
				}
			}
		}
	}

	@Override
	public List<Option> registerOptions() {
		ArrayList<Option> opts = new ArrayList<Option>();

		opts.add(new Option(null, "all", false, "Delete all runs on the server"));

		return opts;
	}
}
