package eu.scape_project.planning.plato.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;

import eu.scape_project.planning.model.interfaces.actions.IPreservationActionInfo;
import eu.scape_project.planning.plato.wfview.full.DefineAlternativesView;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

public class WorkflowInfoData extends ExtendedDataModel<IPreservationActionInfo> implements Serializable {

    private static final long serialVersionUID = -31472856376172968L;

    private Integer rowKey;

    private List<IPreservationActionInfo> actionInfos;

    private DefineAlternativesView serviceLoader;

    public WorkflowInfoData(DefineAlternativesView serviceLoader, List<IPreservationActionInfo> actionInfos) {
        this.actionInfos = actionInfos;
        this.serviceLoader = serviceLoader;
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

        for (int i = firstRow; (i < actionInfos.size()) && (i < (firstRow + ((SequenceRange) range).getRows())); i++) {
            serviceLoader.loadWorkflowDescription(actionInfos.get(i));
            visitor.process(context, i, argument);
        }
    }

    @Override
    public boolean isRowAvailable() {
        return rowKey != null;
    }

    @Override
    public int getRowCount() {
        return actionInfos.size();
    }

    @Override
    public IPreservationActionInfo getRowData() {
        return actionInfos.get(rowKey);
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
