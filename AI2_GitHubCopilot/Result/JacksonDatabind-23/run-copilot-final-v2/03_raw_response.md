package com.fasterxml.jackson.databind.ser.std;

 import static org.junit.Assert.assertEquals;

 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.powermock.modules.junit4.PowerMockRunner;

 import com.fasterxml.jackson.annotation.JsonInclude;
 import com.fasterxml.jackson.databind.ObjectMapper;

 /**
  * Tests for number serializer inclusion behavior (bug #849).
  * Zero values should be omitted when Include.NON_EMPTY is used.
  */
 @RunWith(PowerMockRunner.class)
 public class NumberSerializersEmptyInclusionTest {

     private final ObjectMapper mapper = new ObjectMapper();

     // --------------- bean classes ---------------

     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     public static class IntBean {
         public int intVal;
         public Integer integerVal;
     }

     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     public static class LongBean {
         public long longVal;
         public Long longObjVal;
     }

     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     public static class ShortBean {
         public short shortVal;
         public Short shortObjVal;
     }

     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     public static class ByteBean {
         public byte byteVal;
         public Byte byteObjVal;
     }

     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     public static class FloatBean {
         public float floatVal;
         public Float floatObjVal;
     }

     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     public static class DoubleBean {
         public double doubleVal;
         public Double doubleObjVal;
     }

     @JsonInclude(JsonInclude.Include.NON_DEFAULT)
     public static class NonDefaultIntBean {
         public int intVal;
         public Integer integerVal;
     }

     @JsonInclude(JsonInclude.Include.NON_DEFAULT)
     public static class NonDefaultLongBean {
         public long longVal;
     }

     @JsonInclude(JsonInclude.Include.NON_EMPTY)
     public static class AllNumbersBean {
         public int intVal;
         public Integer integerVal;
         public long longVal;
         public Long longObjVal;
         public short shortVal;
         public Short shortObjVal;
         public byte byteVal;
         public Byte byteObjVal;
         public float floatVal;
         public Float floatObjVal;
         public double doubleVal;
         public Double doubleObjVal;
     }

     // --------------- tests ---------------

     @Test
     public void testZeroIntegersOmittedWithNonEmpty() throws Exception {
         IntBean b = new IntBean();
         assertEquals("{}", mapper.writeValueAsString(b));
     }

     @Test
     public void testZeroLongsOmittedWithNonEmpty() throws Exception {
         LongBean b = new LongBean();
         assertEquals("{}", mapper.writeValueAsString(b));
     }

     @Test
     public void testZeroShortsOmittedWithNonEmpty() throws Exception {
         ShortBean b = new ShortBean();
         assertEquals("{}", mapper.writeValueAsString(b));
     }

     @Test
     public void testZeroBytesOmittedWithNonEmpty() throws Exception {
         ByteBean b = new ByteBean();
         assertEquals("{}", mapper.writeValueAsString(b));
     }

     @Test
     public void testZeroFloatsOmittedWithNonEmpty() throws Exception {
         FloatBean b = new FloatBean();
         assertEquals("{}", mapper.writeValueAsString(b));
     }

     @Test
     public void testZeroDoubleOmittedWithNonEmpty() throws Exception {
         DoubleBean b = new DoubleBean();
         assertEquals("{}", mapper.writeValueAsString(b));
     }

     @Test
     public void testNegativeZeroFloatOmitted() throws Exception {
         FloatBean b = new FloatBean();
         b.floatVal = -0.0f;
         b.floatObjVal = -0.0f;
         assertEquals("{}", mapper.writeValueAsString(b));
     }

     @Test
     public void testNonZeroValuesStillIncluded() throws Exception {
         AllNumbersBean b = new AllNumbersBean();
         b.intVal = 1;
         b.integerVal = 2;
         b.longVal = 3L;
         b.longObjVal = 4L;
         b.shortVal = 5;
         b.shortObjVal = 6;
         b.byteVal = 7;
         b.byteObjVal = 8;
         b.floatVal = 1.1f;
         b.floatObjVal = 2.2f;
         b.doubleVal = 3.3d;
         b.doubleObjVal = 4.4d;
         String json = mapper.writeValueAsString(b);
         assertEquals(
             "{\"intVal\":1,\"integerVal\":2,\"longVal\":3,\"longObjVal\":4," +
             "\"shortVal\":5,\"shortObjVal\":6,\"byteVal\":7,\"byteObjVal\":8," +
             "\"floatVal\":1.1,\"floatObjVal\":2.2,\"doubleVal\":3.3,\"doubleObjVal\":4.4}",
             json);
     }

     @Test
     public void testPrimitiveZeroNonDefaultOmitted() throws Exception {
         NonDefaultIntBean b = new NonDefaultIntBean();
         b.intVal = 0;
         b.integerVal = 0;
         // primitive default is 0 -> omitted; wrapper default is null -> 0 is not default, must be
included
         assertEquals("{\"integerVal\":0}", mapper.writeValueAsString(b));
     }

     @Test
     public void testWrapperZeroNonDefaultIncluded() throws Exception {
         NonDefaultIntBean b = new NonDefaultIntBean();
         b.integerVal = 0;
         assertEquals("{\"integerVal\":0}", mapper.writeValueAsString(b));
     }

     @Test
     public void testNullWrappersOmitted() throws Exception {
         AllNumbersBean b = new AllNumbersBean();
         b.integerVal = null;
         b.longObjVal = null;
         b.shortObjVal = null;
         b.byteObjVal = null;
         b.floatObjVal = null;
         b.doubleObjVal = null;
         assertEquals("{}", mapper.writeValueAsString(b));
     }

     @Test
     public void testMinMaxValuesIncluded() throws Exception {
         AllNumbersBean b = new AllNumbersBean();
         b.intVal = Integer.MAX_VALUE;
         b.integerVal = Integer.MIN_VALUE;
         b.longVal = Long.MAX_VALUE;
         b.longObjVal = Long.MIN_VALUE;
         b.shortVal = Short.MAX_VALUE;
         b.shortObjVal = Short.MIN_VALUE;
         b.byteVal = Byte.MAX_VALUE;
         b.byteObjVal = Byte.MIN_VALUE;
         b.floatVal = Float.MAX_VALUE;
         b.floatObjVal = Float.MIN_VALUE;
         b.doubleVal = Double.MAX_VALUE;
         b.doubleObjVal = Double.MIN_VALUE;
         String json = mapper.writeValueAsString(b);
         // all should be present
         assertEquals(true, json.contains("intVal"));
         assertEquals(true, json.contains("integerVal"));
         assertEquals(true, json.contains("longVal"));
         assertEquals(true, json.contains("longObjVal"));
         assertEquals(true, json.contains("shortVal"));
         assertEquals(true, json.contains("shortObjVal"));
         assertEquals(true, json.contains("byteVal"));
         assertEquals(true, json.contains("byteObjVal"));
         assertEquals(true, json.contains("floatVal"));
         assertEquals(true, json.contains("floatObjVal"));
         assertEquals(true, json.contains("doubleVal"));
         assertEquals(true, json.contains("doubleObjVal"));
     }
 }