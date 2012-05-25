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
package eu.scape_project.pw.planning.plato.wf;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;

import eu.scape_project.planning.plato.wf.DefineAlternatives;

//@RunWith(Arquillian.class)
public class DefineAlternativesActionTest {
	
	@Inject
	private DefineAlternatives action; 
	
	@Deployment
	public static JavaArchive createTestArchive() {
	   return ShrinkWrap.create(JavaArchive.class, "platotest.jar")
	      .addClasses(DefineAlternatives.class)
	      .addAsManifestResource(
	            new ByteArrayAsset("<beans/>".getBytes()), 
	            ArchivePaths.create("beans.xml"));
	}
	
}
