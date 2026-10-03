import java.math.BigInteger;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ParserNumericOverflowRegressionTest
{
    private interface ThrowingCall {
        void call() throws Exception;
    }

    private void expectRangeFailure(ThrowingCall call, String numericType) throws Exception {
        try {
            call.call();
            fail("Expected an out-of-range " + numericType + " failure");
        } catch (JsonParseException e) {
            assertTrue("Exception should identify the requested numeric type: " + e.getMessage(),
                    e.getMessage().contains("out of range of " + numericType));
        }
    }

    @Test
    public void parsesIntBoundaryValuesAsInts() throws Exception {
        JsonFactory factory = new JsonFactory();

        JsonParser parser = factory.createParser("2147483647");
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(2147483647, parser.getIntValue());
        assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
        parser.close();

        parser = factory.createParser("-2147483648");
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(-2147483648, parser.getIntValue());
        assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
        parser.close();
    }

    @Test
    public void parsesLongBoundaryValuesAsLongs() throws Exception {
        JsonFactory factory = new JsonFactory();

        JsonParser parser = factory.createParser("9223372036854775807");
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(Long.MAX_VALUE, parser.getLongValue());
        assertEquals(JsonParser.NumberType.LONG, parser.getNumberType());
        parser.close();

        parser = factory.createParser("-9223372036854775808");
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(Long.MIN_VALUE, parser.getLongValue());
        assertEquals(JsonParser.NumberType.LONG, parser.getNumberType());
        parser.close();
    }

    @Test
    public void parsesElevenDigitIntegralValueAsLong() throws Exception {
        JsonParser parser = new JsonFactory().createParser("12345678907");
        try {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(12345678907L, parser.getLongValue());
            assertEquals(JsonParser.NumberType.LONG, parser.getNumberType());
        } finally {
            parser.close();
        }
    }

    @Test
    public void rejectsLongValueOutsideIntRangeWhenIntRequested() throws Exception {
        expectRangeFailure(new ThrowingCall() {
            @Override
            public void call() throws Exception {
                JsonParser parser = new JsonFactory().createParser("2147483648");
                try {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(2147483648L, parser.getLongValue());
                    parser.getIntValue();
                } finally {
                    parser.close();
                }
            }
        }, "int");
    }

    @Test
    public void rejectsPositiveLongOverflow() throws Exception {
        expectRangeFailure(new ThrowingCall() {
            @Override
            public void call() throws Exception {
                JsonParser parser = new JsonFactory().createParser("9223372036854775817");
                try {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    parser.getLongValue();
                } finally {
                    parser.close();
                }
            }
        }, "long");
    }

    @Test
    public void rejectsNegativeLongOverflow() throws Exception {
        expectRangeFailure(new ThrowingCall() {
            @Override
            public void call() throws Exception {
                JsonParser parser = new JsonFactory().createParser("-9223372036854775809");
                try {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    parser.getLongValue();
                } finally {
                    parser.close();
                }
            }
        }, "long");
    }

    @Test
    public void rejectsVeryLongIntegerWhenIntRequested() throws Exception {
        StringBuilder number = new StringBuilder(199999);
        for (int i = 0; i < 199999; ++i) {
            number.append('9');
        }
        final String input = number.toString();

        expectRangeFailure(new ThrowingCall() {
            @Override
            public void call() throws Exception {
                JsonParser parser = new JsonFactory().createParser(input);
                try {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(new BigInteger(input), parser.getBigIntegerValue());
                    parser.getIntValue();
                } finally {
                    parser.close();
                }
            }
        }, "int");
    }

    @Test
    public void asyncParserRejectsIntOverflow() throws Exception {
        final JsonParser parser = new JsonFactory().createNonBlockingByteArrayParser();
        ByteArrayFeeder feeder = (ByteArrayFeeder) parser;
        byte[] input = "2147483648".getBytes("UTF-8");
        feeder.feedInput(input, 0, input.length);
        feeder.endOfInput();

        expectRangeFailure(new ThrowingCall() {
            @Override
            public void call() throws Exception {
                try {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    parser.getIntValue();
                } finally {
                    parser.close();
                }
            }
        }, "int");
    }

    @Test
    public void asyncParserRejectsLongOverflow() throws Exception {
        final JsonParser parser = new JsonFactory().createNonBlockingByteArrayParser();
        ByteArrayFeeder feeder = (ByteArrayFeeder) parser;
        byte[] input = "9223372036854775817".getBytes("UTF-8");
        feeder.feedInput(input, 0, input.length);
        feeder.endOfInput();

        expectRangeFailure(new ThrowingCall() {
            @Override
            public void call() throws Exception {
                try {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    parser.getLongValue();
                } finally {
                    parser.close();
                }
            }
        }, "long");
    }
}