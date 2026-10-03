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