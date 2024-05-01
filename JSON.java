import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * Utilities for our simple implementation of JSON.
 * 
 * @author Marina Ananias
 * @author Linda Jing
 * @author Keely Miyamoto
 */
public class JSON {
  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /**
   * The current position in the input.
   */
  static int pos;

  /**
   * A field to keep track of the previous value of 'ch'. This helps us
   * check for end characters like ']' and '}' when we recursively call
   * 'parseKernel'.
   */
  static int prevCH;

  // +----------------+----------------------------------------------
  // | Static methods |
  // +----------------+

  /**
   * Parse a string into JSON.
   */
  public static JSONValue parse(String source) throws ParseException, IOException {
    return parse(new StringReader(source));
  } // parse(String)

  /**
   * Parse a file into JSON.
   */
  public static JSONValue parseFile(String filename) throws ParseException, IOException {
    FileReader reader = new FileReader(filename);
    JSONValue result = parse(reader);
    reader.close();
    return result;
  } // parseFile(String)

  /**
   * Parse JSON from a reader.
   */
  public static JSONValue parse(Reader source) throws ParseException, IOException {
    pos = 0;
    JSONValue result = parseKernel(source);
    if (-1 != skipWhitespace(source)) {
      throw new ParseException("Characters remain at end", pos);
    } // if
    return result;
  } // parse(Reader)

  // +---------------+-----------------------------------------------
  // | Local helpers |
  // +---------------+

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  static JSONValue parseKernel(Reader source) throws ParseException, IOException {
    // Declare some objects for possible return types.
    String input = "";
    JSONArray retArr = new JSONArray();
    JSONHash hashTable = new JSONHash();
    
    // Declare 'ch' variable for chars that we read in.
    int ch;
    // Get next char.
    ch = skipWhitespace(source);
    // Check for EOF.
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    } // if

    if ((char) ch == '[') {
      // While within the array...
      while (ch != ']') {
        // Add next element to the array.
        retArr.add(parseKernel(source));
        // Get previous value of 'ch'.
        ch = prevCH;
      } // while
      // If array has ended, we want to save the current value of 'ch' and return the JSONArray.
      if (((ch = skipWhitespace(source)) == -1) || (ch == ',') || (ch == ']') || (ch == '}')) {
        prevCH = ch;
        return retArr;
      } else {
        throw new IOException("Invalid array format.");
      } // if
    } // if JSONArray

    if ((char) ch == '\"') {
      // While we are in the String...
      while ((ch = source.read()) != '\"') {
        // Append next char to the String.
        input += String.valueOf((char) ch);
      } // while
      // If String ends, we want to save the current value of 'ch' and return the String as a JSONString.
      if (((ch = skipWhitespace(source)) == -1) || (ch == ',') || (ch == ']') || (ch == '}') || (ch == ':')) {
        prevCH = ch;
        return new JSONString(input);
      } else {
        throw new IOException("Invalid String format.");
      } // if
    } // if JSONString

    // Match first char to constant.
    if (((char) ch == 't') || ((char) ch == 'f') || ((char) ch == 'n')) {
      // Finish building String.
      do {
        input += String.valueOf((char) ch);
        ch = skipWhitespace(source);
      } while ((ch != -1) && (ch != ',') && (ch != ']') && (ch != '}'));
      // Save current value of 'ch'.
      prevCH = ch;
      // Match to JSONConstant.
      switch(input) {
        case "true":
          return JSONConstant.TRUE;
        case "false":
          return JSONConstant.FALSE;
        case "null":
          return JSONConstant.NULL;  
      } // switch
    } // if JSONConstant

    if ((char) ch == '{') {
      // Declare JSONValues for 'key' and 'val'.
      JSONValue key = null;
      JSONValue val = null;
      // While in the JSONHash...
      while (ch != '}') {
        // Get next JSONValue.
        JSONValue next = parseKernel(source);
        // Get previous value of 'ch'.
        ch = prevCH;
        // If previous is ':', then we found the key. Update 'key'.
        if (':' == ch) {
          key = (JSONString) next;
        // If previous is ',' or '}', then we found the value. Update 'val'.
        } else if (',' == ch || '}' == ch) {
          val = next;
          // Set next KVPair in hashTable.
          hashTable.set((JSONString) key, val);
        } // if
      } // while
      // If at the end of the JSONHash, save previous value of 'ch' and return hashTable.
      if (((ch = skipWhitespace(source)) == -1) || (ch != ',') || (ch == ']') || (ch == '}') || (ch == ':')) {
        prevCH = ch;
        return hashTable;
      } else { 
        throw new IOException("Invalid Hash format.");
      } // if
    } // if JSONHash

    if ((Character.isDigit((char) ch)) || ((char) ch == '.') || ((char) ch == '-')) {
      // Build number following either a digit, decimal, or negative sign.
      do {
        input += String.valueOf((char) ch);
      } while (((ch = skipWhitespace(source)) != -1) && (ch != ',') && (ch != ']') && (ch != '}'));
      // Save previous 'ch'.
      prevCH = ch;

      // Check for leading 00s.
      if (input.length() > 2) {
        if (input.substring(0, 2).equals("-0")) {
          throw new IOException("Negative leading 0s not allowed!");
        }
      } // if

      // Determine if JSONInteger or JSONReal.
      if (input.contains(".")) {
        return new JSONReal(input);
      } else {
        return new JSONInteger(input);
      } // if
    } // if JSONInteger or if JSONReal

    // If none of the above cases were met...
    throw new IOException("No JSONValues were identified");
  } // parseKernel

  /**
   * Get the next character from source, skipping over whitespace.
   */
  static int skipWhitespace(Reader source) throws IOException {
    int ch;
    do {
      ch = source.read();
      ++pos;
    } while (isWhitespace(ch));
    return ch;
  } // skipWhitespace(Reader)

  /**
   * Determine if a character is JSON whitespace (newline, carriage return, space, or tab).
   */
  static boolean isWhitespace(int ch) {
    return (' ' == ch) || ('\n' == ch) || ('\r' == ch) || ('\t' == ch);
  } // isWhiteSpace(int)

} // class JSON
