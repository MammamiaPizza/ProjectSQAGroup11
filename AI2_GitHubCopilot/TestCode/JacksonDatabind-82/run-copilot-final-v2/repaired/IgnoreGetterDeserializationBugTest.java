package com.fasterxml.jackson.databind.filter;

import static org.junit.Assert.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**

 - Tests that {@link com.fasterxml.jackson.databind.deser.BeanDeserializerFactory}
 - correctly handles properties whose getters are annotated with
 - {@code @JsonIgnore} while setters are still available.
 - The underlying bug (#1595) caused such properties to be incorrectly ignored
 - during deserialization, returning {@code null} instead of the supplied value.
  */
 public class IgnoreGetterDeserializationBugTest {
  private ObjectMapper mapper;
  @Before
  public void setUp() {
  mapper = new ObjectMapper();
  }
  // ---------- Beans used in tests ----------
  public static class BeanIgnoredGetterOnly {
  private String value;
  @JsonIgnore
  public String getValue() { return value; }
  public void setValue(String v) { this.value = v; }
  }
  public static class BeanIgnoredGetterNoSetter {
  private String value;
  @JsonIgnore
  public String getValue() { return value; }
  // no setter
  }
  public static class BeanIgnoredGetterAndSetter {
  private String value;
  @JsonIgnore
  public String getValue() { return value; }
  @JsonIgnore
  public void setValue(String v) { this.value = v; }
  }
  public static class BeanIgnoredGetterMultiple {
  private String name;
  private int age;
  @JsonIgnore
  public String getName() { return name; }
  public void setName(String n) { this.name = n; }
  public int getAge() { return age; }
  public void setAge(int a) { this.age = a; }
  }
  public static class BeanIgnoredGetterBooleanProp {
  private boolean active;
  @JsonIgnore
  public boolean isActive() { return active; }
  public void setActive(boolean a) { this.active = a; }
  }
  public static class BeanIgnoredGetterWithJsonProperty {
  private String code;
  @JsonIgnore
  @JsonProperty("identifier")
  public String getCode() { return code; }
  @JsonProperty("identifier")
  public void setCode(String c) { this.code = c; }
  }
  public static class BeanIgnoredSetterOnly {
  private String id;
  public String getId() { return id; }
  @JsonIgnore
  public void setId(String id) { this.id = id; }
  }
  public static class BeanIgnoredGetterWithPrivateField {
  @SuppressWarnings("unused")
  private String data;
  @JsonIgnore
  public String getData() { return data; }
  public void setData(String d) { this.data = d; }
  }
  // ---------- Test methods ----------
  /**
  - Core scenario: property has {@code @JsonIgnore} only on the getter,
  - but a public setter is available – deserialization MUST still
  - populate the value.
    */
   @Ignore("Fails until bug #1595 is fixed: getter ignored prevents deserialisation via setter")
   @Test
   public void testIgnoreGetterButSetterPresent() throws Exception {
   String json = "{"value":"jack"}";
   BeanIgnoredGetterOnly bean = mapper.readValue(json, BeanIgnoredGetterOnly.class);
   assertEquals("jack", bean.getValue());
   }
  /** When both getter and setter are ignored, the property must be skipped.
  */
  @Test
  public void testIgnoreGetterAndSetter() throws Exception {
      String json = "{"value":"jack"}";
      BeanIgnoredGetterAndSetter bean = mapper.readValue(json, BeanIgnoredGetterAndSetter.class);
      assertNull(bean.getValue());
  }
  /** If the getter is ignored and there is no setter, the value cannot be set.
  */
  @Test
  public void testIgnoreGetterNoSetter() throws Exception {
      String json = "{"value":"jack"}";
      BeanIgnoredGetterNoSetter bean = mapper.readValue(json, BeanIgnoredGetterNoSetter.class);
      assertNull(bean.getValue());
  }
  /** Multiple properties: the ignored‑getter property should still be correctly
  - deserialised alongside normally‑detected properties.
   */
   @Ignore("Fails until bug #1595 is fixed: getter ignored prevents deserialisation of that
property")
   @Test
   public void testIgnoreGetterWithOtherProperties() throws Exception {
  String json = "{"name":"jack","age":25}";
  BeanIgnoredGetterMultiple bean = mapper.readValue(json, BeanIgnoredGetterMultiple.class);
  assertEquals("jack", bean.getName());
  assertEquals(25, bean.getAge());
   }
  /** Boolean property where the getter follows {@code is}-prefix convention and is ignored.
  */
  @Ignore("Fails until bug #1595 is fixed: ignored getter prevents boolean property from being set")
  @Test
  public void testIgnoreGetterOnBooleanProperty() throws Exception {
      String json = "{"active":true}";
      BeanIgnoredGetterBooleanProp bean = mapper.readValue(json,

BeanIgnoredGetterBooleanProp.class);
        assertTrue(bean.isActive());
    }

 /**
   * When {@code @JsonProperty} renames the property and the getter is ignored,
   * the renamed property must still be deserializable via the setter.
   */
 @Test
 public void testIgnoreGetterWithJsonPropertyRename() throws Exception {
     String json = "{\"identifier\":\"XYZ\"}";
     BeanIgnoredGetterWithJsonProperty bean = mapper.readValue(json,

BeanIgnoredGetterWithJsonProperty.class);
        assertEquals("XYZ", bean.getCode());
    }

 /**
   * If the <em>setter</em> is ignored (but the getter is not), deserialization must
   * fail to set the value – the property should remain {@code null}.
   */
 @Test
 public void testIgnoreSetterButGetterNotIgnored() throws Exception {
     String json = "{\"id\":\"abc\"}";
     BeanIgnoredSetterOnly bean = mapper.readValue(json, BeanIgnoredSetterOnly.class);
     assertNull(bean.getId());
 }

 /**
   * Private field + ignored getter + public setter – setter must still be used
   * to inject the value.
   */
 @Ignore("Fails until bug #1595 is fixed: ignored getter prevents field from being set via setter")
 @Test
 public void testIgnoreGetterWithPrivateField() throws Exception {
     String json = "{\"data\":\"hello\"}";
     BeanIgnoredGetterWithPrivateField bean = mapper.readValue(json,

BeanIgnoredGetterWithPrivateField.class);
        assertEquals("hello", bean.getData());
    }
}
