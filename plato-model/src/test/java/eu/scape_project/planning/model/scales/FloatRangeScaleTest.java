package eu.scape_project.planning.model.scales;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FloatRangeScaleTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testValidateAndSetRestriction_Valid() {
		FloatRangeScale scale = new FloatRangeScale();
		assertEquals(true, scale.validateAndSetRestriction("1/1"));
		assertEquals(true, scale.validateAndSetRestriction(".1/1"));
		assertEquals(true, scale.validateAndSetRestriction("1/.1"));
		assertEquals(true, scale.validateAndSetRestriction("1/1.1"));
                assertEquals(true, scale.validateAndSetRestriction("1.1/1"));
		assertEquals(true, scale.validateAndSetRestriction("1/1.112312321"));
                assertEquals(true, scale.validateAndSetRestriction("1.25/4.8723"));
	}
	
	@Test
	public void testValidateAndSetRestriction_Invalid() {
		FloatRangeScale scale = new FloatRangeScale();
		assertEquals(false, scale.validateAndSetRestriction("1/"));
		assertEquals(false, scale.validateAndSetRestriction("11"));
		assertEquals(false, scale.validateAndSetRestriction("/"));
		assertEquals(false, scale.validateAndSetRestriction("/1"));
	}

}
