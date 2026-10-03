package com.fasterxml.jackson.databind.convert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Tests for ObjectReader with {@code withValueToUpdate} (update mode)
 - that focus on handling of unknown properties according to
 - {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}.
  */
 public class TestIssue744 {
  // Simple value class matching the bug report's DataB
  public static class DataB {
  public String da;
  public int k;
  public DataB() { }
  public DataB(String da, int k) {
      this.da = da;
      this.k = k;
  }
  }
  // --- Normal update -----------------------------------------------------------------
  @Test
  public void testUpdateWithMatchingFields() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  DataB value = new DataB();
  ObjectReader reader = mapper.reader(DataB.class).withValueToUpdate(value);
  DataB result = reader.readValue("{"da":"hello","k":42}");
  assertSame("update must return the same instance", value, result);
  assertEquals("hello", value.da);
  assertEquals(42, value.k);
  }
  @Test
  public void testUpdateLeavesUnmentionedFieldsUntouched() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  DataB value = new DataB("original", 7);
  ObjectReader reader = mapper.reader(DataB.class).withValueToUpdate(value)
          .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  reader.readValue("{"k":100}");
  assertEquals("original", value.da);
  assertEquals(100, value.k);
  }
  @Test
  public void testUpdateReturnsSameInstance() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  DataB value = new DataB();
  ObjectReader reader = mapper.reader(DataB.class).withValueToUpdate(value);
  DataB result = reader.readValue("{"da":"x","k":1}");
  assertSame(value, result);
  }
  // --- Unknown properties handling ---------------------------------------------------
  @Test
  public void testUpdateWithExtraFieldIgnoredViaReaderOverride() throws Exception {
  ObjectMapper mapper = new ObjectMapper(); // default: fail on unknown = true
  DataB value = new DataB();
  ObjectReader reader = mapper.reader(DataB.class).withValueToUpdate(value)
          .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  // Should silently ignore the extra field "i"
  DataB result = reader.readValue("{"da":"a","k":1,"i":"extra"}");
  assertSame(value, result);
  assertEquals("a", value.da);
  assertEquals(1, value.k);
  }
  @Test(expected = UnrecognizedPropertyException.class)
  public void testUpdateWithExtraFieldThrowsWhenFeatureEnabled() throws Exception {
  ObjectMapper mapper = new ObjectMapper(); // fails on unknown by default
  DataB value = new DataB();
  ObjectReader reader = mapper.reader(DataB.class).withValueToUpdate(value);
  reader.readValue("{"da":"a","k":1,"i":"extra"}");
  }
  @Test
  public void testUpdateWithExtraFieldIgnoredViaMapperConfig() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  DataB value = new DataB();
  ObjectReader reader = mapper.reader(DataB.class).withValueToUpdate(value);
  DataB result = reader.readValue("{"da":"b","k":2,"i":"oops"}");
  assertEquals("b", value.da);
  assertEquals(2, value.k);
  }
  // --- Rejection of invalid input ----------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void testWithValueToUpdateThrowsForNull() {
  new ObjectMapper().reader(DataB.class).withValueToUpdate(null);
  }
  @Test(expected = IllegalArgumentException.class)
  public void testWithValueToUpdateThrowsForArray() {
  new ObjectMapper().reader(String[].class).withValueToUpdate(new String[0]);
  }
  // --- Plain read (without update) also respects feature override --------------------
  @Test
  public void testNormalReadIgnoresExtraFieldWhenFeatureDisabled() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  ObjectReader reader = mapper.reader(DataB.class)
          .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  DataB result = reader.readValue("{"da":"c","k":3,"i":"extra"}");
  assertEquals("c", result.da);
  assertEquals(3, result.k);
  }
  @Test(expected = UnrecognizedPropertyException.class)
  public void testNormalReadThrowsWhenFeatureEnabled() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  ObjectReader reader = mapper.reader(DataB.class);
  reader.readValue("{"da":"c","k":3,"i":"extra"}");
  }

}