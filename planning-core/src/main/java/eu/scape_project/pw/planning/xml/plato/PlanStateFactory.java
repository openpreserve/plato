package eu.scape_project.pw.planning.xml.plato;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

import eu.planets_project.pp.plato.model.PlanState;

public class PlanStateFactory extends AbstractObjectCreationFactory<PlanState> {

    @Override
    public PlanState createObject(Attributes arg0) throws Exception {
        String value = arg0.getValue("value");
        try {
			return PlanState.valueOf(Integer.valueOf(value).intValue());
		} catch (Exception e) {
			// reset it to first step
			return PlanState.CREATED;
		}
    }

}
