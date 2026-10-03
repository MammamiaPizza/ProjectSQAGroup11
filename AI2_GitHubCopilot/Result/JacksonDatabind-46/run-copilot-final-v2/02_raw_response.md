package com.fasterxml.jackson.databind.type;

 import static org.junit.Assert.assertEquals;

 import java.util.List;
 import java.util.concurrent.atomic.AtomicReference;

 import org.junit.Test;

 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.type.TypeFactory;

 /**
  * Tests for the array-bracket placement bug in ReferenceType signatures (Defects4J bug 46).
  * The correct JVM descriptor format is [Ljava/lang/String; – bracket before element, not after.
  */
 public class ReferenceTypeBug46Test {

     private final TypeFactory tf = TypeFactory.defaultInstance();

     // Helper to build ReferenceType for a given referenced type
     private ReferenceType ref(JavaType referenced) {
         return ReferenceType.construct(AtomicReference.class, referenced, null, null);
     }

     // ---------- generic signature tests ----------

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
         // Bug 46: old output would be ...<Ljava/lang/String;[]>; - bracket after element
         String expected = "Ljava/util/concurrent/atomic/AtomicReference<[Ljava/lang/String;>;";
         assertEquals(expected, sig);
     }

     @Test
     public void testGenericSignatureIntArray() {
         JavaType intArray = tf.constructArrayType(tf.constructType(int.class));
         ReferenceType ref = ref(intArray);
         String sig = ref.getGenericSignature(new StringBuilder()).toString();
         String expected = "Ljava/util/concurrent/atomic/AtomicReference<[I>;";
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

     // ---------- erased signature ----------

     @Test
     public void testErasedSignatureIsRawClassDescriptor() {
         // Regardless of array content, erased signature is the reference class descriptor
         ReferenceType ref = ref(tf.constructArrayType(tf.constructType(String.class)));
         String erased = ref.getErasedSignature(new StringBuilder()).toString();
         String expected = "Ljava/util/concurrent/atomic/AtomicReference;";
         assertEquals(expected, erased);
     }

     // ---------- canonical name ----------

     @Test
     public void testBuildCanonicalNameNonArray() {
         ReferenceType ref = ref(tf.constructType(String.class));
         String name = ref.buildCanonicalName(); // protected but same-package access
         String expected = "java.util.concurrent.atomic.AtomicReference<java.lang.String>";
         assertEquals(expected, name);
     }

     @Test
     public void testBuildCanonicalNameStringArray() {
         ReferenceType ref = ref(tf.constructArrayType(tf.constructType(String.class)));
         String name = ref.buildCanonicalName();
         // Java canonical representation uses [] after type name
         String expected = "java.util.concurrent.atomic.AtomicReference<java.lang.String[]>";
         assertEquals(expected, name);
     }

     // ---------- withTypeHandler does not corrupt signature ----------

     @Test
     public void testWithTypeHandlerPreservesGenericSignature() {
         ReferenceType ref = ref(tf.constructArrayType(tf.constructType(String.class)));
         ReferenceType withHandler = ref.withTypeHandler(new Object()); // dummy handler
         String sig = withHandler.getGenericSignature(new StringBuilder()).toString();
         String expected = "Ljava/util/concurrent/atomic/AtomicReference<[Ljava/lang/String;>;";
         assertEquals(expected, sig);
     }

     // ---------- static typing ----------

     @Test
     public void testWithStaticTypingPreservesGenericSignature() {
         ReferenceType ref = ref(tf.constructArrayType(tf.constructType(String.class)));
         ReferenceType staticRef = ref.withStaticTyping();
         String sig = staticRef.getGenericSignature(new StringBuilder()).toString();
         String expected = "Ljava/util/concurrent/atomic/AtomicReference<[Ljava/lang/String;>;";
         assertEquals(expected, sig);
     }

     // ---------- edge: reference to reference type ----------

     @Test
     public void testGenericSignatureNestedReference() {
         ReferenceType inner = ref(tf.constructType(String.class));
         ReferenceType outer = ref(inner);
         String sig = outer.getGenericSignature(new StringBuilder()).toString();
         // inner's generic signature is the full ReferenceType signature (not just class)
         String innerSig = inner.getGenericSignature(new StringBuilder()).toString();
         String expected = "Ljava/util/concurrent/atomic/AtomicReference<"
                 + innerSig + ">;";
         assertEquals(expected, sig);
     }
 }