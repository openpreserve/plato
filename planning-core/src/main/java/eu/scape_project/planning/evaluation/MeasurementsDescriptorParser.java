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
package eu.scape_project.planning.evaluation;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.measurement.MeasurableProperty;
import eu.scape_project.planning.model.measurement.Metric;
import eu.scape_project.planning.model.scales.BooleanScale;
import eu.scape_project.planning.model.scales.FloatRangeScale;
import eu.scape_project.planning.model.scales.FloatScale;
import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.IntRangeScale;
import eu.scape_project.planning.model.scales.IntegerScale;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.PositiveIntegerScale;
import eu.scape_project.planning.model.scales.YanScale;

public class MeasurementsDescriptorParser {
	private static Logger log = LoggerFactory.getLogger(MeasurementsDescriptorParser.class);
    
    /**
     * a list of all known measurable properties, accessible by their propertyId
     * used by the digester
     */
    private Map<String, MeasurableProperty> propertyInfo;
    
    /**
     * a list of all known metrics, accessible by their metricId
     * used by the digester
     */
    private Map<String, Metric> metricInfo;
    
    
    
    public void load(InputStream in, Map<String, MeasurableProperty> propertyInfo, Map<String, Metric> metricInfo) {
        this.propertyInfo = propertyInfo;
        this.metricInfo = metricInfo;
        
        Digester d = setupDigester();
        try {
            d.parse(in);
            resolveMetrics();
        } catch (Exception e) {
           log.error("could not parse measurement infos", e);
        }
    }
    
    /**
     * loads two lists: properties and metrics
     * @param in the XML input
     * @param propertyInfo a map of all properties. Each property can contain 
     * @param metricInfo a map of all metrics
     */
    public void load(Reader in, Map<String, MeasurableProperty> propertyInfo, Map<String, Metric> metricInfo) {
        this.propertyInfo = propertyInfo;
        this.metricInfo = metricInfo;

        Digester d = setupDigester();
        try {
            d.parse(in);
            resolveMetrics();
        } catch (Exception e) {
            log.error("could not parse measurement infos", e);
        }
    }
    
    private void resolveMetrics() throws IllegalArgumentException {
        // resolve references to metrics:
        for(MeasurableProperty p : propertyInfo.values()) {
            for (Metric m : p.getPossibleMetrics()) {
                Metric metric = metricInfo.get(m.getMetricId());
                if (metric != null) {
                    p.getPossibleMetrics().set(p.getPossibleMetrics().indexOf(m), metric);
                } else {
                    throw new IllegalArgumentException("XML file contains invalid metric reference: "+m.getMetricId()+" in property "+p.getPropertyId());
                }
            }
        }
    }
    private Digester setupDigester() {
        Digester d = new Digester();
        d.push(this);
        
/*      <measurableProperties>
                <property>
                        <propertyId>object://image/dimension/width</propertyId>
                        <name>image width</name>
                        <description>the width of an image in pixel</description>
                        <scale type="positiveInteger">
                            <unit>pixel</unit>
                        </scale>
                        <possibleMetrics>
                            <metric metricId="equal"/>
                            <metric metricId="indDiff"/>
                        </possibleMetrics> 
                </property>
 */
        d.addObjectCreate("*/property", MeasurableProperty.class);
        d.addSetNext("*/property", "addProperty");
        d.addBeanPropertySetter("*/property/category",  "categoryAsString");
        d.addBeanPropertySetter("*/property/propertyId");
        d.addBeanPropertySetter("*/property/name");
        d.addBeanPropertySetter("*/property/description");
        d.addObjectCreate("*/property/possibleMetrics", ArrayList.class);
        d.addSetNext("*/property/possibleMetrics", "setPossibleMetrics");
//        d.addObjectCreate("*/property/possibleMetrics/metric", Metric.class);
        d.addObjectCreate("*/possibleMetrics/metric", Metric.class);
        d.addSetProperties("*/possibleMetrics/metric");
        d.addSetNext("*/possibleMetrics/metric", "add");
        
/*              <metric>
                        <name>equal</name>
                        <description></description>
                        <scale type="boolean" />
                </metric>
 */
        d.addObjectCreate("*/metrics/metric", Metric.class);
        d.addSetProperties("*/metrics/metric");
        d.addBeanPropertySetter("*/metrics/metric/metricId");
        d.addBeanPropertySetter("*/metrics/metric/name");
        d.addBeanPropertySetter("*/metrics/metric/description");
        d.addSetNext("*/metrics/metric", "addMetric");

        addCreateScale(d, BooleanScale.class);
        addCreateScale(d, FloatRangeScale.class);
        addCreateScale(d, FloatScale.class);
        addCreateScale(d, IntegerScale.class);
        addCreateScale(d, IntRangeScale.class);
        addCreateScale(d, OrdinalScale.class);
        addCreateScale(d, PositiveFloatScale.class);
        addCreateScale(d, PositiveIntegerScale.class);
        addCreateScale(d, YanScale.class);
        addCreateScale(d, FreeStringScale.class);
        return d;
    }
    
    /**
     * used by digester
     * 
     * @param p
     */
    public void addProperty(MeasurableProperty p) {
        propertyInfo.put(p.getPropertyId(), p);
    }
    
    /**
     * used by digester
     * @param m
     */
    public void addMetric(Metric m) {
        metricInfo.put(m.getMetricId(), m);
    }
    
    private static void addCreateScale(Digester digester, Class c) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);

        String pattern = "*/" + name;
        digester.addObjectCreate(pattern, c);
        digester.addSetProperties(pattern);
        digester.addBeanPropertySetter(pattern+"/unit");
        digester.addBeanPropertySetter(pattern+"/restriction");
        digester.addSetNext(pattern, "setScale");
    }    
}
