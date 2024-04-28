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
    int ch;
    ch = skipWhitespace(source);
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    }

    char c = (char) ch;

    if (c == '[') {
      JSONArray value = new JSONArray();
      while (!(c == ']')) {
        value.add(parseKernel(source));
      }
      return value;
    } // if JSONArray

    if (c == ('"')) {
      String comp = "";
      while (!(c == ('"'))) {
        comp += (char) ch;
        ch = skipWhitespace(source);
      }
      skipWhitespace(source);
      if ((comp.equals("null")) || (comp.equals("true")) || (comp.equals("false"))) {
        JSONConstant value = new JSONConstant(comp);
        return value;
      }
      return new JSONString(comp);
    } // if JSONConstant or JSONString

    if (c == '{') {
      JSONHash hashTable = new JSONHash();
      while (!(c == '}')) {
        JSONValue key = null;
        JSONValue val = null;
        while (!(c == ':')) {
          key = parseKernel(source);
        }
        while (!(c == ',')) {
          val = parseKernel(source);
        }
        hashTable.set((JSONString) key, (JSONValue) val);
      }
      return hashTable;
    } // if JSONHash

    if ((Character.isDigit(c)) || (c == '.') || (c == '-')) {
      String ret = String.valueOf(c);

      while ((Character.isDigit(c)) || (c == '.')) {
        ret += (char) ch;
        ch = skipWhitespace(source);
      } // while
      if (ret.contains(".")) {
        return new JSONReal(ret);
      } else {

        if ((ret.substring(0, 2).equals("-0")) && (ret.length() > 2)) {
          throw new IOException("Negative leading 0s not allowed!");
        }

        return new JSONInteger(ret);
      }
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
