package com.fasterxml.jackson.databind.deser;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BeanDeserializerBaseBug1421Test {
    private static final ObjectMapper MAPPER = new ObjectMapper();

 // ---------- test POJOs ----------

 static class BeanWithArrayDelegate {
     final List<String> values;

     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     public BeanWithArrayDelegate(List<String> values) {
         this.values = values;
     }
 }

 static class BeanWithStringDelegate {
     final String value;

     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     public BeanWithStringDelegate(String value) {
         this.value = value;
     }
 }

 static class ChainA {
     final ChainB inner;

     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     public ChainA(ChainB inner) {
         this.inner = inner;
     }
 }

 static class ChainB {
     final List<String> values;

     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     public ChainB(List<String> values) {
         this.values = values;
     }
 }

 static class BeanNoCreator {
     public int x;
 }

 public static class NormalBean {
     public int x;
 }

 static class BeanFromFactory {
     final String str;

     private BeanFromFactory(String str) {
         this.str = str;
     }

     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     static BeanFromFactory create(String value) {
         return new BeanFromFactory(value);
     }
 }

 static class Bean1421B {
     final String value;

     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     public Bean1421B(String value) {
         this.value = value;
     }
 }

 static class Messages {
     private final List<String> values;

     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     private Messages(List<String> values) {
         this.values = values;
     }

     public List<String> getValues() {
         return values;
     }
 }

 static class BothWays {
     final List<String> values;

     public BothWays() {
         this.values = null;
     }

     @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     public BothWays(List<String> v) {
         this.values = v;
     }
 }

 // ---------- tests ----------

 @Test
 public void testArrayDelegateFromArray() throws Exception {
     BeanWithArrayDelegate bean = MAPPER.readValue("[\"a\",\"b\"]", BeanWithArrayDelegate.class);
     assertNotNull(bean);
     assertEquals(Arrays.asList("a", "b"), bean.values);
 }

 @Test
 public void testArrayDelegateEmptyArray() throws Exception {
     BeanWithArrayDelegate bean = MAPPER.readValue("[]", BeanWithArrayDelegate.class);
     assertNotNull(bean);
     assertTrue(bean.values.isEmpty());
 }

 @Test
 public void testArrayDelegateFailsOnString() throws Exception {
     try {
         MAPPER.readValue("\"test\"", BeanWithArrayDelegate.class);
         fail("Expected JsonMappingException");
     } catch (JsonMappingException e) {
         assertTrue(e.getMessage().contains("no String-argument constructor"));
     }
 }

 @Test
 public void testStringDelegateFromString() throws Exception {
     BeanWithStringDelegate bean = MAPPER.readValue("\"hello\"", BeanWithStringDelegate.class);
     assertNotNull(bean);
     assertEquals("hello", bean.value);
 }

 @Test
 public void testStringDelegateFailsOnArray() throws Exception {
     try {
         MAPPER.readValue("[\"x\"]", BeanWithStringDelegate.class);
         fail("Expected JsonMappingException");
     } catch (JsonMappingException e) {
         assertTrue(e.getMessage().contains("no suitable constructor"));
     }
 }

 @Test
 public void testChainedArrayCreators() throws Exception {
     ChainA bean = MAPPER.readValue("[\"one\",\"two\"]", ChainA.class);
     assertNotNull(bean);
     assertNotNull(bean.inner);
     assertEquals(Arrays.asList("one", "two"), bean.inner.values);
 }

 @Test
 public void testNoSuitableConstructorForJsonObject() throws Exception {
     try {
         MAPPER.readValue("{\"x\":1}", BeanNoCreator.class);
         fail("Expected JsonMappingException");
     } catch (JsonMappingException e) {
         assertTrue(e.getMessage().contains("no suitable constructor"));
     }
 }

 @Test
 public void testNormalBeanDefaultConstructorWorks() throws Exception {
     NormalBean bean = MAPPER.readValue("{\"x\":42}", NormalBean.class);
     assertEquals(42, bean.x);
 }

 @Test
 public void testFactoryMethodStringDelegate() throws Exception {
     BeanFromFactory bean = MAPPER.readValue("\"world\"", BeanFromFactory.class);
     assertEquals("world", bean.str);
 }

 @Test
 public void testBean1421BStringDelegate() throws Exception {
     Bean1421B bean = MAPPER.readValue("\"test2\"", Bean1421B.class);
     assertEquals("test2", bean.value);
 }

 @Test
 public void testMessagesWithChainedArrayCreators() throws Exception {
     Messages msg = MAPPER.readValue("[\"a\",\"b\"]", Messages.class);
     assertEquals(Arrays.asList("a", "b"), msg.getValues());
 }

 @Test
 public void testBothWaysArrayDelegatingPrioritized() throws Exception {
     BothWays bean = MAPPER.readValue("[\"x\"]", BothWays.class);
     assertNotNull(bean.values);
     assertEquals(Arrays.asList("x"), bean.values);
 }

}
