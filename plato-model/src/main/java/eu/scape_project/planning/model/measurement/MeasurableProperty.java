/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
package eu.scape_project.planning.model.measurement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.IChangesHandler;
import eu.scape_project.planning.model.ITouchable;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.values.INumericValue;

/**
 * denotes a property that can be measured (not necessarily in a fully automated
 * way!). A property has a name and a {@link Scale}
 * 
 * @author Christoph Becker
 * 
 */
@Entity
public class MeasurableProperty implements Comparable<MeasurableProperty>, ITouchable, Serializable {
    private static final long serialVersionUID = -6675251424999307492L;

    @Id
    @GeneratedValue
    private int id;

    private String propertyId;

    private String name;

    /**
     * Hibernate note: standard length for a string column is 255 validation is
     * broken because we use facelet templates (issue resolved in Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private CriterionCategory category;

    @Enumerated(EnumType.STRING)
    private EvaluationScope evaluationScope;

    @OneToOne(cascade = CascadeType.ALL)
    private Scale scale;

    /**
     * a list of all metrics that can be applied to this property
     */
    @Transient
    List<Metric> possibleMetrics = new ArrayList<Metric>();

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    public MeasurableProperty() {
    }

    public MeasurableProperty(Scale scale, String name) {
        this.scale = scale;
        this.name = name;
    }

    public void clear() {
        id = Integer.MAX_VALUE;
        propertyId = null;
        name = null;
        description = null;
        category = null;
        scale = null;
        possibleMetrics = null;
    }

    public boolean isNumeric() {
        if (scale == null) {
            return false;
        }
        return (scale.createValue() instanceof INumericValue);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public int compareTo(MeasurableProperty p) {
        return name.toLowerCase().compareTo(p.getName().toLowerCase());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public List<Metric> getPossibleMetrics() {
        return possibleMetrics;
    }

    public void setPossibleMetrics(List<Metric> possibleMetrics) {
        this.possibleMetrics = possibleMetrics;
    }

    public void addPossibleMetric(Metric possibleMetric) {
        this.possibleMetrics.add(possibleMetric);
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void handleChanges(IChangesHandler h) {
        h.visit(this);

    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    public void setChangeLog(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    public CriterionCategory getCategory() {
        return category;
    }

    public void setCategory(CriterionCategory category) {
        this.category = category;
    }

    /**
     * currently used by digester usage: setCategoryAsString("outcome:object")
     * 
     * @param category
     */
    public void setCategoryAsString(String category) {
        if ((category == null) || ("".equals(category))) {
            setCategory(null);
        } else {
            String cat[] = category.split(":");
            if (cat.length == 2) {
                setCategory(CriterionCategory.getType(cat[0], cat[1]));
            } else if (cat.length == 1) {
                setCategory(CriterionCategory.getType(cat[0], ""));
            } else {
                throw new IllegalArgumentException("invalid criterion category:" + category);
            }
        }
    }
    
    /**
     * Method allowing setting of evaluationScope via its string representation.
     * (This is currently used by MeasurementsDescriptorParser.setupDigester() function)
     * 
     * @param evalScopeString Evaluation scope string representation.
     */
    public void setEvaluationScopeAsString(String evalScopeString) {
        // only set evaluationScope if a meaningful string is passed
        if (!evalScopeString.isEmpty()) {
            evaluationScope = EvaluationScope.valueOf(evalScopeString);
        }
    }

    public EvaluationScope getEvaluationScope() {
        return evaluationScope;
    }

    public void setEvaluationScope(EvaluationScope evaluationScope) {
        this.evaluationScope = evaluationScope;
    }

    /*
    // equals method to compare equality not on instance level but on property level.
    // this method was intended for the reload of criteria - but was not needed on second thought.
    // maybe it can be useful at at later stage
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MeasurableProperty)) {
            return false;
        }

        MeasurableProperty otherMP = (MeasurableProperty) other;

        if (this.propertyId.equals(otherMP.propertyId) || this.name.equals(otherMP.name)
            || this.description.equals(otherMP.description) || this.category.equals(otherMP.category)
            || this.evaluationScope.equals(otherMP.evaluationScope) || this.scale.equals(otherMP.scale)
            || this.possibleMetrics.equals(otherMP.possibleMetrics)) {
            return true;
        }

        return false;
    }
    */
}
