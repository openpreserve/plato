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
package eu.scape_project.planning.model.sensitivity;

import eu.scape_project.planning.model.tree.TreeNode;

/**
 * This implementation of IWeightModifier makes the weights of the children more extreme.
 * E.g. if you have two children with A=0.2 and B=0.8 this modifier will change them to
 * A=0.1 and B=0.9 (drag them even more apart).
 * @author Jan Zarnikov
 *
 */
public class ExtremeWeightModifier implements IWeightModifier {

    public boolean performModification(TreeNode node) {
        int nodeCount = node.getChildren().size();
        double mean = 1/ ((double) nodeCount);
        
        // make everything thats below mean even smaller and everything above even bigger
        for(TreeNode child : node.getChildren()) {
            double oldWeight = child.getWeight();
            
            if(oldWeight == 0 || oldWeight == 1) {
                continue;
            }
            
            // TODO: optimize this!
            if(oldWeight < mean) {
                double deltaWeight = oldWeight;
                double newWeight = oldWeight - deltaWeight/2;
                child.setWeight(newWeight);
            } else if (oldWeight > mean) {
                double deltaWeight = 1 - oldWeight;
                double newWeight = oldWeight + deltaWeight/2;
                child.setWeight(newWeight);
            }
            // note that after this, the weights might not be normalized (sum!=1)
            // this doesn't matter since they will be normalized by the test anyway
            // (as specified by the interface IWeightModifier)
        }
        return false;
    }

}
