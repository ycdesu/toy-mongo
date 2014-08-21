package org.ycavatars.toymongo.core.collection;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

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
    // create map with data
    List<Map.Entry<String, String>> entries =
        Lists.newArrayListWithCapacity(BTreeMap.MAX_NODE_KEYS);
    Map<String, String> that = new HashMap<>();
    for (int i = 0; i < BTreeMap.MAX_NODE_KEYS; i++) {
      String key = "key" + i;
      String value = "value" + i;
      that.put(key, value);
      entries.add(new BTreeMap.Entry<>(key, value));
    }
    BTreeMap<String, String> map = new BTreeMap<>(that);

    // sort key in the same way as BTreeMap
    entries.sort(Comparator.comparing((Map.Entry e) -> (Comparable) e.getKey()));

    Iterator<Map.Entry<String, String>> entrySetIterator = map.entrySet()
        .iterator();
    for (Map.Entry<String, String> e : entries) {
      Assert.assertTrue(entrySetIterator.hasNext());

      Map.Entry<String, String> thatEntry = entrySetIterator.next();
      Assert.assertEquals(e.getKey(), thatEntry.getKey());
      Assert.assertEquals(e.getValue(), thatEntry.getValue());
    }

    try {
      Assert.assertFalse(entrySetIterator.hasNext());
    } catch (ArrayIndexOutOfBoundsException e) {
      Assert.fail(Throwables.getStackTraceAsString(e));
    }

    try {
      entrySetIterator.next();
      Assert.fail();
    } catch (NoSuchElementException e) {
    }
  }

  @Test
  public void testCopyEntryIterator_changeNextElement() {
    Map<String, String> map = new BTreeMap<>();
    map.put("a", "b");

    Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();

    try {
      Assert.assertTrue(iterator.hasNext());
      Map.Entry<String, String> e = iterator.next();
      Assert.assertEquals("a", e.getKey());
      Assert.assertEquals("b", e.getValue());

      e.setValue("123");

      Assert.assertEquals("b", map.get("a"));
      Assert.assertEquals("123", e.getValue());
    } catch (Exception e) {
      Assert.fail(Throwables.getStackTraceAsString(e));
    }
  }
}
