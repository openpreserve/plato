/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
package eu.scape_project.planning.model.transform;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.values.INumericValue;
import eu.scape_project.planning.model.values.IOrdinalValue;
import eu.scape_project.planning.model.values.TargetValue;
import eu.scape_project.planning.validation.ValidationError;

/**
 * Transforms numeric values to the {@link TargetValue}.
 * For Transformation modes see {@link TransformationMode}. 
 *
 * @author Christoph Becker & Stephan Strodl
 */
@Entity
@DiscriminatorValue("N")
public class NumericTransformer extends Transformer {

    private static final long serialVersionUID = 5425443938944214916L;

    private Double threshold1 = 0.0;
    private Double threshold2 = 0.0;
    private Double threshold3 = 0.0;
    private Double threshold4 = 0.0;
    private Double threshold5 = 0.0;
    
    
    public Double getThreshold1() {
        return threshold1;
    }



    public void setThreshold1(Double threshold1) {
        this.threshold1 = threshold1;
    }



    public Double getThreshold2() {
        return threshold2;
    }



    public void setThreshold2(Double threshold2) {
        this.threshold2 = threshold2;
    }



    public Double getThreshold3() {
        return threshold3;
    }



    public void setThreshold3(Double threshold3) {
        this.threshold3 = threshold3;
    }



    public Double getThreshold4() {
        return threshold4;
    }



    public void setThreshold4(Double threshold4) {
        this.threshold4 = threshold4;
    }



    public Double getThreshold5() {
        return threshold5;
    }



    public void setThreshold5(Double threshold5) {
        this.threshold5 = threshold5;
    }

   
    @Enumerated
    private TransformationMode mode = TransformationMode.THRESHOLD_STEPPING;

    public void setMode(TransformationMode mode) {
        this.mode = mode;
    }

   
    
    public void defaults(double best,double worst) {
        threshold1 = worst;
        threshold5 = best;
        
        threshold2 = worst+((best-worst)/4);
        threshold3 = worst+((best-worst)/4)*2;
        threshold4 = worst+((best-worst)/4)*3;
    }
    

    /**
     * Transforms the provided value to a target value. According to the {@link #mode transformation mode},
     * either {@link #thresholdstepping(double)} or {@link #linear(double)} is used. 
     * 
     * 
     * @param d value to be transformed
     * @return transformed {@link TargetValue}
     */
    private TargetValue doTransform(double d) {
        TargetValue v = new TargetValue();
        switch (mode) {
        case THRESHOLD_STEPPING:
            v.setValue(thresholdstepping(d));
            break;
        case LINEAR:
            v.setValue(linear(d));
            break;
        default:
            LoggerFactory.getLogger(this.getClass()).error(
                    "TransformationMode is not set correctly.");
        }
        return v;
    }


    public TargetValue transform(INumericValue v) {
        return doTransform(v.value());
    }

    /**
     * This operation is not supported. Always throws an {@link UnsupportedOperationException}
     * @throws UnsupportedOperationException because this is the numeric transformer.
     */
    public TargetValue transform(IOrdinalValue v) {
        throw new UnsupportedOperationException("Cannot transform ordinal values. only numeric ones!");
    }


    /**
     * Depending on {@link #thresholds} a value between 0 and 5 is returned. Transformation is
     * performed without interpolation.
     *
     * @see #linear(double)
     * @param value to be transformed
     * @return transformed value depending on the {@link #thresholds}
     */
    private double thresholdstepping(double value) {
        if (threshold1 < threshold5) // increasing thresholdscale
        {
            if (value < threshold1) {
                return 0;
            } else if (value < threshold2) {
                return 1;
            } else if (value < threshold3) {
                return 2;
            } else if (value < threshold4) {
                return 3;
            } else if (value < threshold5) {
                return 4;
            } else {
                return 5;
            }

        } else // decreasing thresholdscale
        {
            if (value > threshold1) {
                return 0;
            } else if (value > threshold2) {
                return 1;
            } else if (value > threshold3) {
                return 2;
            } else if (value > threshold4) {
                return 3;
            } else if (value > threshold5) {
                return 4;
            } else {
                return 5;
            }
        }
    }

    /**
     * In contrast to {@link #thresholdstepping(double) threshold stepping}, linear transformation 
     * interpolates values between thresholds.
     *
     * @param value to be transformed
     * @return value (linearly interopolated) depending on the {@link #thresholds}
     */
    private double linear(double value) {
        if (threshold1 < threshold5) // increasing thresholdscale
        {
            if (value < threshold1) {
                return 0;
            } else if (value < threshold2) {
                return ((value - threshold1) // difference to previous threshold 
                        / (threshold2 - threshold1))
                          // difference between the surrounding thresholds 
                        + 1; // plus the fixed value that results from being over the previous boundary
            } else if (value < threshold3) {
                return ((value - threshold2)
                        / (threshold3 - threshold2)) + 2;
            } else if (value < threshold4) {
                return ((value - threshold3)
                        / (threshold4 - threshold3)) + 3;
            } else if (value >= threshold5) {
                return 5;
            } else {
                return ((value - threshold4)
                        / (threshold5 - threshold4)) + 4;
            }

        } else // decreasing thresholdscale
        {
            if (value > threshold1) {
                return 0;
            } else if (value > threshold2) {
                return ((value - threshold1)
                        / (threshold2 - threshold1)) + 1;
            } else if (value > threshold3) {
                return ((value - threshold2)
                        / (threshold3 - threshold2)) + 2;
            } else if (value > threshold4) {
                return ((value - threshold3)
                        / (threshold4 - threshold3)) + 3;
            } else if (value <= threshold5) {
                return 5;
            } else {
                return ((value - threshold4)
                        / (threshold5 - threshold4)) + 4;
            }
        }
    }

    public TransformationMode getMode() {
        return mode;
    }

    /**
     * checks if the order of thresholds is consistent
     * @return true iff threshold are consistently ascending  or consistently descending or consistently identical 
     * (e.g. initialised to 0.0)
     */
    public boolean checkOrder() {
        int signum = Integer.signum(threshold1.compareTo(threshold2));
        return ((signum == Integer.signum(threshold2.compareTo(threshold3)))
          && (signum == Integer.signum(threshold3.compareTo(threshold4)))
          && (signum == Integer.signum(threshold4.compareTo(threshold5))));
    }

    /**
     * A NumericTransformer is correctly configured if the thresholds are
     * <b>strictly</b> either ascending or descending.
     * 
     * @return true if thresholds are in proper order
     */
    public boolean isTransformable(List<ValidationError> errors) {
        boolean toReturn = true;
        if (!checkOrder())  {
            errors.add(new ValidationError("The order of thresholds is not consistent.", this));
            toReturn = false;
        }
        return toReturn;
    }

    @Override
    public Transformer clone() {
        NumericTransformer nt = new NumericTransformer();
        nt.setId(0);
        nt.setMode(mode);
        nt.setThreshold1(threshold1);
        nt.setThreshold2(threshold2);
        nt.setThreshold3(threshold3);
        nt.setThreshold4(threshold4);
        nt.setThreshold5(threshold5);
        return nt;
    }
}
