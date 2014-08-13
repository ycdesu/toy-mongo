package org.ycavatars.toymongo.core.collection;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ycavatars
 */
public class TestBtreeMap {

  @Test
  public void testPutAll_throughConstructor() {
    Map<String, String> that = new HashMap<>();
    for (int i = 0; i < BTreeMap.MAX_NODE_KEYS; i++) {
      that.put("key" + i, " value" + i);
    }

    BTreeMap<String, String> map = new BTreeMap<>(that);
    Assert.assertEquals(map.size(), BTreeMap.MAX_NODE_KEYS);

    //TODO test equals after implementing entry set
  }

  @Test
  public void testPut_existingKeyValue() {
    BTreeMap<String, String> map = new BTreeMap<>();
    map.put("keyA", "valueA");
    map.put("keyB", "valueB");

    Assert.assertEquals(map.get("keyA"), "valueA");
    Assert.assertEquals(map.get("keyB"), "valueB");
  }

  public void testSplitRootIfFull_insertToFullNode() {
    BTreeMap<String, String> map = new BTreeMap<>();

  }
}
