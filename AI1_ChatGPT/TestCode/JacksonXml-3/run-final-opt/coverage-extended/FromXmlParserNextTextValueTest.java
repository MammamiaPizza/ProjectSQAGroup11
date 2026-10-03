package com.fasterxml.jackson.dataformat.xml.deser;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FromXmlParserNextTextValueTest
{
    private FromXmlParser parserFor(String xml) throws Exception {
        return (FromXmlParser) new XmlMapper().getFactory().createParser(xml);
    }

    @Test
    public void nextTextValueReturnsAttributeValue() throws Exception {
        FromXmlParser parser = parserFor("<root value='7'/>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("value", parser.getCurrentName());

            assertEquals("7", parser.nextTextValue());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void nextTextValueReturnsEachOfMultipleAttributeValues() throws Exception {
        FromXmlParser parser = parserFor("<root first='7' second='8'/>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("first", parser.getCurrentName());
            assertEquals("7", parser.nextTextValue());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("second", parser.getCurrentName());
            assertEquals("8", parser.nextTextValue());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void normalTokenIterationExposesAttributeValue() throws Exception {
        FromXmlParser parser = parserFor("<root value='7'/>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("value", parser.getCurrentName());

            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("7", parser.getText());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void nextTextValueKeepsParserUsableForElementTextAfterAttribute() throws Exception {
        FromXmlParser parser = parserFor("<root attribute='7'>text</root>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("attribute", parser.getCurrentName());
            assertEquals("7", parser.nextTextValue());

            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals(FromXmlParser.DEFAULT_UNNAMED_TEXT_PROPERTY, parser.getCurrentName());
            assertEquals("text", parser.nextTextValue());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        } finally {
            parser.close();
        }
    }

    @Test
    public void nextTextValueReturnsEmptyAttributeValue() throws Exception {
        FromXmlParser parser = parserFor("<root value=''/>");
        try {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.FIELD_NAME, parser.nextToken());
            assertEquals("value", parser.getCurrentName());

            assertEquals("", parser.nextTextValue());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        } finally {
            parser.close();
        }
    }

@org.junit.Test
public void nextTextValueReturnsAttributeValueBeforeNestedElementText() throws Exception {
    com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser parser =
            (com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser) new com.fasterxml.jackson.dataformat.xml.XmlFactory()
                    .createParser("<root code='7'><child>8</child></root>");
    try {
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals("code", parser.getCurrentName());
        org.junit.Assert.assertEquals("7", parser.nextTextValue());

        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals("child", parser.getCurrentName());
        org.junit.Assert.assertEquals("8", parser.nextTextValue());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.END_OBJECT, parser.nextToken());
        org.junit.Assert.assertNull(parser.nextToken());
    } finally {
        parser.close();
    }
}

@org.junit.Test
public void getBinaryValueDecodesSuccessiveXmlTextValues() throws Exception {
    com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser parser =
            (com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser) new com.fasterxml.jackson.dataformat.xml.XmlFactory()
                    .createParser("<root><first>QQ==</first><second>Qg==</second></root>");
    try {
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals("first", parser.getCurrentName());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        org.junit.Assert.assertArrayEquals(new byte[] { 65 },
                parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.getDefaultVariant()));

        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals("second", parser.getCurrentName());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        org.junit.Assert.assertArrayEquals(new byte[] { 66 },
                parser.getBinaryValue(com.fasterxml.jackson.core.Base64Variants.getDefaultVariant()));
    } finally {
        parser.close();
    }
}

@org.junit.Test
public void getIntValueCoercesXmlTextValue() throws Exception {
    com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser parser =
            (com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser) new com.fasterxml.jackson.dataformat.xml.XmlFactory()
                    .createParser("<root>-37</root>");
    try {
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.START_OBJECT, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.FIELD_NAME, parser.nextToken());
        org.junit.Assert.assertEquals(com.fasterxml.jackson.core.JsonToken.VALUE_STRING, parser.nextToken());
        org.junit.Assert.assertEquals(-37, parser.getIntValue());
    } finally {
        parser.close();
    }
}
}
