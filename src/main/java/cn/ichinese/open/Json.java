package cn.ichinese.open;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small JSON reader and writer, so the SDK needs no other library. {@link #parse} gives
 * {@code Map<String, Object>} (keys in order), {@code List<Object>}, {@code String}, {@code Long} (whole
 * numbers that fit; {@code BigInteger} for larger ones), {@code Double} (other numbers), {@code Boolean} or {@code null}. {@link #stringify}
 * writes maps, iterables, arrays, strings, numbers, booleans and null.
 */
public final class Json {
  private Json() {}

  /**
   * Parses a JSON text.
   *
   * @param text the JSON text
   * @return the value
   * @throws IllegalArgumentException when the text is not valid JSON
   */
  public static Object parse(String text) {
    Parser p = new Parser(text);
    p.skipSpace();
    Object v = p.value();
    p.skipSpace();
    if (p.pos != text.length()) throw p.error("unexpected text after the value");
    return v;
  }

  /**
   * Parses a JSON object.
   *
   * @param text the JSON text
   * @return the object
   * @throws IllegalArgumentException when the text is not a JSON object
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Object> parseObject(String text) {
    Object v = parse(text);
    if (!(v instanceof Map)) throw new IllegalArgumentException("JSON: not an object");
    return (Map<String, Object>) v;
  }

  /**
   * Writes a value as JSON.
   *
   * @param value a Map, Iterable, array, String, Number, Boolean, Character, Enum or null
   * @return the JSON text
   * @throws IllegalArgumentException for values JSON cannot hold (NaN, infinity, other objects)
   */
  public static String stringify(Object value) {
    StringBuilder sb = new StringBuilder();
    write(sb, value);
    return sb.toString();
  }

  private static void write(StringBuilder sb, Object v) {
    if (v == null) {
      sb.append("null");
    } else if (v instanceof String || v instanceof Character || v instanceof Enum) {
      quote(sb, v.toString());
    } else if (v instanceof Boolean) {
      sb.append(v);
    } else if (v instanceof Double || v instanceof Float) {
      double d = ((Number) v).doubleValue();
      if (Double.isNaN(d) || Double.isInfinite(d)) throw new IllegalArgumentException("JSON: " + d);
      if (d == Math.rint(d) && Math.abs(d) < 1e15) sb.append((long) d);
      else sb.append(v);
    } else if (v instanceof BigDecimal) {
      sb.append(((BigDecimal) v).toPlainString());
    } else if (v instanceof Number) {
      sb.append(v);
    } else if (v instanceof Map) {
      sb.append('{');
      boolean first = true;
      for (Map.Entry<?, ?> e : ((Map<?, ?>) v).entrySet()) {
        if (!first) sb.append(',');
        first = false;
        quote(sb, String.valueOf(e.getKey()));
        sb.append(':');
        write(sb, e.getValue());
      }
      sb.append('}');
    } else if (v instanceof Iterable) {
      sb.append('[');
      boolean first = true;
      for (Object x : (Iterable<?>) v) {
        if (!first) sb.append(',');
        first = false;
        write(sb, x);
      }
      sb.append(']');
    } else if (v.getClass().isArray()) {
      sb.append('[');
      int n = java.lang.reflect.Array.getLength(v);
      for (int i = 0; i < n; i++) {
        if (i > 0) sb.append(',');
        write(sb, java.lang.reflect.Array.get(v, i));
      }
      sb.append(']');
    } else {
      throw new IllegalArgumentException("JSON: cannot write a " + v.getClass().getName());
    }
  }

  private static void quote(StringBuilder sb, String s) {
    sb.append('"');
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '"':
          sb.append("\\\"");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\f':
          sb.append("\\f");
          break;
        default:
          if (c < 0x20 || c == ' ' || c == ' ') sb.append(String.format("\\u%04x", (int) c));
          else sb.append(c);
      }
    }
    sb.append('"');
  }

  private static final class Parser {
    private final String s;
    private int pos;
    private int depth;

    Parser(String s) {
      if (s == null) throw new IllegalArgumentException("JSON: null text");
      this.s = s;
    }

    IllegalArgumentException error(String message) {
      return new IllegalArgumentException("JSON: " + message + " at " + pos);
    }

    void skipSpace() {
      while (pos < s.length()) {
        char c = s.charAt(pos);
        if (c == ' ' || c == '\t' || c == '\n' || c == '\r') pos++;
        else break;
      }
    }

    Object value() {
      if (pos >= s.length()) throw error("unexpected end");
      char c = s.charAt(pos);
      switch (c) {
        case '{':
          return object();
        case '[':
          return array();
        case '"':
          return string();
        case 't':
          return literal("true", Boolean.TRUE);
        case 'f':
          return literal("false", Boolean.FALSE);
        case 'n':
          return literal("null", null);
        default:
          if (c == '-' || (c >= '0' && c <= '9')) return number();
          throw error("unexpected '" + c + "'");
      }
    }

    private Object literal(String word, Object v) {
      if (!s.startsWith(word, pos)) throw error("unexpected text");
      pos += word.length();
      return v;
    }

    private Map<String, Object> object() {
      if (++depth > 512) throw error("nested too deep");
      pos++;
      Map<String, Object> m = new LinkedHashMap<>();
      skipSpace();
      if (pos < s.length() && s.charAt(pos) == '}') {
        pos++;
        depth--;
        return m;
      }
      while (true) {
        skipSpace();
        if (pos >= s.length() || s.charAt(pos) != '"') throw error("expected a key");
        String k = string();
        skipSpace();
        if (pos >= s.length() || s.charAt(pos) != ':') throw error("expected ':'");
        pos++;
        skipSpace();
        m.put(k, value());
        skipSpace();
        if (pos >= s.length()) throw error("unexpected end");
        char c = s.charAt(pos++);
        if (c == '}') break;
        if (c != ',') throw error("expected ',' or '}'");
      }
      depth--;
      return m;
    }

    private List<Object> array() {
      if (++depth > 512) throw error("nested too deep");
      pos++;
      List<Object> list = new ArrayList<>();
      skipSpace();
      if (pos < s.length() && s.charAt(pos) == ']') {
        pos++;
        depth--;
        return list;
      }
      while (true) {
        skipSpace();
        list.add(value());
        skipSpace();
        if (pos >= s.length()) throw error("unexpected end");
        char c = s.charAt(pos++);
        if (c == ']') break;
        if (c != ',') throw error("expected ',' or ']'");
      }
      depth--;
      return list;
    }

    private String string() {
      pos++;
      StringBuilder sb = new StringBuilder();
      while (true) {
        if (pos >= s.length()) throw error("unterminated string");
        char c = s.charAt(pos++);
        if (c == '"') return sb.toString();
        if (c == '\\') {
          if (pos >= s.length()) throw error("unterminated string");
          char e = s.charAt(pos++);
          switch (e) {
            case '"':
            case '\\':
            case '/':
              sb.append(e);
              break;
            case 'b':
              sb.append('\b');
              break;
            case 'f':
              sb.append('\f');
              break;
            case 'n':
              sb.append('\n');
              break;
            case 'r':
              sb.append('\r');
              break;
            case 't':
              sb.append('\t');
              break;
            case 'u':
              if (pos + 4 > s.length()) throw error("bad \\u escape");
              try {
                sb.append((char) Integer.parseInt(s.substring(pos, pos + 4), 16));
              } catch (NumberFormatException ex) {
                throw error("bad \\u escape");
              }
              pos += 4;
              break;
            default:
              throw error("bad escape");
          }
        } else if (c < 0x20) {
          throw error("control character in a string");
        } else {
          sb.append(c);
        }
      }
    }

    private Number number() {
      int start = pos;
      if (s.charAt(pos) == '-') pos++;
      int digits = pos;
      while (pos < s.length() && Character.isDigit(s.charAt(pos))) pos++;
      if (pos == digits) throw error("bad number");
      if (s.charAt(digits) == '0' && pos - digits > 1) throw error("leading zero");
      boolean whole = true;
      if (pos < s.length() && s.charAt(pos) == '.') {
        whole = false;
        pos++;
        int f = pos;
        while (pos < s.length() && Character.isDigit(s.charAt(pos))) pos++;
        if (pos == f) throw error("bad number");
      }
      if (pos < s.length() && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
        whole = false;
        pos++;
        if (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) pos++;
        int e = pos;
        while (pos < s.length() && Character.isDigit(s.charAt(pos))) pos++;
        if (pos == e) throw error("bad number");
      }
      String t = s.substring(start, pos);
      if (whole) {
        BigInteger b = new BigInteger(t);
        if (b.bitLength() < 64) return b.longValue();
        return b;
      }
      return Double.parseDouble(t);
    }
  }

  /**
   * A list of strings from a JSON value (a list of anything, or null).
   *
   * @param v the value
   * @return the strings (empty for null)
   */
  static List<String> strings(Object v) {
    List<String> out = new ArrayList<>();
    if (v instanceof Collection) for (Object x : (Collection<?>) v) out.add(String.valueOf(x));
    return out;
  }
}
