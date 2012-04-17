package eu.planets_project.pp.plato.model.kbrowser;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class CriteriaHierarchy implements Serializable {
    private static final long serialVersionUID = 7043155199631459302L;

    @Id
    @GeneratedValue
    private int id;

    private String name;
    
    //private Boolean scaleWeights;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private CriteriaNode criteriaTreeRoot;

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

    /*
    public void setScaleWeights(Boolean scaleWeights) {
        this.scaleWeights = scaleWeights;
    }

    public Boolean getScaleWeights() {
        return scaleWeights;
    }
    */

    public void setCriteriaTreeRoot(CriteriaNode criteriaTreeRoot) {
        this.criteriaTreeRoot = criteriaTreeRoot;
    }

    public CriteriaNode getCriteriaTreeRoot() {
        return criteriaTreeRoot;
    }
}
