package com.fasterxml.jackson.databind.type;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;

public class ResolvedRecursiveTypeTest {

 private final TypeFactory tf = TypeFactory.defaultInstance();

 private ResolvedRecursiveType newRecursive(Class<?> erased, JavaType ref) {
     TypeBindings bindings;
     if (ref != null) {
         bindings = ref.getBindings();
     } else {
         bindings = tf.constructType(Object.class).getBindings();
     }
     return new ResolvedRecursiveType(erased, bindings);
 }

 @Test
 public void testSetReferenceAndGetSelfReferencedType() {
     JavaType refType = tf.constructType(String.class);
     ResolvedRecursiveType rec = newRecursive(String.class, refType);
     rec.setReference(refType);
     assertSame(refType, rec.getSelfReferencedType());
 }

 @Test
 public void testSetReferenceNullAllowed() {
     ResolvedRecursiveType rec = newRecursive(String.class, null);
     rec.setReference(null);
     assertNull(rec.getSelfReferencedType());
 }

 @Test(expected = IllegalStateException.class)
 public void testMultipleSetReferenceThrows() {
     JavaType refType = tf.constructType(String.class);
     ResolvedRecursiveType rec = newRecursive(String.class, refType);
     rec.setReference(refType);
     rec.setReference(tf.constructType(Integer.class));
 }

 @Test
 public void testGetSuperClassAfterSetReference() {
     JavaType listType = tf.constructType(ArrayList.class);
     ResolvedRecursiveType rec = newRecursive(ArrayList.class, listType);
     rec.setReference(listType);
     JavaType superClass = rec.getSuperClass();
     assertNotNull("Superclass must not be null", superClass);
     assertEquals(listType.getSuperClass(), superClass);
 }

 @Test
 public void testGetGenericSignatureAfterSetReference() {
     JavaType listType = tf.constructType(ArrayList.class);
     ResolvedRecursiveType rec = newRecursive(ArrayList.class, listType);
     rec.setReference(listType);
     String listSig = listType.getGenericSignature(new StringBuilder()).toString();
     String recSig = rec.getGenericSignature(new StringBuilder()).toString();
     assertEquals(listSig, recSig);
 }

 @Test(expected = NullPointerException.class)
 public void testGetGenericSignatureBeforeSetReferenceThrowsNPE() {
     ResolvedRecursiveType rec = newRecursive(String.class, null);
     rec.getGenericSignature(new StringBuilder());
 }

 @Test
 public void testToStringUnresolved() {
     ResolvedRecursiveType rec = newRecursive(String.class, null);
     assertTrue(rec.toString().contains("UNRESOLVED"));
 }

 @Test
 public void testToStringResolved() {
     JavaType refType = tf.constructType(String.class);
     ResolvedRecursiveType rec = newRecursive(String.class, refType);
     rec.setReference(refType);
     assertTrue(rec.toString().contains("java.lang.String"));
 }

 @Test
 public void testEqualsBehavior() {
     JavaType refType = tf.constructType(String.class);
     ResolvedRecursiveType rec1 = newRecursive(String.class, refType);
     rec1.setReference(refType);
     ResolvedRecursiveType rec2 = newRecursive(String.class, refType);
     rec2.setReference(refType);
     assertEquals(rec1, rec2);
     assertEquals(rec1, rec1);
     ResolvedRecursiveType rec3 = newRecursive(Integer.class, tf.constructType(Integer.class));
     rec3.setReference(tf.constructType(Integer.class));
     assertFalse(rec1.equals(rec3));
     ResolvedRecursiveType unresolved = newRecursive(String.class, null);
     assertFalse(unresolved.equals(rec1));
     assertFalse(rec1.equals(unresolved));
     assertEquals(unresolved, unresolved);
 }

 @Test
 public void testIsContainerTypeFalse() {
     ResolvedRecursiveType rec = newRecursive(String.class, null);
     assertFalse(rec.isContainerType());
 }

 @Test
 public void testWithMethodsReturnThis() {
     ResolvedRecursiveType rec = newRecursive(String.class, null);
     assertSame(rec, rec.withContentType(tf.constructType(Integer.class)));
     assertSame(rec, rec.withTypeHandler(new Object()));
     assertSame(rec, rec.withContentTypeHandler(new Object()));
     assertSame(rec, rec.withValueHandler(new Object()));
     assertSame(rec, rec.withContentValueHandler(new Object()));
     assertSame(rec, rec.withStaticTyping());
 }

 @Test
 public void testRefineReturnsNull() {
     ResolvedRecursiveType rec = newRecursive(String.class, null);
     assertNull(rec.refine(Number.class, null, null, null));
 }

}