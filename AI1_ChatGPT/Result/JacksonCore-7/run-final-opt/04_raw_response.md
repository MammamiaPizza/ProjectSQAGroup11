@org.junit.Test
public void duplicateObjectFieldNamesAreRejectedWhenDupDetectionIsEnabled() throws Exception {
    com.fasterxml.jackson.core.json.DupDetector detector =
            com.fasterxml.jackson.core.json.DupDetector.rootDetector(
                    (com.fasterxml.jackson.core.JsonGenerator) null);
    com.fasterxml.jackson.core.json.JsonWriteContext object =
            com.fasterxml.jackson.core.json.JsonWriteContext.createRootContext(detector)
                    .createChildObjectContext();

    object.writeFieldName("name");
    object.writeValue();

    try {
        object.writeFieldName("name");
        org.junit.Assert.fail("Expected duplicate field name to be rejected");
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Duplicate field 'name'"));
    }
}

@org.junit.Test
public void childContextsDescribeObjectArrayAndRootPaths() throws Exception {
    com.fasterxml.jackson.core.json.JsonWriteContext root =
            com.fasterxml.jackson.core.json.JsonWriteContext.createRootContext();
    com.fasterxml.jackson.core.json.JsonWriteContext array = root.createChildArrayContext();

    org.junit.Assert.assertSame(root, array.getParent());
    array.writeValue();
    array.writeValue();
    org.junit.Assert.assertEquals("[1]", array.toString());

    com.fasterxml.jackson.core.json.JsonWriteContext object = root.createChildObjectContext();
    org.junit.Assert.assertSame(array, object);
    org.junit.Assert.assertEquals("{?}", object.toString());
    object.writeFieldName("field");
    org.junit.Assert.assertEquals("{\"field\"}", object.toString());
    org.junit.Assert.assertEquals("/", root.toString());
}

@org.junit.Test
public void contextsRetainCurrentValuesAndExposeCurrentFieldName() throws Exception {
    com.fasterxml.jackson.core.json.JsonWriteContext root =
            com.fasterxml.jackson.core.json.JsonWriteContext.createRootContext();
    Object rootValue = new Object();
    root.setCurrentValue(rootValue);

    com.fasterxml.jackson.core.json.JsonWriteContext object = root.createChildObjectContext();
    object.writeFieldName("property");
    Object objectValue = new Object();
    object.setCurrentValue(objectValue);

    org.junit.Assert.assertSame(rootValue, root.getCurrentValue());
    org.junit.Assert.assertSame(objectValue, object.getCurrentValue());
    org.junit.Assert.assertEquals("property", object.getCurrentName());
}