package eu.scape_project.planning.model.transform;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import eu.scape_project.planning.model.TargetValueObject;
import eu.scape_project.planning.model.values.OrdinalValue;
import eu.scape_project.planning.model.values.TargetValue;

public class OrdinalTransformerTest {

	@Test
	public void testTransform() {
		OrdinalTransformer ord = new OrdinalTransformer();
		TargetValueObject t1 = new TargetValueObject();
		t1.setValue(1.4);
		TargetValueObject t2 = new TargetValueObject();
		t2.setValue(2.4);
		TargetValueObject t3 = new TargetValueObject();
		t3.setValue(3.2);
		Map<String,TargetValueObject> map = new HashMap<String,TargetValueObject>();
		map.put("GOOD", t1);
		map.put("BAD", t2);
		map.put("HORRIBLE", t3);
		ord.setMapping(map);
		OrdinalValue value = new OrdinalValue();
		value.setValue("GOOD");
		TargetValue r = ord.transform(value);
		Assert.assertTrue(r.getValue()==1.4);
		//value.setValue("EXCELENT");
		//r = ord.transform(value);
		//Assert.assertTrue(r.getValue()==null);
	}
}
