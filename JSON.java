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
    }
    return result;
  } // parse(Reader)

  // +---------------+-----------------------------------------------
  // | Local helpers |
  // +---------------+

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  static JSONValue parseKernel(Reader source) throws ParseException, IOException {
    String input = "";
    JSONArray ret = new JSONArray();
    JSONHash hashTable = new JSONHash();
    
    int ch;
    ch = skipWhitespace(source);
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    } // if

    if ((char) ch == '[') {
      while (ch != ']') {
        ret.add(parseKernel(source));
      } // while
      return ret;
    } // if JSONArray

    if ((char) ch == '\"') {
      while ((ch = source.read()) != '\"') {
        input += String.valueOf((char) ch);
      } // while
      skipWhitespace(source);
      return new JSONString(input);
    } // if JSONString


    if (((char) ch == 't') || ((char) ch == 'f') || ((char) ch == 'n')) {
      do {
        input += String.valueOf((char) ch);
        ch = skipWhitespace(source);
      } while ((ch != -1) && (ch != ',') && (ch != ']') && (ch != '}'));

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
      JSONValue key = null;
      JSONValue val = null;
      while (ch != '}') {
        key = parseKernel(source);
        ch = skipWhitespace(source);
        val = parseKernel(source);
        hashTable.set((JSONString) key, val);
      } // while
    } // if JSONHash

    if ((Character.isDigit((char) ch)) || ((char) ch == '.') || ((char) ch == '-')) {
      do {
        input += String.valueOf((char) ch);
      } while (((ch = skipWhitespace(source)) != -1) && (ch != ',') && (ch != ']') && (ch != '}'));

      if ((input.substring(0, 2).equals("-0")) && (input.length() > 2)) {
        throw new IOException("Negative leading 0s not allowed!");
      } // if

      if (input.contains(".")) {
        return new JSONReal(input);
      } else {
        return new JSONInteger(input);
      } // if
    } // if JSONInteger or if JSONReal

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
