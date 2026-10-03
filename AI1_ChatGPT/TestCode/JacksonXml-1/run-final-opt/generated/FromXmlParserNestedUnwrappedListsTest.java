package com.fasterxml.jackson.dataformat.xml.deser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class FromXmlParserNestedUnwrappedListsTest
{
    @Test
    public void emptyNestedUnwrappedObjectEntryIsNotDropped() throws Exception
    {
        XmlMapper mapper = new XmlMapper();
        GroupsDocument result = mapper.readValue(
                "<groupsDocument>"
                + "<group><entry/></group>"
                + "<group><entry><name>kept</name></entry></group>"
                + "</groupsDocument>",
                GroupsDocument.class);

        assertNotNull(result.groups);
        assertEquals(2, result.groups.size());

        assertNotNull(result.groups.get(0).entries);
        assertEquals(1, result.groups.get(0).entries.size());

        assertNotNull(result.groups.get(1).entries);
        assertEquals(1, result.groups.get(1).entries.size());
        assertEquals("kept", result.groups.get(1).entries.get(0).name);
    }

    @Test
    public void emptyUnwrappedScalarElementRemainsAnArrayEntryInsideUnwrappedParent()
            throws Exception
    {
        XmlMapper mapper = new XmlMapper();
        TaggedDocument result = mapper.readValue(
                "<taggedDocument>"
                + "<group><tag/></group>"
                + "<group><tag>present</tag></group>"
                + "</taggedDocument>",
                TaggedDocument.class);

        assertNotNull(result.groups);
        assertEquals(2, result.groups.size());

        assertNotNull(result.groups.get(0).tags);
        assertEquals(1, result.groups.get(0).tags.size());

        assertNotNull(result.groups.get(1).tags);
        assertEquals(1, result.groups.get(1).tags.size());
        assertEquals("present", result.groups.get(1).tags.get(0));
    }

    @Test
    public void multipleNestedUnwrappedEntriesAfterAnEmptyEntryAreAllRetained()
            throws Exception
    {
        XmlMapper mapper = new XmlMapper();
        GroupsDocument result = mapper.readValue(
                "<groupsDocument>"
                + "<group>"
                + "<entry/>"
                + "<entry><name>first</name></entry>"
                + "<entry><name>second</name></entry>"
                + "</group>"
                + "</groupsDocument>",
                GroupsDocument.class);

        assertNotNull(result.groups);
        assertEquals(1, result.groups.size());
        assertNotNull(result.groups.get(0).entries);
        assertEquals(3, result.groups.get(0).entries.size());
        assertEquals("first", result.groups.get(0).entries.get(1).name);
        assertEquals("second", result.groups.get(0).entries.get(2).name);
    }

    public static class GroupsDocument
    {
        @JacksonXmlProperty(localName = "group")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Group> groups;
    }

    public static class Group
    {
        @JacksonXmlProperty(localName = "entry")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Entry> entries;

        @JacksonXmlProperty(localName = "tag")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<String> tags;
    }

    public static class Entry
    {
        public String name;
    }

    public static class TaggedDocument
    {
        @JacksonXmlProperty(localName = "group")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Group> groups;
    }
}
