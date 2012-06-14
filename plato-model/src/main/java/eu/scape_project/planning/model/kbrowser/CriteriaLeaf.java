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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import eu.scape_project.planning.model.measurement.Criterion;

@Entity
@DiscriminatorValue("L")
public class CriteriaLeaf extends CriteriaTreeNode {
    private static final long serialVersionUID = 7451573756065378955L;

    @ManyToOne
    private Criterion criterion;

    private Boolean mapped;

    @Transient
    private List<VPlanLeaf> planLeaves;

    @Transient
    private long nrOfRelevantPlans = -1L;

    @Transient
    private int plansUsingCriterion = 0;

    public CriteriaLeaf() {
        criterion = null;
        name = "SELECT A CRITERION -->";
        mapped = false;
        planLeaves = new ArrayList<VPlanLeaf>();
    }

    public CriteriaLeaf(long nrOfRelevantPlans) {
        criterion = null;
        name = "SELECT A CRITERION -->";
        mapped = false;
        planLeaves = new ArrayList<VPlanLeaf>();
        this.nrOfRelevantPlans = nrOfRelevantPlans;
    }


    @Override
    public boolean isLeaf() {
        return true;
    }

    public HashSet<Integer> getUsingPlans() {
        HashSet<Integer> plans = new HashSet<Integer>();

        for (VPlanLeaf leaf : planLeaves) {
            int planId = leaf.getPlanId();
            plans.add(planId);
        }

        return plans;
    }

    /**
     * Method necessary for view element "rich:recursiveTreeNodesAdaptor" which
     * requires for each displayed node its associated children.
     * 
     * @return An empty List because a leaf does not have children.
     */
    public List<CriteriaTreeNode> getChildren() {
        return new ArrayList<CriteriaTreeNode>();
    }

    // -------- single criterion impact factors --------

    /**
     * Method responsible for calculating the importance factor IF1. IF1(Count)
     * specifies the number of plans using this criterion.
     * 
     * @return Importance factor IF1.
     */
    public double getImportanceFactorIF1() {

        return plansUsingCriterion;
    }

    /**
     * Method responsible for calculating the importance factor IF2. IF2(Spread)
     * specifies the percentage of plans using this criterion IF2 = number of
     * plans using this criterion / number of all plans
     * 
     * @return Importance factor IF2.
     */
    public double getImportanceFactorIF2() {
        if (nrOfRelevantPlans == 0) {
            return 0;
        }

        double result = (double) plansUsingCriterion / (double) nrOfRelevantPlans;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF3. IF3(Weight)
     * specifies the average total weight of this criterion.
     * 
     * @return Importance factor IF3.
     */
    public double getImportanceFactorIF3() {
        double sum = 0;
        double count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getTotalWeight();
            if (value != -1) {
                sum = sum + value;
                count++;
            }
        }

        // if no plan leaf exists or deliver results
        if (count == 0) {
            return 0;
        }

        double result = sum / count;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF4.
     * IF4(Discounted Weight) specifies the sum of total weights of this
     * criterion, divided by the number of plans. IF4 = sum of total weights of
     * this criterion / number of all plans
     * 
     * @return Importance factor IF4.
     */
    public double getImportanceFactorIF4() {
        if (nrOfRelevantPlans == 0) {
            return 0;
        }

        double weightSum = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            weightSum = weightSum + pLeaf.getTotalWeight();
        }

        double result = weightSum / (double) nrOfRelevantPlans;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF5.
     * IF5(Potential) specifies the average potential output range of this
     * criterion
     * 
     * @return Importance factor IF5.
     */
    public double getImportanceFactorIF5() {
        double sum = 0;
        double count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getPotentialOutputRange();
            if (value != -1) {
                sum = sum + value;
                count++;
            }
        }

        // if no plan leaf exists or deliver results
        if (count == 0) {
            return 0;
        }

        double result = sum / count;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF6. IF6(Range)
     * specifies the average actual output range of this criterion.
     * 
     * @return Importance factor IF6.
     */
    public double getImportanceFactorIF6() {
        double sum = 0;
        double count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getActualOutputRange();
            if (value != -1) {
                sum = sum + value;
                count++;
            }
        }

        // if no plan leaf exists or deliver results
        if (count == 0) {
            return 0;
        }

        double result = sum / count;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF7.
     * IF7(Discounted Potential) specifies the sum of all criterion potential
     * output ranges, divided by the number of all plans. IF7 = sum of all
     * criterion potential output ranges / number of all plans
     * 
     * @return Importance factor LIF7.
     */
    public double getImportanceFactorIF7() {
        if (nrOfRelevantPlans == 0) {
            return 0;
        }

        double sum = 0;
        int count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getPotentialOutputRange();
            if (value != -1) {
                sum = sum + value;
                count++;
            }
        }

        // if no plan leaf exists or deliver results
        if (count == 0) {
            return 0;
        }

        double result = sum / (double) nrOfRelevantPlans;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF8.
     * IF8(Discounted Range) specifies the sum of all criterion actual output
     * ranges, divided by the number of all plans. IF8 = sum of all criterion
     * actual output ranges / number of all plans
     * 
     * @return Importance factor IF8.
     */
    public double getImportanceFactorIF8() {
        if (nrOfRelevantPlans == 0) {
            return 0;
        }

        double sum = 0;
        int count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getActualOutputRange();
            if (value != -1) {
                sum = sum + value;
                count++;
            }
        }

        // if no plan leaf exists or deliver results
        if (count == 0) {
            return 0;
        }

        double result = sum / (double) nrOfRelevantPlans;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF9. IF9(Maximum
     * Potential) specifies the maximum potential output range.
     * 
     * @return Importance factor IF9.
     */
    public double getImportanceFactorIF9() {
        double maxOR = 0;

        int count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getPotentialOutputRange();
            if (value != -1) {
                if (value > maxOR) {
                    maxOR = value;
                }
                count++;
            }
        }

        if (count == 0) {
            return 0;
        }

        return maxOR;
    }

