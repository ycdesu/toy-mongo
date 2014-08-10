package org.ycavatars.toymongo.core.collection;

import com.google.common.base.Preconditions;

import java.util.*;

/**
 * A BTree based {@link java.util.NavigableMap} implementation. The map is sorted
 * according to the {@link java.lang.Comparable} natural ordering of its keys,
 * or by a {@link java.util.Comparator} provided through constructor.
 * <p>
 * Operations {@code containsKey}, {@code get}, {@code put} and {@code remove}
 * take log(n) time.  Algorithms are described in Cormen, Leiserson, Rivest, and
 * stein's <em>Introduction to Algorithms</em>.
 *
 * @author ycavatars
 */
public class BTreeMap<K, V> extends AbstractMap<K, V> {

  /**
   * Must be greater than 2.
   */
  private static final int MAX_NODE_DEGREE = 1001;

  private static final int MAX_NODE_KEYS = MAX_NODE_DEGREE - 1;

  /**
   * Use this comparator to maintain the key order or empty if use the key natural
   * ordering.
   */
  private final Optional<Comparator<? super K>> comparator;

  /**
   * The number of entries in the tree
   */
  private transient int size = 0;

  /**
   * The number of structural modifications to the tree.
   */
  private transient int modCount = 0;

  /**
   * Create an empty BTreeMap which uses the natural ordering of keys.
   * Note that every key object has to implement {@link java.lang.Comparable}.
   */
  public BTreeMap() {
    comparator = Optional.empty();
  }


  /**
   * @param comparator which is used to sort the keys
   * @throws java.lang.NullPointerException if {@code comparator} is null
   */
  public BTreeMap(Comparator<? super K> comparator) {
    this.comparator = Optional.of(comparator);
  }

  /**
   * Create a new BTreeMap which contains the same entries as the {@code map}.
   * Keys of the map have to implement {@link java.lang.Comparable} because the
   * {@code comparator} is null.
   *
   * @param map
   * @throws java.lang.NullPointerException if {@code map} is null
   */
  public BTreeMap(Map<? extends K, ? extends V> map) {
    comparator = null;
    putAll(Preconditions.checkNotNull(map));
  }

  /**
   * Node in the BTree.
   */
  private static class Node {

    // e[0].key <= e[1].key <= ... <= e[n].key
    Entry[] keys;

    Node[] children;

    boolean isLeaf;

    Node(Entry[] keys, Node[] children, boolean isLeaf) {
      this.keys = keys;
      this.children = children;
      this.isLeaf = isLeaf;
    }
  }

  private static final class Entry<K, V> implements Map.Entry<K, V> {
    K key;
    V value;

    Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override public K getKey() {
      return this.key;
    }

    @Override public V getValue() {
      return this.value;
    }

    @Override public V setValue(V value) {
      V old = this.value;
      this.value = value;
      return old;
    }
  }


  @Override public Set<Map.Entry<K, V>> entrySet() {
    return null;
  }
}
