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
  private static final int MIN_NODE_DEGREE = 500;

  private static final int MIN_NODE_KEYS = MIN_NODE_DEGREE - 1;

  private static final int MAX_NODE_DEGREE = 2 * MIN_NODE_DEGREE;

  private static final int MAX_NODE_KEYS = 2 * MIN_NODE_DEGREE - 1;

  /**
   * Use this comparator to maintain the key order or empty if use the key natural
   * ordering.
   */
  private final Optional<Comparator<? super K>> comparator;

  private Optional<Node<K, V>> root = Optional.empty();

  /**
   * The number of entries in the tree
   */
  private int size = 0;

  /**
   * The number of structural modifications to the tree.
   */
  private int modCount = 0;

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
    comparator = Optional.empty();
    putAll(Preconditions.checkNotNull(map));
  }

  /**
   * Node in the BTree.
   */
  private static class Node<K, V> {

    boolean isLeaf;

    int keySize = 0;

    //TODO apply load factor, resize arrays

    // keys
    Entry[] entries = new Entry[MAX_NODE_KEYS];

    // degrees
    Node[] children = new Node[MAX_NODE_DEGREE];
  }

  /**
   * Object inside BTree Node.
   */
  private static final class Entry<K, V> implements Map.Entry<K, V> {
    K key;
    V value;

    @Override public K getKey() {
      return key;
    }

    @Override public V getValue() {
      return value;
    }

    @Override public V setValue(V value) {
      V old = this.value;
      this.value = value;
      return old;
    }

  }

  /**
   * Create an empty tree.
   */
  private void initialize() {
    Node<K, V> emptyNode = new Node<>();
    emptyNode.isLeaf = true;
    root = Optional.of(emptyNode);
  }

  /**
   * Associates the specified value with the specified key in this map.
   * If the map previously contained a mapping for the key, the old value is replaced by the specified value.  (A map
   * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
   * if {@link #containsKey(Object) m.containsKey(k)} would return
   * <tt>true</tt>.)
   *
   * @param key   key with which the specified value is to be associated
   * @param value value to be associated with the specified key
   * @return the previous value associated with {@code key}, or
   * {@code null} if there was no mapping for {@code key}.
   * (A {@code null} return can also indicate that the map
   * previously associated {@code null} with {@code key}.)
   * @throws ClassCastException   if the specified key cannot be compared
   *                              with the keys currently in the map
   * @throws NullPointerException if the specified key is null
   *                              and this map uses natural ordering, or its comparator
   *                              does not permit null keys
   */
  @Override public V put(K key, V value) {
    Preconditions.checkNotNull(key);
    // create empty btree
    if (!root.isPresent()) {
      Entry<K, V> entry = new Entry<>(key, value);
      root = Optional.of(new Node<>(entry, null, null));
      size = 1;
      modCount++;
      return null;
    }
    return null;
  }


  /**
   * Returns the value to which the specified key is mapped,
   * or {@code null} if this map contains no mapping for the key.
   * <p>
   * <p>More formally, if this map contains a mapping from a key
   * {@code k} to a value {@code v} such that {@code key} compares
   * equal to {@code k} according to the map's ordering, then this
   * method returns {@code v}; otherwise it returns {@code null}.
   * <p>
   * <p>If this map permits null values, then a return value of
   * {@code null} does not <i>necessarily</i> indicate that the map
   * contains no mapping for the key; it's also possible that the map
   * explicitly maps the key to {@code null}.  The {@link #containsKey
   * containsKey} operation may be used to distinguish these two cases.
   *
   * @param key the key whose associated value is to be returned
   * @return the value to which the specified key is mapped, or
   * {@code null} if this map contains no mapping for the key
   * @throws ClassCastException   if the specified key cannot be compared
   *                              with the keys currently in the map
   * @throws NullPointerException if the specified key is null
   *                              and this map uses natural ordering, or its comparator
   *                              does not permit null keys
   */
  @Override public V get(Object key) {
    return root.isPresent() ? getEntry(root.get(), key).get().value : null;
  }

  @SuppressWarnings("unchecked")
  private Optional<Entry<K, V>> getEntry(Node<K, V> node, Object key) {
    if (comparator.isPresent()) {
      return getEntryUsingComparator(key);
    }

    Comparable<K> k = (Comparable<K>) key;

    int index = 0;
    while (index < node.keySize && k.compareTo((K) node.entries[index].key) > 0) {
      index++;
    }

    if (index < node.keySize && k.equals(node.entries[index].key)) {
      return Optional.of(node.entries[index]);
    } else if (node.isLeaf) {
      return Optional.empty();
    } else {
      return getEntry(node.children[index], key);
    }
  }

  private Optional<Entry<K, V>> getEntryUsingComparator(Object key) {
    return null;
  }

  @Override public Set<Map.Entry<K, V>> entrySet() {
    return null;
  }
}
