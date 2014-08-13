package org.ycavatars.toymongo.core.collection;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author ycavatars
 */
public class TestBtreeMap {

  @Test
  public void testPut_existingKeyValue() {
    BTreeMap<String, String> map = new BTreeMap<>();
    map.put("keyA", "valueA");
    map.put("keyB", "valueB");

    Assert.assertEquals(map.get("keyA"), "valueA");
    Assert.assertEquals(map.get("keyB"), "valueB");
  }
}
