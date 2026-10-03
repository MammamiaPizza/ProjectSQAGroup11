package com.fasterxml.jackson.databind.node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TreeTraversingParserBinaryValueTest
{
    @Test
    public void decodesUnpaddedUrlBase64SingleByteText() throws Exception
    {
        TreeTraversingParser parser = new TreeTraversingParser(TextNode.valueOf("AA"));

        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertArrayEquals(new byte[] { 0 }, parser.getBinaryValue(Base64Variants.MODIFIED_FOR_URL));
    }

    @Test
    public void readBinaryValueUsesRequestedUrlVariantForText() throws Exception
    {
        TreeTraversingParser parser = new TreeTraversingParser(TextNode.valueOf("-w"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals(1, parser.readBinaryValue(Base64Variants.MODIFIED_FOR_URL, output));
        assertArrayEquals(new byte[] { (byte) 0xFB }, output.toByteArray());
    }

    @Test
    public void binaryNodeValueIsReturnedWithoutTextualDecoding() throws Exception
    {
        byte[] input = new byte[] { 1, 2, 3 };
        TreeTraversingParser parser = new TreeTraversingParser(BinaryNode.valueOf(input));

        assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
        assertArrayEquals(input, parser.getBinaryValue(Base64Variants.MODIFIED_FOR_URL));
    }

    @Test(expected = IOException.class)
    public void malformedUrlBase64TextReportsDecodingFailure() throws Exception
    {
        TreeTraversingParser parser = new TreeTraversingParser(TextNode.valueOf("!"));

        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        parser.getBinaryValue(Base64Variants.MODIFIED_FOR_URL);
    }
}
