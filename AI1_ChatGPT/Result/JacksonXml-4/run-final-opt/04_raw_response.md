@org.junit.Test
public void dynamicRootNameIsUsedWhenSerializingUntypedScalarValue() throws Exception {
    com.fasterxml.jackson.dataformat.xml.XmlMapper mapper =
            new com.fasterxml.jackson.dataformat.xml.XmlMapper();

    String xml = mapper.writer().withRootName("rudy").writeValueAsString("value");

    org.junit.Assert.assertEquals("<rudy>value</rudy>", xml);
}

@org.junit.Test
public void dynamicRootNameIsUsedWhenSerializingNullValue() throws Exception {
    com.fasterxml.jackson.dataformat.xml.XmlMapper mapper =
            new com.fasterxml.jackson.dataformat.xml.XmlMapper();

    String xml = mapper.writer().withRootName("rudy").writeValueAsString(null);

    org.junit.Assert.assertEquals("<rudy/>", xml);
}

@org.junit.Test
public void dynamicRootNameIsUsedWhenSerializingRootArray() throws Exception {
    com.fasterxml.jackson.dataformat.xml.XmlMapper mapper =
            new com.fasterxml.jackson.dataformat.xml.XmlMapper();

    String xml = mapper.writer().withRootName("rudy")
            .writeValueAsString(new String[] { "first", "second" });

    org.junit.Assert.assertEquals("<rudy><item>first</item><item>second</item></rudy>", xml);
}