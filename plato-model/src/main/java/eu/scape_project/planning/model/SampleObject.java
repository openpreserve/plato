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
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * Encapsulates a sample record that we want to use for testing and evaluating
 * Alternatives. At the moment, this data will be uploaded and saved through
 * this object. Later we will want to allow references to repository data.
 *
 * @author Christoph Becker
 *
 */
@Entity
@DiscriminatorValue("S")
public class SampleObject extends DigitalObject {

    private static final long serialVersionUID = 1391649509828156427L;

    @NotNull
    @Length(min = 1)
    private String shortName = "";

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     *
     * When changing something here please don't forget to update the length
     * restriction in xhtml !
     */
    @Length(max = 5000)
    @Column(length = 5000)
    private String description = "";

    /**
     * Please note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     *
     * When changing something here please don't forget to update the length
     * restriction in xhtml !
     */
    @Length(max = 5000)
    @Column(length = 5000)
    private String originalTechnicalEnvironment = "";

    /**
     * This is our index when he store the sampleRecords in a list.
     * Due to a bug of hibernate http://opensource.atlassian.com/projects/hibernate/browse/HHH-3160
     * we have to maintain the index ourselves
     */
    private long sampleIndex;
    
    @ManyToOne
    private SampleRecordsDefinition sampleRecordsDefinition;
    
    private SampleObject(){
    }
    
    public SampleObject(final String shortName) {
        this.shortName = shortName;
    }
    
    public void assignValues(SampleObject source){
    	super.assignValues(source);
    	this.shortName = source.getShortName();
    	this.contentType = source.getContentType();
    	this.description = source.getDescription();
    	this.originalTechnicalEnvironment = source.getOriginalTechnicalEnvironment();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public SampleRecordsDefinition getSampleRecordsDefinition() {
        return sampleRecordsDefinition;
    }

    public void setSampleRecordsDefinition(
            SampleRecordsDefinition sampleRecordsDefinition) {
        this.sampleRecordsDefinition = sampleRecordsDefinition;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalTechnicalEnvironment() {
        return originalTechnicalEnvironment;
    }

    public void setOriginalTechnicalEnvironment(String originalTechnicalEnvironment) {
        this.originalTechnicalEnvironment = originalTechnicalEnvironment;
    }

    public long getSampleIndex() {
        return sampleIndex;
    }

    public void setSampleIndex(long sampleIndex) {
        this.sampleIndex = sampleIndex;
    }

    public boolean isFormatDefined() {
        return (formatInfo != null && formatInfo.isDefined()); 
    }
}
