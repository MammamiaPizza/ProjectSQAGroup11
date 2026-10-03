package com.fasterxml.jackson.core.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Bug18BigDecimalPlainTest
{
    private JsonFactory plainBigDecimalFactory() {
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        return factory;
    }

    private void assertPlainBigDecimalRejected(JsonGenerator generator,
            BigDecimal value) throws IOException {
        boolean rejected = false;
        try {
            generator.writeNumber(value);
        } catch (IOException e) {
            rejected = true;
        } finally {
            generator.close();
        }
        assertTrue("Plain BigDecimal with an out-of-range scale must be rejected",
                rejected);
    }

    @Test
    public void testUtf8GeneratorRejectsTooLargePositiveExponentWhenPlainEnabled()
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = plainBigDecimalFactory()
                .createGenerator(output, JsonEncoding.UTF8);

        assertPlainBigDecimalRejected(generator, new BigDecimal("1E+10000"));
    }

    @Test
    public void testWriterGeneratorRejectsTooLargePositiveExponentWhenPlainEnabled()
            throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = plainBigDecimalFactory().createGenerator(output);

        assertPlainBigDecimalRejected(generator, new BigDecimal("1E+10000"));
    }

    @Test
    public void testUtf8GeneratorRejectsTooLargePositiveScaleWhenPlainEnabled()
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = plainBigDecimalFactory()
                .createGenerator(output, JsonEncoding.UTF8);

        assertPlainBigDecimalRejected(generator, new BigDecimal("1E-10000"));
    }

    @Test
    public void testWriterGeneratorRejectsTooLargePositiveScaleWhenPlainEnabled()
            throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = plainBigDecimalFactory().createGenerator(output);

        assertPlainBigDecimalRejected(generator, new BigDecimal("1E-10000"));
    }

    @Test
    public void testUtf8GeneratorWritesOrdinaryBigDecimalInPlainNotation()
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = plainBigDecimalFactory()
                .createGenerator(output, JsonEncoding.UTF8);

        generator.writeNumber(new BigDecimal("1E+3"));
        generator.close();

        assertEquals("1000", new String(output.toByteArray(), "UTF-8"));
    }

    @Test
    public void testWriterGeneratorWritesOrdinaryBigDecimalInPlainNotation()
            throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = plainBigDecimalFactory().createGenerator(output);

        generator.writeNumber(new BigDecimal("1.2300"));
        generator.close();

        assertEquals("1.2300", output.toString());
    }

    @Test
    public void testLargeExponentIsStillWritableWhenPlainNotationIsDisabled()
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new JsonFactory()
                .createGenerator(output, JsonEncoding.UTF8);

        generator.writeNumber(new BigDecimal("1E+10000"));
        generator.close();

        assertEquals("1E+10000", new String(output.toByteArray(), "UTF-8"));
    }
}