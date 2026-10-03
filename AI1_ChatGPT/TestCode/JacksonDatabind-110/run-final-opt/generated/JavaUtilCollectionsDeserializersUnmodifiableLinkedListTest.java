package com.fasterxml.jackson.databind.deser.jdk;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JavaUtilCollectionsDeserializersUnmodifiableLinkedListTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    private List<?> readAsUnmodifiableLinkedList(String json) throws Exception {
        List<String> source = new LinkedList<String>();
        List<String> targetType = Collections.unmodifiableList(source);
        JavaType type = mapper.getTypeFactory().constructType(targetType.getClass());
        return mapper.readValue(json, type);
    }

    @Test
    public void deserializesUnmodifiableListCreatedFromLinkedList() throws Exception {
        List<?> result = readAsUnmodifiableLinkedList("[\"first\",\"second\",\"third\"]");

        Assert.assertEquals(Arrays.asList("first", "second", "third"), result);
    }

    @Test
    public void preservesElementOrderForUnmodifiableLinkedList() throws Exception {
        List<?> result = readAsUnmodifiableLinkedList("[3,1,4,1,5]");

        Assert.assertEquals(Arrays.asList(3, 1, 4, 1, 5), result);
    }

    @Test
    public void deserializesEmptyUnmodifiableLinkedList() throws Exception {
        List<?> result = readAsUnmodifiableLinkedList("[]");

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void deserializesSingleElementUnmodifiableLinkedList() throws Exception {
        List<?> result = readAsUnmodifiableLinkedList("[\"only\"]");

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("only", result.get(0));
    }

    @Test
    public void resultingListIsUnmodifiable() throws Exception {
        List<?> result = readAsUnmodifiableLinkedList("[\"value\"]");

        Assert.assertEquals(Collections.singletonList("value"), result);
        try {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            List mutableView = result;
            mutableView.add("another");
            Assert.fail("Deserialized unmodifiable list should reject additions");
        } catch (UnsupportedOperationException expected) {
            Assert.assertEquals(Collections.singletonList("value"), result);
        }
    }

    @Test
    public void deserializesNullAsNullForUnmodifiableLinkedList() throws Exception {
        List<?> result = readAsUnmodifiableLinkedList("null");

        Assert.assertNull(result);
    }
}
