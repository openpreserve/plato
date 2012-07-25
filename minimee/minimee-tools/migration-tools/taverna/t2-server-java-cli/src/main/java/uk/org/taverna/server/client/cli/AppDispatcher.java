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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * @author Robert Haines
 * 
 */
public final class AppDispatcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelpAndExit(1);
		}

		String program = args[0];
		String[] programArgs = Arrays.copyOfRange(args, 1, args.length);

		new AppDispatcher().run(program, programArgs);
	}

	public void run(String program, String[] args) {

		try {
			Package pack = this.getClass().getPackage();
			Class<?> cls = Class.forName(pack.getName() + "." + program);
			if (!cls.getSuperclass().getName()
					.equals(pack.getName() + ".ConsoleApp")) {
				System.out.format(
						"Program '%s' is not a runnable program.\n\n", program);
				AppDispatcher.printHelpAndExit(1);
			}

			ConsoleApp app = (ConsoleApp) cls.getConstructor().newInstance();
			List<Option> opts = app.registerOptions();
			app.run(app.parseOpts(opts, args));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.format("Program '%s' not found.\n\n", program);
			AppDispatcher.printHelpAndExit(1);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private static void printHelpAndExit(int exitcode) {
		String header = "\nWhere "
				+ "server-address is the full URI of the server to "
				+ "connect to, e.g.: http://example.com:8080/taverna"
				+ ", and [options] are program specific. To see program "
				+ "specific help, use:\nprogram -h";
		String footer = "Current available programs are:\n * ServerInfo"
				+ "\n * RunWorkflow\n * DeleteRuns";
		HelpFormatter help = new HelpFormatter();
		help.printHelp("program [options] server-address", header,
				new Options(), footer);
		System.exit(exitcode);
	}
}
