package eu.scape_project.planning.plato.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import eu.scape_project.planning.services.action.IActionInfo;
import eu.scape_project.planning.services.taverna.TavernaServiceLoader;
import eu.scape_project.planning.services.taverna.model.WorkflowDescription;

/**
 * Taverna service cache for loading details.
 */
@Dependent
public class TavernaServices implements Serializable, IServiceLoader {

    private static final long serialVersionUID = -65374305723742598L;

    @Inject
    private TavernaServiceLoader loader;

    private Map<IActionInfo, Future<WorkflowDescription>> workflowDescriptions = new HashMap<IActionInfo, Future<WorkflowDescription>>();

    @Override
    public void load(IActionInfo serviceInfo) {
        if (!workflowDescriptions.containsKey(serviceInfo)) {
            workflowDescriptions.put(serviceInfo, loader.loadWorkflowDescription(serviceInfo));
        }
    }

    /**
     * Checks if the details for the provided service info is ready.
     * 
     * @param serviceInfo
     *            the service to check
     * @return true if the details are ready, false otherwise
     */
    public boolean isWorkflowDescriptionReady(IActionInfo serviceInfo) {
        Future<WorkflowDescription> futureWorkflowDescription = workflowDescriptions.get(serviceInfo);
        if (futureWorkflowDescription == null) {
            return false;
        }

        return futureWorkflowDescription.isDone();
    }

    /**
     * Returns the service details if ready.
     * 
     * @param serviceInfo
     *            the service to get
     * @return details of the service
     */
    public WorkflowDescription getWorkflowDescription(IActionInfo serviceInfo) {

        Future<WorkflowDescription> futureWorkflowDescription = workflowDescriptions.get(serviceInfo);
        if (futureWorkflowDescription == null || !futureWorkflowDescription.isDone()) {
            return null;
        }

        try {
            return futureWorkflowDescription.get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Clears the service detail cache.
     */
    public void clear() {
        workflowDescriptions.clear();
    }
}
