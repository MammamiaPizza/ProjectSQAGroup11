@org.junit.Test
public void emptyNestedObjectBetweenValuesRemainsInTheSameUnwrappedList() throws Exception {
    String xml = "<document><groups><entries><name>first</name></entries>"
            + "<entries/>"
            + "<entries><name>last</name></entries></groups></document>";

    Xml180Document result = new com.fasterxml.jackson.dataformat.xml.XmlMapper()
            .readValue(xml, Xml180Document.class);

    org.junit.Assert.assertNotNull(result.groups);
    org.junit.Assert.assertEquals(1, result.groups.size());
    org.junit.Assert.assertNotNull(result.groups.get(0).entries);
    org.junit.Assert.assertEquals(3, result.groups.get(0).entries.size());
    org.junit.Assert.assertEquals("first", result.groups.get(0).entries.get(0).name);
    org.junit.Assert.assertEquals("last", result.groups.get(0).entries.get(2).name);
}

@org.junit.Test
public void emptyUnwrappedScalarBetweenValuesRemainsInTheSameNestedList() throws Exception {
    String xml = "<document><groups><tags>first</tags><tags/><tags>last</tags>"
            + "</groups></document>";

    Xml180Document result = new com.fasterxml.jackson.dataformat.xml.XmlMapper()
            .readValue(xml, Xml180Document.class);

    org.junit.Assert.assertNotNull(result.groups);
    org.junit.Assert.assertEquals(1, result.groups.size());
    org.junit.Assert.assertNotNull(result.groups.get(0).tags);
    org.junit.Assert.assertEquals(3, result.groups.get(0).tags.size());
    org.junit.Assert.assertEquals("first", result.groups.get(0).tags.get(0));
    org.junit.Assert.assertEquals("last", result.groups.get(0).tags.get(2));
}

@org.junit.Test
public void trailingEmptyNestedUnwrappedObjectEntryIsNotDropped() throws Exception {
    String xml = "<document><groups><entries><name>kept</name></entries></groups>"
            + "<groups><entries/></groups></document>";

    Xml180Document result = new com.fasterxml.jackson.dataformat.xml.XmlMapper()
            .readValue(xml, Xml180Document.class);

    org.junit.Assert.assertNotNull(result.groups);
    org.junit.Assert.assertEquals(2, result.groups.size());
    org.junit.Assert.assertNotNull(result.groups.get(0).entries);
    org.junit.Assert.assertEquals(1, result.groups.get(0).entries.size());
    org.junit.Assert.assertEquals("kept", result.groups.get(0).entries.get(0).name);
    org.junit.Assert.assertNotNull(result.groups.get(1).entries);
    org.junit.Assert.assertEquals(1, result.groups.get(1).entries.size());
}

public static class Xml180Document {
    @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper(useWrapping = false)
    public java.util.List<Xml180Group> groups;
}

public static class Xml180Group {
    @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper(useWrapping = false)
    public java.util.List<Xml180Entry> entries;

    @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper(useWrapping = false)
    public java.util.List<String> tags;
}

public static class Xml180Entry {
    public String name;
}