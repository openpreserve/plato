/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.taverna.generator.model;

/**
 * An output port definition.
 */
public class OutputPort {
    private String name;
    private int depth;

    private String semanticAnnotation;

    /**
     * Creates a new output port with no semantic annotation and depth 0.
     * 
     * @see {@link #OutputPort(String, int)}
     */
    public OutputPort(String name) {
        this.name = name;
    }

    /**
     * Creates a new output port with no semantic annotation.
     * 
     * @param name
     *            the port name
     * @param depth
     *            the port depth
     */
    public OutputPort(String name, int depth) {
        this(name);
        this.depth = depth;
    }

    /**
     * Creates a new output port with depth 0.
     * 
     * @param name
     *            the port name
     * @param semanticAnnotation
     *            the port's semantic annotation
     */
    public OutputPort(String name, String semanticAnnotation) {
        this(name);
        this.semanticAnnotation = semanticAnnotation;
    }

    // ---------- getter/setter ----------
    public String getName() {
        return name;
    }

    public int getDepth() {
        return depth;
    }

    public String getSemanticAnnotation() {
        return semanticAnnotation;
    }
}
