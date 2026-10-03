package com.fasterxml.jackson.databind.node;

  import com.fasterxml.jackson.core.JsonParser;
  import com.fasterxml.jackson.core.JsonToken;
  import com.fasterxml.jackson.databind.node.JsonNode;
  import com.fasterxml.jackson.databind.node.JsonNodeFactory;

  import java.io.IOException;
  import java.math.BigDecimal;
  import java.math.BigInteger;

  import org.junit.Before;
  import org.junit.Test;

  import static org.junit.Assert.*;

  public class TestTreeTraversingParser {

      private JsonNodeFactory nodeFactory;

      @Before
      public void setUp() {
          nodeFactory = JsonNodeFactory.instance;
      }

      // ---------- int values ---------

      @Test
      public void testIntNormalRange() throws IOException {
          JsonNode node = nodeFactory.numberNode(42);
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          assertEquals(42, parser.getIntValue());
          parser.close();
      }

      @Test
      public void testIntBoundaryMax() throws IOException {
          JsonNode node = nodeFactory.numberNode(Integer.MAX_VALUE);
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          assertEquals(Integer.MAX_VALUE, parser.getIntValue());
          parser.close();
      }

      @Test
      public void testIntBoundaryMin() throws IOException {
          JsonNode node = nodeFactory.numberNode(Integer.MIN_VALUE);
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          assertEquals(Integer.MIN_VALUE, parser.getIntValue());
          parser.close();
      }

      @Test(expected = IOException.class)
      public void testIntOverflowAboveMax() throws IOException {
          // value > Integer.MAX_VALUE
          JsonNode node = nodeFactory.numberNode(BigInteger.valueOf(2147483648L)); // 2^31
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          parser.getIntValue(); // must throw
          parser.close();
      }

      @Test(expected = IOException.class)
      public void testIntOverflowBelowMin() throws IOException {
          // value < Integer.MIN_VALUE
          JsonNode node = nodeFactory.numberNode(BigInteger.valueOf(-2147483649L)); // -2^31 - 1
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          parser.getIntValue(); // must throw
          parser.close();
      }

      // ---------- long values ---------

      @Test
      public void testLongNormal() throws IOException {
          JsonNode node = nodeFactory.numberNode(123L);
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          assertEquals(123L, parser.getLongValue());
          parser.close();
      }

      @Test
      public void testLongBoundaryMax() throws IOException {
          JsonNode node = nodeFactory.numberNode(Long.MAX_VALUE);
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          assertEquals(Long.MAX_VALUE, parser.getLongValue());
          parser.close();
      }

      @Test
      public void testLongBoundaryMin() throws IOException {
          JsonNode node = nodeFactory.numberNode(Long.MIN_VALUE);
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          assertEquals(Long.MIN_VALUE, parser.getLongValue());
          parser.close();
      }

      @Test(expected = IOException.class)
      public void testLongOverflowAboveMax() throws IOException {
          // value > Long.MAX_VALUE
          JsonNode node = nodeFactory.numberNode(new BigInteger("9223372036854775808")); // 2^63
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          parser.getLongValue(); // must throw
          parser.close();
      }

      @Test(expected = IOException.class)
      public void testLongOverflowBelowMin() throws IOException {
          // value < Long.MIN_VALUE
          JsonNode node = nodeFactory.numberNode(new BigInteger("-9223372036854775809")); // -2^63
-1
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_INT);
          parser.getLongValue(); // must throw
          parser.close();
      }

      // ---------- decimal cases ---------

      @Test(expected = IOException.class)
      public void testDecimalTruncationInt() throws IOException {
          // fractional BigDecimal cannot be safely coerred to int
          JsonNode node = nodeFactory.numberNode(new BigDecimal("3.14"));
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_FLOAT);
          parser.getIntValue(); // expect failure
          parser.close();
      }

      @Test(expected = IOException.class)
      public void testDecimalLargeBeyondLong() throws IOException {
          // very large decimal that cannot fit into long
          JsonNode node = nodeFactory.numberNode(new BigDecimal("1e30"));
          TreeTraversingParser parser = new TreeTraversingParser(node);
          assertToken(parser, JsonToken.VALUE_NUMBER_FLOAT);
          parser.getLongValue(); // expect failure
          parser.close();
      }

      // helper
      private static void assertToken(JsonParser parser, JsonToken expected) throws IOException {
          assertNotNull("First token must not be null (did you forget to call nextToken()?)",
                       parser.nextToken());
          assertEquals("Current token mismatch", expected, parser.getCurrentToken());
      }
  }
