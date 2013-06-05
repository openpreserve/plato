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
package eu.scape_project.planning.plato.wf;

import java.util.HashMap;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.scape_project.planning.application.PlatoDeploymentBuilder;

@RunWith(Arquillian.class)
public class DefineAlternativesIT {

    @Inject
    @Bound
    private BoundConversationContext conversationContext;

    @Inject
    private DefineAlternatives action;

    @Deployment
    public static WebArchive createDeployment() {
        
        WebArchive platoWar = PlatoDeploymentBuilder.createPlatoWebArchive();
        platoWar
            .addClasses(AbstractWorkflowStep.class, DefineAlternatives.class, DefineAlternativesIT.class);
            ;
            
        return platoWar;
    }
    

    @Test
    public void test() {
        conversationContext.associate(new MutableBoundRequest(
            new HashMap<String, Object>(), new HashMap<String, Object>()));
        conversationContext.activate();

        Assert.assertTrue(conversationContext.isActive());
        Assert.assertNotNull(action);
        action.init(null);
        
    }

}
