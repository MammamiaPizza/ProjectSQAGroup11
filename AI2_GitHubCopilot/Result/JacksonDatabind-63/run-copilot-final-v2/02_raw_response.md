package com.fasterxml.jackson.databind.test;

 import static org.junit.Assert.*;

 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.JsonMappingException.Reference;

 import java.io.IOException;
 import java.util.List;

 import org.junit.Test;

 public class JsonMappingExceptionTest {

     // --- Helper classes for testing inner-class naming ---
     static class PlainInner {
     }

     static class Outer {
         static class Middle {
             static class Inner {
             }
         }
     }

     // --- Tests for getPathReference / _appendPathDesc ---

     @Test
     public void testGetPathReferenceWithNoPath() {
         JsonMappingException ex = new JsonMappingException("msg");
         // No path added -> getPathReference should return empty String
         assertEquals("", ex.getPathReference());
         assertEquals("", ex.getPathReference(new StringBuilder()).toString());
     }

     @Test
     public void testGetPathReferenceWithTopLevelClassField() {
         JsonMappingException ex = new JsonMappingException("msg");
         ex.prependPath(new Object(), "simpleField");

         String ref = ex.getPathReference();
         // Reference for a top-level class: binary name + ["fieldName"]
         String expected = Object.class.getName() + "[\"simpleField\"]";
         assertEquals(expected, ref);
     }

     @Test
     public void testGetPathReferenceWithInnerClassField() {
         JsonMappingException ex = new JsonMappingException("msg");
         PlainInner inner = new PlainInner();
         ex.prependPath(inner, "innerField");

         String ref = ex.getPathReference();
         // For inner classes the binary name must include '$' (enclosing+inner)
         String expectedBinaryName = PlainInner.class.getName(); // com...$PlainInner
         String expected = expectedBinaryName + "[\"innerField\"]";
         assertEquals(expected, ref);
     }

     @Test
     public void testGetPathReferenceWithMultipleInnerClasses() {
         JsonMappingException ex = new JsonMappingException("msg");
         Outer.Middle.Inner deep = new Outer.Middle.Inner();
         ex.prependPath(deep, "deepField");

         String ref = ex.getPathReference();
         // Must contain '$' for each enclosing class
         String expectedName = Outer.Middle.Inner.class.getName();
         String expected = expectedName + "[\"deepField\"]";
         assertEquals(expected, ref);
     }

     @Test
     public void testGetPathReferenceWithMixedFieldAndIndex() {
         JsonMappingException ex = new JsonMappingException("msg");
         ex.prependPath(new Object(), "firstField");  // [0]
         ex.prependPath(new Object(), 7);             // [1]

         String ref = ex.getPathReference();
         // Reference format: binaryName["field"] for field, binaryName[index] for index
         String name = Object.class.getName();
         // The most recently prepended reference (index) comes first
         String expectedFirst = name + "[7]";
         String expectedSecond = name + "[\"firstField\"]";
         String expected = expectedFirst + "->" + expectedSecond;
         assertEquals(expected, ref);
     }

     @Test
     public void testGetPathReferenceWithNullFrom() {
         JsonMappingException ex = new JsonMappingException("msg");
         Reference ref = new Reference(null, "someField");
         ex.prependPath(ref);

         String path = ex.getPathReference();
         // null _from - the description should be something like "null" or "?"
         // The buggy version may produce "null[\"someField\"]" or similar.
         // We just verify it does not throw NPE and is consistent.
         assertFalse(path == null);
         assertTrue(path.length() > 0);
         // A reasonable expectation: it contains the field name
         assertTrue(path.contains("someField"));
     }

     @Test
     public void testGetPathReferenceWithEmptyFieldName() {
         JsonMappingException ex = new JsonMappingException("msg");
         // Default Reference constructor (no-arg, protected) is inaccessible;
         // we use the one with Object and fieldName but fieldName cannot be null.
         // Test with index reference.
         ex.prependPath(new Object(), 42);
         String ref = ex.getPathReference();
         assertTrue(ref.contains(Object.class.getName()));
         assertTrue(ref.contains("42"));
     }

     @Test
     public void testGetPathReferenceConsistencyWithMultiplePrepends() {
         JsonMappingException ex = new JsonMappingException("msg");
         ex.prependPath(new String(), "a");
         ex.prependPath(new Integer(1), 1);
         ex.prependPath(new PlainInner(), "inner");

         String ref = ex.getPathReference();
         // Should have three references with -> separators
         String nameStr = String.class.getName();
         String nameInt = Integer.class.getName();
         String nameInner = PlainInner.class.getName();
         String expected = nameInner + "[\"inner\"]->"
                         + nameInt + "[1]->"
                         + nameStr + "[\"a\"]";
         assertEquals(expected, ref);
     }

     @Test
     public void testPathListContentAfterPrepend() {
         JsonMappingException ex = new JsonMappingException("msg");
         Reference r1 = new Reference(new Object(), "fieldX");
         ex.prependPath(r1);
         List<Reference> path = ex.getPath();
         assertEquals(1, path.size());
         assertEquals(r1, path.get(0));
     }

     @Test
     public void testWrapWithPathConstructsCorrectChain() {
         JsonMappingException original = new JsonMappingException("original");
         JsonMappingException wrapped = JsonMappingException.wrapWithPath(original,
                 new PlainInner(), "wrappedField");

         String pathRef = wrapped.getPathReference();
         // The original has no path, so only the new Reference should appear
         String expectedName = PlainInner.class.getName();
         assertEquals(expectedName + "[\"wrappedField\"]", pathRef);
     }

     @Test
     public void testWrapWithPathPreservesPreviousPath() {
         JsonMappingException original = new JsonMappingException("original");
         original.prependPath(new Object(), "first");
         JsonMappingException wrapped = JsonMappingException.wrapWithPath(original,
                 new PlainInner(), "second");

         String ref = wrapped.getPathReference();
         // The new reference is prepended, so order in path: second, then first
         String expected = PlainInner.class.getName() + "[\"second\"]->"
                         + Object.class.getName() + "[\"first\"]";
         assertEquals(expected, ref);
     }

     @Test
     public void testReferenceGetDescription() {
         Reference ref = new Reference(new PlainInner(), "bar");
         String desc = ref.getDescription();
         // Must include binary name (with '$') for inner class
         assertEquals(PlainInner.class.getName() + "[\"bar\"]", desc);
     }

     @Test
     public void testMessageIncludesThroughReferenceChain() {
         JsonMappingException ex = new JsonMappingException("problem");
         ex.prependPath(new PlainInner(), "badField");
         String message = ex.getMessage();
         assertTrue(message.contains("through reference chain"));
         String refPortion = ex.getPathReference();
         assertTrue(message.contains(refPortion));
     }
 }