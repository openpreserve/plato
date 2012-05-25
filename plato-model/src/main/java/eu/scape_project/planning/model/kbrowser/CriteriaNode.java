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
package eu.scape_project.planning.model.kbrowser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.annotations.IndexColumn;

@Entity
@DiscriminatorValue("N")
public class CriteriaNode extends CriteriaTreeNode {
    private static final long serialVersionUID = 7843526513261328018L;
    
    @Transient
    private long nrOfRelevantPlans = -1L;
    
    @Valid
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_fk")
    @IndexColumn(name="indexcol",base=1)
    protected List<CriteriaTreeNode> children = new ArrayList<CriteriaTreeNode>();

    public CriteriaNode() { }
    
    public CriteriaNode(long nrOfRelevantPlans) {
        this.nrOfRelevantPlans = nrOfRelevantPlans;
    }
        
    // -------- universal functions --------
    
    @Override
    public Boolean getLeaf() {
        return false;
    }
    
    /**
     * Method responsible for adding a child-node.
     * 
     * @param child child-node to add.
     */
    public void addChild(CriteriaTreeNode child) {
        child.setParent(this);
        children.add(child);
    }
    
    /**
     * Method responsible for removing a child-node.
     *  
     * @param child child-node to remove.
     */
    public void removeChild(CriteriaTreeNode child) {
        children.remove(child);
        child.setParent(null);
    }
    
    /**
     * Method responsible for returning all successive treenodes. 
     * @return Successive treenodes.
     */
    public List<CriteriaTreeNode> getAllSuccessiveTreeNodes() {
        List<CriteriaTreeNode> successiveTreeNodes = new ArrayList<CriteriaTreeNode>();
        
        for (CriteriaTreeNode child : this.children) {
            successiveTreeNodes.add(child);
            if (child instanceof CriteriaNode) {
                CriteriaNode childNode = (CriteriaNode) child;
                successiveTreeNodes.addAll(childNode.getAllSuccessiveTreeNodes());
            }
        }           
        
        return successiveTreeNodes;
    }
       
    /**
     * Method responsible for returning all successive leaves.
     * @return Successive leaves.
     */
    public List<CriteriaLeaf> getAllSuccessiveLeaves() {
        List<CriteriaLeaf> successiveLeaves = new ArrayList<CriteriaLeaf>();
        
        for (CriteriaTreeNode node : getAllSuccessiveTreeNodes()) {
            if (node instanceof CriteriaLeaf) {
                CriteriaLeaf leaf = (CriteriaLeaf) node;
                successiveLeaves.add(leaf);
            }
        }
            
        return successiveLeaves;
    }
    
    /**
     * Method responsible for returning the number of successive leaves
     * @return Number of successive leaves.
     */
    public int getNrOfSuccessiveLeaves() {
    	return getAllSuccessiveLeaves().size();
    }

    // -------- criteria set importance factors --------

    /**
     * Method responsible for calculating the criteria set importance factor SIF1.
     * SIF1(Spread) is specified by the average spread of the criteria in the set.
     * @return Criteria set importance factor SIF1.
     */
    public double getImportanceFactorSIF1() {       
        double sum = 0;
        double count = 0;
        
        for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
            double value = criteriaLeaf.getImportanceFactorIF2();
            sum += value;
            count++;
        }
        
        if (count == 0) {
            return 0;
        }
        
