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
package eu.scape_project.planning.model.transform;

import eu.scape_project.planning.model.values.INumericValue;
import eu.scape_project.planning.model.values.IOrdinalValue;
import eu.scape_project.planning.model.values.TargetValue;

/**
 * Declares both types of possible transformations - numeric and ordinal.
 * Subclasses can choose to only implement one of these and throw an UnsupportedOperation
 * if their unsupported operations are falsely called.
 * @author Christoph Becker
 */
public interface ITransformer {
    public TargetValue transform(INumericValue v);
    public TargetValue transform(IOrdinalValue v);

}
