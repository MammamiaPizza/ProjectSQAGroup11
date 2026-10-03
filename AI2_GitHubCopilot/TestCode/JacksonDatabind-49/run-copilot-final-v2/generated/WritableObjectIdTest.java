package com.fasterxml.jackson.databind.ser.impl;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.Mock;
 import org.powermock.reflect.Whitebox;
 import org.powermock.modules.junit4.PowerMockRunner;

 import com.fasterxml.jackson.annotation.ObjectIdGenerator;
 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.core.SerializableString;
 import com.fasterxml.jackson.databind.JsonSerializer;
 import com.fasterxml.jackson.databind.SerializerProvider;

 /**
  * Unit tests for {@link WritableObjectId}.  Focuses on the buggy
  * behavior of issue #1255: {@code generateId} does not reset
  * {@code idWritten}, causing incorrect reference writes when
  * "AlwaysAsReferenceFirst" serialization is expected.
  */
 @RunWith(PowerMockRunner.class)
 public class WritableObjectIdTest {

     @Mock
     private JsonGenerator gen;
     @Mock
     private SerializerProvider provider;
     @Mock
     private ObjectIdGenerator<?> generator;
     @Mock
     private JsonSerializer<Object> serializer;
     @Mock
     private SerializableString propertyName;

     private static final String OBJECT_REF = "1";

     @Before
     public void setUp() throws Exception {
         when(generator.generateId(any())).thenReturn(OBJECT_REF);
         when(gen.canWriteObjectId()).thenReturn(false);
     }

     // ------------------------------------------------------------------
     // Constructor
     // ------------------------------------------------------------------

     @Test
     public void testConstructorAssignsGenerator() {
         WritableObjectId woid = new WritableObjectId(generator);
         // Indirect verification: generateId delegates to the passed generator
         woid.generateId(new Object());
         verify(generator).generateId(any());
     }

     // ------------------------------------------------------------------
     // generateId
     // ------------------------------------------------------------------

     @Test
     public void testGenerateIdReturnsGeneratedId() {
         WritableObjectId woid = new WritableObjectId(generator);
         String id = (String) woid.generateId(new Object());
         assertEquals("Generated id must match generator output", OBJECT_REF, id);
         verify(generator).generateId(any());
     }

     @Test
     public void testGenerateIdOverwritesExistingId() throws Exception {
         WritableObjectId woid = new WritableObjectId(generator);
         when(generator.generateId(any())).thenReturn("first");
         woid.generateId(new Object());
         when(generator.generateId(any())).thenReturn("second");
         String id = (String) woid.generateId(new Object());
         assertEquals("second", id);
         assertEquals("second", Whitebox.getInternalState(woid, "id"));
     }

     // ------------------------------------------------------------------
     // writeAsField – sets idWritten = true
     // ------------------------------------------------------------------

     @Test
     public void testWriteAsFieldWritesPropertyNameAndSetsIdWritten() throws Exception {
         WritableObjectId woid = new WritableObjectId(generator);
         woid.generateId(new Object());
         ObjectIdWriter w = createObjectIdWriter(false, serializer, propertyName);
         woid.writeAsField(gen, provider, w);
         assertTrue("idWritten must be true after writeAsField",
                 Whitebox.<Boolean>getInternalState(woid, "idWritten"));
         verify(gen).writeFieldName(propertyName);
         verify(serializer).serialize(OBJECT_REF, gen, provider);
     }

     @Test
     public void testWriteAsFieldWithNullPropertyNameStillSetsIdWritten() throws Exception {
         WritableObjectId woid = new WritableObjectId(generator);
         woid.generateId(new Object());
         ObjectIdWriter w = createObjectIdWriter(false, serializer, null);
         woid.writeAsField(gen, provider, w);
         assertTrue("idWritten must be true", Whitebox.getInternalState(woid, "idWritten"));
         // No writeFieldName invocation expected
         verify(gen, never()).writeFieldName(any(SerializableString.class));
     }

     @Test
     public void testWriteAsFieldWithNativeObjectIdWritesObjectId() throws Exception {
         when(gen.canWriteObjectId()).thenReturn(true);
         WritableObjectId woid = new WritableObjectId(generator);
         woid.generateId(new Object());
         ObjectIdWriter w = createObjectIdWriter(false, serializer, propertyName);
         woid.writeAsField(gen, provider, w);
         verify(gen).writeObjectId(OBJECT_REF);
         verify(gen, never()).writeFieldName(any(SerializableString.class));
     }

     // ------------------------------------------------------------------
     // writeAsId – decisions based on idWritten / alwaysAsId
     // ------------------------------------------------------------------

     @Test
     public void testWriteAsIdIdNullReturnsFalse() throws Exception {
         WritableObjectId woid = new WritableObjectId(generator);
         // id is null by default
         ObjectIdWriter w = createObjectIdWriter(false, serializer, null);
         boolean result = woid.writeAsId(gen, provider, w);
         assertFalse("Must return false when id is null", result);
         verifyNoInteractions(gen);
         verifyNoInteractions(serializer);
     }

     @Test
     public void testWriteAsIdIdWrittenTrueWritesReference() throws Exception {
         WritableObjectId woid = new WritableObjectId(generator);
         woid.generateId(new Object());
         ObjectIdWriter w = createObjectIdWriter(false, serializer, null);
         // Manually set idWritten to true to simulate previous write
         Whitebox.setInternalState(woid, "idWritten", true);
         boolean result = woid.writeAsId(gen, provider, w);
         assertTrue(result);
         verify(serializer).serialize(OBJECT_REF, gen, provider);
     }

     @Test
     public void testWriteAsIdIdWrittenFalseAlwaysAsIdFalseReturnsFalse() throws Exception {
         WritableObjectId woid = new WritableObjectId(generator);
         woid.generateId(new Object()); // idWritten stays false
         ObjectIdWriter w = createObjectIdWriter(false, serializer, null);
         boolean result = woid.writeAsId(gen, provider, w);
         assertFalse(result);
         verifyNoInteractions(serializer);
     }

     @Test
     public void testWriteAsIdAlwaysAsIdTrueWritesReferenceEvenWhenIdWrittenFalse() throws Exception
{
         WritableObjectId woid = new WritableObjectId(generator);
         woid.generateId(new Object());
         ObjectIdWriter w = createObjectIdWriter(true, serializer, null);
         boolean result = woid.writeAsId(gen, provider, w);
         assertTrue(result);
         verify(serializer).serialize(OBJECT_REF, gen, provider);
     }

     @Test
     public void testWriteAsIdWithNativeObjectIdWritesObjectRef() throws Exception {
         when(gen.canWriteObjectId()).thenReturn(true);
         WritableObjectId woid = new WritableObjectId(generator);
         woid.generateId(new Object());
         ObjectIdWriter w = createObjectIdWriter(false, serializer, null);
         Whitebox.setInternalState(woid, "idWritten", true);
         boolean result = woid.writeAsId(gen, provider, w);
         assertTrue(result);
         verify(gen).writeObjectRef(OBJECT_REF);
         verifyNoInteractions(serializer);
     }

     // ------------------------------------------------------------------
     // Bug #1255 – generateId does not reset idWritten
     // ------------------------------------------------------------------

     /**
      * Core bug: after writing an id as a field (idWritten=true), a subsequent
      * call to {@code generateId} (for the full object definition) leaves
      * {@code idWritten} true.  This causes the full object write to be
      * skipped and only a reference to be written, leading to
      * {@code UnresolvedForwardReference} on deserialization.
      */
     @Test
     public void testBug1255GenerateIdDoesNotResetIdWritten() throws Exception {
         WritableObjectId woid = new WritableObjectId(generator);

         // First occurrence: generate id for the reference, write as field
         woid.generateId(new Object());
         ObjectIdWriter w = createObjectIdWriter(false, serializer, null);
         woid.writeAsField(gen, provider, w);
         assertTrue(Whitebox.<Boolean>getInternalState(woid, "idWritten"));

         // Second occurrence: full object should be written.
         // According to the bug, generateId is called but idWritten stays true.
         woid.generateId(new Object());
         assertTrue("BUG: idWritten remains true after generateId",
                 Whitebox.<Boolean>getInternalState(woid, "idWritten"));

         // Because idWritten is still true, writeAsId incorrectly returns true
         // and writes a reference instead of allowing the full object to be written.
         boolean result = woid.writeAsId(gen, provider, w);
         assertTrue("BUG: writeAsId treats full object as a reference", result);
         verify(serializer, atLeastOnce()).serialize(OBJECT_REF, gen, provider);
     }

     // ------------------------------------------------------------------
     // Helper
     // ------------------------------------------------------------------

     private ObjectIdWriter createObjectIdWriter(boolean alwaysAsId,
             JsonSerializer<Object> ser, SerializableString propName) throws Exception {
         ObjectIdWriter w = Whitebox.newInstance(ObjectIdWriter.class);
         Whitebox.setInternalState(w, "alwaysAsId", alwaysAsId);
         Whitebox.setInternalState(w, "serializer", ser);
         Whitebox.setInternalState(w, "propertyName", propName);
         return w;
     }
 }
