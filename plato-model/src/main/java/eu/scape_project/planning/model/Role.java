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

import java.io.Serializable;

//@Entity
public class Role implements Serializable, Comparable<Role> {

    /**
     * 
     */
    private static final long serialVersionUID = 2613722499444439263L;

    // @Id
    // @GeneratedValue
    // private int id;

    private String name;

    // public int getId() {
    // return id;
    // }
    //
    // public void setId(int id) {
    // this.id = id;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Role r) {
        if (r != null && r.getName().equals(name)) {
            return 0;
        }
        return 1;
    }

    public boolean equals(Object o) {
        return (o instanceof Role && ((Role) o).getName().equals(name));
    }
}
