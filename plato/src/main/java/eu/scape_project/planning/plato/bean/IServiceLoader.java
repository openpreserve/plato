package eu.scape_project.planning.plato.bean;

import eu.scape_project.planning.services.IServiceInfo;

/**
 * Service loader that, given a service info, loads further details about the
 * service.
 */
public interface IServiceLoader {

    /**
     * Load details about the service.
     * 
     * @param serviceInfo
     *            service info about the service to load
     */
    void load(IServiceInfo serviceInfo);
}
