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

import org.junit.Test;

import eu.scape_project.planning.model.values.FloatValue;

public class FloatValueTest {

    @Test
    public void testFormattedValue() {
        FloatValue fv = new FloatValue();

        double d = 1.234567890123450;
        double d2 = d;

        for (int i = 1; i < 30; i++) {
            fv.setValue(d);
            // System.out.println(fv.getFormattedValue());
            fv.setValue(-d);
            // System.out.println(fv.getFormattedValue());
            fv.setValue(d2);
            // System.out.println(fv.getFormattedValue());
            fv.setValue(-d2);
            // System.out.println(fv.getFormattedValue());
            d = d * 10;
            d2 = d2 / 10.;
        }

    }

}
