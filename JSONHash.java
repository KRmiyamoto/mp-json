import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * JSON hashes/objects.
 * 
 * @author Marina Ananias
 * @author Linda Jing
 * @author Keely Miyamoto
 */
public class JSONHash implements JSONValue {

  // +-----------+-------------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The load factor for expanding the table.
   */
  static final double LOAD_FACTOR = 0.5;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The number of values currently stored in the hash table. We use this to determine when to
   * expand the hash table.
   */
  int size = 0;

  /**
   * The array that we use to store the ArrayList of key/value pairs. (We use an array, rather than
   * an ArrayList, because we want to control expansion and ArrayLists of ArrayLists are just
   * weird.)
   */
  Object[] buckets;

  /**
   * Our helpful random number generator, used primarily when expanding the size of the table..
   */
  Random rand;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new hash table.
   */
  public JSONHash() {
    this.rand = new Random();
    this.clear();
  } // JSONHash

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    String ret = "";
    for (int i = 0; i < this.buckets.length; i++) {
      @SuppressWarnings("unchecked")
      ArrayList<KVPair<JSONString, JSONValue>> alist =
          (ArrayList<KVPair<JSONString, JSONValue>>) this.buckets[i];
      if (alist != null) {
        for (int j = 0; j < alist.size(); j++) {
          KVPair<JSONString, JSONValue> pair = alist.get(j);
          String p = ("  " + i + ": <" + pair.key() + "(" + pair.key().hashCode() + "):"
              + pair.value() + ">");
          ret += p;
          if (j != alist.size() - 1) {
            ret += ",";
          }
        } // for each pair in the bucket
      } // if the current bucket is not null
    } // for each bucket
    return ret;
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    if (other instanceof JSONHash) {
      return (Arrays.equals(((JSONHash) other).buckets, this.buckets));
    } else {
      return false;
    }

  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return this.buckets.hashCode();
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON. /////////////////////////////////////////////////////////////////////////
   */
  public void writeJSON(PrintWriter pen) {
    pen.println(this.buckets.toString());
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<KVPair<JSONString, JSONValue>> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Clear the whole table.
   */
  public void clear() {
    this.buckets = new Object[30];
    this.size = 0;
  } // clear()

  /**
   * Get the value associated with a key.
   */
  public JSONValue get(JSONString key) {
    int index = find(key);
    @SuppressWarnings("unchecked")
    ArrayList<KVPair<JSONString, JSONValue>> alist = (ArrayList<KVPair<JSONString, JSONValue>>) buckets[index];
    if (alist == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } else {
      for (KVPair<JSONString, JSONValue> pair : alist) {
        if (pair.key().equals(key)) {
          return pair.value();
        } // if
      } // for
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } // get
  } // get(JSONString)

    /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString, JSONValue>> iterator() {
    return null; // STUB
  } // iterator()

  /**
   * Set the value associated with a key.
   * @throws Exception 
   */
  public void set(JSONString key, JSONValue value) {

    // If there are too many entries, expand the table.
    if (this.size > (this.buckets.length * LOAD_FACTOR)) {
      expand();
    } // if there are too many entries

    // Find out where the key belongs and put the pair there.
    int index = find(key);
    @SuppressWarnings("unchecked")
    ArrayList<KVPair<JSONString, JSONValue>> alist = (ArrayList<KVPair<JSONString, JSONValue>>) this.buckets[index];
    // Special case: Nothing there yet
    if (alist == null) {
      alist = new ArrayList<KVPair<JSONString, JSONValue>>();
      this.buckets[index] = alist;
    }

    for (int i = 0; i < alist.size(); i++) {
      if (alist.get(i).key().equals(key)) {
        alist.set(i, new KVPair<JSONString,JSONValue>(key, value));
      }
    } // for

    alist.add(new KVPair<JSONString, JSONValue>(key, value));
    ++this.size;
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;
  } // size()

  // +---------+---------------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Find the index of the entry with a given key. If there is no such entry, return the index of an
   * entry we can use to store that key.
   */
  int find(JSONString key) {
    return Math.abs(key.hashCode()) % this.buckets.length;
  } // find(K)

  /**
   * Expand the size of the table.
   */
  @SuppressWarnings("unchecked")
  void expand() {
    // Figure out the size of the new table
    int newSize = 2 * this.buckets.length + rand.nextInt(10);
    // Remember the old table
    Object[] oldBuckets = this.buckets;
    // Create a new table of that size.
    this.buckets = new Object[newSize];
    // Move all buckets from the old table to their appropriate
    // location in the new table.
    for (int i = 0; i < oldBuckets.length; i++) {
      if (oldBuckets[i] != null) {
        ArrayList<KVPair<JSONString, JSONValue>> alist = (ArrayList<KVPair<JSONString, JSONValue>>) oldBuckets[i];
        for (KVPair<JSONString, JSONValue> pair : alist) {
          this.set(pair.key(), pair.value());
        } // for
      } // if
    } // for
  } // expand()

} // class JSONHash
