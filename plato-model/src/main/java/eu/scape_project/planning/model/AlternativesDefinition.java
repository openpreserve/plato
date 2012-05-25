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
package eu.scape_project.planning.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import eu.scape_project.planning.exception.PlanningException;

/**
 * Class containing all properties for workflow step 'Define Alternatives'.
 *
 * @author Hannes Kulovits
 */
@Entity
public class AlternativesDefinition implements Serializable, ITouchable {

    private static final long serialVersionUID = 5305133244443843393L;

    @Id @GeneratedValue
    private int id;
    
    @Lob
    private String description;

    public Alternative alternativeByName(String name) {
        for (Alternative a : alternatives) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }
    
    /**
     *
     * One reason we had to use @IndexColumn was because of fetch type EAGER. This problem
     * can be resolved by using @Fetch(FetchMode.SUBSELECT)
     */
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
    @OrderColumn(name="alt_index")
    @JoinColumn(name="parent_id", nullable=false)
    @Fetch(value=FetchMode.SELECT)
    private List<Alternative> alternatives = new ArrayList<Alternative>();

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

//    /**
//     * List of alternative preservation solutions that shall not be considered for evaluation because
//     * they are definitely inappropriate for instance. Consequently they need not to be deleted
//     * to finish the workflow.
//     */
//    @Transient
//    private List<Alternative> consideredAlternatives;

    public List<Alternative> getAlternatives() {
        return Collections.unmodifiableList(alternatives);
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }   

    /**
     * Returns a list of alternatives which shall be evaluated.
     * 
     * NOTE: There is no caching, the resulting list is created every time this method is invoked
     *       therefore use with care. 
     *   
     * @return alternatives that shall be considered for evaluation
     */
    public List<Alternative> getConsideredAlternatives() {
        ArrayList<Alternative> consideredAlternatives = new ArrayList<Alternative>();

        for(Alternative alt : alternatives) {
            if (! alt.isDiscarded()) { 
                consideredAlternatives.add(alt);
            }
        }

        return consideredAlternatives;
    }

    public void setConsideredAlternatives(List<Alternative> consideredAlternatives) {

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
        for (Alternative alt : alternatives) {
            alt.handleChanges(h);
        }
    }

    public void removeAlternative(Alternative alternative) {
        alternatives.remove(alternative);
    }
    
    /**
     * returns a list containing the lower case names of all alternatives (including discarded)
     */
    private List<String> getUsedNames(){
        List<String> usedNames = new ArrayList<String>();
        for (Alternative a: alternatives) {
            usedNames.add(a.getName().toLowerCase());
        }
        return usedNames;
    }
    
    /**
     * Creates a unique alternative name based on <code>name</code>
     * and adds the new name to the list of <code>usedNames</code>
     *
     * @param usedNames a list of all names of the alternatives in the current project
     * @param name alternative name to create an unique name from
     * @return a unique alternative name with a maximum length of 20 characters.
     */
    public String createUniqueName(String name){
        List<String> usedNames = getUsedNames();
        
        String shortname = name.substring(0, Math.min(30, name.length()));
        if (!usedNames.contains(shortname.toLowerCase())) {
            return shortname;
        } else {
            // start with 1-digit numbers
            int i = 1;
            int exp = 0;
            String base;
            if (shortname.length() <= 28)
                base = shortname;
            else
                base = shortname.substring(0, 28);
            String newName = base+ "-" + i;
            while (usedNames.contains(newName.toLowerCase())) {
                i++;
                if ((int)Math.log10(i) > exp) {
                    // i-digits are not enough - extend the postfix
                    exp = (int)Math.log10(i);
                    // and reduce the length of the base if necessary
                    base = shortname.substring(0, Math.min(shortname.length(), 28-exp));
                }
                newName = base + "-" + i;
            }
            return newName;
        }
    }
    

    /**
     * adds the given alternative to the list of alternatives.
     * used for importing by the digester.
     *
     * we have to ensure referential integrity!
     *
     * @param alternative Alternative to add.
     * @throws PlanningException if an error at adding occurs (e.g. want to add an Alternative with an already existing name).
     */
    public void addAlternative(Alternative alternative) throws PlanningException {
    	// throw an Exception if the given alternative name is not unique
        if (!isAlternativeNameUnique(alternative)) {
        	throw new PlanningException("An unique name must be provided for the alternative.");
        }
    	
    	// to ensure referential integrity
        alternative.setAlternativesDefinition(this);
        alternatives.add(alternative);
    }
    
    /**
     * Method responsible for giving an Alternative a new name.
     * 
     * @param alternative Alternative to modify.
     * @param newName New name of the Alternative
     * @throws PlanningException If an error occurs at changing the name this exception is thrown.
     */
    @SuppressWarnings("deprecation")
	public void renameAlternative(Alternative alternative, String newName) throws PlanningException {
    	// check if the alternative to change exists
    	if (alternativeByName(alternative.getName()) == null) {
    		throw new PlanningException("Alternative to rename does not exists.");
    	}
    	
    	// check if the new-name is unique
    	Alternative tempAlternativeForUniqueNameTesting = new Alternative();
    	tempAlternativeForUniqueNameTesting.setName(newName);
    	if (!alternative.getName().equals(newName) && !isAlternativeNameUnique(tempAlternativeForUniqueNameTesting)) {
    		throw new PlanningException("A unique name must be provided for the alternative.");
    	}
    	
    	// At this stage renaming of the alternative is okay
    	alternative.setName(newName);
    }

    /**
     * Method responsible for checking if the given Alternative has an unique name regarding the already defined Alternatives.
     * The used unique name check is case-insensitive.
     * 
     * @param alternative Alternative to check for name uniqueness.
     * @return true if the Alternative has an unique name. False otherwise.
     */
	private boolean isAlternativeNameUnique(Alternative alternative) {
    	List<String> existingNames = new ArrayList<String>();
    	for (Alternative alt : alternatives) {
    		existingNames.add(alt.getName().toLowerCase());
    	}
    	
    	if (existingNames.contains(alternative.getName().toLowerCase())) {
    		return false;
    	}
    	
    	return true;
    }
}
