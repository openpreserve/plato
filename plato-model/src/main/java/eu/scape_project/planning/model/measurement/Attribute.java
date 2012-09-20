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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
