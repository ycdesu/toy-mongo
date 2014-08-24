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
    // make it split
    int size = BTreeMap.MAX_NODE_KEYS + 100;
    Map<String, String> that = new HashMap<>();
    for (int i = 0; i < size; i++) {
      that.put("key" + i, "value" + i);
    }
    BTreeMap<String, String> map = new BTreeMap<>(that);

    Assert.assertEquals(size, map.size());
  }

  @Test
  public void testPut_rootIsNotFull() {
    BTreeMap<String, String> map = new BTreeMap<>();
    map.put("keyA", "valueA");
    map.put("keyB", "valueB");

    Assert.assertEquals(map.get("keyA"), "valueA");
    Assert.assertEquals(map.get("keyB"), "valueB");
    Assert.assertEquals(2, map.size());
  }


  @Test
  public void testCopyEntryIterator_sortByKey() {
    // create map with data
    List<Map.Entry<String, String>> entries =
        Lists.newArrayListWithCapacity(BTreeMap.MAX_NODE_KEYS);
    BTreeMap<String, String> map = new BTreeMap<>();
    for (int i = 0; i < BTreeMap.MAX_NODE_KEYS; i++) {
      String key = "key" + i;
      String value = "value" + i;
      map.put(key, value);
      entries.add(new BTreeMap.Entry<>(key, value));
    }

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

  @Test
  public void testSplitChild_splitOnce() {
    BTreeMap<String, String> map = new BTreeMap<>();
    for (int i = 0; i < BTreeMap.MAX_NODE_KEYS; i++) {
      map.put("key" + i, "value" + i);
    }
    for (int i = 0; i < (BTreeMap.MAX_NODE_KEYS / 2); i++) {
      map.put("skey" + i, "svalue" + i);
    }

    int expectedSize = BTreeMap.MAX_NODE_KEYS + BTreeMap.MAX_NODE_KEYS / 2;
    Assert.assertEquals(expectedSize, map.size());
    for (int i = 0; i < BTreeMap.MAX_NODE_KEYS; i++) {
      Assert.assertEquals("value" + i, map.get("key" + i));
    }
    for (int i = 0; i < (BTreeMap.MAX_NODE_KEYS / 2); i++) {
      Assert.assertEquals("svalue" + i, map.get("skey" + i));
    }
  }
}
