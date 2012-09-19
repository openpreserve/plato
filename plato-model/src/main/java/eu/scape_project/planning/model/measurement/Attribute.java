package eu.scape_project.planning.model.measurement;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    private long id;

    private String name;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private CriterionCategory category;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CriterionCategory getCategory() {
        return category;
    }

    public void setCategory(CriterionCategory category) {
        this.category = category;
    }

}
