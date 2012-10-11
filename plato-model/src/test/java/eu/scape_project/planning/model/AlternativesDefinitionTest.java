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
package eu.scape_project.planning.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.scape_project.planning.exception.PlanningException;

public class AlternativesDefinitionTest {
    private AlternativesDefinition alternativesDefinition;

    public AlternativesDefinitionTest() {
        alternativesDefinition = new AlternativesDefinition();
    }

    @Test(expected = PlanningException.class)
    public void addAlternative_addAlternativeWithAlreadyExistingNameThrowsException() throws PlanningException {
        // existing alternatives
        Alternative alt1 = new Alternative("alt1", "alt1");
        Alternative alt2 = new Alternative("alt2", "alt2");
        alternativesDefinition.addAlternative(alt1);
        alternativesDefinition.addAlternative(alt2);

        // new alternative
        Alternative altToAdd = new Alternative("alt2", "alt2");

        alternativesDefinition.addAlternative(altToAdd);
    }

    @Test
    public void addAlternative_addNewAlternativeWithUniqueNameIsAdded() throws PlanningException {
        // existing alternatives
        Alternative alt1 = new Alternative("alt1", "alt1");
        Alternative alt2 = new Alternative("alt2", "alt2");
        alternativesDefinition.addAlternative(alt1);
        alternativesDefinition.addAlternative(alt2);

        // new alternative
        Alternative altToAdd = new Alternative("unique name", "unique name");
        alternativesDefinition.addAlternative(altToAdd);

        assertEquals(3, alternativesDefinition.getAlternatives().size());
    }

    @Test
    public void renameAlternative_renameAlternativeToTheSameNameDoesNothing() throws PlanningException {
        // existing alternatives
        Alternative alt1 = new Alternative("alt1", "alt1");
        Alternative alt2 = new Alternative("alt2", "alt2");
        alternativesDefinition.addAlternative(alt1);
        alternativesDefinition.addAlternative(alt2);

        alternativesDefinition.renameAlternative(alt2, "alt2");

        assertEquals("alt2", alt2.getName());
    }

    @Test(expected = PlanningException.class)
    public void renameAlternative_renameAlternativeToNotUniqueNameThrowsException() throws PlanningException {
        // existing alternatives
        Alternative alt1 = new Alternative("alt1", "alt1");
        Alternative alt2 = new Alternative("alt2", "alt2");
        alternativesDefinition.addAlternative(alt1);
        alternativesDefinition.addAlternative(alt2);

        alternativesDefinition.renameAlternative(alt2, "alt1");
    }

    @Test(expected = PlanningException.class)
    public void renameAlternative_renameUnknownAlternativeThrowsException() throws PlanningException {
        // existing alternatives
        Alternative alt1 = new Alternative("alt1", "alt1");
        Alternative alt2 = new Alternative("alt2", "alt2");
        alternativesDefinition.addAlternative(alt1);
        alternativesDefinition.addAlternative(alt2);

        Alternative unknownAlternative = new Alternative("unknown", "unkown");

        alternativesDefinition.renameAlternative(unknownAlternative, "xxxx");
    }

    @Test
    public void renameAlternative_renameAlternativeToUniqueNameSucceeds() throws PlanningException {
        // existing alternatives
        Alternative alt1 = new Alternative("alt1", "alt1");
        Alternative alt2 = new Alternative("alt2", "alt2");

        alternativesDefinition.addAlternative(alt1);
        alternativesDefinition.addAlternative(alt2);

        alternativesDefinition.renameAlternative(alt2, "newName");

        assertEquals("newName", alt2.getName());
    }

}
