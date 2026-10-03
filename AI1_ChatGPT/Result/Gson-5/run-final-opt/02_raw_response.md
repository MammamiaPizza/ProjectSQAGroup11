import com.google.gson.internal.bind.util.ISO8601Utils;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import junit.framework.TestCase;

public class ISO8601UtilsTest extends TestCase {

  public void testParsesHourOnlyPositiveTimezoneOffset() throws Exception {
    String value = "1970-01-01T01:00:00+01";
    ParsePosition position = new ParsePosition(0);

    Date parsed = ISO8601Utils.parse(value, position);

    assertEquals(0L, parsed.getTime());
    assertEquals(value.length(), position.getIndex());
  }

  public void testParsesUtcTimezoneAndAdvancesPosition() throws Exception {
    String value = "1970-01-01T00:00:00Z";
    ParsePosition position = new ParsePosition(0);

    Date parsed = ISO8601Utils.parse(value, position);

    assertEquals(0L, parsed.getTime());
    assertEquals(value.length(), position.getIndex());
  }

  public void testParsesPositiveAndNegativeOffsetsWithMinutes() throws Exception {
    ParsePosition positivePosition = new ParsePosition(0);
    Date positive = ISO8601Utils.parse("1970-01-01T01:30:00+01:30", positivePosition);

    ParsePosition negativePosition = new ParsePosition(0);
    Date negative = ISO8601Utils.parse("1970-01-01T00:00:00-01:30", negativePosition);

    assertEquals(0L, positive.getTime());
    assertEquals(90L * 60L * 1000L, negative.getTime());
    assertEquals("1970-01-01T01:30:00+01:30".length(), positivePosition.getIndex());
    assertEquals("1970-01-01T00:00:00-01:30".length(), negativePosition.getIndex());
  }

  public void testParsesFractionalSecondsWithTimezone() throws Exception {
    ParsePosition position = new ParsePosition(0);

    Date parsed = ISO8601Utils.parse("1970-01-01T00:00:00.123Z", position);

    assertEquals(123L, parsed.getTime());
    assertEquals("1970-01-01T00:00:00.123Z".length(), position.getIndex());
  }

  public void testParsesFromSpecifiedParsePosition() throws Exception {
    String value = "prefix1970-01-01T01:00:00+01";
    ParsePosition position = new ParsePosition("prefix".length());

    Date parsed = ISO8601Utils.parse(value, position);

    assertEquals(0L, parsed.getTime());
    assertEquals(value.length(), position.getIndex());
  }

  public void testRejectsTruncatedTimezoneOffset() {
    try {
      ISO8601Utils.parse("1970-01-01T00:00:00+", new ParsePosition(0));
      fail("A truncated timezone offset must not be accepted");
    } catch (ParseException expected) {
      assertNotNull(expected.getMessage());
    }
  }

  public void testRejectsMissingTimezoneIndicatorForTimeValue() {
    try {
      ISO8601Utils.parse("1970-01-01T00:00:00", new ParsePosition(0));
      fail("A time value without a timezone indicator must not be accepted");
    } catch (ParseException expected) {
      assertNotNull(expected.getMessage());
    }
  }
}