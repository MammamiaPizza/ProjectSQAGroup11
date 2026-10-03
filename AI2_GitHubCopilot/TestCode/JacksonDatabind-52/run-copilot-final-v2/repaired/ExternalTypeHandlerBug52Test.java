package com.fasterxml.jackson.databind.jsontype.ext;

import static org.junit.Assert.*;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;

/**

 - Tests for ExternalTypeHandler bug (databind#999): external type id property is not set
 - on bean when using property-based creator.
  */
 public class ExternalTypeHandlerBug52Test {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  // Polymorphic value base with external type id
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
      property = "type", visible = true)
  @JsonSubTypes({@JsonSubTypes.Type(value = FooValue.class, name = "foo"),
      @JsonSubTypes.Type(value = BarValue.class, name = "bar")})
  interface ValueBase { }
  static class FooValue implements ValueBase {
  public String foo;
  }
  static class BarValue implements ValueBase {
  public int bar;
  }
  // Bean using property-based creator (the buggy scenario)
  static class CreatorBean {
  public final String type;
  public final ValueBase value;
  @JsonCreator
  public CreatorBean(@JsonProperty("type") String type,
                     @JsonProperty("value") ValueBase value) {
      this.type = type;
      this.value = value;
  }
  }
  // Bean using setters (should work even in buggy version)
  static class SetterBean {
  private String type;
  private ValueBase value;
  public void setType(String type) { this.type = type; }
  public void setValue(ValueBase value) { this.value = value; }
  public String getType() { return type; }
  public ValueBase getValue() { return value; }
  }
  // Bean with external type and default implementation
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
      property = "type", visible = true, defaultImpl = BarValue.class)
  @JsonSubTypes({@JsonSubTypes.Type(value = FooValue.class, name = "foo"),
      @JsonSubTypes.Type(value = BarValue.class, name = "bar")})
  interface DefaultableValue extends ValueBase { }
  static class DefaultableCreatorBean {
  public final String type;
  public final DefaultableValue value;
  @JsonCreator
  public DefaultableCreatorBean(@JsonProperty("type") String type,
                                @JsonProperty("value") DefaultableValue value) {
      this.type = type;
      this.value = value;
  }
  }
  // Helper
  private void assertBean(String type, Class<? extends ValueBase> valueClass,
                       CreatorBean bean) {
  assertEquals(type, bean.type);
  assertNotNull(bean.value);
  assertTrue(valueClass.isInstance(bean.value));
  }
  // ---- Tests ----
  //
  1. Core bug: external type id property not set with creator parameters
  @Test
  public void testExternalTypeIdWithCreator() throws Exception {
  String json = "{"type":"foo", "value":{"foo":"bar"}}";
  CreatorBean bean = MAPPER.readValue(json, CreatorBean.class);
  assertBean("foo", FooValue.class, bean);
  assertEquals("bar", ((FooValue) bean.value).foo);
  }
  //
  2. Same scenario but with setter-based bean (should pass in buggy version too)
  @Test
  public void testExternalTypeIdWithSetters() throws Exception {
  String json = "{"type":"bar", "value":{"bar":42}}";
  SetterBean bean = MAPPER.readValue(json, SetterBean.class);
  assertEquals("bar", bean.getType());
  assertTrue(bean.getValue() instanceof BarValue);
  assertEquals(42, ((BarValue) bean.getValue()).bar);
  }
  //
  3. Null type id: type property should be null, no subtype resolution needed
  @Test
  public void testNullExternalTypeId() throws Exception {
  String json = "{"type":null, "value":{"foo":"baz"}}";
  CreatorBean bean = MAPPER.readValue(json, CreatorBean.class);
  assertNull(bean.type);
  // With null type id, natural type deserialization may apply; FooValue doesn't
  // have a natural type mapping, so the property might be set or error.
  // In buggy version this path likely fails; after fix, type property is null.
  // We check that type is null (the external id value).
  assertNull(bean.type);
  }
  //
  4. Type field appears before value in JSON (order independence)
  @Test
  public void testTypeOrderBeforeValue() throws Exception {
  String json = "{"type":"foo", "value":{"foo":"first"}}";
  CreatorBean bean = MAPPER.readValue(json, CreatorBean.class);
  assertBean("foo", FooValue.class, bean);
  }
  //
  5. Type field appears after value in JSON
  @Test
  public void testTypeOrderAfterValue() throws Exception {
  String json = "{"value":{"bar":7}, "type":"bar"}";
  SetterBean bean = MAPPER.readValue(json, SetterBean.class);
  assertEquals("bar", bean.getType());
  assertTrue(bean.getValue() instanceof BarValue);
  assertEquals(7, ((BarValue) bean.getValue()).bar);
  }
  //
  6. Missing type field with defaultImpl – value should be default subtype,
  //    type property should remain null (no type id to set)
  @Test
  public void testMissingTypeWithDefaultImpl() throws Exception {
  String json = "{"value":{"bar":99}}";
  DefaultableCreatorBean bean = MAPPER.readValue(json, DefaultableCreatorBean.class);
  assertNull(bean.type);
  assertTrue(bean.value instanceof BarValue);
  assertEquals(99, ((BarValue) bean.value).bar);
  }
  //
  7. Unknown type id should cause an exception
  @Test(expected = InvalidTypeIdException.class)
  public void testUnknownExternalTypeId() throws Exception {
  String json = "{"type":"unknown", "value":{"foo":"x"}}";
  MAPPER.readValue(json, CreatorBean.class);
  }
  //
  8. Both type and value missing from JSON: when using creator, should fail
  //    because value property is required
  @Test(expected = JsonMappingException.class)
  public void testBothTypeAndValueMissing() throws Exception {
  String json = "{}";
  MAPPER.readValue(json, CreatorBean.class);
  }
  //
  9. Type id set to empty string: invalid type name, should throw
  @Test(expected = InvalidTypeIdException.class)
  public void testEmptyTypeId() throws Exception {
  String json = "{"type":"", "value":{"foo":"e"}}";
  MAPPER.readValue(json, CreatorBean.class);
  }

}
