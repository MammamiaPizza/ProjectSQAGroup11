import java.io.IOException;
import java.util.*;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;

import static org.junit.Assert.*;

/**

 - Regression tests for JacksonDatabind bug #1804: abstract types with an
 - array-delegate creator must be instantiable.
  */
 public class ValueInstantiatorArrayDelegateTest {
  // ---- abstract with array delegate ----
  static abstract class AbstractArrayDelegate {
  @JsonCreator
  public static AbstractArrayDelegate create(String[] values) {
      return new ImplArrayDelegate(values);
  }
  }
  static class ImplArrayDelegate extends AbstractArrayDelegate {
  final String[] values;
  ImplArrayDelegate(String[] v) { this.values = v; }
  }
  // ---- abstract without any creator ----
  static abstract class AbstractWithoutCreator { }
  // ---- concrete with array delegate ----
  static class ConcreteArrayDelegate {
  final int[] nums;
  private ConcreteArrayDelegate(int[] nums) { this.nums = nums; }
  @JsonCreator
  public static ConcreteArrayDelegate create(int[] values) {
      return new ConcreteArrayDelegate(values);
  }
  }
  // ---- abstract with a simple delegate (non-array) ----
  static abstract class AbstractDelegateOnly {
  final String data;
  protected AbstractDelegateOnly(String data) { this.data = data; }
  @JsonCreator
  public static AbstractDelegateOnly from(String s) {
      return new ImplDelegateOnly(s);
  }
  }
  static class ImplDelegateOnly extends AbstractDelegateOnly {
  ImplDelegateOnly(String s) { super(s); }
  }
  // ---- abstract with both array-delegate and simple delegate ----
  static abstract class AbstractBothDelegates {
  final Object payload;
  protected AbstractBothDelegates(Object payload) { this.payload = payload; }
  @JsonCreator
  public static AbstractBothDelegates fromArray(String[] vals) {
      return new ImplBothArray(vals);
  }
  @JsonCreator
  public static AbstractBothDelegates fromString(String s) {
      return new ImplBothString(s);
  }
  }
  static class ImplBothArray extends AbstractBothDelegates {
  ImplBothArray(String[] vals) { super(vals); }
  }
  static class ImplBothString extends AbstractBothDelegates {
  ImplBothString(String s) { super(s); }
  }
  // ---- abstract with a no-arg default creator (impossible to instantiate) ----
  static abstract class AbstractWithDefault {
  public AbstractWithDefault() { }
  }
  // ---- concrete POJO with no creators (default instantiation works) ----
  static class ConcreteDefault {
  public int x;
  }
  private final ObjectMapper mapper = new ObjectMapper();
  @Test
  public void abstractWithArrayDelegateShouldSucceed() throws Exception {
  String json = "["a","b"]";
  AbstractArrayDelegate result = mapper.readValue(json, AbstractArrayDelegate.class);
  assertNotNull(result);
  assertTrue(result instanceof ImplArrayDelegate);
  assertArrayEquals(new String[]{"a","b"}, ((ImplArrayDelegate) result).values);
  }
  @Test
  public void abstractWithoutCreatorShouldFail() throws Exception {
  try {
      mapper.readValue("{"a":1}", AbstractWithoutCreator.class);
      fail("Should have thrown");
  } catch (Exception e) {
      // should be a JsonMappingException or InvalidDefinitionException
      assertTrue(e.getMessage(), e.getMessage().contains("abstract types either need"));
  }
  }
  @Test
  public void concreteArrayDelegateShouldSucceed() throws Exception {
  String json = "[1,2,3]";
  ConcreteArrayDelegate result = mapper.readValue(json, ConcreteArrayDelegate.class);
  assertNotNull(result);
  assertArrayEquals(new int[]{1,2,3}, result.nums);
  }
  @Test
  public void abstractWithDelegateOnlyShouldSucceed() throws Exception {
  String json = ""hello"";
  AbstractDelegateOnly result = mapper.readValue(json, AbstractDelegateOnly.class);
  assertNotNull(result);
  assertTrue(result instanceof ImplDelegateOnly);
  assertEquals("hello", ((ImplDelegateOnly) result).data);
  }
  @Test
  public void abstractBothDelegatesArrayInputShouldUseArray() throws Exception {
  String json = "["x","y"]";
  AbstractBothDelegates result = mapper.readValue(json, AbstractBothDelegates.class);
  assertNotNull(result);
  assertTrue(result instanceof ImplBothArray);
  String[] arr = (String[]) ((ImplBothArray) result).payload;
  assertArrayEquals(new String[]{"x","y"}, arr);
  }
  @Test
  public void abstractBothDelegatesStringInputShouldUseDelegate() throws Exception {
  String json = ""single"";
  AbstractBothDelegates result = mapper.readValue(json, AbstractBothDelegates.class);
  assertNotNull(result);
  assertTrue(result instanceof ImplBothString);
  assertEquals("single", ((ImplBothString) result).payload);
  }
  @Test
  public void abstractWithDefaultCreatorShouldFail() throws Exception {
  try {
      mapper.readValue("{}", AbstractWithDefault.class);
      fail("Should have thrown");
  } catch (Exception e) {
      assertTrue(e.getMessage(), e.getMessage().toLowerCase().contains("abstract"));
  }
  }
  @Test
  public void concreteDefaultShouldSucceed() throws Exception {
  String json = "{"x":42}";
  ConcreteDefault result = mapper.readValue(json, ConcreteDefault.class);
  assertEquals(42, result.x);
  }
  // -----------------------------------------------------------
  //  Direct ValueInstantiator API tests (ensure canInstantiate returns correct boolean)
  // -----------------------------------------------------------
  @Test
  public void arrayDelegateGivesCanInstantiateTrue() throws Exception {
  DeserializationConfig config = mapper.getDeserializationConfig();
  JavaType type = config.constructType(AbstractArrayDelegate.class);
  BasicBeanDescription desc = (BasicBeanDescription) config.introspect(type);
  DeserializationContext ctxt = mapper.getDeserializationContext();
  ValueInstantiator vi = ctxt.getFactory().findValueInstantiator(ctxt, desc);
  assertNotNull(vi);
  assertTrue("canInstantiate should be true for abstract + array delegate",

vi.canInstantiate());
        assertTrue("canCreateUsingArrayDelegate should be true", vi.canCreateUsingArrayDelegate());
        assertNotNull("getArrayDelegateCreator should not be null", vi.getArrayDelegateCreator());
    }

 @Test
 public void delegateOnlyGivesCanInstantiateTrue() throws Exception {
     DeserializationConfig config = mapper.getDeserializationConfig();
     JavaType type = config.constructType(AbstractDelegateOnly.class);
     BasicBeanDescription desc = (BasicBeanDescription) config.introspect(type);
     DeserializationContext ctxt = mapper.getDeserializationContext();
     ValueInstantiator vi = ctxt.getFactory().findValueInstantiator(ctxt, desc);
     assertNotNull(vi);
     assertTrue("canInstantiate should be true for abstract + delegate", vi.canInstantiate());
     assertTrue("canCreateUsingDelegate should be true", vi.canCreateUsingDelegate());
     assertNotNull("getDelegateCreator should not be null", vi.getDelegateCreator());
     assertNull("no array delegate", vi.getArrayDelegateCreator());
 }

 @Test
 public void noCreatorGivesCanInstantiateFalse() throws Exception {
     DeserializationConfig config = mapper.getDeserializationConfig();
     JavaType type = config.constructType(AbstractWithoutCreator.class);
     BasicBeanDescription desc = (BasicBeanDescription) config.introspect(type);
     DeserializationContext ctxt = mapper.getDeserializationContext();
     ValueInstantiator vi = ctxt.getFactory().findValueInstantiator(ctxt, desc);
     assertNotNull(vi);
     assertFalse("canInstantiate should be false for abstract without creators",

vi.canInstantiate());
        assertFalse(vi.canCreateUsingArrayDelegate());
        assertFalse(vi.canCreateUsingDelegate());
    }

 @Test
 public void bothDelegatesGiveCorrectFlags() throws Exception {
     DeserializationConfig config = mapper.getDeserializationConfig();
     JavaType type = config.constructType(AbstractBothDelegates.class);
     BasicBeanDescription desc = (BasicBeanDescription) config.introspect(type);
     DeserializationContext ctxt = mapper.getDeserializationContext();
     ValueInstantiator vi = ctxt.getFactory().findValueInstantiator(ctxt, desc);
     assertNotNull(vi);
     assertTrue(vi.canInstantiate());
     assertTrue(vi.canCreateUsingArrayDelegate());
     assertTrue(vi.canCreateUsingDelegate());
     assertNotNull(vi.getArrayDelegateCreator());
     assertNotNull(vi.getDelegateCreator());
 }

}