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

package eu.planets_project.pp.plato.model;

/**
 * Classes implementing this interface allow to update their changed status stored in
 * {@link eu.planets_project.pp.plato.model.ChangeLog}.
 *
 * @author Hannes Kulovits
 */
public interface ITouchable {

    /**
     * Tell the object that it has been changed by calling this method.
     */
    public void touch();

    /**
     * @return true if the object has been changed at some time.
     */
    public boolean isChanged();

    /**
     * Returns the {@link ChangeLog} of the object, so a changes handler can access it.
     *
     * @return ChangeLog
     */
    public ChangeLog getChangeLog();

    /**
     * This function ensures that a {@link IChangesHandler} can visit every touchable object in the model.
     *
     * The object has to pass itself to the changes handler <code>h</code> via {@link IChangesHandler#visit(ITouchable)}.
     * And also all aggregated children which implement ITouchable themselves, by calling their
     * handleChanges() method.
     *
     * @param h
     */
    public void handleChanges(IChangesHandler h);
}
