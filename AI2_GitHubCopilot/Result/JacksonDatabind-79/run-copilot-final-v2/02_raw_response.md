package com.fasterxml.jackson.databind.objectid;

import static org.junit.Assert.*;

import java.util.*;

import com.fasterxml.jackson.annotation.;
import com.fasterxml.jackson.databind.;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

/**

 - Tests for propagation of {@code alwaysAsId} from {@link JsonIdentityInfo}
 - into {@link ObjectIdInfo} and its effect on serialization.
  */
 @SuppressWarnings("serial")
 public class JacksonAnnotationIntrospectorAlwaysAsIdTest {
  private final JacksonAnnotationIntrospector introspector = new JacksonAnnotationIntrospector();
  // ----- Annotated helper classes -----
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  static class DefaultAlwaysAsIdBean {
  public int id;
  public int value;
  public DefaultAlwaysAsIdBean() {}
  public DefaultAlwaysAsIdBean(int id, int value) { this.id = id; this.value = value; }
  }
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
alwaysAsId = true)
  static class TrueAlwaysAsIdBean {
  public int id;
  public int value;
  public TrueAlwaysAsIdBean() {}
  public TrueAlwaysAsIdBean(int id, int value) { this.id = id; this.value = value; }
  }
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
alwaysAsId = false)
  static class FalseAlwaysAsIdBean {
  public int id;
  public int value;
  public FalseAlwaysAsIdBean() {}
  public FalseAlwaysAsIdBean(int id, int value) { this.id = id; this.value = value; }
  }
  static class PropertyLevelBean {
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
alwaysAsId = true)
  public TrueAlwaysAsIdBean bean;
  public PropertyLevelBean() {}
  public PropertyLevelBean(TrueAlwaysAsIdBean bean) { this.bean = bean; }
  }
  static class ContainerClassLevel {
  public List<TrueAlwaysAsIdBean> alwaysClass;
  public int alwaysProp;
  public ContainerClassLevel() {}
  public ContainerClassLevel(List<TrueAlwaysAsIdBean> alwaysClass, int alwaysProp) {
      this.alwaysClass = alwaysClass;
      this.alwaysProp = alwaysProp;
  }
  }
  static class ContainerPropertyLevel {
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id",
alwaysAsId = true)
  public TrueAlwaysAsIdBean alwaysPropBean;
  public int alwaysProp;
  public ContainerPropertyLevel() {}
  public ContainerPropertyLevel(TrueAlwaysAsIdBean bean, int prop) {
      this.alwaysPropBean = bean;
      this.alwaysProp = prop;
  }
  }
  // ===== Introspection tests =====
  @Test
  public void testClassLevelAlwaysAsIdTrue() {
  AnnotatedClass ac = AnnotatedClass.construct(TrueAlwaysAsIdBean.class, introspector, null);
  ObjectIdInfo info = introspector.findObjectIdInfo(ac);
  assertNotNull("ObjectIdInfo should be present for annotated class", info);
  assertTrue("alwaysAsId must be true when annotation sets it to true", info.getAlwaysAsId());
  }
  @Test
  public void testClassLevelAlwaysAsIdFalseExplicit() {
  AnnotatedClass ac = AnnotatedClass.construct(FalseAlwaysAsIdBean.class, introspector, null);
  ObjectIdInfo info = introspector.findObjectIdInfo(ac);
  assertNotNull(info);
  assertFalse("Explicit alwaysAsId=false must be preserved", info.getAlwaysAsId());
  }
  @Test
  public void testClassLevelAlwaysAsIdDefault() {
  AnnotatedClass ac = AnnotatedClass.construct(DefaultAlwaysAsIdBean.class, introspector, null);
  ObjectIdInfo info = introspector.findObjectIdInfo(ac);
  assertNotNull(info);
  assertFalse("Default alwaysAsId should be false", info.getAlwaysAsId());
  }
  @Test
  public void testPropertyLevelAlwaysAsIdTrue() throws Exception {
  AnnotatedClass ac = AnnotatedClass.construct(PropertyLevelBean.class, introspector, null);
  AnnotatedField field = ac.find(PropertyLevelBean.class.getDeclaredField("bean"));
  ObjectIdInfo info = introspector.findObjectIdInfo(field);
  assertNotNull("ObjectIdInfo must be found for property-level annotation", info);
  assertTrue("Property-level alwaysAsId must be true", info.getAlwaysAsId());
  }
  // ===== ObjectIdInfo mutation tests =====
  @Test
  public void testObjectIdInfoWithAlwaysAsIdChangesState() {
  ObjectIdInfo orig = new ObjectIdInfo(new PropertyName("test"), null,
ObjectIdGenerators.PropertyGenerator.class, false);
  assertFalse(orig.getAlwaysAsId());
  ObjectIdInfo changed = orig.withAlwaysAsId(true);
  assertNotSame("withAlwaysAsId should return a new instance when state differs", orig, changed);
  assertTrue(changed.getAlwaysAsId());
  assertEquals(orig.getPropertyName(), changed.getPropertyName());
  assertEquals(orig.getGeneratorType(), changed.getGeneratorType());
  assertEquals(orig.getScope(), changed.getScope());
  }
  @Test
  public void testObjectIdInfoWithAlwaysAsIdSameState() {
  ObjectIdInfo info = new ObjectIdInfo(new PropertyName("test"), null,
ObjectIdGenerators.PropertyGenerator.class, true);
  ObjectIdInfo same = info.withAlwaysAsId(true);
  assertSame("withAlwaysAsId with same state should return the same instance", info, same);
  }
  @Test
  public void testDeprecatedConstructorsSetAlwaysAsIdFalse() {
  ObjectIdInfo c1 = new ObjectIdInfo(new PropertyName("prop"), null,
ObjectIdGenerators.PropertyGenerator.class);
  assertFalse("Deprecated constructor (2-arg) must default alwaysAsId to false",
c1.getAlwaysAsId());
  ObjectIdInfo c2 = new ObjectIdInfo("prop", null, ObjectIdGenerators.PropertyGenerator.class);
  assertFalse("Deprecated constructor (String) must default alwaysAsId to false",
c2.getAlwaysAsId());
  }
  // ===== Serialization tests =====
  @Test
  public void testSerializationClassLevelAlwaysAsId() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  TrueAlwaysAsIdBean bean1 = new TrueAlwaysAsIdBean(1, 13);
  TrueAlwaysAsIdBean bean2 = new TrueAlwaysAsIdBean(2, 42);
  ContainerClassLevel container = new ContainerClassLevel(Arrays.asList(bean1, bean2), 2);
  String json = mapper.writeValueAsString(container);
  // Expected: alwaysClass contains only IDs, alwaysProp as is
  ObjectNode expected = (ObjectNode) mapper.readTree("{"alwaysClass":[1,2],"alwaysProp":2}");
  ObjectNode actual = (ObjectNode) mapper.readTree(json);
  assertEquals("class-level alwaysAsId: list elements must be serialized as IDs only", expected,
actual);
  }
  @Test
  public void testSerializationPropertyLevelAlwaysAsId() throws Exception {
  ObjectMapper mapper = new ObjectMapper();
  TrueAlwaysAsIdBean bean = new TrueAlwaysAsIdBean(1, 13);
  ContainerPropertyLevel container = new ContainerPropertyLevel(bean, 2);
  String json = mapper.writeValueAsString(container);
  // Expected: alwaysPropBean is serialized as ID (1)
  ObjectNode expected = (ObjectNode) mapper.readTree("{"alwaysPropBean":1,"alwaysProp":2}");
  ObjectNode actual = (ObjectNode) mapper.readTree(json);
  assertEquals("property-level alwaysAsId: property must be serialized as ID", expected, actual);
  }
  @Test
  public void testSerializationClassAlwaysAsIdFalseProducesFullObjectOnFirstOccurrence() throws
