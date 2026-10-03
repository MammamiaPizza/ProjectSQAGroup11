package org.apache.commons.lang3.builder;

 import static org.junit.Assert.*;

 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;

 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for ToStringStyle focusing on registry bookkeeping, cyclic-object
  * detection, array handling in appendInternal, and registry cleanup.
  *
  * The bug LANG-586 causes arrays and other objects to be improperly treated
  * as cyclic references, outputting "{}" instead of detailed content even
  * on first encounter. These tests expose the core registry and cleanup
  * problems in the buggy version.
  */
 public class ToStringStyleTest {

     private ToStringStyle style;

     @Before
     public void setUp() {
         style = ToStringStyle.DEFAULT_STYLE;
         Map<Object, Object> reg = ToStringStyle.getRegistry();
         if (reg != null) {
             reg.clear();
         }
     }

     // ---- Registry bookkeeping ----

     @Test
     public void testGetRegistryReturnsMap() {
         Object val = new Object();
         ToStringStyle.register(val);
         Map<Object, Object> reg = ToStringStyle.getRegistry();
         assertNotNull("Registry should never be null after registration", reg);
         ToStringStyle.unregister(val);
     }

     @Test
     public void testRegisterIsRegisteredUnregisterCycle() {
         Object val = new Object();
         assertFalse("not registered before register", ToStringStyle.isRegistered(val));
         ToStringStyle.register(val);
         assertTrue("registered after register", ToStringStyle.isRegistered(val));
         ToStringStyle.unregister(val);
         assertFalse("not registered after unregister", ToStringStyle.isRegistered(val));
     }

     @Test
     public void testMultipleRegistrationsIndependent() {
         Object a = new Object();
         Object b = new Object();
         ToStringStyle.register(a);
         ToStringStyle.register(b);
         assertTrue("a should be registered", ToStringStyle.isRegistered(a));
         assertTrue("b should be registered", ToStringStyle.isRegistered(b));
         ToStringStyle.unregister(a);
         assertFalse("a should be gone", ToStringStyle.isRegistered(a));
         assertTrue("b should remain registered", ToStringStyle.isRegistered(b));
         ToStringStyle.unregister(b);
         assertFalse("b should be gone", ToStringStyle.isRegistered(b));
     }

     // ---- Primitive array handling (appendInternal path via generic append) ----

     @Test
     public void testIntarrayNotCyclicOnFirstAppend() {
         int[] array = new int[] {10, 20, 30};
         StringBuffer buf = new StringBuffer();
         style.append(buf, "ints", (Object) array, Boolean.TRUE);
         String result = buf.toString();
         assertTrue("int[] must show elements, got: " + result,
                 result.contains("10"));
         assertFalse("int[] must be unregistered after append",
                 ToStringStyle.isRegistered(array));
     }

     @Test
     public void testLongArrayNotCyclicOnFirstAppend() {
         long[] array = new long[] {100L, 200L};
         StringBuffer buf = new StringBuffer();
         style.append(buf, "longs", (Object) array, Boolean.TRUE);
         String result = buf.toString();
         assertTrue("long[] must show elements", result.contains("100"));
         assertFalse(ToStringStyle.isRegistered(array));
     }

     @Test
     public void testDoubleArrayNotCyclicOnFirstAppend() {
         double[] array = new double[] {1.5, 3.5};
         StringBuffer buf = new StringBuffer();
         style.append(buf, "doubles", (Object) array, Boolean.TRUE);
         String result = buf.toString();
         assertTrue("double[] must show elements", result.contains("1.5"));
         assertFalse(ToStringStyle.isRegistered(array));
     }

     @Test
     public void testCharArrayNotCyclicOnFirstAppend() {
         char[] array = new char[] {'X', 'Y'};
         StringBuffer buf = new StringBuffer();
         style.append(buf, "chars", (Object) array, Boolean.TRUE);
         String result = buf.toString();
         assertTrue("char[] must show elements", result.contains("X"));
         assertFalse(ToStringStyle.isRegistered(array));
     }

     @Test
     public void testBooleanArrayNotCyclicOnFirstAppend() {
         boolean[] array = new boolean[] {true, false, true};
         StringBuffer buf = new StringBuffer();
         style.append(buf, "bools", (Object) array, Boolean.TRUE);
         String result = buf.toString();
         assertTrue("boolean[] must show elements", result.contains("true"));
         assertFalse(ToStringStyle.isRegistered(array));
     }

     @Test
     public void testFloatArrayNotCyclicOnFirstAppend() {
         float[] array = new float[] {1.1f, 2.2f};
         StringBuffer buf = new StringBuffer();
         style.append(buf, "floats", (Object) array, Boolean.TRUE);
         String result = buf.toString();
         assertTrue("float[] must show elements", result.contains("1.1"));
         assertFalse(ToStringStyle.isRegistered(array));
     }

     // ---- Object array and cycle detection ----

     @Test
     public void testObjectArraySelfCycleHandled() {
         Object[] selfArray = new Object[1];
         selfArray[0] = selfArray;
         StringBuffer buf = new StringBuffer();
         style.append(buf, "cycle", (Object) selfArray, Boolean.TRUE);
         String result = buf.toString();
         assertNotNull("must produce non-null output", result);
         assertTrue("must produce non-empty output", result.length() > 0);
         assertFalse("self-cycling array must be unregistered",
                 ToStringStyle.isRegistered(selfArray));
     }

     // ---- Collection / Map through appendInternal ----

     @Test
     public void testCollectionAppendInternalCleanup() {
         Collection<String> coll = Arrays.asList("x", "y");
         StringBuffer buf = new StringBuffer();
         style.append(buf, "coll", (Object) coll, Boolean.TRUE);
         assertFalse("Collection must be unregistered after append",
                 ToStringStyle.isRegistered(coll));
     }

     @Test
     public void testMapAppendInternalCleanup() {
         Map<String, Integer> map = new HashMap<String, Integer>();
         map.put("k", 1);
         StringBuffer buf = new StringBuffer();
         style.append(buf, "map", (Object) map, Boolean.TRUE);
         assertFalse("Map must be unregistered after append",
                 ToStringStyle.isRegistered(map));
     }

@Test
 public void testAppendInt() {
     StringBuffer buffer = new StringBuffer();
     ToStringStyle.DEFAULT_STYLE.append(buffer, "intField", 123);
     assertTrue("Buffer should contain field name", buffer.toString().contains("intField"));
 }

 @Test
 public void testAppendLong() {
     StringBuffer buffer = new StringBuffer();
     ToStringStyle.DEFAULT_STYLE.append(buffer, "longField", 456L);
     assertTrue("Buffer should contain field name", buffer.toString().contains("longField"));
 }

 @Test
 public void testAppendByte() {
     StringBuffer buffer = new StringBuffer();
     ToStringStyle.DEFAULT_STYLE.append(buffer, "byteField", (byte) 7);
     assertTrue("Buffer should contain field name", buffer.toString().contains("byteField"));
 }

 @Test
 public void testAppendChar() {
     StringBuffer buffer = new StringBuffer();
     ToStringStyle.DEFAULT_STYLE.append(buffer, "charField", 'x');
     assertTrue("Buffer should contain field name", buffer.toString().contains("charField"));
 }
}
