import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NumberSerializersTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testEmptyInclusionOmitsZeroPrimitiveInteger() throws Exception {
        assertEquals("{}", mapper.writeValueAsString(new ZeroPrimitiveInteger()));
    }

    @Test
    public void testEmptyInclusionRetainsNonZeroPrimitiveInteger() throws Exception {
        assertEquals("{\"value\":3}", mapper.writeValueAsString(new NonZeroPrimitiveInteger()));
    }

    @Test
    public void testEmptyInclusionOmitsAllZeroPrimitiveNumbers() throws Exception {
        assertEquals("{}", mapper.writeValueAsString(new ZeroPrimitiveNumbers()));
    }

    @Test
    public void testEmptyInclusionRetainsAllNonZeroPrimitiveNumbers() throws Exception {
        JsonNode result = mapper.readTree(mapper.writeValueAsString(new NonZeroPrimitiveNumbers()));

        assertEquals(6, result.size());
        assertEquals(12, result.get("integer").intValue());
        assertEquals(123456789L, result.get("longValue").longValue());
        assertEquals(7, result.get("byteValue").intValue());
        assertEquals(9, result.get("shortValue").intValue());
        assertEquals(1.25D, result.get("floatValue").doubleValue(), 0.0D);
        assertEquals(2.5D, result.get("doubleValue").doubleValue(), 0.0D);
    }

    @Test
    public void testEmptyInclusionOmitsAllZeroBoxedNumbers() throws Exception {
        assertEquals("{}", mapper.writeValueAsString(new ZeroBoxedNumbers()));
    }

    @Test
    public void testEmptyInclusionRetainsAllNonZeroBoxedNumbers() throws Exception {
        JsonNode result = mapper.readTree(mapper.writeValueAsString(new NonZeroBoxedNumbers()));

        assertEquals(6, result.size());
        assertEquals(15, result.get("integer").intValue());
        assertEquals(987654321L, result.get("longValue").longValue());
        assertEquals(4, result.get("byteValue").intValue());
        assertEquals(6, result.get("shortValue").intValue());
        assertEquals(3.25D, result.get("floatValue").doubleValue(), 0.0D);
        assertEquals(4.5D, result.get("doubleValue").doubleValue(), 0.0D);
    }

    @Test
    public void testEmptyInclusionDoesNotSuppressNegativeNumbers() throws Exception {
        JsonNode result = mapper.readTree(mapper.writeValueAsString(new NegativeBoxedNumbers()));

        assertEquals(6, result.size());
        assertFalse(result.get("integer").isMissingNode());
        assertEquals(-1, result.get("integer").intValue());
        assertEquals(-2L, result.get("longValue").longValue());
        assertEquals(-3, result.get("byteValue").intValue());
        assertEquals(-4, result.get("shortValue").intValue());
        assertEquals(-1.5D, result.get("floatValue").doubleValue(), 0.0D);
        assertEquals(-2.5D, result.get("doubleValue").doubleValue(), 0.0D);
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ZeroPrimitiveInteger {
        public int value = 0;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NonZeroPrimitiveInteger {
        public int value = 3;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ZeroPrimitiveNumbers {
        public int integer = 0;
        public long longValue = 0L;
        public byte byteValue = 0;
        public short shortValue = 0;
        public float floatValue = 0.0F;
        public double doubleValue = 0.0D;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NonZeroPrimitiveNumbers {
        public int integer = 12;
        public long longValue = 123456789L;
        public byte byteValue = 7;
        public short shortValue = 9;
        public float floatValue = 1.25F;
        public double doubleValue = 2.5D;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ZeroBoxedNumbers {
        public Integer integer = Integer.valueOf(0);
        public Long longValue = Long.valueOf(0L);
        public Byte byteValue = Byte.valueOf((byte) 0);
        public Short shortValue = Short.valueOf((short) 0);
        public Float floatValue = Float.valueOf(0.0F);
        public Double doubleValue = Double.valueOf(0.0D);
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NonZeroBoxedNumbers {
        public Integer integer = Integer.valueOf(15);
        public Long longValue = Long.valueOf(987654321L);
        public Byte byteValue = Byte.valueOf((byte) 4);
        public Short shortValue = Short.valueOf((short) 6);
        public Float floatValue = Float.valueOf(3.25F);
        public Double doubleValue = Double.valueOf(4.5D);
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class NegativeBoxedNumbers {
        public Integer integer = Integer.valueOf(-1);
        public Long longValue = Long.valueOf(-2L);
        public Byte byteValue = Byte.valueOf((byte) -3);
        public Short shortValue = Short.valueOf((short) -4);
        public Float floatValue = Float.valueOf(-1.5F);
        public Double doubleValue = Double.valueOf(-2.5D);
    }
}