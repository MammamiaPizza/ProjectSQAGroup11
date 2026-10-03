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
}