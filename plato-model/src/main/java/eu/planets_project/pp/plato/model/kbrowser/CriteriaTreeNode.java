package eu.planets_project.pp.plato.model.kbrowser;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Inheritance
@DiscriminatorColumn(name = "nodetype")
public abstract class CriteriaTreeNode implements Serializable {
    private static final long serialVersionUID = -5616348201551634176L;

    @Id
    @GeneratedValue
    protected int id;

    protected String name;
        
    /**
     * Field indicating if this node is a leaf or not.
     * The associated get-method is abstract, to be mandatory (and so specific) in each subclass.
     */
    private Boolean leaf;
        
    @ManyToOne
    @JoinColumn(name = "parent_fk", insertable = false, updatable = false)
    private CriteriaNode parent;
               
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParent(CriteriaNode parent) {
        this.parent = parent;
    }

    public CriteriaNode getParent() {
        return parent;
    }

    public abstract Boolean getLeaf();
}
