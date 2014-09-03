/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,  
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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model.transform;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.IChangesHandler;
import eu.scape_project.planning.model.ITouchable;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.values.INumericValue;
import eu.scape_project.planning.model.values.IOrdinalValue;
import eu.scape_project.planning.model.values.TargetValues;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.validation.ValidationError;

/**
 * Implements basic transformation functionality, i.e. aggregation over {@link Values} and
 * common properties of transformers.
 * @author Hannes Kulovits
 */
@Entity
@Inheritance
@DiscriminatorColumn(name = "type")
public abstract class Transformer implements ITransformer, Serializable, ITouchable
{
    private static final long serialVersionUID = -3708795251848706848L;

    @Id
    @GeneratedValue
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    /**
     * Transforms all the values in the list of the provided {@link Values}.
     * According to the type of each {@link Value}, either
     * {@link ITransformer#transform(INumericValue)} or {@link ITransformer#transform(IOrdinalValue)}
     * is called.
     * @param values List of values to be transformed
     * @return {@link TargetValues}, which contains a list of all transformed values corresponding to the provided input
     */
    public TargetValues transformValues(Values values) {
        TargetValues result = new TargetValues();
        for (Value v : values.getList()) {
            if (v instanceof INumericValue) {
                result.add(transform((INumericValue) v));
            } else {
                result.add(transform((IOrdinalValue) v));
            }
        }
        return result;
    }
    
    public ChangeLog getChangeLog() {
        return this.changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }
    
    public void touch(String username) {
        getChangeLog().touch(username);
    }
    
    public void touch() {
        getChangeLog().touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h){
        h.visit(this);
    }
    /**
     * If this Transformer is not correctly configured, this method adds
     * an appropriate error-message to the given list and returns false.
     *
     * @return true if this transformer is correctly configured
     */
    public abstract boolean isTransformable(List<ValidationError> errors);

    public abstract Transformer clone(); 
}
