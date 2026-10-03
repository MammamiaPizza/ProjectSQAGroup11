package com.fasterxml.jackson.databind.type;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ReferenceTypeBug46Test {

 private final TypeFactory tf = TypeFactory.defaultInstance();

 private ReferenceType ref(JavaType referenced) {
     return ReferenceType.construct(AtomicReference.class, referenced, null, null);
 }

 @Test
 public void testGenericSignatureNonArray() {
     ReferenceType ref = ref(tf.constructType(String.class));
     String sig = ref.getGenericSignature(new StringBuilder()).toString();
     String expected = "Ljava/util/concurrent/atomic/AtomicReference<Ljava/lang/String;>;";
     assertEquals(expected, sig);
 }

 @Test
 public void testGenericSignatureStringArray() {
     JavaType stringArray = tf.constructArrayType(tf.constructType(String.class));
     ReferenceType ref = ref(stringArray);
     String sig = ref.getGenericSignature(new StringBuilder()).toString();
     String expected = "Ljava/util/concurrent/atomic/AtomicReference<[Ljava/lang/String;>;";
     assertEquals(expected, sig);
 }

 @Test
 public void testGenericSignatureIntArray() {
     JavaType intArray = tf.constructArrayType(tf.constructType(int.class));
     ReferenceType ref = ref(intArray);
     String sig = ref.getGenericSignature(new StringBuilder()).toString();
     String expected = "Ljava/util/concurrent/atomic/AtomicReference<I[]>;";
     assertEquals(expected, sig);
 }

 @Test
 public void testGenericSignatureMultiDimArray() {
     JavaType objArray = tf.constructArrayType(tf.constructType(Object.class));
     JavaType multiDim = tf.constructArrayType(objArray);
     ReferenceType ref = ref(multiDim);
     String sig = ref.getGenericSignature(new StringBuilder()).toString();
     String expected = "Ljava/util/concurrent/atomic/AtomicReference<[[Ljava/lang/Object;>;";
     assertEquals(expected, sig);
 }

 @Test
 public void testGenericSignatureParameterizedArray() {
     JavaType listOfString = tf.constructParametricType(List.class, String.class);
     JavaType arrayOfList = tf.constructArrayType(listOfString);
     ReferenceType ref = ref(arrayOfList);
     String sig = ref.getGenericSignature(new StringBuilder()).toString();
     String expected =
"Ljava/util/concurrent/atomic/AtomicReference<[Ljava/util/List<Ljava/lang/String;>;>;";
     assertEquals(expected, sig);
 }

 @Test
 public void testErasedSignatureIsRawClassDescriptor() {
     ReferenceType ref = ref(tf.constructArrayType(tf.constructType(String.class)));
     String erased = ref.getErasedSignature(new StringBuilder()).toString();
     String expected = "Ljava/util/concurrent/atomic/AtomicReference;";
     assertEquals(expected, erased);
 }

 @Test
 public void testBuildCanonicalNameNonArray() {
     ReferenceType ref = ref(tf.constructType(String.class));
     String name = ref.buildCanonicalName();
     String expected = "java.util.concurrent.atomic.AtomicReference";
     assertEquals(expected, name);
 }

 @Test
 public void testBuildCanonicalNameStringArray() {
     ReferenceType ref = ref(tf.constructArrayType(tf.constructType(String.class)));
     String name = ref.buildCanonicalName();
     String expected = "java.util.concurrent.atomic.AtomicReference";
     assertEquals(expected, name);
 }

 @Test
 public void testWithTypeHandlerPreservesGenericSignature() {
     ReferenceType ref = ref(tf.constructArrayType(tf.constructType(String.class)));
     ReferenceType withHandler = ref.withTypeHandler(new Object());
     String sig = withHandler.getGenericSignature(new StringBuilder()).toString();
     String expected = "Ljava/util/concurrent/atomic/AtomicReference<[Ljava/lang/String;>;";
     assertEquals(expected, sig);
 }

 @Test
 public void testWithStaticTypingPreservesGenericSignature() {
     ReferenceType ref = ref(tf.constructArrayType(tf.constructType(String.class)));
     ReferenceType staticRef = ref.withStaticTyping();
     String sig = staticRef.getGenericSignature(new StringBuilder()).toString();
     String expected = "Ljava/util/concurrent/atomic/AtomicReference<[Ljava/lang/String;>;";
     assertEquals(expected, sig);
 }

 @Test
 public void testGenericSignatureNestedReference() {
     ReferenceType inner = ref(tf.constructType(String.class));
     ReferenceType outer = ref(inner);
     String sig = outer.getGenericSignature(new StringBuilder()).toString();
     String innerSig = inner.getGenericSignature(new StringBuilder()).toString();
     String expected = "Ljava/util/concurrent/atomic/AtomicReference<" + innerSig + ">;";
     assertEquals(expected, sig);
 }

}