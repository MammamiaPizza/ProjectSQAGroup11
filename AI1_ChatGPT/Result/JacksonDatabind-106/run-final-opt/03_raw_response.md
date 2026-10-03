package com.fasterxml.jackson.databind.node;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TreeTraversingParserNumericOverflowTest
{
    private TreeTraversingParser parserFor(JsonNode node) throws IOException {
        TreeTraversingParser parser = new TreeTraversingParser(node);
        assertEquals(node.asToken(), parser.nextToken());
        return parser;
    }

    private void assertIntAccessFails(JsonNode node) throws Exception {
        TreeTraversingParser parser = parserFor(node);
        boolean failed = false;
        try {
            parser.getIntValue();
        } catch (IOException e) {
            failed = true;
        }
        assertTrue("Expected getIntValue() to reject an out-of-range value", failed);
    }

    private void assertLongAccessFails(JsonNode node) throws Exception {
        TreeTraversingParser parser = parserFor(node);
        boolean failed = false;
        try {
            parser.getLongValue();
        } catch (IOException e) {
            failed = true;
        }
        assertTrue("Expected getLongValue() to reject an out-of-range value", failed);
    }

    @Test
    public void testIntValueAcceptsBothIntBoundaries() throws Exception {
        TreeTraversingParser minimum = parserFor(JsonNodeFactory.instance.numberNode(Integer.MIN_VALUE));
        assertEquals(Integer.MIN_VALUE, minimum.getIntValue());

        TreeTraversingParser maximum = parserFor(JsonNodeFactory.instance.numberNode(Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, maximum.getIntValue());
    }

    @Test
    public void testLongValueAcceptsBothLongBoundaries() throws Exception {
        TreeTraversingParser minimum = parserFor(JsonNodeFactory.instance.numberNode(Long.MIN_VALUE));
        assertEquals(Long.MIN_VALUE, minimum.getLongValue());

        TreeTraversingParser maximum = parserFor(JsonNodeFactory.instance.numberNode(Long.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, maximum.getLongValue());
    }

    @Test
    public void testIntValueRejectsValuesAboveAndBelowIntRange() throws Exception {
        assertIntAccessFails(JsonNodeFactory.instance.numberNode(((long) Integer.MAX_VALUE) + 1L));
        assertIntAccessFails(JsonNodeFactory.instance.numberNode(((long) Integer.MIN_VALUE) - 1L));
    }

    @Test
    public void testLongValueRejectsValuesAboveAndBelowLongRange() throws Exception {
        assertLongAccessFails(JsonNodeFactory.instance.numberNode(
                new BigInteger("9223372036854775808")));
        assertLongAccessFails(JsonNodeFactory.instance.numberNode(
                new BigInteger("-9223372036854775809")));
    }

    @Test
    public void testNumericAccessRejectsNonNumericToken() throws Exception {
        TreeTraversingParser parser = parserFor(TextNode.valueOf("123"));
        assertEquals(JsonToken.VALUE_STRING, parser.getCurrentToken());

        boolean failed = false;
        try {
            parser.getIntValue();
        } catch (IOException e) {
            failed = true;
        }
        assertTrue("Expected numeric accessor to reject a string token", failed);
    }
}