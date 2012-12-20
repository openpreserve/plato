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
package eu.scape_project.planning.model.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.EvaluationStatus;
import eu.scape_project.planning.model.IChangesHandler;
import eu.scape_project.planning.model.ITouchable;
import eu.scape_project.planning.model.SampleAggregationMode;
import eu.scape_project.planning.model.TargetValueObject;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.scales.FloatRangeScale;
import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.IntRangeScale;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.PositiveIntegerScale;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.scales.ScaleType;
import eu.scape_project.planning.model.scales.YanScale;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.transform.Transformer;
import eu.scape_project.planning.model.values.FreeStringValue;
import eu.scape_project.planning.model.values.INumericValue;
import eu.scape_project.planning.model.values.IOrdinalValue;
import eu.scape_project.planning.model.values.TargetValue;
import eu.scape_project.planning.model.values.TargetValues;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.validation.ValidationError;

/**
 * A leaf node in the objective tree does not contain any children,
 * but instead defines the actual measurement scale to be used and points
 * to conforming valueMap. Part of the implementation of the Composite
 * design pattern, cf. TreeNode, Node - Leaf corresponds to the
 * <code>Leaf</code>, surprise!
 * @author Christoph Becker
 */
@Entity
@NamedQuery(
    name="getLaevesById",
    query="SELECT l from Leaf l WHERE id IN (:leafList)"
)
@DiscriminatorValue("L")
public class Leaf extends TreeNode {

    private static final long serialVersionUID = -6561945098296876384L;
    
    private static final Logger log = LoggerFactory.getLogger(Leaf.class);

    /**
     * The {@link Transformer} stores the user-set transformation rules.
     * There are two types:
     * <ul>
     * <li>numeric transformation (thresholds) </li>
     * <li>ordinal transformation: direct mapping from values to numeric
     * values. This also applies to boolean scales. </li>
     */
    @OneToOne(cascade = CascadeType.ALL)
    private Transformer transformer;


    /**
     * determines the aggregation mode for the values of the sample records(!)
     * WITHIN one alternative. The overall aggregation method over the tree is a
     * different beer!
     * Is initialised with {@link SampleAggregationMode#WORST}, but later initialised
     * according to the {@link Scale} in {@link #setDefaultAggregation()}
     */
    @Enumerated
    private SampleAggregationMode aggregationMode = SampleAggregationMode.WORST;

    /**
     * specifies the {@link Scale} to be used for evaluating experiment
     * outcomes
     */
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private Scale scale;

    /**
     * We have values actually per
     * <ul>
     * <li> preservation strategy ({@link Alternative}),</li>
     * <li> decision criteria (leaf node), AND </li>
     * <li> sample record.</li>
     * </ul>
     * So we have another encapsulation: {@link Values}
     *
     * Note: For some databases it might be necessary to rename the key member of Map, 
     *       as it might be a reserved keyword, e.g.: Derby
     */
//    @IndexColumn(name = "key_name")
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
    private Map<String, Values> valueMap = new HashMap<String, Values>();

    /**
     * The measure this decision criterion is mapped to.
     * 
     * Note that orphanRemoval does not work on OneToOne relationships 
     * if the orphan is replaced by a new entity ({@link https://hibernate.onjira.com/browse/HHH-6484}  
     * If you want to do so, you have to take care of deleting the orphan yourself
     * 
     */
    @OneToOne(cascade=CascadeType.ALL, orphanRemoval=true)
    private Measure measure;
    
