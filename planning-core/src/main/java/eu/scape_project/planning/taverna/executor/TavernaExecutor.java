package eu.scape_project.planning.taverna.executor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eu.scape_project.planning.taverna.TavernaPort;

public interface TavernaExecutor {

	/**
	 * Executes the workflow.
	 * 
	 * @throws IOException
	 * @throws TavernaExecutorException
	 */
	public abstract void execute() throws IOException, TavernaExecutorException;

	/**
	 * Returns the output data of the previous workflow run.
	 * 
	 * @return
	 */
	public abstract Map<TavernaPort, Object> getOutputData();

	/**
	 * Returns the ouput files of the previous workflow run.
	 * 
	 * @return
	 */
	public abstract HashMap<TavernaPort, Object> getOutputFiles();

}