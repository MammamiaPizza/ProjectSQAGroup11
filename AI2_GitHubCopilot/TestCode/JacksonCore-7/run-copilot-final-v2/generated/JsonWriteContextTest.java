package com.fasterxml.jackson.core.json;

import static org.junit.Assert.*;

import org.junit.Test;

/**

 - Tests for {@link JsonWriteContext} focusing on status transitions,
 - field-name/value alternation, and the bug where writeValue() did not
 - require a preceding writeFieldName() in OBJECT context (Issue #177).
  */
 public class JsonWriteContextTest {
  // --- root context ---
  @Test
  public void testRootContextWriteValue() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  assertEquals(JsonWriteContext.STATUS_OK_AS_IS, root.writeValue());
  assertEquals(JsonWriteContext.STATUS_OK_AFTER_SPACE, root.writeValue());
  }
  // --- array context ---
  @Test
  public void testArrayContextWriteValue() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext array = root.createChildArrayContext();
  assertEquals(JsonWriteContext.STATUS_OK_AS_IS, array.writeValue());
  assertEquals(JsonWriteContext.STATUS_OK_AFTER_COMMA, array.writeValue());
  }
  // --- object context: missing field name (CORE BUG) ---
  @Test
  public void testObjectWriteValueWithoutFieldNameReturnsExpectName() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext obj = root.createChildObjectContext();
  // BUG: before the fix this returned STATUS_OK_AFTER_COLON
  assertEquals(JsonWriteContext.STATUS_EXPECT_NAME, obj.writeValue());
  }
  @Test
  public void testObjectWriteValueWithoutFieldNameAfterValidField() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext obj = root.createChildObjectContext();
  obj.writeFieldName("a");
  obj.writeValue(); // valid field value
  // next value must be preceded by a field name
  assertEquals(JsonWriteContext.STATUS_EXPECT_NAME, obj.writeValue());
  }
  // --- object context: correct name/value pair ---
  @Test
  public void testObjectWriteValueAfterFieldNameReturnsAfterColon() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext obj = root.createChildObjectContext();
  obj.writeFieldName("key");
  assertEquals(JsonWriteContext.STATUS_OK_AFTER_COLON, obj.writeValue());
  }
  // --- field name statuses ---
  @Test
  public void testObjectFirstFieldNameReturnsOkAsIs() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext obj = root.createChildObjectContext();
  assertEquals(JsonWriteContext.STATUS_OK_AS_IS, obj.writeFieldName("first"));
  }
  @Test
  public void testObjectSecondFieldNameReturnsOkAfterComma() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext obj = root.createChildObjectContext();
  obj.writeFieldName("first");
  obj.writeValue(); // complete first field (increments index)
  assertEquals(JsonWriteContext.STATUS_OK_AFTER_COMMA, obj.writeFieldName("second"));
  }
  @Test
  public void testObjectDoubleWriteFieldNameReturnsExpectValue() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext obj = root.createChildObjectContext();
  obj.writeFieldName("first");
  // calling writeFieldName again before a value should indicate a value is expected
  assertEquals(JsonWriteContext.STATUS_EXPECT_VALUE, obj.writeFieldName("second"));
  }
  // --- context creation & type ---
  @Test
  public void testCreateRootContext() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  assertNotNull(root);
  assertNull(root.getParent());
  assertEquals(com.fasterxml.jackson.core.JsonStreamContext.TYPE_ROOT, root.getTypeDesc());
  }
  @Test
  public void testCreateChildArrayContext() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext arr = root.createChildArrayContext();
  assertNotNull(arr);
  assertSame(root, arr.getParent());
  assertEquals(com.fasterxml.jackson.core.JsonStreamContext.TYPE_ARRAY, arr.getTypeDesc());
  }
  @Test
  public void testCreateChildObjectContext() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext obj = root.createChildObjectContext();
  assertNotNull(obj);
  assertSame(root, obj.getParent());
  assertEquals(com.fasterxml.jackson.core.JsonStreamContext.TYPE_OBJECT, obj.getTypeDesc());
  }
  // --- writeFieldName side-effects ---
  @Test
  public void testWriteFieldNameSetsCurrentName() {
  JsonWriteContext root = JsonWriteContext.createRootContext(null);
  JsonWriteContext obj = root.createChildObjectContext();
  obj.writeFieldName("myField");
  assertEquals("myField", obj.getCurrentName());
  }

}
        ]]>
    </source>
</test>
