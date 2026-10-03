@Test
    public void testAtomicRefDeserializerNullValue() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.DeserializationContext ctxt =
mapper.getDeserializationContext();
        com.fasterxml.jackson.databind.JavaType type =
mapper.constructType(java.util.concurrent.atomic.AtomicReference.class);
        com.fasterxml.jackson.databind.JsonDeserializer<?> deser =
ctxt.findRootValueDeserializer(type);
        assertTrue("Not AtomicReferenceDeserializer", deser instanceof
com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer);
        com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer refDeser =
                (com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer) deser;
        java.util.concurrent.atomic.AtomicReference<Object> ref = refDeser.getNullValue(ctxt);
        assertNotNull("getNullValue should return non-null AtomicReference", ref);
        assertNull("getNullValue contents should be null", ref.get());
    }

 @Test
 public void testAtomicRefDeserializerEmptyValue() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.DeserializationContext ctxt =
mapper.getDeserializationContext();
     com.fasterxml.jackson.databind.JavaType type =
mapper.constructType(java.util.concurrent.atomic.AtomicReference.class);
     com.fasterxml.jackson.databind.JsonDeserializer<?> deser =
ctxt.findRootValueDeserializer(type);
     com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer refDeser =
             (com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer) deser;
     Object empty = refDeser.getEmptyValue(ctxt);
     assertNotNull("getEmptyValue should return non-null", empty);
     assertTrue("getEmptyValue should return AtomicReference", empty instanceof
java.util.concurrent.atomic.AtomicReference);
     assertNull("getEmptyValue contents should be null",
((java.util.concurrent.atomic.AtomicReference<?>) empty).get());
 }

 @Test
 public void testAtomicRefDeserializerReferenceValue() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.DeserializationContext ctxt =
mapper.getDeserializationContext();
     com.fasterxml.jackson.databind.JavaType type =
mapper.constructType(java.util.concurrent.atomic.AtomicReference.class);
     com.fasterxml.jackson.databind.JsonDeserializer<?> deser =
ctxt.findRootValueDeserializer(type);
     com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer refDeser =
             (com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer) deser;
     java.util.concurrent.atomic.AtomicReference<Object> ref = refDeser.referenceValue("hello");
     assertNotNull("referenceValue should return non-null", ref);
     assertEquals("referenceValue contents should match", "hello", ref.get());
 }

 @Test
 public void testAtomicRefDeserializerReferencedAndUpdate() throws Exception {
     com.fasterxml.jackson.databind.ObjectMapper mapper = new
com.fasterxml.jackson.databind.ObjectMapper();
     com.fasterxml.jackson.databind.DeserializationContext ctxt =
mapper.getDeserializationContext();
     com.fasterxml.jackson.databind.JavaType type =
mapper.constructType(java.util.concurrent.atomic.AtomicReference.class);
     com.fasterxml.jackson.databind.JsonDeserializer<?> deser =
ctxt.findRootValueDeserializer(type);
     com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer refDeser =
             (com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer) deser;
     java.util.concurrent.atomic.AtomicReference<Object> ref = new
java.util.concurrent.atomic.AtomicReference<>("old");
     Object contents = refDeser.getReferenced(ref);
     assertEquals("getReferenced should return contents", "old", contents);
     java.util.concurrent.atomic.AtomicReference<Object> updated = refDeser.updateReference(ref,
"new");
     assertSame("updateReference should return same instance", ref, updated);
     assertEquals("updateReference should set new contents", "new", ref.get());
 }