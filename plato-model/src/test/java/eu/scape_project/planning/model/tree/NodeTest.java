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
package eu.scape_project.planning.model.tree;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.scape_project.planning.validation.ValidationError;

public class NodeTest {
    @Test
    public void isCompletelySpecified() {
        Node node = new Node();
        Leaf leaf = new Leaf();
        leaf.setName("Name");
        node.addChild(leaf);
        node.addChild(leaf);
        Leaf leaf2 = new Leaf();
        leaf2.setName("Name2");
        node.addChild(leaf2);
        node.addChild(leaf2);
        List<ValidationError> errors = new ArrayList<ValidationError>();
        // System.out.println(node.getChildren().size());
        node.isCompletelySpecified(errors);
        // System.out.println(errors.size());
        // for (ValidationError error : errors) {
        // System.out.println(error.getMessage());
        // }

        assert (errors.size() == 2);
    }
}
