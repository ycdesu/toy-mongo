package org.ycavatars.toymongo.core.collection;

import com.google.common.annotations.VisibleForTesting;
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
  static final int MIN_NODE_DEGREE = 500;
  static final int MIN_NODE_KEYS = MIN_NODE_DEGREE - 1;
  static final int MAX_NODE_DEGREE = 2 * MIN_NODE_DEGREE;
  static final int MAX_NODE_KEYS = 2 * MIN_NODE_DEGREE - 1;

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

    Entry(K key, V value) {
      this.key = key;
      this.value = value;
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
      Node<K, V> rootNode = new Node<>();
      rootNode.isLeaf = true;
      root = Optional.of(rootNode);
      modCount++;
    }

    splitRootIfFull();

    return insertNonFull(root.get(), new Entry<>(key, value)).orElse(null);
  }

  @VisibleForTesting void splitRootIfFull() {
    // if the root is full
    if (root.get().keySize == MAX_NODE_KEYS) {
      Node<K, V> newRoot = new Node<>();
      newRoot.isLeaf = false;
      newRoot.keySize = 0;
      newRoot.children[0] = root.get();
      root = Optional.of(newRoot);

      splitChild(newRoot, 0); // split old root node which is the first child of newRoot
      modCount++;
    }
  }

  /**
   * Insert {@code entry} to {@code node} if the node is a leaf node. Otherwise,
   * traverse from the node to find the leaf.
   *
   * @param node
   * @param entry
   */
  private Optional<V> insertNonFull(Node<K, V> node, Entry<K, V> entry) {
    int index = node.keySize - 1;
    // same as binary tree, we add a key to a leaf
    if (node.isLeaf) {
      // TODO the comparator may affect the algorithm you choose, and binary search
      // may not work, so have to add constraints on comparator. Before that, make it
      // works first.
      while (index >= 0 && compare(entry.key, node.entries[index].key) < 0) {
        node.entries[index + 1] = node.entries[index];
        index--;
      }

      // set the new entry
      Optional<V> old = Optional.empty();
      index++;
      if (Optional.ofNullable(node.entries[index]).isPresent()) {
        old = Optional.ofNullable((V) node.entries[index].getValue());
      }
      node.entries[index] = entry;
      node.keySize++;

      size++;

      return old;
    } else {
      while (index >= 0 && compare(entry.key, node.entries[index].key) < 0) {
        index--;
      }
      index++;

      assert Optional.ofNullable(node.children[index]).isPresent()
          : "the index:" + index + ", the keySize:" + node.keySize;

      // if the child is full
      if (node.children[index].keySize == MAX_NODE_KEYS) {
        splitChild(node, index);
        if (compare(entry.key, node.entries[index].key) > 0) {
          index++;
        }
      }
      return insertNonFull(node.children[index], entry);
    }
  }

  /**
   * Split {@code parent.children[index]}.
   *
   * @param parent
   * @param index
   */
  @SuppressWarnings("unchecked")
  private void splitChild(Node<K, V> parent, int index) {
    Node<K, V> rightNode = new Node<>();
    Node<K, V> fullNode = parent.children[index];

    assert Optional.ofNullable(fullNode).isPresent();

    rightNode.isLeaf = fullNode.isLeaf;

    assert fullNode.entries[MAX_NODE_KEYS - MIN_NODE_KEYS] != null;

    // fullNode.entries[MIN_NODE_KEYS] will be the median key
    System.arraycopy(fullNode.entries, MIN_NODE_KEYS + 1,
        rightNode.entries, 0, MAX_NODE_KEYS - MIN_NODE_KEYS - 1);
    rightNode.keySize = MAX_NODE_KEYS - MIN_NODE_KEYS;

    if (!fullNode.isLeaf) {
      System.arraycopy(fullNode.children, MIN_NODE_KEYS + 1, rightNode.children, 0
          , fullNode.keySize + 1 - MIN_NODE_KEYS // num of children is more than keySize
      );
    }
    fullNode.keySize = MIN_NODE_KEYS;

    // shift children to right
    System.arraycopy(parent.children, index, parent.children, index + 1,
        parent.keySize + 1 - index);
    // parent.children[index] still point to the fullNode
    parent.children[index + 1] = rightNode;

    // shift keys to right
    System.arraycopy(parent.entries, index, parent.entries, index + 1,
        parent.keySize - index);
    // set the median key
    parent.entries[index] = fullNode.entries[MIN_NODE_KEYS];

    parent.keySize++;
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

    int index = 0;
    while (index < node.keySize && compare(key, node.entries[index].key) > 0) {
      index++;
    }

    if (index < node.keySize && key.equals(node.entries[index].key)) {
      return Optional.of(node.entries[index]);
    } else if (node.isLeaf) {
      return Optional.empty();
    } else {
      return getEntry(node.children[index], key);
    }
  }

  private Optional<Entry<K, V>> getEntryUsingComparator(Object key) {
    //TODO
    return null;
  }

  @SuppressWarnings("unchecked")
  private int compare(Object key1, Object key2) {
    if (comparator.isPresent()) {
      return compareUsingComparator(key1, key2);
    }

    return ((Comparable<? super K>) key1).compareTo((K) key2);
  }

  @SuppressWarnings("unchecked")
  private int compareUsingComparator(Object key1, Object key2) {
    Comparator<? super K> cmp = comparator.get();
    return cmp.compare((K) key1, (K) key2);
  }

  @Override public Set<Map.Entry<K, V>> entrySet() {
    //TODO
    return null;
  }

  @Override public int size() {
    //TODO
    return size;
  }
}
