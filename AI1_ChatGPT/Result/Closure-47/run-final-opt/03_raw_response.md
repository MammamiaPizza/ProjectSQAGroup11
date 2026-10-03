import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.proto.Mapping.OriginalMapping;
import org.junit.Test;

public class SourceMapConsumerV3FinalLineTest {

  private SourceMapConsumerV3 parse(String mappings, int lineCount, String[] sources,
      String[] names) throws SourceMapParseException {
    StringBuilder json = new StringBuilder();
    json.append("{\"version\":3,\"file\":\"out.js\",\"lineCount\":")
        .append(lineCount)
        .append(",\"mappings\":\"")
        .append(mappings)
        .append("\",\"sources\":[");
    for (int i = 0; i < sources.length; i++) {
      if (i > 0) {
        json.append(',');
      }
      json.append('"').append(sources[i]).append('"');
    }
    json.append("],\"names\":[");
    for (int i = 0; i < names.length; i++) {
      if (i > 0) {
        json.append(',');
      }
      json.append('"').append(names[i]).append('"');
    }
    json.append("]}");

    SourceMapConsumerV3 consumer = new SourceMapConsumerV3();
    consumer.parse(json.toString());
    return consumer;
  }

  @Test
  public void testFinalLineMappingsAreAvailableWithoutTrailingSemicolon()
      throws SourceMapParseException {
    SourceMapConsumerV3 consumer =
        parse("EAAA,CAAC", 1, new String[] {"input.js"}, new String[0]);

    assertNull(consumer.getMappingForLine(0, 1));

    OriginalMapping first = consumer.getMappingForLine(0, 2);
    assertEquals("input.js", first.getOriginalFile());
    assertEquals(0, first.getLineNumber());
    assertEquals(0, first.getColumnPosition());

    OriginalMapping last = consumer.getMappingForLine(0, 100);
    assertEquals("input.js", last.getOriginalFile());
    assertEquals(0, last.getLineNumber());
    assertEquals(1, last.getColumnPosition());
  }

  @Test
  public void testFinalGeneratedLineIsRetainedAfterEarlierLineSeparator()
      throws SourceMapParseException {
    SourceMapConsumerV3 consumer =
        parse("AAAA;AACA", 2, new String[] {"original.js"}, new String[0]);

    OriginalMapping firstLine = consumer.getMappingForLine(0, 0);
    assertEquals("original.js", firstLine.getOriginalFile());
    assertEquals(0, firstLine.getLineNumber());
    assertEquals(0, firstLine.getColumnPosition());

    OriginalMapping finalLine = consumer.getMappingForLine(1, 0);
    assertEquals("original.js", finalLine.getOriginalFile());
    assertEquals(1, finalLine.getLineNumber());
    assertEquals(0, finalLine.getColumnPosition());
  }

  @Test
  public void testNamedMappingOnFinalLineRetainsIdentifier()
      throws SourceMapParseException {
    SourceMapConsumerV3 consumer =
        parse("AAAAA", 1, new String[] {"source.js"}, new String[] {"symbol"});

    OriginalMapping mapping = consumer.getMappingForLine(0, 0);

    assertEquals("source.js", mapping.getOriginalFile());
    assertEquals(0, mapping.getLineNumber());
    assertEquals(0, mapping.getColumnPosition());
    assertEquals("symbol", mapping.getIdentifier());
  }

  @Test(expected = SourceMapParseException.class)
  public void testRejectsUnsupportedSourceMapVersion() throws SourceMapParseException {
    SourceMapConsumerV3 consumer = new SourceMapConsumerV3();
    consumer.parse(
        "{\"version\":2,\"file\":\"out.js\",\"lineCount\":1,"
            + "\"mappings\":\"AAAA\",\"sources\":[\"input.js\"],\"names\":[]}");
  }
}