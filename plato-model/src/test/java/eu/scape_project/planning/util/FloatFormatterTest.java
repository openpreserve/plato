package eu.scape_project.planning.util;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import eu.scape_project.planning.model.util.FloatFormatter;

public class FloatFormatterTest {

  /**
   * Object under test.
   */
  private FloatFormatter formatter;
  
  @Before
  public void setup() {
    this.formatter = new FloatFormatter();
  }
  
  @After
  public void tearDown() {
    this.formatter = null;
  }
  
  
  @Ignore @Test
  public void shouldTestFormatNullFloatPrecisly() throws Exception {
    Double d = null;
    
    String string = this.formatter.formatFloatPrecisly(d);
    
    Assert.assertNull(string);
    
  }
  
  @Ignore @Test
  public void shouldTestFormatScientificFloatPrecisly() throws Exception {
    Double d = new Double(10000000000000000000000d);
    String string = this.formatter.formatFloatPrecisly(d);
    
    System.out.println(string);
    Assert.assertTrue(string.contains("E"));
    Assert.assertEquals(" 1E22", string); //TODO inspect if this whitespace is correct in front of the metric...
  }
}
