package eu.scape_project.planning.plato.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import eu.scape_project.planning.services.action.IActionInfo;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

/**
 * Data model for action infos.
 */
public class ServiceInfoDataModel extends ExtendedDataModel<IActionInfo> implements Serializable {

    private static final long serialVersionUID = -31472856376172968L;

    private Integer rowKey;

    private List<IActionInfo> serviceInfos;

    private Map<String, IServiceLoader> serviceLoaders;

    /**
     * Creates a new service info data model.
     * 
     * @param serviceInfos
     *            list of service infos
     * @param serviceLoaders
     *            map of service identifiers and their loaders
     */
    public ServiceInfoDataModel(List<IActionInfo> serviceInfos, Map<String, IServiceLoader> serviceLoaders) {
        this.serviceInfos = serviceInfos;
        this.serviceLoaders = serviceLoaders;
    }

    @Override
    public void setRowKey(Object key) {
        rowKey = (Integer) key;
    }

    @Override
    public Object getRowKey() {
        return rowKey;
    }

    @Override
    public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) {
        int firstRow = ((SequenceRange) range).getFirstRow();

        for (int i = firstRow; (i < serviceInfos.size()) && (i < (firstRow + ((SequenceRange) range).getRows())); i++) {
            IServiceLoader serviceLoader = serviceLoaders.get(serviceInfos.get(i).getServiceIdentifier());
            if (serviceLoader != null) {
                serviceLoader.load(serviceInfos.get(i));
            }
            visitor.process(context, i, argument);
        }
    }

    @Override
    public boolean isRowAvailable() {
        return rowKey != null;
    }

    @Override
    public int getRowCount() {
        return serviceInfos.size();
    }

    @Override
    public IActionInfo getRowData() {
        return serviceInfos.get(rowKey);
    }

    @Override
    public int getRowIndex() {
        // Used for backwards compatibility with JSF 1
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRowIndex(int rowIndex) {
        // Used for backwards compatibility with JSF 1
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getWrappedData() {
        // Used for backwards compatibility with JSF 1
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWrappedData(Object data) {
        // Used for backwards compatibility with JSF 1
        throw new UnsupportedOperationException();
    }
}
