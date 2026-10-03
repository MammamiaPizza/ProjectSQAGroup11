package com.fasterxml.jackson.core.json;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import static org.junit.Assert.assertArrayEquals;

public class UTF8JsonGeneratorRawSurrogateTest
{
    @Test
    public void writeRawStringPreservesSurrogatePairSplitAtConcatBufferBoundary() throws Exception {
        StringBuilder input = new StringBuilder(4002);
        for (int i = 0; i < 3999; ++i) {
            input.append('a');
        }
        input.append('\uD83D');
        input.append('\uDE00');
        input.append('z');

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new JsonFactory().createGenerator(out, JsonEncoding.UTF8);
        generator.writeRaw(input.toString());
        generator.close();

        assertArrayEquals(input.toString().getBytes("UTF-8"), out.toByteArray());
    }

    @Test
    public void writeRawStringRangePreservesCompleteSurrogatePair() throws Exception {
        String prefix = "not selected:";
        String selected = "A\uD83D\uDE00B";
        String source = prefix + selected + ":not selected";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new JsonFactory().createGenerator(out, JsonEncoding.UTF8);
        generator.writeRaw(source, prefix.length(), selected.length());
        generator.close();

        assertArrayEquals(selected.getBytes("UTF-8"), out.toByteArray());
    }

    @Test
    public void writeRawCharArrayPreservesSurrogatePairDuringSegmentedOutput() throws Exception {
        StringBuilder input = new StringBuilder(12010);
        for (int i = 0; i < 7999; ++i) {
            input.append('x');
        }
        input.append('\uD83D');
        input.append('\uDE00');
        for (int i = 0; i < 4000; ++i) {
            input.append('y');
        }

        char[] chars = input.toString().toCharArray();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new JsonFactory().createGenerator(out, JsonEncoding.UTF8);
        generator.writeRaw(chars, 0, chars.length);
        generator.close();

        assertArrayEquals(input.toString().getBytes("UTF-8"), out.toByteArray());
    }
}