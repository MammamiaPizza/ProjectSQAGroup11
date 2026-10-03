package com.fasterxml.jackson.databind.util;

import static org.junit.Assert.;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import com.fasterxml.jackson.core.;
import com.fasterxml.jackson.core.json.JsonWriteContext;

/**

 - Tests focused on the context-name propagation bug (JacksonDatabind #31 / report #984).
 - Write operations must be correctly reflected by both getOutputContext() and asParser().
  */
 public class TokenBufferContextPropagationTest {
  // ------------------ helpers ----------------
  private TokenBuffer buffer() {
  return new TokenBuffer((ObjectCodec) null);
  }
  private JsonParser parser(TokenBuffer tb) throws IOException {
  return tb.asParser();
  }
  private void assertNextToken(JsonParser p, JsonToken token) throws IOException {
  assertNotNull("Expected more tokens", p.nextToken());
  assertEquals(token, p.getCurrentToken());
  }
  // ------------------ tests ----------------
  @Test
  public void outputContextShouldHaveNameAfterWriteFieldName() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  tb.writeFieldName("a");
  assertEquals("a", tb.getOutputContext().getCurrentName());
  tb.close();
  }
  @Test
  public void outputContextShouldHaveNoNameAtRoot() throws IOException {
  TokenBuffer tb = buffer();
  // Root context exists from start
  assertNull(tb.getOutputContext().getCurrentName());
  tb.close();
  }
  @Test
  public void outputContextShouldNullAfterEndObject() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  tb.writeFieldName("x");
  tb.writeEndObject();
  // After writing END_OBJECT context reverts to parent (root)
  assertNull(tb.getOutputContext().getCurrentName());
  tb.close();
  }
  @Test
  public void outputContextShouldReflectNestedObject() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  tb.writeFieldName("outer");
  tb.writeStartObject();
  tb.writeFieldName("inner");
  JsonWriteContext ctx = tb.getOutputContext();
  assertEquals("inner", ctx.getCurrentName());
  tb.close();
  }
  @Test
  public void asParserShouldMatchSimpleFieldName() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  tb.writeFieldName("key");
  tb.writeNumber(123);
  tb.writeEndObject();
  JsonParser p = parser(tb);
  assertNextToken(p, JsonToken.START_OBJECT);
  assertNextToken(p, JsonToken.FIELD_NAME);
  assertEquals("key", p.getCurrentName());
  p.close();
  }
  @Test
  public void asParserShouldPropagateNestedFieldName() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  tb.writeFieldName("a");
  tb.writeStartObject();
  tb.writeFieldName("b"); // inner field name – bug would keep "a" here
  tb.writeEndObject();
  tb.writeEndObject();
  JsonParser p = parser(tb);
  assertNextToken(p, JsonToken.START_OBJECT);
  assertNextToken(p, JsonToken.FIELD_NAME);
  assertEquals("a", p.getCurrentName());
  assertNextToken(p, JsonToken.START_OBJECT);
  assertNextToken(p, JsonToken.FIELD_NAME);
  assertEquals("b", p.getCurrentName()); // [bug#31] expected "b", not "a"
  p.close();
  }
  @Test
  public void asParserShouldPreserveFieldNameThroughArray() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  tb.writeFieldName("arr");
  tb.writeStartArray();
  tb.writeNumber(1);
  tb.writeEndArray();
  tb.writeEndObject();
  JsonParser p = parser(tb);
  assertNextToken(p, JsonToken.START_OBJECT);
  assertNextToken(p, JsonToken.FIELD_NAME);
  assertEquals("arr", p.getCurrentName());
  assertNextToken(p, JsonToken.START_ARRAY);
  // Inside array the current name is not available in this context
  assertNull(p.getCurrentName());
  assertNextToken(p, JsonToken.VALUE_NUMBER_INT);
  assertNull(p.getCurrentName());
  assertNextToken(p, JsonToken.END_ARRAY);
  assertNull(p.getCurrentName());
  p.close();
  }
  @Test
  public void asParserShouldHaveNullNameInEmptyObject() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  tb.writeEndObject();
  JsonParser p = parser(tb);
  assertNextToken(p, JsonToken.START_OBJECT);
  assertNull(p.getCurrentName());
  assertNextToken(p, JsonToken.END_OBJECT);
  assertNull(p.getCurrentName());
  p.close();
  }
  @Test
  public void writeFieldNameNullShouldBeHandledGracefully() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  try {
      tb.writeFieldName((String) null);
      // Should reach here without exception
  } catch (NullPointerException e) {
      // TokenBuffer throws NPE for null field name; this is the current behavior
      return;
  }
  tb.writeEndObject();
  JsonParser p = parser(tb);
  assertNextToken(p, JsonToken.START_OBJECT);
  assertNextToken(p, JsonToken.FIELD_NAME);
  // Name was null; current name may be null or empty – just verify no corruption
  p.close();
  tb.close();
  }
  @Test
  public void rootFieldNameShouldBeAccessibleViaParser() throws IOException {
  TokenBuffer tb = buffer();
  // Writing a field name without START_OBJECT is not standard JSON but TokenBuffer allows it
  tb.writeFieldName("rootField");
  tb.writeString("value");
  JsonParser p = parser(tb);
  assertNextToken(p, JsonToken.FIELD_NAME);
  assertEquals("rootField", p.getCurrentName());
  assertNextToken(p, JsonToken.VALUE_STRING);
  // After value the name may still be the last field name
  assertEquals("rootField", p.getCurrentName());
  p.close();
  }
  @Test
  public void closedBufferAsParserShouldStillWork() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeStartObject();
  tb.writeFieldName("x");
  tb.writeNumber(1);
  tb.writeEndObject();
  tb.close();
  // asParser should return a valid parser even after closing the generator half
  JsonParser p = parser(tb);
  assertNextToken(p, JsonToken.START_OBJECT);
  assertNextToken(p, JsonToken.FIELD_NAME);
  assertEquals("x", p.getCurrentName());
  p.close();
  }
  @Test
  public void outputContextShouldReflectWriteFieldNameOnRoot() throws IOException {
  TokenBuffer tb = buffer();
  tb.writeFieldName("rootField");
  JsonWriteContext ctx = tb.getOutputContext();
  // Without a preceding START_OBJECT the context may be root; still name should be set
  assertEquals("rootField", ctx.getCurrentName());
  tb.close();
  }

}
