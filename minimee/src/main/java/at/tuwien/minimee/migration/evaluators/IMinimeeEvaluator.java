/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package at.tuwien.minimee.migration.evaluators;

import java.util.List;

import eu.scape_project.planning.model.measurement.Measurement;

public interface IMinimeeEvaluator {
    public List<Measurement> evaluate(String tempDir, String inFile,String outFile);
    public void setConfigParam(String configParam);
    public String getConfigParam();
    public String getName();
    public void setName(String name);
}
