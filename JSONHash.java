import java.io.PrintWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;

/**
 * JSON hashes/objects.
 * @author Marina Ananias
 * @author Linda Jing
 * @author Keely Miyamoto
 */
public class JSONHash implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  Object[] buckets;
  int size;


  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    return "";          // STUB
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    return true;        // STUB
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return 0;           // STUB
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
                        // STUB
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<KVPair<JSONString,JSONValue>> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Get the value associated with a key.
   */
  public JSONValue get(JSONString key) {
    return null;        // STUB
  } // get(JSONString)

    /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString,JSONValue>> iterator() {
    return new Iterator<KVPair<JSONString, JSONValue>>() {

      // Starting positions in array of buckets and index within that bucket.
      int currentBucket = 0;
      int indexInBucket = 0;

      public boolean hasNext() {
        // Check currentBucket is within bounds.
        if (currentBucket >= buckets.length) {
          return false;
        } // if

        // Look for next unempty bucket. If one exists, return true.
        do {
          currentBucket++;
          if (buckets[currentBucket] != null) {
            return true;
          } // if
        } while (currentBucket < buckets.length);

        // Otherwise return false.
        return false;
      } // hasNext()

      @SuppressWarnings("unchecked")
      public KVPair<JSONString, JSONValue> next() throws NoSuchElementException {
        // If there is no next bucket, throw exception.
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        } // if

        // If current bucket is empty, find next nonempty bucket.
        if (buckets[currentBucket] == null) {
          currentBucket++;
          while (buckets[currentBucket] == null) {
            currentBucket += 1;
          } // while
          indexInBucket = 0;
        } // if

        // Initialize an ArrayList for the current bucket.
        ArrayList<KVPair<JSONString, JSONValue>> alist = (ArrayList<KVPair<JSONString, JSONValue>>) buckets[currentBucket];
        // Save next index in the current bucket.
        int indexOfReturned = indexInBucket;

        // If indexInBucket is last index in ArrayList, find next nonempty bucket.
        if (indexInBucket == (alist.size() - 1)) {
          indexInBucket = 0;
          currentBucket++;
          while ((buckets[currentBucket] == null) && (currentBucket < buckets.length)) {
            currentBucket += 1;
          } // while
        } else {
          indexInBucket++;
        } // if

        return alist.get(indexOfReturned);
      } // next()

    }; // new Iterator
  }

  /**
   * Set the value associated with a key.
   */
  public void set(JSONString key, JSONValue value) {
                        // STUB
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return 0;           // STUB
  } // size()

} // class JSONHash
