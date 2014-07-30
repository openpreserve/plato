/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.plato.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;

import eu.scape_project.planning.services.IServiceInfo;

/**
 * Data model for service infos.
 */
public class ServiceInfoDataModel extends ExtendedDataModel<IServiceInfo> implements Serializable {

    private static final long serialVersionUID = -31472856376172968L;

    private Integer rowKey;

    private List<IServiceInfo> serviceInfos;

    private Map<String, IServiceLoader> serviceLoaders;

    /**
     * Creates a new service info data model.
     * 
     * @param serviceInfos
     *            list of service infos
     * @param serviceLoaders
     *            map of service identifiers and their loaders
     */
    public ServiceInfoDataModel(List<IServiceInfo> serviceInfos, Map<String, IServiceLoader> serviceLoaders) {
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
    public IServiceInfo getRowData() {
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