    public Map<String, Values> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, Values> v) {
        this.valueMap = v;
    }

    /**
     * @return the <b>unweighted</b> result value for an Alternative. This is the aggregation of
     *         all transformed evaluation values
     * @see #aggregateValues(TargetValues)
     * @see #transformValues(Alternative)
     */
    public double getResult(Alternative a) {
        return aggregateValues(transformValues(a));
    }

    /**
     * Aggregates values of one Alternative, depending on the {@link #aggregationMode}
     * @param values the TargetValue element over which aggregation shall be
     * performed according to the {@link #aggregationMode}
     * @return a single number denoting the aggregated, transformed, unweighted
     * result value of this Leaf.
     */
    private double aggregateValues(TargetValues values) {
        if (aggregationMode == SampleAggregationMode.WORST) {
            return values.worst();
        } else {
            return values.average();
        }
    }

    /**
     * Returns the {@link TargetValues evaluation values} for each SampleObject for one {@link Alternative}
     * already transformed from the measurement scale to the final scale used for ranking.
     *
     * @see #getResult(Alternative)
     * @param a the {@link Alternative} for which evaluation values shall be returned
     * @return {@link TargetValues}
     */
    public TargetValues transformValues(Alternative a) {
        Values v = valueMap.get(a.getName());
        if (transformer == null) {
            log.error("transformer is null!");
        }
        return transformer.transformValues(v);
    }

    public Leaf() {
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    public void setValues(String alternative, Values values) {
        valueMap.put(alternative, values);
    }

    public Values getValues(String alternative) {
        return valueMap.get(alternative);
    }


    public Scale getScale() {
        return scale;
    }

    /**
     * The standard setter sets the scale of the leaf to the given instance <code>scale</code>,
     * but leaves {@link #transformer} and {@link #aggregationMode} unchanged.
     *
     * <b>Important: If you want to change the type of the scale, e.g. from Boolean to Numeric,
     * you have to take transformation settings and aggregation mode into account.
     *  Thus you need to use {@link #changeScale(Scale)} instead, which also takes care
     *  of the transformer and aggregationMode.</b>
     *
     * @param scale
     */
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    /**
     * When a scale is changed e.g. from Boolean to a number,
     * all evaluation values that have already been associated become
     * invalid and need to be removed.
     *
     * This function resets all evaluation {@link Values} associated with
     * this Leaf, which depend on the {@link Scale} that is set.
     * This means that if the scale is not set, all Values are removed.
     * If the scale is set, we iterate into all values for all alternatives
     * and samplerecords and check if the scale in there differs from the
     * scale that has been set. If yes, we remove the values.
     * Furthermore, if this Leaf has been changed from an Object criterion
     * to an Action criterion, all excess values are removed.
     */
    public void resetValues(List<Alternative> list) {
        if (scale == null) {
            /*
             * there is no scaletype set, so we remove existing values
             */
            valueMap.clear();
            return;
        }
        // Get the Values for each Alternative
        for (Alternative a : list) {
            Values values = valueMap.get(a.getName());
            if (values == null) {
                log.debug("values is null for alternative "+ a.getName()+ " in Leaf "+name);
                continue;
            }
            // Check value of each sample object for conformance with Scale -
            // if we find a changed scale, we reset everything.
            // It might be faster not to check ALL values, but this is safer.
            for (Value value : values.getList()) {
                // If the scale has changed, we reset all evaluation values of this Alternative:
                // this may look strange, but it is OK that the scale of a value is null.
                // If there have been values before, you change the scale and then save - the linkage is lost                
                // if (value.getScale() == null) {
                //      LogFactory.getLog(Leaf.class).error("WHAT THE...?? no scale for value"+getName());
                // } else {
                    if ((value.getScale() == null) ||
                        (!value.getScale().getClass().equals(scale.getClass())) ) {
                        if (!a.isDiscarded()) { // for discarded alternatives, that's ok.
                            log.debug(
                                    "Leaf "+this.getName()+" Class: " + value.getClass() + " not like "
                                            + scale.getClass()+". RESETTING the valuemap now!");
                            valueMap.clear(); // reset all values
                            return;
                        }
                    }
                // }
                // PLEASE NOTE- WRT ORDINAL RESTRICTIONS:
                // we do NOT reset values when the restriction has changed, such as 
                // the ordinal values or the boundaries.
                // Instead, those values that are still valid remain, the others will be checked
                // and need to be corrected anyway in the evaluate step.
                // Should be nicer for the user. If we find out this leads to validation problems
                // (which shouldnt be the case because the data types are valid as long as the scale
                // doesnt change) then we will reset the values even if just the restriction changes.
            }
            /*
             * maybe this leaf was set to single, reset all values
             */
            if (isSingle() && values.size() > 1) {
                valueMap.clear();
                return;
            }
        }
    }

    /**
     * Sets a default transformer corresponding to the current scale of this
     * leaf. The transformer is initialized with default-values.
     *
     * If no scale is set, the current transformer will be set to null!
     */
    public void setDefaultTransformer() {
        if (scale == null) {
            log.warn(
                    "Can't set DefaultTransformer, no scale set!");
            this.setTransformer(null);
            return;
        }
        if (ScaleType.ordinal.equals(scale.getType())) {
            OrdinalTransformer t = new OrdinalTransformer();
            this.setTransformer(t);
            if (!(scale instanceof FreeStringScale)) {
                Map<String, TargetValueObject> map = t.getMapping();
                OrdinalScale o = (OrdinalScale) scale;
                for (String s : o.getList()) {
                    map.put(s, new TargetValueObject());
                }
            }
        } else {
            NumericTransformer t = new NumericTransformer();
            this.setTransformer(t);
        }
    }

    /**
     * Returns the fully qualified class-name ("canonical name") of the current scale
     * @return the canonical classname of the scale, or null if no scale is set
     */
    public String getScaleByClassName() {
        if (scale == null)
            return null;
        else
            return scale.getClass().getCanonicalName();
    }

    /**
     * Sets the Scale according to the provided name, IF the name differs from the
     * classname of the currently set {@link #scale}
     * 
     * resets property mappings, if present.
     * 
     * @param className canonical class name of the new scale
     */
    public void setScaleByClassName(String className) {
        Scale scaleType = null;
        try {
            if (className != null && !"".equals(className)) {
                scaleType = (Scale) Class.forName(className).newInstance();
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        }
        changeScale(scaleType);
    }

    /**
     * Changes the {@link Scale} to the provided one.
     * if the new scale differs from the type of the current scale,
     * it also:
     * <ul>
     *     <li>sets: default aggregators and transformers.</li>
     * </ul>
     * It does not set a reference to the provided scale, but clones it instead!
     * @param newScale the new Scale to be set
     */
    public void changeScale(Scale newScale) {
        if (newScale == null) {
            log.debug("CHECK THIS: setting scale to null.");
            scale = null;
            // remove mapping
            setMeasure(null);
        } else {
            // If
            if ((this.scale == null) //we don't have a scale yet
               || (!scale.getClass().getName().equals(newScale.getClass().getName())))
                // the new scale is not the same as ours
            {
                // a new scale was chosen, remove mapping
                setMeasure(null);//new Criterion());

                setScale(newScale.clone());
                setDefaultAggregation();
                
                if (scale != null) {
                    setDefaultTransformer();
                }
            }
        }
    }

    /**
     * is used to adjust the scale of this leaf to its mapping
     * - the type of the new scale has already been checked, mapping information is not discarded.
     * - a new scale is created, even the types of the current and the new Scale match 
     *   (to get clean aggregation and transformer values)
     * 
     * @param newScale
     */
    public void adjustScale(Scale newScale) {
        if (newScale == null) {
            log.debug("CHECK THIS: try to setg scale to null due to measurement info: this should NOT happen at all.");
        } else {
            if ((this.scale == null) //we don't have a scale yet
               || (!scale.getClass().getName().equals(newScale.getClass().getName())))
                // the new scale is not the same as ours
            {
                setScale(newScale.clone());
                setDefaultAggregation();
                if (scale != null) {
                    setDefaultTransformer();
                }
            }
        }
    }

    /**
     * sets the {@link #aggregationMode} depending on {@link #scale}.
     * For all ordinal scales we set it to using the worst result,
     * and for numeric scales we use the average result
     * @see SampleAggregationMode
     */
    private void setDefaultAggregation() {
        if (scale instanceof OrdinalScale) {
            setAggregationMode(SampleAggregationMode.WORST);
        } else { // numeric
            setAggregationMode(SampleAggregationMode.AVERAGE);
        }
    }

    @Override
    /**
     * This is a leaf, so: YES, I am.
     * @return true
     */
    public boolean isLeaf() {
        return true;
    }

    public SampleAggregationMode getAggregationMode() {
        return aggregationMode;
    }

    public void setAggregationMode(SampleAggregationMode aggregationMode) {
        this.aggregationMode = aggregationMode;
    }

    /**
     * unused at the moment.
     * TODO checking the size of the valuemap is not enough.
     */
    public EvaluationStatus getEvaluationStatus() {
        return (valueMap.size() > 0) ? EvaluationStatus.COMPLETE
                : EvaluationStatus.NONE;
    }

    /**
     * Unused at the moment.
     * @return the transformation status.
     * TODO checking transformer for null state is NOT enough
     */
    public EvaluationStatus getTransformationStatus() {
        return (transformer != null) ? EvaluationStatus.COMPLETE
                : EvaluationStatus.NONE;
    }

    /**
     * removes associated evaluation {@link Values} for a given list of alternatives
     * and a give record index.
     * @param list list of Alternatives for which values shall be removed
     * @param record index of the record for which  values shall be removed
     */
    public void removeValues(List<Alternative> list, int record) {
        for (Alternative a : list) {
            Values v = getValues(a.getName());
            // maybe this alternative has no values at all - e.g. because it was just created
            if ((v != null)  // there is a Values object
                && (v.getList().size() > record) // there can be a value for this sample record
                && (v.getList().get(record) != null)) { // there is a value
                log.debug("removing values:: "+getName()+" ,"+record+", "+a.getName());
                v.getList().remove(record);
            }
        }
    }
    
    /**
     * The value map is properly initialized if its size equals the number of alternatives and the 
     * number of values equals the number of records. 
     * 
     * @return true if value map is properly initialized
     */
    @Override
    public boolean isValueMapProperlyInitialized(List<Alternative> alternatives, int numberRecords) {
        if (valueMap.size() != alternatives.size()) {
            return false;
        }
        
        for (Alternative a : alternatives) {
            if (!valueMap.keySet().contains(a.getName())) {
                return false;
            }
        }
        
        for (String a : valueMap.keySet()) {
            if (!isSingle() && valueMap.get(a).size() != numberRecords) {
                return false;
            } else if (isSingle() && valueMap.get(a).size() != 1) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Creates empty Values for all Alternatives and SampleRecords as provided
     * in the parameters, PLUS ensures that values are linked to scales if the
     * parameter addLinkage is true
     *
     * An assumption here is that other methods take care of removing values when
     * removing records ({@link #removeValues(List, int)}),
     * and of resetting values when changing scales and from object
     * to action criterion. ({@link #resetValues()})
     * These methods need to be called when manipulating the object model.
     *
     * @param list of Alternatives
     * @param records The number of records determines how many {@link Values} are
     * created and associated for every {@link Alternative}
     * @param addLinkage If true, ensure that values are linked to scales
     * by calling {@link #initScaleValueLinkage(List, int)}
     */
    public void initValues(List<Alternative> list, int records,
            boolean addLinkage) {
        /** maybe we have not completed the step identify requirements yet -
         * so there might be no scales! **/
        if (scale == null)
            return;
        for (Alternative a : list) {
            // for every Alternative we get the container of the values of each sample object
            // from the map
            Values v = valueMap.get(a.getName());

            // If it doesnt exist, we create it and link it in the map
            if (v == null) {
                v = new Values();
                valueMap.put(a.getName(), v);
                // it the valueMap has just been created and the leaf is single,
                // we need to add one value.
                if (isSingle()) {
                    v.add(scale.createValue());
                }
            }

            // 20090217, hotfix CB: if a Leaf is set to SINGLE *after* initValues has been called,
            // the Value object at position 0 of the ValueS object might not be properly initialised.
            // Check and initialise if needed:
            if (isSingle()) {
                if (v.size() == 0) {
                    log.warn("adding value to a SINGLE LEAF WITH A VALUES OBJECT WITHOUT A PROPER VALUE:" + getName());
                    v.getList().add(scale.createValue());
                } else {
                    if (v.getValue(0) == null) {
                        log.warn("adding value to a SINGLE LEAF WITH A VALUES OBJECT WITHOUT A PROPER VALUE:" + getName());
                        v.setValue(0,scale.createValue());
                    }
                }
            }
            // end hotfix 20090217
            
            // So we can be sure now that we have a value container and
            // that it is linked and that for Action criteria, i.e. single
            // values, we have the one value.
            // For Object criteria we have to be sure that the number of values
            // corresponds to the number of sample objects, so we fill the list up
            if (!isSingle()) {
                // this is to add MISSING values for records.
                // it doesnt make a difference for this condition
                // whether we just created a new valuemap or are
                // refilling an existing one

                // Note that the index here starts at the size of the values array
                // and runs to the total number of records.
                // so if we have enough - nothing happens; if some are missing, they are
                // added at the end
                for (int i = v.size(); i < records; i++) {
                    v.add(scale.createValue());
                }
            }
        }
        if (addLinkage) {
            initScaleValueLinkage(list, records);
        }
    }

    /**
     * ensures that values are linked to scales by setting all of them
     * explicitly. We need that especially for export/import
     *
     * @param list List of Alternatives over which to iterate
     * @param records denotes the number of records for the iteration
     */
    public void initScaleValueLinkage(List<Alternative> list, int records) {
        for (Alternative a : list) {
            Values v = valueMap.get(a.getName());
            if (v == null) {
                throw new IllegalStateException("initScaleLinkage called,"
                        + " but the valueMap is still empty - that's a bug."
                        + " Leaf:" + getName());
            }
            if (isSingle()) {
                v.getValue(0).setScale(scale);
            } else {
                for (int i = 0; i < records; i++) {
                    v.getValue(i).setScale(scale);
                }
            }
        }
    }

    /**
     * Checks if the Scale of this Leaf is existent and correctly specified.
     * To achieve this, it calls {@link Scale#isCorrectlySpecified(String, List)}
     * if there is a scale, or returns false otherwise.
     * @see TreeNode#isCompletelySpecified(List<ValidationError>)
     * @see Scale#isCorrectlySpecified(String, List)
     */
    @Override
    public boolean isCompletelySpecified(List<ValidationError> errors) {
        if (this.scale == null) {
        	errors.add(new ValidationError("Leaf " + this.getName() + " has no scale", this));
            return false;
        }
        if (scale instanceof YanScale) {
        	errors.add(new ValidationError("Criterion "+getName()+" is associated with a 'Yes/Acceptable/No' scale, which is discouraged. We recommend to refine the criterion to be as objective as possible.", this));
        }
        return this.scale.isCorrectlySpecified(this.getName(), errors);
    }

    /**
     * Checks if this Leaf is completely evaluated, i.e. we have correct
     * values for all Alternatives and samples.
     * For this means we need to iterate over all alternatives and check
     * all values. This is done by calling {@link Scale#isEvaluated(Value)}
     * @param alternatives the list of Alternatives over which to iterate when checking
     * for evaluation values
     * @param errorMessages This is the <b>list of messages</b> where we add a message about this Leaf in case validation
     * fails, i.e. it is not completely evaluated.
     * @see eu.scape_project.planning.model.tree.TreeNode#isCompletelyEvaluated(List, List)
     * @see Scale#isEvaluated(Value)
     */
    @Override
    public boolean isCompletelyEvaluated(List<Alternative> alternatives,
            List<ValidationError> errors) {
        boolean validates = true;
        log.debug("checking complete evaluation for leaf " +getName());
        for (Alternative a : alternatives) {
            Values values = valueMap.get(a.getName());
            log.debug("checking values for "+a.getName());
            if (this.isSingle()) {
                if (values.size() < 1) {
                    log.warn(
                            "Not Enough Value Objects in Values");
                    validates = false;
                } else {
                    if (!scale.isEvaluated(values.getValue(0))) {
                        validates = false;
                    }
                }
            } else {
                int i = 0;
                for (Value value : values.getList()) {
                    log.debug("checking value for "+(i));
                    if (!scale.isEvaluated(value)) {
                        validates = false;
                        break;
                    }
                    i++;
                }
            }
        }
        if (!validates) {
            // I add an error message to the list, and myself to the list of error nodes
        	errors.add(new ValidationError("Leaf " + this.getName() + " is not properly evaluated", this));
        }
        return validates;
    }

    /**
     * Checks if the transformation settings for this Leaf are complete and correct.
     * @see Transformer#isTransformable(List)
     * @see TreeNode#isCompletelyTransformed(List)
     */
    @Override
    public boolean isCompletelyTransformed(List<ValidationError> errors) {
        if (this.transformer == null) {
        	errors.add(new ValidationError("Leaf " + this.getName()+" is not properly transformed", this));
            log.error("Transformer is NULL in Leaf "+getParent().getName()+" > "+getName());
            return false;
        }
        if (!this.transformer.isTransformable(errors) || !this.transformer.isChanged()) {
        	errors.add(new ValidationError("Leaf " + this.getName()+" is not properly transformed", this));
            return false;
        }
        return true;
    }

    @Override
    /**
     * Checks if the weight is in [0,1].
     * @see Node#isCorrecltlyWeighted(List<String>)
     */
    public boolean isCorrectlyWeighted(List<ValidationError> errors) {
        // A leaf is always weighted correctly as long as its weight is in [0,1]
        if (this.weight >= 0 && this.weight <= 1) {
            return true;
        }
        errors.add(new ValidationError("Leaf " + this.getName() + " has an illegal weight (" + this.weight + ")", this));
        return false;
    }


    @Override
    /**
     * Returns a clone of this Leaf. Includes: <ul>
     * <li>{@link Scale}</li>
     * <li>{@link AggregationMode}</li>
     * <li>{@link ValueMap} which is initialised, but not cloned</li>
     * </ul>
     * Excludes transformer! The transformer is set to <code>null</code>
     */
    public TreeNode clone() {
        Leaf clone = (Leaf) super.clone();
        if (this.getScale() != null) {
            clone.setScale(this.getScale().clone());
        }
        clone.setValueMap(new HashMap<String, Values>());
        
        Transformer newTransformer = null;
        if (transformer != null) {
            newTransformer = transformer.clone();
        }
        clone.setTransformer(newTransformer);
        clone.setAggregationMode(this.getAggregationMode());
        if (measure != null) {
            clone.setMeasure(new Measure(measure));
        }
        return clone;
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
      public void handleChanges(IChangesHandler h) {
        super.handleChanges(h);

        // call handleChanges of all properties
        if (scale != null) {
            scale.handleChanges(h);
        }
        if (transformer != null) {
            transformer.handleChanges(h);
        }
        if (measure != null) {
            measure.handleChanges(h);
        }

    }

    @Transient
    public boolean isMapped() {
        return (measure != null);
    }

    /**
     * this method updates the value map, changing the name of the alternative to the new one.
     * @param oldName old name to be updated
     * @param newName new name to be used instead of oldName
     */
    public void updateAlternativeName(String oldName, String newName) {
        if (valueMap.containsKey(oldName))
            valueMap.put(newName, valueMap.remove(oldName));
        
        /*
         for (String name: valueMap.keySet()) {
            if (name.equals(oldName)) {
                valueMap.put(newName, valueMap.get(oldName));
                valueMap.remove(oldName);
            }
        }
        */       
        
    }

    /**
     * <ul>
     * <li>
     * removes all {@link Values} from the {@link #valueMap} which are not mapped by one of the 
     * names provided in the list
     * </li>
     * <li>
     * removes all {@link Value} objects in the {@link Values} which are out of the index of 
     * the sample records (which should not happen, but apparently we have some projects where this
     * is the case), or where a leaf is single and there is more than one {@link Value}
     * </li>
     * </ul>
     * @param alternatives list of names of alternatives
     * @return number of {@link Values} objects removed
     */
    public int removeLooseValues(List<String> alternatives, int records) {
        int number = 0;
        Iterator<String> it =  valueMap.keySet().iterator();
        List<String> namesToRemove = new ArrayList<String>();
        while (it.hasNext()) {
            String altName = it.next();
            if (!alternatives.contains(altName)) {
                log.warn("removing Values for "+altName+" at leaf "
                        +getName());
                namesToRemove.add(altName);
                number++;
            } else {
                Values v = valueMap.get(altName);
                int removed  = v.removeLooseValues(isSingle() ? 1 : records);
                log.warn("removed "+removed+" Value objects " +
                                "for "+altName+" at leaf "+getName());
                number += removed;
            }
        }
        for (String s: namesToRemove) { 
            valueMap.remove(s);
        }
        return number;
    }
    
    public void normalizeWeights(boolean recoursive) {
        // this is a leaf which means there are no children 
        // and therefore there is nothing to do
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }
    
    /**
     * initialises the ordinal transformer for free text scales
     * AND has a side effect: textual values in free text scales
     * with equalsIgnoreCase=true to an existing mapping are changed
     * to the case of the mapping string!

     */
    public void initTransformer() {
        initTransformer(null);
    }
    
    /**
     * initialises the ordinal transformer for free text scales, @see #initTransformer()
     * @param defaultTarget if this is used (must be 0.0<=defaultTarget<=5.0, unchecked)
     * then for each newly added mapping, the default target is set as provided.
     */
    public void initTransformer(Double defaultTarget) {

        if (scale instanceof FreeStringScale) {
            FreeStringScale freeScale = (FreeStringScale) scale;
            // We collect all distinct actually EXISTING values
            OrdinalTransformer t = (OrdinalTransformer) transformer;
            Map<String, TargetValueObject> map = t.getMapping();

            HashSet<String> allValues = new HashSet<String>();
            for (Values values: valueMap.values()) {
                for (Value v : values.getList()) {
                    FreeStringValue text = (FreeStringValue) v;
                    if (!text.toString().equals("")) {
                        for (String s: map.keySet()) {
                            // if the value is NOT the same, but IS the same with other case, 
                            // we replace the value with the cases predefined by the mapping
                            if (text.getValue().equalsIgnoreCase(s) && !text.getValue().equals(s)) {
                                text.setValue(s);
                            }
                        }
                        allValues.add(text.getValue());
                    }
                }
            }
            
            // We remove all values from the transformer that do not actually occur (anymore)
            // I am disabling this for now - why would we want to remove known mappings?
            // They don't do harm because for the lookup, we use the actually encountered values
            // (see below)
//            HashSet<String> keysToRemove = new HashSet<String>(); 
//           for (String s: map.keySet()) {
//               if (!allValues.contains(s)) {
//                   keysToRemove.add(s);
//               }
//           }
//           for (String s: keysToRemove) {
//               map.remove(s);
//           }
           
            // We add all values that occur, but dont are not in the map yet:
            for (String s: allValues) {
                if (!map.containsKey(s)) {
                    if (defaultTarget == null) {
                        map.put(s, new TargetValueObject());
                    } else {
                        map.put(s, new TargetValueObject(defaultTarget.doubleValue()));
                    }
                }
            }      
            
            // We also have to publish the known values
            // to the SCALE because it provides the reference lookup
            // for iterating and defining the transformation
            freeScale.setPossibleValues(allValues);
        }
    }
    
    /**
     * Method responsible for assessing the potential output range of this requirement.
     * Calculation rule:
     * if (minPossibleTransformedValue == 0) koFactor = 1; else koFactor = 0;
     * potentialOutputRange = relativeWeight * (maxPossibleTransformedValue - minPossibleTransformedValue) + koFactor;
     * 
     * @return potential output range. 
     *         If the plan is not yet at a evaluation stage where potential output range can be calculated 0 is returned.
     */
    public double getPotentialOutputRange() {
        // If the plan is not yet at a evaluation stage where potential output range can be calculated - return 0.
        if (transformer == null) {
            return 0;
        }
        
        double outputLowerBound = 10;
        double outputUpperBound = -10;
        
        // Check OrdinalTransformer
        if (transformer instanceof OrdinalTransformer) {
            OrdinalTransformer ot = (OrdinalTransformer) transformer;
            Map<String, TargetValueObject> otMapping = ot.getMapping();

            // set upper- and lower-bound  
            for(TargetValueObject tv : otMapping.values()) {
                if (tv.getValue() > outputUpperBound) {
                    outputUpperBound = tv.getValue();
                }
                if (tv.getValue() < outputLowerBound) {
                    outputLowerBound = tv.getValue();
                }
            }
        }
        
        // Check OrdinalTransformer
        if (transformer instanceof NumericTransformer) {
            // I have to identify the scale bounds before I can calculate the output bounds.   
            double scaleLowerBound = Double.MIN_VALUE;
            double scaleUpperBound = Double.MAX_VALUE;

            // At Positive Scales lowerBound is 0, upperBound has to be fetched
            if (scale instanceof PositiveIntegerScale) {
                PositiveIntegerScale s = (PositiveIntegerScale) scale;
                scaleLowerBound = 0;
                scaleUpperBound = s.getUpperBound();
            }
            if (scale instanceof PositiveFloatScale) {
                PositiveFloatScale s = (PositiveFloatScale) scale;
                scaleLowerBound = 0;
                scaleUpperBound = s.getUpperBound();                
            }
            
            // At Range Scales lowerBound and upperBound have to be fetched
            if (scale instanceof IntRangeScale) {
                IntRangeScale s = (IntRangeScale) scale;
                scaleLowerBound = s.getLowerBound();
                scaleUpperBound = s.getUpperBound();
            }
            if (scale instanceof FloatRangeScale) {
                FloatRangeScale s = (FloatRangeScale) scale;
                scaleLowerBound = s.getLowerBound();
                scaleUpperBound = s.getUpperBound();
            }

            // get Transformer thresholds
            NumericTransformer nt = (NumericTransformer) transformer;
            double transformerT1 = nt.getThreshold1();
            double transformerT2 = nt.getThreshold2();
            double transformerT3 = nt.getThreshold3();
            double transformerT4 = nt.getThreshold4();
            double transformerT5 = nt.getThreshold5();
            
            // calculate output bounds
            // increasing thresholds
            if (transformerT1 <= transformerT5) {
                // lower bound
                if (scaleLowerBound < transformerT1) {
                    outputLowerBound = 0;
                }
                else if (scaleLowerBound < transformerT2) {
                    outputLowerBound = 1;
                }
                else if (scaleLowerBound < transformerT3) {
                    outputLowerBound = 2;
                }
                else if (scaleLowerBound < transformerT4) {
                    outputLowerBound = 3;
                }
                else if (scaleLowerBound < transformerT5) {
                    outputLowerBound = 4;
                }
                else {
                    outputLowerBound = 5;
                }
                
                // upper bound
                if (scaleUpperBound < transformerT1) {
                    outputUpperBound = 0;
                }
                else if (scaleUpperBound < transformerT2) {
                    outputUpperBound = 1;
                }
                else if (scaleUpperBound < transformerT3) {
                    outputUpperBound = 2;
                }
                else if (scaleUpperBound < transformerT4) {
                    outputUpperBound = 3;
                }
                else if (scaleUpperBound < transformerT5) {
                    outputUpperBound = 4;
                }
                else {
                    outputUpperBound = 5;
                }
            }
            
            // decreasing thresholds
            if (transformerT1 > transformerT5) {
                // lower bound
                if (scaleUpperBound > transformerT1) {
                    outputLowerBound = 0;
                }
                else if (scaleUpperBound > transformerT2) {
                    outputLowerBound = 1;
                }
                else if (scaleUpperBound > transformerT3) {
                    outputLowerBound = 2;
                }
                else if (scaleUpperBound > transformerT4) {
                    outputLowerBound = 3;
                }
                else if (scaleUpperBound > transformerT5) {
                    outputLowerBound = 4;
                }
                else {
                    outputLowerBound = 5;
                }
                
                // upper bound
                if (scaleLowerBound > transformerT1) {
                    outputUpperBound = 0;
                }
                else if (scaleLowerBound > transformerT2) {
                    outputUpperBound = 1;
                }
                else if (scaleLowerBound > transformerT3) {
                    outputUpperBound = 2;
                }
                else if (scaleLowerBound > transformerT4) {
                    outputUpperBound = 3;
                }
                else if (scaleLowerBound > transformerT5) {
                    outputUpperBound = 4;
                }
                else {
                    outputUpperBound = 5;
                }
            }
        }
        
        double koFactor = 0;
        if (outputLowerBound == 0) {
            koFactor = 1;
        }
        
        double potentialOutputRange = getTotalWeight() * (outputUpperBound - outputLowerBound) + koFactor;
                
        return potentialOutputRange;
    }

    /**
     * Method responsible for assessing the actual output range of this requirement.
     * Calculation rule:
     * if (minActualTransformedValue == 0) koFactor = 1; else koFactor = 0;
     * actualOutputRange = relativeWeight * (maxActualTransformedValue - minActualTransformedValue) + koFactor;
     * 
     * @return actual output range.
     *         If the plan is not yet at a evaluation stage where actual output range can be calculated 0 is returned.
     */
    public double getActualOutputRange() {
        // If the plan is not yet at a evaluation stage where actual output range can be calculated - return 0.
        if (transformer == null) {
            return 0;
        }
        
        // Collect all measured values from all alternatives
        List<Value> valueList = new ArrayList<Value>();
        Collection<Values> valuesCollection = valueMap.values();
        for (Values values : valuesCollection) {
            for (Value value : values.getList()) {
                valueList.add(value);
            }
        }
        
        // if nothing is measured yet - return 0
        if (valueList.size() == 0) {
            return 0;
        }
               
        // transform measured values
        List<Double> transformedValues = new ArrayList<Double>();
        for (Value val : valueList) {
            TargetValue targetValue;
            
            // do ordinal transformationCriterion
            if (transformer instanceof OrdinalTransformer) {
                OrdinalTransformer ordTrans = (OrdinalTransformer) transformer;
                
                if (val instanceof IOrdinalValue) {
                    try {
                        targetValue = ordTrans.transform((IOrdinalValue) val);
                    }
                    catch (NullPointerException e) {
                        log.warn("Measurement of leaf doesn't match with OrdinalTransformer! Ignoring it!");
                        log.warn("MeasuredValue-id: " + val.getId() + "; Transformer-id: " + ordTrans.getId());
                        continue;
                    }
                    transformedValues.add(targetValue.getValue());
                }
                else {
                    log.warn("getActualOutputRange(): INumericValue value passed to OrdinalTransformer - ignore value");
                }
            }

            // do numeric transformation
            if (transformer instanceof NumericTransformer) {
                NumericTransformer numericTrans = (NumericTransformer) transformer;
                
                if (val instanceof INumericValue) {
                    targetValue = numericTrans.transform((INumericValue) val);
                    transformedValues.add(targetValue.getValue());
                }
                else {
                    log.warn("getActualOutputRange(): IOrdinalValue value passed to NumericTransformer - ignore value");
                }
            }
        }
        
        // if nothing could be transformed successfully - return 0
        if (transformedValues.size() == 0) {
            return 0;
        }

        // calculate upper/lower bound
        double outputLowerBound = 10;
        double outputUpperBound = -10;

        for (Double tVal : transformedValues) {
            if (tVal > outputUpperBound) {
                outputUpperBound = tVal;
            }
            if (tVal < outputLowerBound) {
                outputLowerBound = tVal;
            }
        }
        
        double koFactor = 0;
        if (outputLowerBound == 0) {
            koFactor = 1;
        }
        
        double actualOutputRange = getTotalWeight() * (outputUpperBound - outputLowerBound) + koFactor;
        
        return actualOutputRange;
    }
    /**
     * touches everything: this, the scale and the transformer (if existing)
     */
    @Override
    public void touchAll(String username) {
        touch(username);
        if (scale != null) {
            scale.touch(username);
        }
        if (transformer != null) {
            transformer.touch(username);
        }
    }
    
    /**
     * Method responsible for touching this Leaf and its Scale.
     */
    public void touchIncludingScale() {
        touch();
        if (scale != null) {
                scale.touch();
        }
    }
    
}