Exception {
  ObjectMapper mapper = new ObjectMapper();
  DefaultAlwaysAsIdBean bean1 = new DefaultAlwaysAsIdBean(1, 13);
  DefaultAlwaysAsIdBean bean2 = new DefaultAlwaysAsIdBean(1, 99); // same id to trigger reference
  List<DefaultAlwaysAsIdBean> list = Arrays.asList(bean1, bean2);
  String json = mapper.writeValueAsString(list);
  // First occurrence full, second reference by id.
  ObjectNode expected = (ObjectNode) mapper.readTree("[{"id":1,"value":13},1]");
  ObjectNode actual = (ObjectNode) mapper.readTree(json);
  assertEquals("alwaysAsId=false: first occurrence full, subsequent by id", expected, actual);
  }
  @Test
  public void testBeanSerializerBaseReceivesAlwaysAsId() throws Exception {
  // Verify that BeanSerializerBase for TrueAlwaysAsIdBean has objectIdWriter with alwaysAsId=true
  ObjectMapper mapper = new ObjectMapper();
  SerializerProvider provider = mapper.getSerializerProvider();
  JsonSerializer<Object> ser = provider.findValueSerializer(TrueAlwaysAsIdBean.class);
  assertTrue("Serializer must be BeanSerializerBase", ser instanceof BeanSerializerBase);
  BeanSerializerBase base = (BeanSerializerBase) ser;
  assertNotNull(base._objectIdWriter);
  assertTrue("BeanSerializerBase must carry alwaysAsId=true", base._objectIdWriter.alwaysAsId);
  }

}