package cn.ichinese.open;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class JsonTest {
  @Test
  public void parsesValues() {
    Map<String, Object> m = Json.parseObject(" {\"a\": 1, \"b\": -2.5e1, \"c\": [true, false, null], \"d\": \"x\\\"\\u4e2d\\n\", \"e\": {}, \"f\": []} ");
    assertEquals(1L, m.get("a"));
    assertEquals(-25.0, m.get("b"));
    assertEquals(Arrays.asList(true, false, null), m.get("c"));
    assertEquals("x\"中\n", m.get("d"));
    assertEquals(new LinkedHashMap<>(), m.get("e"));
    assertEquals(Arrays.asList(), m.get("f"));
    assertEquals(new BigInteger("123456789012345678901234567890"), Json.parse("123456789012345678901234567890"));
    assertNull(Json.parse("null"));
    assertEquals("😀", Json.parse("\"\\ud83d\\ude00\""));
  }

  @Test
  public void keepsKeyOrder() {
    Map<String, Object> m = Json.parseObject("{\"z\":1,\"a\":2,\"m\":3}");
    assertEquals(Arrays.asList("z", "a", "m"), Arrays.asList(m.keySet().toArray()));
  }

  @Test
  public void rejectsBadJson() {
    for (String bad : new String[] {"", "{", "[1,]", "{\"a\" 1}", "01", "1.", "tru", "\"a", "{} x", "\"\u0001\"", "nul"}) {
      try {
        Json.parse(bad);
        fail("accepted " + bad);
      } catch (IllegalArgumentException expected) {
        // ok
      }
    }
  }

  @Test
  public void writesValues() {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("s", "a\"b\\c\n\t\u0001中");
    m.put("i", 3);
    m.put("l", 4L);
    m.put("d", 1.5);
    m.put("w", 2.0);
    m.put("b", true);
    m.put("n", null);
    m.put("list", List.of(1, "x"));
    m.put("arr", new int[] {1, 2});
    assertEquals(
        "{\"s\":\"a\\\"b\\\\c\\n\\t\\u0001中\",\"i\":3,\"l\":4,\"d\":1.5,\"w\":2,\"b\":true,\"n\":null,\"list\":[1,\"x\"],\"arr\":[1,2]}",
        Json.stringify(m));
  }

  @Test
  public void roundTrips() {
    String text = "{\"a\":[1,2.5,\"x\",{\"b\":null}],\"c\":\"中文\"}";
    assertEquals(text, Json.stringify(Json.parse(text)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsNaN() {
    Json.stringify(Double.NaN);
  }
}
