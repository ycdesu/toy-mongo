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
    Optional<Entry<K, V>> first;

    //TODO doubly linkedlist
    Optional<Entry<K, V>> last;

    Optional<Entry<K, V>> current;

    Node(Entry<K, V> first, Entry<K, V> last, Entry<K, V> current) {
      this.first = Optional.ofNullable(first);
      this.last = Optional.ofNullable(last);
      this.current = Optional.ofNullable(current);
    }

    Node(Node<K, V> that) {
      this.first = that.first;
      this.last = that.last;
      this.current = that.current;
    }
  }

  /**
   * Object inside BTree Node.
   */
  private static final class Entry<K, V> implements Map.Entry<K, V> {
    K key;
    V value;

    Optional<Entry<K, V>> prevEntry;
    Optional<Entry<K, V>> nextEntry;

    Optional<Node<K, V>> leftChild;
    Optional<Node<K, V>> rightChild;

    Entry(K key, V value, Entry<K, V> prevEntry, Entry<K, V> nextEntry,
        Node<K, V> leftChild, Node<K, V> rightChild) {
      this.key = Preconditions.checkNotNull(key);
      this.value = value;
      this.prevEntry = Optional.ofNullable(prevEntry);
      this.nextEntry = Optional.ofNullable(nextEntry);
      this.leftChild = Optional.ofNullable(leftChild);
      this.rightChild = Optional.ofNullable(rightChild);
    }

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
    return getEntry(Preconditions.checkNotNull(key))
        .flatMap(node -> node.current) // to Entry optional
        .map(entry -> entry.value)
        .orElse(null);
  }

  private Optional<Node<K, V>> getEntry(Object key) {
    if (comparator.isPresent()) {
      return getEntryUsingComparator(key);
    }

    //TODO initialize root
    assert root.isPresent();
    Optional<Node<K, V>> node = root;
    do {
      @SuppressWarnings("unchecked")
      Comparable<K> k = (Comparable<K>) key;

      Optional<Entry<K, V>> entry = node.get().first;
      Optional<Entry<K, V>> lastEntry = Optional.empty();
      while (entry.isPresent()) {
        Entry<K, V> e = entry.get();
        int cmp = k.compareTo(e.key);
        if (cmp == 0) {
          return node;
        } else if (cmp > 0) {
          lastEntry = entry;
          entry = e.nextEntry;
        } else {
          node = e.leftChild;
          break;
        }
      }

      if (lastEntry.isPresent()) {
        node = lastEntry.get().rightChild;
      }
    } while (node.isPresent());

    // if the tree is empty
    return Optional.empty();
  }

  private Optional<Node<K, V>> getEntryUsingComparator(Object key) {
    return null;
  }

  @Override public Set<Map.Entry<K, V>> entrySet() {
    return null;
  }
}