        return sum / count;
    }

    /**
     * Method responsible for calculating the criteria set importance factor SIF2.
     * SIF2(Coverage) is specified by the percentage of plans using at least one of the criteria in the set.
     * SIF2 = plans using at least one of the criteria / number of all plans
     * @return Criteria set importance factor SIF1.
     */
    public double getImportanceFactorSIF2() {
        if (nrOfRelevantPlans == 0) {
            return 0;
        }
        
        HashSet<Integer> plansUsingAtLeastOneOfTheCriteria = new HashSet<Integer>();
        
        for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
            plansUsingAtLeastOneOfTheCriteria.addAll(criteriaLeaf.getUsingPlans());
        }
        
        double coverage = (double) plansUsingAtLeastOneOfTheCriteria.size() / (double) nrOfRelevantPlans;
        
        return coverage;
    }
  
    /**
     * Method responsible for calculating the criteria set importance factor SIF3.
     * SIF3(Weight) is specified by the average of the per plan accumulated criteria weights.
     * SIF3 = sum of criteria leaf IF4(Discounted Weight)
     * @return Criteria set importance factor SIF3.
     */
   public double getImportanceFactorSIF3(){
       double sum = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF4();
           sum += value;
       }
             
       return sum;
   }
    
   /**
    * Method responsible for calculating the criteria set importance factor SIF4.
    * SIF4(Potential) is specified by the average of the per plan accumulated criteria potentials.
    * SIF4 = sum of criteria leaf IF7(Discounted Potential)
    * @return Criteria set importance factor SIF4.
    */
   public double getImportanceFactorSIF4(){
       double sum = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF7();
           sum += value;
       }
       
       return sum;
   }
   
   /**
    * Method responsible for calculating the criteria set importance factor SIF5.
    * SIF5(Maximum Potential) is specified by the maximum the per plan accumulated criteria potential.
    * SIF3 = max(per plan accumulated potential)
    * @return Criteria set importance factor SIF5.
    */
   public double getImportanceFactorSIF5(){       
       // data structure for storing accumulated values per plan <plan, accumulated potential> - values are aggregated by summing them up
       Hashtable<Integer, Double> planAccumulation = new Hashtable<Integer, Double>();

       // iterate all criterionLeaves
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           // iterate all corresponding plan leaves and separate the values per plan
           for (VPlanLeaf planLeaf : criteriaLeaf.getPlanLeaves()) {
               int planId = planLeaf.getPlanId();
               double por = planLeaf.getPotentialOutputRange();
               
               if (!planAccumulation.containsKey(planId)) {
                   planAccumulation.put(planId, por);
               }
               else {
                   double oldValue = planAccumulation.get(planId);
                   double newValue = oldValue + por;
                   planAccumulation.put(planId, newValue);
               }
           }
       }
       
       // now aggregate over accumulation results
       double max = 0;
       for (Integer planId : planAccumulation.keySet()) {
           double planAgg = planAccumulation.get(planId);
           if (planAgg > max) {
               max = planAgg;
           }
       }
       
       return max;
   }
   
   /**
    * Method responsible for calculating the criteria set importance factor SIF5.
    * SIF6(Range) is specified by the average of the per plan accumulated criteria ranges.
    * SIF6 = sum of criteria leaf IF8(Discounted Range)
    * @return Criteria set importance factor SIF6.
    */
   public double getImportanceFactorSIF6(){
       double sum = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF8();
           sum += value;
       }
       
       return sum;
   }

   /**
    * Method responsible for calculating the criteria set importance factor SIF7.
    * SIF7(Maximum Range) is specified by the maximum the per plan accumulated criteria range.
    * SIF7 = max(per plan accumulated range)
    * @return Criteria set importance factor SIF7.
    */
   public double getImportanceFactorSIF7(){
       // data structure for storing accumulated values per plan <plan, accumulated potential> - values are aggregated by summing them up
       Hashtable<Integer, Double> planAccumulation = new Hashtable<Integer, Double>();

       // iterate all criterionLeaves
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           // iterate all corresponding plan leaves and separate the values per plan
           for (VPlanLeaf planLeaf : criteriaLeaf.getPlanLeaves()) {
               int planId = planLeaf.getPlanId();
               double aor = planLeaf.getActualOutputRange();
               
               if (!planAccumulation.containsKey(planId)) {
                   planAccumulation.put(planId, aor);
               }
               else {
                   double oldValue = planAccumulation.get(planId);
                   double newValue = oldValue + aor;
                   planAccumulation.put(planId, newValue);
               }
           }
       }
       
       // now aggregate over accumulation results
       double max = 0;
       for (Integer planId : planAccumulation.keySet()) {
           double planAgg = planAccumulation.get(planId);
           if (planAgg > max) {
               max = planAgg;
           }
       }
       
       return max;
   }
   
   /**
    * Method responsible for calculating the criteria set importance factor SIF8.
    * SIF8(Variation) is specified by the average of all criteria relative output ranges.
    * SIF8 = average mean of criteria leaf IF11(Variation)
    * @return Criteria set importance factor SIF8.
    */
   public double getImportanceFactorSIF8(){
       double sum = 0;
       double count = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF11();
           sum += value;
           count++;
       }
       
       if (count == 0) {
           return 0;
       }
       
       return sum / count;   
   }
   
   /**
    * Method responsible for calculating the criteria set importance factor SIF9.
    * SIF9(Maximum Variation) is specified by the average of all criteria maximum relative output ranges.
    * SIF9 = average mean of criteria leaf IF12(Maximum Variation)
    * @return Criteria set importance factor SIF9.
    */
   public double getImportanceFactorSIF9(){
       double sum = 0;
       double count = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF12();
           sum += value;
           count++;
       }
       
       if (count == 0) {
           return 0;
       }
       
       return sum / count;   
   }   
    
   /**
    * Method responsible for calculating the criteria set importance factor SIF10.
    * SIF10(Rejection Potential Count) is specified by the number of decision criteria with alternative rejection potential.
    * SIF10 = sum of single criteria factor IF13(Rejection Potential Count)
    * @return Criteria set importance factor SIF10.
    */
   public double getImportanceFactorSIF10(){
       double sum = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF13();
           sum += value;
       }
       
       return sum;   
   }   
   
   /**
    * Method responsible for calculating the criteria set importance factor SIF11.
    * SIF11(Rejection Potential Rate) is specified by the percentage of decision criteria with alternative rejection potential.
    * SIF11 = sum of single criteria factor IF13(Rejection Potential Count) / number of decision criteria
    * @return Criteria set importance factor SIF11.
    */
   public double getImportanceFactorSIF11(){
       double sum = 0;
       double count = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF13();
           sum += value;
           count = count + criteriaLeaf.getPlanLeaves().size();
       }
       
       if (count == 0) {
           return 0;
       }
       
       return sum / count;   
   }   

   /**
    * Method responsible for calculating the criteria set importance factor SIF12.
    * SIF12(Rejection Count) is specified by the number of decision criteria actually rejecting alternatives.
    * SIF12 = sum of single criteria factor IF15(Rejection Count)
    * @return Criteria set importance factor SIF12.
    */
   public double getImportanceFactorSIF12(){
       double sum = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF15();
           sum += value;
       }
       
       return sum;     
   }   

   /**
    * Method responsible for calculating the criteria set importance factor SIF13.
    * SIF13(Rejection Rate) is specified by the percentage of decision criteria actually rejecting alternatives.
    * SIF13 = sum of single criteria factor IF15(Rejection Potential Count) / number of decision criteria
    * @return Criteria set importance factor SIF13.
    */
   public double getImportanceFactorSIF13(){
       double sum = 0;
       double count = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF15();
           sum += value;
           count = count + criteriaLeaf.getPlanLeaves().size();
       }
       
       if (count == 0) {
           return 0;
       }
       
       return sum / count;   
   }

   /**
    * Method responsible for calculating the criteria set importance factor SIF14.
    * SIF14(Rejection Spread) is specified by the percentage of plans affected by a actual alternative reject out of this criteria set.
    * @return Criteria set importance factor SIF14.
    */   
   public double getImportanceFactorSIF14(){
	   if (nrOfRelevantPlans == 0) {
		   return 0;
	   }
	   
	   // data structure for storing aggregated values per plan <plan, aggregated weight> - values are aggregated by summing them up
       HashSet<Integer> koPlans = new HashSet<Integer>();

       // iterate all criterion leaves contained by this structural node
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           // iterate all plan leaves and separate the values per plan
           for (VPlanLeaf planLeaf : criteriaLeaf.getPlanLeaves()) {
               int planId = planLeaf.getPlanId();
               double ako = planLeaf.getActualKO();
               
               if (ako > 0) {
                   koPlans.add(planId);
               }
           }
       }
       
       double koPlansCount = koPlans.size();
       double result = koPlansCount / (double) nrOfRelevantPlans;
       
       return result;
   }
   
   /**
    * Method responsible for calculating the criteria set importance factor SIF15.
    * SIF15(Reject Count) is specified by the number alternatives actually rejected.
    * SIF15 = sum of single criteria factor IF17(Rejection Count)
    * @return Criteria set importance factor SIF15.
    */
   public double getImportanceFactorSIF15(){
       HashSet<String> koAlternatives = new HashSet<String>();
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           for (VPlanLeaf planLeaf : criteriaLeaf.getPlanLeaves()) {
               Map<String,Double> alternativeEvaluations = planLeaf.getAlternativeResultsAsMap();
               
               for (String alternative : alternativeEvaluations.keySet()) {
                   if (alternativeEvaluations.get(alternative) == 0) {
                       koAlternatives.add(planLeaf.getPlanId() + "-" + alternative);
                   }
               }
           }
       }
       
       double result = koAlternatives.size();
       return result;

       /*
       double sum = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           double value = criteriaLeaf.getImportanceFactorIF17();
           sum += value;
       }
       
       return sum;
       */        
   }   

   /**
    * Method responsible for calculating the criteria set importance factor SIF16.
    * SIF16(Reject Rate) is specified by the percentage of alternatives actually rejected.
    * SIF16 = sum of single criteria factor IF15(Reject Count) / number of all alternatives
    * @return Criteria set importance factor SIF16.
    */
   public double getImportanceFactorSIF16(){
       HashSet<String> allAlternatives = new HashSet<String>();
       HashSet<String> koAlternatives = new HashSet<String>();
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           for (VPlanLeaf planLeaf : criteriaLeaf.getPlanLeaves()) {
               Map<String,Double> alternativeEvaluations = planLeaf.getAlternativeResultsAsMap();
               
               for (String alternative : alternativeEvaluations.keySet()) {
                   allAlternatives.add(planLeaf.getPlanId() + "-" + alternative);
                   if (alternativeEvaluations.get(alternative) == 0) {
                       koAlternatives.add(planLeaf.getPlanId() + "-" + alternative);
                   }
               }
           }
       }
       
       double allAlternativesCount = allAlternatives.size();
       double koAlternativesCount = koAlternatives.size();
       
       if (allAlternativesCount == 0) {
           double result = 0;
           return result;
       }
       
       double result = koAlternativesCount / allAlternativesCount;
       return result;
       
       /*
       double sum = 0;
       double count = 0;
       
       for (CriteriaLeaf criteriaLeaf : getAllSuccessiveLeaves()) {
           for (VPlanLeaf planLeaf : criteriaLeaf.getPlanLeaves()) {
               sum = sum + planLeaf.getActualKO();
               count = count + planLeaf.getValueMap().size();
           }
       }
       
       if (count == 0) {
           return 0;
       }
       
       return sum / count;
       */   
   }
   
   // -------- view formatting --------
   
   public String getStringFormattedImportanceFactorSIF1() {
       DecimalFormat format = new DecimalFormat("#.##");
       return format.format(getImportanceFactorSIF1() * 100) + "%";                                     
   }    

   public String getStringFormattedImportanceFactorSIF2() {
       DecimalFormat format = new DecimalFormat("#.##");
       return format.format(getImportanceFactorSIF2() * 100) + "%";                                     
   }    

   public String getStringFormattedImportanceFactorSIF3() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF3());                                     
   }    

   public String getStringFormattedImportanceFactorSIF4() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF4());                                     
   }    

   public String getStringFormattedImportanceFactorSIF5() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF5());                                     
   }    

   public String getStringFormattedImportanceFactorSIF6() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF6());                                     
   }    

   public String getStringFormattedImportanceFactorSIF7() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF7());                                     
   }    

   public String getStringFormattedImportanceFactorSIF8() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF8());                                     
   }    

   public String getStringFormattedImportanceFactorSIF9() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF9());                                     
   }    

   public String getStringFormattedImportanceFactorSIF10() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF10());                                     
   }    

   public String getStringFormattedImportanceFactorSIF11() {
       DecimalFormat format = new DecimalFormat("#.##");
       return format.format(getImportanceFactorSIF11() * 100) + "%";                                     
   }    

   public String getStringFormattedImportanceFactorSIF12() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF12());                                     
   }    

   public String getStringFormattedImportanceFactorSIF13() {
       DecimalFormat format = new DecimalFormat("#.##");
       return format.format(getImportanceFactorSIF13() * 100) + "%";                                     
   }    

   public String getStringFormattedImportanceFactorSIF14() {
       DecimalFormat format = new DecimalFormat("#.##");
       return format.format(getImportanceFactorSIF14() * 100) + "%";                                     
   }    

   public String getStringFormattedImportanceFactorSIF15() {
       DecimalFormat format = new DecimalFormat("#.###");
       return format.format(getImportanceFactorSIF15());                                     
   }    

   public String getStringFormattedImportanceFactorSIF16() {
       DecimalFormat format = new DecimalFormat("#.##");
       return format.format(getImportanceFactorSIF16() * 100) + "%";                                     
   }    
   
   // -------- getter/setter --------

    public void setNrOfRelevantPlans(long nrOfRelevantPlans) {
        this.nrOfRelevantPlans = nrOfRelevantPlans;
    }

    public long getNrOfRelevantPlans() {
        return nrOfRelevantPlans;
    }
    
    public void setChildren(List<CriteriaTreeNode> children) {
        this.children = children;
    }

    public List<CriteriaTreeNode> getChildren() {
        return children;
    }
}
