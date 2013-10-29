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
package eu.scape_project.planning.model.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.SampleAggregationMode;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.scales.FloatScale;
import eu.scape_project.planning.model.scales.IntegerScale;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.values.IntegerValue;
import eu.scape_project.planning.model.values.Value;

public class LeafTest {

    @Test
    @Ignore("True, a leaf should not be able to have children, but moving the list to Node would require massive changes.")
    public void test_Leaf_Can_Not_Contain_Children() {
        Leaf l1 = new Leaf();
        Leaf l2 = new Leaf();

        Leaf testLeaf = new Leaf();

        List<TreeNode> list = new ArrayList<TreeNode>();
        list.add(l1);
        list.add(l2);

        testLeaf.setChildren(list);
        Assert.assertTrue(testLeaf.getChildren().size() == 0);
    }

    public void testResetValues_List_Equals_Null() {

    }

    @Test
    public void testResetValues_Scale_Equals_Null() {
        Leaf l = new Leaf();
        Map<String, Values> map = new HashMap<String, Values>();
        map.put("test", new Values());
        l.setValueMap(map);
        Assert.assertTrue(l.getValueMap().size() == 1);
        l.resetValues(null);
        Assert.assertTrue(l.getValueMap().size() == 0);
    }

    @Test
    public void testResetValues_Different_Scale_In_Alternatives() {
        Leaf l = new Leaf();
        l.setScale(new FloatScale());
        Alternative a = new Alternative("test", "test");
        List<Alternative> alternatives = new ArrayList<Alternative>();
        alternatives.add(a);

        Map<String, Values> map = new HashMap<String, Values>();
        Value v1 = new IntegerValue();
        v1.setScale(new IntegerScale());
        Values values = new Values();
        values.add(v1);
        map.put("test", values);
        l.setValueMap(map);
        Assert.assertTrue(l.getValueMap().size() == 1);
        l.resetValues(alternatives);
        Assert.assertTrue(l.getValueMap().size() == 0);
    }

    @Test
    public void testSetDefaultTransformer_Scale_Equals_Null() {
        Leaf l = new Leaf();
        l.setDefaultTransformer();
        Assert.assertTrue(l.getTransformer() == null);
    }

    @Test
    public void testSetDefaultTransformer_Scale_Equals_Ordinal() {
        OrdinalScale o = new OrdinalScale();
        o.setRestriction("GOD/BAD/HORRIBLE");
        Leaf l = new Leaf();
        l.setScale(o);
        l.setDefaultTransformer();
        Assert.assertTrue(l.getTransformer() instanceof OrdinalTransformer);
        OrdinalTransformer ord = (OrdinalTransformer) l.getTransformer();
        Assert.assertTrue(ord.getMapping().containsKey("GOD"));
        Assert.assertTrue(ord.getMapping().containsKey("BAD"));
        Assert.assertTrue(ord.getMapping().containsKey("HORRIBLE"));
    }

    @Test
    public void testSetDefaultTransformer_Scale_Equals_NonOrdinal() {
        FloatScale o = new FloatScale();
        Leaf l = new Leaf();
        l.setScale(o);
        l.setDefaultTransformer();
        Assert.assertTrue(l.getTransformer() instanceof NumericTransformer);
    }

    public void testChangeScale_Scale_Equals_Null() {

    }

    @Test
    public void testChangeScale_Scale_Not_Null() {
        OrdinalScale ord = new OrdinalScale();
        Leaf l = new Leaf();
        l.changeScale(ord);
        Assert.assertFalse(l.getScale() == ord); // it is a clone
        Assert.assertTrue(l.getAggregationMode().equals(SampleAggregationMode.WORST));
    }

    @Test
    public void testRemoveValues() {
        Leaf l = new Leaf();
        Alternative a = new Alternative("test", "test");
        List<Alternative> alternatives = new ArrayList<Alternative>();
        alternatives.add(a);

        Map<String, Values> map = new HashMap<String, Values>();
        Value v1 = new IntegerValue();
        Value v2 = new IntegerValue();
        Value v3 = new IntegerValue();
        v1.setScale(new IntegerScale());
        Values values = new Values();
        values.add(v1);
        values.add(v2);
        values.add(v3);
        map.put("test", values);
        l.setValueMap(map);
        Assert.assertTrue(l.getValueMap().get("test").size() == 3);
        l.removeValues(alternatives, 1);
        Assert.assertTrue(l.getValueMap().get("test").size() == 2);
        Assert.assertTrue(!l.getValueMap().get("test").getList().contains(v2));
        l.removeValues(alternatives, 1);
        Assert.assertTrue(l.getValueMap().get("test").size() == 1);
        Assert.assertTrue(!l.getValueMap().get("test").getList().contains(v3));
    }

    public void testInitValues() {

    }

    @Test
    public void testRemoveLooseValues() {
        List<String> list = new ArrayList<String>();
        list.add("test1");
        list.add("test2");

        Leaf l = new Leaf();
        // l.setScale(new FloatScale());
        // Alternative a = new Alternative();
        // List<Alternative> alternatives = new ArrayList<Alternative>();
        // alternatives.add(a);
        // a.setName("test");
        Map<String, Values> map = new HashMap<String, Values>();
        Value v1 = new IntegerValue();
        v1.setScale(new IntegerScale());
        Values values1 = new Values();
        values1.add(v1);
        map.put("test1", values1);
        Value v2 = new IntegerValue();
        v2.setScale(new IntegerScale());
        Values values2 = new Values();
        values2.add(v2);
        map.put("test2", values2);
        Value v3 = new IntegerValue();
        v3.setScale(new IntegerScale());
        Values values3 = new Values();
        values3.add(v3);
        map.put("test3", values3);
        l.setValueMap(map);
        l.removeLooseValues(list, 5);
        Map<String, Values> res = l.getValueMap();
        Assert.assertTrue(res.containsKey("test1"));
        Assert.assertTrue(res.containsKey("test2"));
        Assert.assertFalse(res.containsKey("test3"));
    }
}
