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
package eu.scape_project.planning.services.myexperiment.domain;

/**
 * Contains a constants related to components and component profiles.
 */
public final class ComponentConstants {

    /**
     * Private constructor to avoid instantiation.
     */
    private ComponentConstants() {
    }

    public static final String VALUE_SOURCE_OBJECT = "http://purl.org/DP/components#SourceObject";

    public static final String VALUE_TARGET_OBJECT = "http://purl.org/DP/components#TargetObject";

    public static final String VALUE_LEFT_OBJECT = "http://purl.org/DP/components#LeftObject";

    public static final String VALUE_RIGHT_OBJECT = "http://purl.org/DP/components#RightObject";

    public static final String VALUE_PARAMETER = "http://purl.org/DP/components#Parameter";

    public static final String VALUE_STATUS = "http://purl.org/DP/components#StatusValue";
}
