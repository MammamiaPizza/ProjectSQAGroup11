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

@org.junit.Test
public void deserializesUnmodifiableListCreatedFromArrayList() throws Exception {
    java.util.List<String> source = new java.util.ArrayList<String>();
    source.add("template");
    Class<?> targetType = java.util.Collections.unmodifiableList(source).getClass();

    Object result = new com.fasterxml.jackson.databind.ObjectMapper()
            .readValue("[\"first\",\"second\"]", targetType);

    org.junit.Assert.assertEquals(java.util.Arrays.asList("first", "second"), result);
}

@org.junit.Test
public void deserializesSingletonListAndSetImplementations() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    Object listResult = mapper.readValue("[\"only\"]",
            java.util.Collections.singletonList("template").getClass());
    Object setResult = mapper.readValue("[\"only\"]",
            java.util.Collections.singleton("template").getClass());

    org.junit.Assert.assertEquals(java.util.Collections.singletonList("only"), listResult);
    org.junit.Assert.assertEquals(java.util.Collections.singleton("only"), setResult);
}

@org.junit.Test
public void deserializesUnmodifiableSetImplementation() throws Exception {
    java.util.Set<String> source = new java.util.LinkedHashSet<String>();
    source.add("template");
    Class<?> targetType = java.util.Collections.unmodifiableSet(source).getClass();

    Object result = new com.fasterxml.jackson.databind.ObjectMapper()
            .readValue("[\"first\",\"second\"]", targetType);

    org.junit.Assert.assertEquals(
            new java.util.LinkedHashSet<String>(java.util.Arrays.asList("first", "second")),
            result);
}

@org.junit.Test
public void deserializesSingletonAndUnmodifiableMapImplementations() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    Object singletonResult = mapper.readValue("{\"answer\":42}",
            java.util.Collections.singletonMap("template", "value").getClass());

    java.util.Map<String, Integer> source = new java.util.LinkedHashMap<String, Integer>();
    source.put("template", Integer.valueOf(0));
    Object unmodifiableResult = mapper.readValue("{\"answer\":42}",
            java.util.Collections.unmodifiableMap(source).getClass());

    org.junit.Assert.assertEquals(
            java.util.Collections.singletonMap("answer", Integer.valueOf(42)),
            singletonResult);
    org.junit.Assert.assertEquals(
            java.util.Collections.singletonMap("answer", Integer.valueOf(42)),
            unmodifiableResult);
}
}
