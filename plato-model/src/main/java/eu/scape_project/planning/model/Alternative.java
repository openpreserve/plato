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
package eu.scape_project.planning.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * Entity describing a preservation alternative. A preservation action can also be a preservation
 * action service, then {@link #action} is set.
 *
 * @author Hannes Kulovits
 */
@Entity
public class Alternative implements Serializable, ITouchable {

    private static final long serialVersionUID = -2995207877066383992L;

    @OneToOne(cascade = CascadeType.ALL)
    private Experiment experiment;

    @OneToOne(cascade = CascadeType.ALL)
    private ResourceDescription resourceDescription;

    @ManyToOne
    @JoinColumn(name="parent_id", insertable=false, updatable=false, nullable=false)
    private AlternativesDefinition alternativesDefinition;

    @NotNull
    @Length(min = 1, max = 30)
    private String name;

    /**
     * standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    
    @NotNull
    @Lob
    private String description;

    @Id
    @GeneratedValue
    private int id;

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    /**
     * An alternative might correspond to a preservation action service.
     */
    @OneToOne(cascade = CascadeType.ALL)
    private PreservationActionDefinition action;

    
    private boolean discarded = false;

    public boolean isExecutable() {
        return action != null && action.isExecutable();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Alternative() {

    }

    public Alternative(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public ResourceDescription getResourceDescription() {
        return resourceDescription;
    }

    public void setResourceDescription(ResourceDescription resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    /**
     * Sets the name of this alternative.
     * CAUTION:
     * Do NOT use this, if the alternative is already part of an alternative definition:
     * - if it is part of a stand alone alternative definition, use {@link AlternativesDefinition#renameAlternative(Alternative, String)} instead
     * - it the alternative definition is part of a plan, use {@link Plan#renameAlternative(Alternative, String)}} instead.
     * 
     * This is crucial, as integrity checks have to be performed, and other objects might refer to this alternative. 
     * 
     * @param name new name of this alternative
     */
    @Deprecated
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Creates an empty alternative.
     * @return {@link Alternative} object
     */
    public static Alternative createAlternative() {
        Alternative alt = new Alternative();
        alt.setResourceDescription(new ResourceDescription());
        alt.setExperiment(new Experiment());
        return alt;
    }

    public AlternativesDefinition getAlternativesDefinition() {
        return alternativesDefinition;
    }

    public void setAlternativesDefinition(
            AlternativesDefinition alternativesDefinition) {
        this.alternativesDefinition = alternativesDefinition;
    }

    public boolean isDiscarded() {
        return discarded;
    }

    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }
    
    public boolean getDiscarded() {
    	return this.discarded;
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

    public boolean isChanged(){
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
        // call handleChanges of all child elementss
        experiment.handleChanges(h);
        resourceDescription.handleChanges(h);
        // manually created alternatives obviously don't have related preservation actions
        if (action != null)
           action.handleChanges(h);
    }

    public PreservationActionDefinition getAction() {
        return action;
    }

    public void setAction(PreservationActionDefinition action) {
        this.action = action;
    }

    public static Alternative createAlternative (String uniqueName, PreservationActionDefinition action) {
        Alternative a = Alternative.createAlternative();
        
        ResourceDescription rd = new ResourceDescription();
        //rd.setConfigSettings(configSettings);
        
        a.setResourceDescription(rd);
        
        a.setName(uniqueName);
        // generate service description
        String descr = action.getInfo()+ " using service at: " + action.getUrl();
        
        if (action.getParameterInfo() != null && !"".equals(action.getParameterInfo())) {                    
            descr += "\n\n" + "Additional information about parameters: \n" + action.getParameterInfo();
        }
        
        a.setDescription(descr);
        a.setAction(action);
        
        return a;
    }    
}