    /**
     * Method responsible for calculating the importance factor IF10.
     * IF10(Maximum Range) specifies the maximum actual output range.
     * 
     * @return Importance factor IF10.
     */
    public double getImportanceFactorIF10() {
        double maxOR = 0;

        int count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getActualOutputRange();
            if (value != -1) {
                if (value > maxOR) {
                    maxOR = value;
                }
                count++;
            }
        }

        if (count == 0) {
            return 0;
        }

        return maxOR;
    }

    /**
     * Method responsible for calculating the importance factor IF11.
     * IF11(Variation) specifies the average relative output range.
     * 
     * @return Importance factor IF11.
     */
    public double getImportanceFactorIF11() {
        double sum = 0;
        double count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getRelativeOutputRange();
            if (value != -1) {
                sum = sum + value;
                count++;
            }
        }

        // if no plan leaf exists or deliver results
        if (count == 0) {
            return 0;
        }

        double result = sum / count;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF12.
     * IF12(Maximum Variation) specifies the maximum relative output range.
     * 
     * @return Importance factor IF12.
     */
    public double getImportanceFactorIF12() {
        double maxOR = 0;

        int count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            double value = pLeaf.getRelativeOutputRange();
            if (value != -1) {
                if (value > maxOR) {
                    maxOR = value;
                }
                count++;
            }
        }

        if (count == 0) {
            return 0;
        }

        return maxOR;
    }

    /**
     * Method responsible for calculating the importance factor IF13.
     * IF13(Rejection Potential Count) specifies the number of decision criteria
     * with alternative rejection potential.
     * 
     * @return Importance factor IF13.
     */
    public double getImportanceFactorIF13() {
        if (planLeaves.size() == 0) {
            return 0;
        }

        double result = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            if (pLeaf.hasKOPotential()) {
                result++;
            }
        }

        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF14.
     * IF14(Rejection Potential Rate) specifies the percentage of decision
     * criteria with alternative rejection potential. IF14 = number of decision
     * criteria with alternative rejection potential / number of decision
     * criteria
     * 
     * @return Importance factor IF14.
     */
    public double getImportanceFactorIF14() {
        if (planLeaves.size() == 0) {
            return 0;
        }

        double sum = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            if (pLeaf.hasKOPotential()) {
                sum++;
            }
        }

        double result = sum / (double) planLeaves.size();
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF15.
     * IF15(Rejection Count) specifies the number of decision criteria actually
     * rejecting alternatives.
     * 
     * @return Importance factor IF15.
     */
    public double getImportanceFactorIF15() {
        if (planLeaves.size() == 0) {
            return 0;
        }

        double result = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            if (pLeaf.getActualKO() > 0) {
                result++;
            }
        }

        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF16.
     * IF16(Rejection Rate) specifies the percentage of decision criteria
     * actually rejecting alternatives. IF16 = number of decision criteria
     * actually rejecting alternatives / number of decision criteria
     * 
     * @return Importance factor IF16.
     */
    public double getImportanceFactorIF16() {
        if (planLeaves.size() == 0) {
            return 0;
        }

        double sum = 0;
        double count = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            if (pLeaf.getActualKO() > 0) {
                sum++;
            }
            count++;
        }

        double result = sum / count;
        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF17.
     * IF17(Reject Count) specifies the actually rejected alternatives.
     * 
     * @return Importance factor IF17.
     */
    public double getImportanceFactorIF17() {
        if (planLeaves.size() == 0) {
            return 0;
        }

        double result = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            result = result + pLeaf.getActualKO();
        }

        return result;
    }

    /**
     * Method responsible for calculating the importance factor IF18.
     * IF18(Reject Rate) specifies the percentage of alternatives actually
     * rejected. IF18 = alternatives actually rejected / all alternatives
     * 
     * @return Importance factor IF18.
     */
    public double getImportanceFactorIF18() {
        if (planLeaves.size() == 0) {
            return 0;
        }

        double koSum = 0;
        double alternativeSum = 0;
        for (VPlanLeaf pLeaf : planLeaves) {
            koSum = koSum + pLeaf.getActualKO();
            alternativeSum = alternativeSum + pLeaf.getValueMap().size();
        }

        if (alternativeSum == 0) {
            return 0;
        }

        return koSum / alternativeSum;
    }

    /**
     * Calculates the importance factor: "robustness" - this is: to which extend
     * can the measured value change, without impact on the winning alternative.
     * 
     * @return
     */
    public double calculateImportanceFactorIF19()  {
        // TODO calculate robustness
        // we need the overall results for each alternatives
        for (VPlanLeaf pLeaf : planLeaves) {

        }
        return 0;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setMapped(Boolean mapped) {
        this.mapped = mapped;
    }

    public Boolean getMapped() {
        return mapped;
    }

    /**
     * Sets plan leaves and recalculates the number of plans which use this
     * criterion. (the list is transient, so recalculation is ok)
     * 
     * @param planLeaves
     */
    public void setPlanLeaves(List<VPlanLeaf> planLeaves) {
        this.planLeaves = planLeaves;
        plansUsingCriterion = getUsingPlans().size();
    }

    public List<VPlanLeaf> getPlanLeaves() {
        return planLeaves;
    }

    public void setNrOfRelevantPlans(long nrOfRelevantPlans) {
        this.nrOfRelevantPlans = nrOfRelevantPlans;
    }

    public long getNrOfRelevantPlans() {
        return nrOfRelevantPlans;
    }

}
