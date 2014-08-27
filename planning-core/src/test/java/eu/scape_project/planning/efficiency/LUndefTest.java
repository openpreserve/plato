package eu.scape_project.planning.efficiency;

import org.junit.Assert;
import org.junit.Test;
import org.supercsv.exception.SuperCsvCellProcessorException;

public class LUndefTest {
    
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testNullNotAccepted() {
        LUndef lundef = new LUndef();
        Assert.assertSame("NaN", lundef.execute(null, null));
    }

    @Test
    public void testMaxLongIsNaN() {
        LUndef lundef = new LUndef();
        Assert.assertSame("NaN", lundef.execute(Long.MAX_VALUE, null));
    }
    
    
    @Test
    public void testLongsToString() {
        LUndef lundef = new LUndef();

        Assert.assertEquals(""+(Long.MIN_VALUE+1), lundef.execute(Long.MIN_VALUE+1, null));
        Assert.assertEquals("0", lundef.execute(0L, null));
        Assert.assertEquals(""+(Long.MAX_VALUE-1), lundef.execute(Long.MAX_VALUE-1, null));
    }

}
