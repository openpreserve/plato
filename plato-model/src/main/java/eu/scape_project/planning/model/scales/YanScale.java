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
package eu.scape_project.planning.model.scales;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import eu.scape_project.planning.model.values.YanValue;


@Entity
@DiscriminatorValue("Y")
public class YanScale extends OrdinalScale {

    private static final long serialVersionUID = 595959962684173519L;

    public  String getDisplayName() {
        return "Yes, Acceptable, No";
    }
    
    public YanValue createValue() {
        YanValue v = new YanValue();
        v.setScale(this);
        return v;
    }
    
    
    public YanScale() {
/*        list.add("Yes");
        list.add("Acceptable");
        list.add("No");
*/
        super.setRestriction("Yes/Acceptable/No");
        // this is a Yan-value, the restrictions above must not be changed
        immutableRestriction = true;
    }
    /*
     * this restriction cannot be changed
     */
    @Override
    public void setRestriction(String restriction) {
    }

}
