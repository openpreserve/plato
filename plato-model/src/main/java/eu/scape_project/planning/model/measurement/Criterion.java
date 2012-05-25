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
 ******************************************************************************/
package eu.scape_project.planning.model.measurement;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.IChangesHandler;
import eu.scape_project.planning.model.ITouchable;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.util.CriterionUri;

@Entity
public class Criterion implements Comparable<Criterion>, Serializable, ITouchable {
    private static final long serialVersionUID = -3942656115528678720L;

    @Id
    @GeneratedValue
    private long id;

    private String uri;

    @ManyToOne(cascade = CascadeType.MERGE)
    private MeasurableProperty property;

    @ManyToOne(cascade = CascadeType.MERGE)
    private Metric metric;

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    public CriterionUri toCriterionUri() {
        final CriterionUri uri = new CriterionUri(this.getUri());
        return uri;
    }

    public Scale getScale() {
        Scale s = null;
        if ((this.metric != null) && (this.metric.getScale() != null)) {
            s = this.metric.getScale();
        } else if ((this.property != null) && (this.property.getScale() != null)) {
            s = this.property.getScale();
        }
        return s;
    }

    public String getUnit() {
        final Scale s = this.getScale();
        if (s != null) {
            return s.getUnit();
        } else {
            return null;
        }
    }

    /**
     * configures a criterion (its property and metric) according to the given
     * uri - The URI is interpreted as follows: <criterion
     * category>://<criterion sub-category>/<propertyId>[#<metricId>]
     * 
     * - if null or an empty string is passed to this method, all measurement
     * information is reset.
     * 
     * This instance is then NOT linked
     * 
     * @throws IllegalArgumentException
     *             if uri is invalid, or does not correspond to a criterion
     *             category
     * @param uri
     */
    // public void mockFromUri(String uri) throws IllegalArgumentException {
    // CriterionUri info = new CriterionUri();
    //
    // // this may throw an IllegalArgumentException, we do not catch it, but
    // pass it on directly
    // info.setAsURI(uri);
    // String scheme = info.getScheme();
    // String path = info.getPath();
    // String fragment = info.getFragment();
    //
    // // if the URI is empty, reset measurement info
    // if ((scheme == null)||(path == null)) {
    // property = null;
    // metric = null;
    // return;
    // }
    //
    // // check, if scheme and path correspond to a valid CriterionCategory:
    // // 1. extract criterion sub-category from path
    // int subCategoryEndIdx = path.indexOf("/");
    // if (subCategoryEndIdx == -1) {
    // throw new
    // IllegalArgumentException("invalid measurment info uri - scheme and path do not correspond to a criterion category: "
    // + uri);
    // } else if ((subCategoryEndIdx + 1) >= path.length()) {
    // throw new
    // IllegalArgumentException("invalid measurment info uri - no property defined: "
    // + uri);
    // }
    // // 2. extract propertyId and criterion sub-category
    // String propertyId = path.substring(subCategoryEndIdx+1);
    // String subCategory = path.substring(0, subCategoryEndIdx);
    //
    // // 3. try to get the corresponding category
    // CriterionCategory cat = CriterionCategory.getType(scheme, subCategory);
    // if (cat == null) {
    // throw new
    // IllegalArgumentException("invalid measurement info uri - scheme and path don't correspond to a criterion category: "
    // +uri);
    // }
    // // reuse existing property, when possible
    // MeasurableProperty prop = getProperty();
    // if (prop == null) {
    // prop = new MeasurableProperty();
    // setProperty(prop);
    // } else {
    // // there is a property, reset the old values
    // prop.setName(null);
    // prop.setDescription(null);
    // prop.getPossibleMetrics().clear();
    // prop.setScale(null);
    // }
    // // populate it with known values from URI
    // getProperty().setCategory(cat);
    // getProperty().setPropertyId(propertyId);
    //
    // // if fragment is null metric should also be null
    // if (fragment == null) {
    // metric = null;
    // }
    // // otherwise fill it with known values from URI
    // else {
    // // reuse existing metric, when possible
    // Metric metr = getMetric();
    // if (metr == null) {
    // metr = new Metric();
    // setMetric(metr);
    // } else {
    // metr.setDescription(null);
    // metr.setName(null);
    // metr.setScale(null);
    // metr.setType(null);
    // }
    // // populate it with known values from URI
    // metr.setMetricId(fragment);
    // }
    // }

    /**
     * returns the string representation of this measurement info <criterion
     * category>://<criterion subcategory>/<propertyId>[#<metricId>]
     * 
     * @return
     */
    @Deprecated()
    public String buildUri() {
        String scheme = null;
        String path = null;

        if ((this.property == null) || (this.property.getCategory() == null)) {
            return null;
        }
        final CriterionCategory cat = this.property.getCategory();

        scheme = cat.getCategory();
        path = "".equals(cat.getSubCategory()) ? "" : cat.getSubCategory() + "/";

        String fragment;
        if ((this.metric != null) && (this.metric.getMetricId() != null)) {
            fragment = "#" + this.metric.getMetricId();
        } else {
            fragment = "";
        }
        return scheme + "://" + path + this.property.getPropertyId() + fragment;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public MeasurableProperty getProperty() {
        return this.property;
    }

    public void setProperty(final MeasurableProperty property) {
        this.property = property;
    }

    public Metric getMetric() {
        return this.metric;
    }

    public void setMetric(final Metric metric) {
        this.metric = metric;
    }

    @Override
    public ChangeLog getChangeLog() {
        return this.changeLog;
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    @Override
    public void handleChanges(final IChangesHandler h) {
        h.visit(this);
        // call handleChanges of all properties
        if (this.property != null) {
            this.property.handleChanges(h);
        }
        if (this.metric != null) {
            this.metric.handleChanges(h);
        }
    }

    @Override
    public boolean isChanged() {
        return this.changeLog.isAltered();
    }

    @Override
    public void touch() {
        this.changeLog.touch();

    }

    public void setChangeLog(final ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public long getId() {
        return this.id;
    }

    @Override
    public int compareTo(final Criterion c) {
        return this.uri.toLowerCase().compareTo(c.getUri().toLowerCase());
        // return
        // property.getName().toLowerCase().compareTo(c.getProperty().getName().toLowerCase());
    }
}
