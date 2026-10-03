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