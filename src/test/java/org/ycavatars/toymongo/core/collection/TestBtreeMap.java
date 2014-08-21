package org.ycavatars.toymongo.core.collection;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ycavatars
 */
public class TestBtreeMap {

  @Test
  public void testPutAll_throughConstructor() {
    Map<String, String> that = new HashMap<>();
    for (int i = 0; i < BTreeMap.MAX_NODE_KEYS; i++) {
      that.put("key" + i, "value" + i);
    }

    BTreeMap<String, String> map = new BTreeMap<>(that);
    Assert.assertEquals(map.size(), BTreeMap.MAX_NODE_KEYS);

    //TODO test equals after implementing entry set
  }

  @Test
  public void testPut_rootIsNotFull() {
    BTreeMap<String, String> map = new BTreeMap<>();
    map.put("keyA", "valueA");
    map.put("keyB", "valueB");

    Assert.assertEquals(map.get("keyA"), "valueA");
    Assert.assertEquals(map.get("keyB"), "valueB");
  }

  @Test
  public void testPut_rootIsFull() {
    Map<String, String> that = new HashMap<>();
    for (int i = 0; i < BTreeMap.MAX_NODE_KEYS; i++) {
      that.put("key" + i, "value" + i);
    }

    BTreeMap<String, String> map = new BTreeMap<>(that);
    map.put("key1001", "value1001");
    map.put("key1002", "value1002");
    map.put("key1003", "value1003");

    Assert.assertEquals(map.get("key1001"), "value1001");
    Assert.assertEquals(map.get("key1002"), "value1002");
    Assert.assertEquals(map.get("key1003"), "value1003");
  }

  @Test
  public void testCopyEntryIterator_fullRoot() {
    Map<String, String> that = new HashMap<>();
    for (int i = 0; i < BTreeMap.MAX_NODE_KEYS; i++) {
      that.put("key" + i, "value" + i);
    }

    BTreeMap<String, String> map = new BTreeMap<>(that);
    Iterator<Map.Entry<String, String>> entrySetIterator = map.entrySet()
        .iterator();
    int i = 0;
    while (entrySetIterator.hasNext()) {
      Map.Entry<String, String> e = entrySetIterator.next();
      System.out.println(e.getKey() + "::" + e.getValue());
    }
    while (entrySetIterator.hasNext()) {
      Map.Entry<String, String> e = entrySetIterator.next();
      Assert.assertEquals("key" + i, e.getKey());
      Assert.assertEquals("value" + i, e.getValue());
      i++;
    }
    System.out.println(i);
  }
}
