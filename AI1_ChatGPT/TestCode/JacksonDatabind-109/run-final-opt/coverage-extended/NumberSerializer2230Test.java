import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class NumberSerializer2230Test
{
    public static class StringBigDecimalBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public BigDecimal value;

        public StringBigDecimalBean(BigDecimal value) {
            this.value = value;
        }
    }

    public static class BigDecimalBean {
        public BigDecimal value;

        public BigDecimalBean(BigDecimal value) {
            this.value = value;
        }
    }

    public static class IntegralBean {
        public int integer;
        public long longer;
        public byte tiny;
        public short smaller;

        public IntegralBean(int integer, long longer, byte tiny, short smaller) {
            this.integer = integer;
            this.longer = longer;
            this.tiny = tiny;
            this.smaller = smaller;
        }
    }

    @Test
    public void stringFormattedBigDecimalUsesPlainNotationWhenConfigured() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);

        String json = mapper.writeValueAsString(
                new StringBigDecimalBean(new BigDecimal("5E-10")));

        assertEquals("{\"value\":\"0.0000000005\"}", json);
    }

    @Test
    public void unformattedBigDecimalUsesPlainNumericNotationWhenConfigured() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);

        String json = mapper.writeValueAsString(
                new BigDecimalBean(new BigDecimal("5E-10")));

        assertEquals("{\"value\":0.0000000005}", json);
    }

    @Test
    public void integralWrapperAndPrimitiveSerializersProduceNumericValues() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(
                new IntegralBean(13, 9000000000L, (byte) -2, (short) 7));

        assertEquals("{\"integer\":13,\"longer\":9000000000,\"tiny\":-2,\"smaller\":7}", json);
    }

@Test
public void numberSerializerHandlesGeneralNumberSubtypes() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.ObjectWriter writer = mapper.writerFor(Number.class);

    assertEquals("9000000000", writer.writeValueAsString(Long.valueOf(9000000000L)));
    assertEquals("1.25", writer.writeValueAsString(Double.valueOf(1.25)));
    assertEquals("2.5", writer.writeValueAsString(Float.valueOf(2.5f)));
    assertEquals("-3", writer.writeValueAsString(Byte.valueOf((byte) -3)));
    assertEquals("11", writer.writeValueAsString(new java.util.concurrent.atomic.AtomicInteger(11)));
}

@Test
public void numberSerializerReportsAppropriateVisitorFormats() throws Exception {
    final java.util.List<com.fasterxml.jackson.core.JsonParser.NumberType> integerTypes =
            new java.util.ArrayList<com.fasterxml.jackson.core.JsonParser.NumberType>();
    final java.util.List<com.fasterxml.jackson.core.JsonParser.NumberType> numberTypes =
            new java.util.ArrayList<com.fasterxml.jackson.core.JsonParser.NumberType>();
    final int[] numberRequests = new int[1];

    com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper visitor =
            new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper.Base() {
                @Override
                public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor expectIntegerFormat(
                        com.fasterxml.jackson.databind.JavaType type) {
                    return new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor.Base() {
                        @Override
                        public void numberType(com.fasterxml.jackson.core.JsonParser.NumberType type) {
                            integerTypes.add(type);
                        }
                    };
                }

                @Override
                public com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor expectNumberFormat(
                        com.fasterxml.jackson.databind.JavaType type) {
                    ++numberRequests[0];
                    return new com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor.Base() {
                        @Override
                        public void numberType(com.fasterxml.jackson.core.JsonParser.NumberType type) {
                            numberTypes.add(type);
                        }
                    };
                }
            };

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    new com.fasterxml.jackson.databind.ser.std.NumberSerializer(java.math.BigInteger.class)
            .acceptJsonFormatVisitor(visitor, mapper.constructType(java.math.BigInteger.class));
    new com.fasterxml.jackson.databind.ser.std.NumberSerializer(java.math.BigDecimal.class)
            .acceptJsonFormatVisitor(visitor, mapper.constructType(java.math.BigDecimal.class));
    new com.fasterxml.jackson.databind.ser.std.NumberSerializer(Number.class)
            .acceptJsonFormatVisitor(visitor, mapper.constructType(Number.class));

    assertEquals(java.util.Collections.singletonList(com.fasterxml.jackson.core.JsonParser.NumberType.BIG_INTEGER),
            integerTypes);
    assertEquals(java.util.Collections.singletonList(com.fasterxml.jackson.core.JsonParser.NumberType.BIG_DECIMAL),
            numberTypes);
    assertEquals(2, numberRequests[0]);
}

@Test
public void numberSerializersRegistersWrapperSerializers() {
    java.util.Map<String, com.fasterxml.jackson.databind.JsonSerializer<?>> serializers =
            new java.util.HashMap<String, com.fasterxml.jackson.databind.JsonSerializer<?>>();

    com.fasterxml.jackson.databind.ser.std.NumberSerializers.addAll(serializers);

    assertEquals(com.fasterxml.jackson.databind.ser.std.NumberSerializers.IntegerSerializer.class,
            serializers.get(Integer.class.getName()).getClass());
    assertEquals(com.fasterxml.jackson.databind.ser.std.NumberSerializers.LongSerializer.class,
            serializers.get(Long.class.getName()).getClass());
    assertEquals(com.fasterxml.jackson.databind.ser.std.NumberSerializers.DoubleSerializer.class,
            serializers.get(Double.class.getName()).getClass());
}
}
