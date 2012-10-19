package eu.scape_project.planning.model.values;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.scape_project.planning.model.values.TargetValue;
import eu.scape_project.planning.model.values.TargetValues;

public class TargetValuesTest {

    private static final double EPSILON = 0.00001;

    @Test
    public void testWorstEmptyList() {
        TargetValues values = new TargetValues();
        assertEquals(0.0, values.worst(), EPSILON);
    }

    @Test
    public void testWorstOneValue() {
        TargetValues values = new TargetValues();
        values.list().add(new TargetValue(1.0));
        assertEquals(1.0, values.worst(), EPSILON);
    }

    @Test
    public void testWorstAscendingValues() {
        TargetValues values = new TargetValues();
        values.getList().add(new TargetValue(1.5));
        values.getList().add(new TargetValue(2.0));
        values.getList().add(new TargetValue(5.0));

        assertEquals(1.5, values.worst(), EPSILON);
    }

    @Test
    public void testWorstDescendingValues() {
        TargetValues values = new TargetValues();
        values.getList().add(new TargetValue(5.0));
        values.getList().add(new TargetValue(2.0));
        values.getList().add(new TargetValue(1.5));

        assertEquals(1.5, values.worst(), EPSILON);
    }

    @Test
    public void testAverageEmptyList() {
        TargetValues values = new TargetValues();
        assertEquals(0.0, values.average(), EPSILON);
    }

}
