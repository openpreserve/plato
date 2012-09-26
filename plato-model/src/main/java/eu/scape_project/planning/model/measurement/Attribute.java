package eu.scape_project.planning.model.measurement;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Describes an attribute of a significant property. Attributes might be
 * {@link Measure measured} directly, or can only be grasped / approximated by a
 * couple of measures.
 * 
 * @author Michael Kraxner
 * 
 */
@Entity
public class Attribute {

    @Id
    @GeneratedValue
    private long id;

    private String uri;

    private String name;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private CriterionCategory category;
    
    public Attribute(){
    }
    
    /**
     * Create a new attribute
     * 
     * With the values from the given Attribute
     * The id is NOT copied
     * 
     * @param attribute
     */
    public Attribute(final Attribute attribute) {
        this.category = attribute.category;
        this.description = attribute.description;
        this.name = attribute.name;
        this.uri = attribute.uri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public CriterionCategory getCategory() {
        return category;
    }

    public void setCategory(final CriterionCategory category) {
        this.category = category;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

}
