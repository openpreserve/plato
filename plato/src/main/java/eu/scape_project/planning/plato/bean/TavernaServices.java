package eu.scape_project.planning.plato.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.services.action.IActionInfo;
import eu.scape_project.planning.services.taverna.TavernaServiceLoader;
import eu.scape_project.planning.services.taverna.model.WorkflowDescription;
import eu.scape_project.planning.utils.FacesMessages;

/**
 * Taverna service cache for loading details.
 */
@Dependent
public class TavernaServices implements Serializable, IServiceLoader {

    private static final long serialVersionUID = -65374305723742598L;

    @Inject
    private Logger log;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private TavernaServiceLoader loader;

    private Map<String, Future<WorkflowDescription>> workflowDescriptions = new HashMap<String, Future<WorkflowDescription>>();

    @Override
    public void load(IActionInfo serviceInfo) {
        if (!workflowDescriptions.containsKey(serviceInfo.getDescriptor())) {
            log.debug("Loading service [{}]", serviceInfo.getUrl());
            workflowDescriptions.put(serviceInfo.getDescriptor(),
                loader.loadWorkflowDescription(serviceInfo.getDescriptor()));
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
        Future<WorkflowDescription> futureWorkflowDescription = workflowDescriptions.get(serviceInfo.getDescriptor());
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
        if (serviceInfo == null) {
            return null;
        }
        Future<WorkflowDescription> futureWorkflowDescription = workflowDescriptions.get(serviceInfo.getDescriptor());
        if (futureWorkflowDescription == null || !futureWorkflowDescription.isDone()) {
            return null;
        }
        try {
            return futureWorkflowDescription.get();
        } catch (InterruptedException e) {
            log.warn("Loading of service [{}] interrupted.", serviceInfo.getUrl(), e);
            facesMessages.addWarning("Loading of service details interrupted");
        } catch (ExecutionException e) {
            log.warn("Loading of service [{}] failed.", serviceInfo.getUrl(), e);
            facesMessages.addWarning("Loading of service " + serviceInfo.getUrl() + " details failed");
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
