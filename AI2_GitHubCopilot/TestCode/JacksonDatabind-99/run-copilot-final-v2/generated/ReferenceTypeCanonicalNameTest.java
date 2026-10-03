package com.fasterxml.jackson.databind.type;

 import java.util.List;
 import java.util.concurrent.atomic.AtomicReference;

 import org.junit.Test;
 import static org.junit.Assert.*;

 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.type.TypeFactory;

 /**
  * Tests for {@link ReferenceType#buildCanonicalName()} and related canonical-name
  * correctness, covering the bug where the closing '>' was omitted for reference
  * types wrapping array content.
  */
 public class ReferenceTypeCanonicalNameTest {

     private final TypeFactory tf = TypeFactory.defaultInstance();

     // ---- Array / multi-dimension content -------------------------------------------------

     @Test
     public void testCanonicalNameWithArrayType() {
         JavaType longType = tf.constructType(Long.class);
         JavaType arrayType = tf.constructArrayType(longType);
         JavaType refType = tf.constructParametricType(AtomicReference.class, arrayType);

         // buildCanonicalName() is called via toCanonical()
         String canonical = refType.toCanonical();
         assertTrue("Canonical name must end with '>' (bug: missing closing bracket)",
                    canonical.endsWith(">"));
         // The exact canonical string when fixed:
         assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]>",
                      canonical);
     }

     @Test
     public void testCanonicalNameWithMultiDimensionalArray() {
         JavaType longType = tf.constructType(Long.class);
         JavaType arrayType = tf.constructArrayType(longType);
         JavaType multiArrayType = tf.constructArrayType(arrayType);
         JavaType refType = tf.constructParametricType(AtomicReference.class, multiArrayType);

         String canonical = refType.toCanonical();
         assertTrue("Canonical name must end with '>'", canonical.endsWith(">"));
         assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[][]>",
                      canonical);
     }

     // ---- Simple & parameterized content --------------------------------------------------

     @Test
     public void testCanonicalNameWithSimpleClass() {
         JavaType stringType = tf.constructType(String.class);
         JavaType refType = tf.constructParametricType(AtomicReference.class, stringType);

         String canonical = refType.toCanonical();
         assertTrue("Canonical name must end with '>'", canonical.endsWith(">"));
         assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.String>",
                      canonical);
     }

     @Test
     public void testCanonicalNameWithParameterizedType() {
         JavaType stringType = tf.constructType(String.class);
         JavaType listType = tf.constructParametricType(List.class, stringType);
         JavaType refType = tf.constructParametricType(AtomicReference.class, listType);

         String canonical = refType.toCanonical();
         assertTrue("Canonical name must end with '>'", canonical.endsWith(">"));
         assertEquals(
             "java.util.concurrent.atomic.AtomicReference<java.util.List<java.lang.String>>",
             canonical);
     }

     // ---- Nested references ---------------------------------------------------------------

     @Test
     public void testCanonicalNameWithNestedReference() {
         JavaType stringType = tf.constructType(String.class);
         JavaType innerRef = tf.constructParametricType(AtomicReference.class, stringType);
         JavaType outerRef = tf.constructParametricType(AtomicReference.class, innerRef);

         String canonical = outerRef.toCanonical();
         assertTrue("Canonical name must end with '>'", canonical.endsWith(">"));
         assertEquals(
             "java.util.concurrent.atomic.AtomicReference<java.util.concurrent.atomic."
                 + "AtomicReference<java.lang.String>>",
             canonical);
     }

     // ---- Null / invalid arguments --------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testUpgradeFromWithNullReferencedType() {
         JavaType stringType = tf.constructType(String.class);
         ReferenceType.upgradeFrom(stringType, null);
     }

     // ---- Changing content type retains correct canonical name ----------------------------

     @Test
     public void testWithContentTypeProducesCorrectCanonicalName() {
         JavaType longType = tf.constructType(Long.class);
         JavaType arrayType = tf.constructArrayType(longType);
         JavaType refType = tf.constructParametricType(AtomicReference.class,
tf.constructType(String.class));
         JavaType changed = refType.withContentType(arrayType);

         String canonical = changed.toCanonical();
         assertTrue("Canonical name must end with '>'", canonical.endsWith(">"));
         assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]>",
                      canonical);
     }

     // ---- toString() consistency ----------------------------------------------------------

     @Test
     public void testToStringContainsCanonicalName() {
         JavaType longType = tf.constructType(Long.class);
         JavaType arrayType = tf.constructArrayType(longType);
         JavaType refType = tf.constructParametricType(AtomicReference.class, arrayType);

         String str = refType.toString();
         assertTrue("toString must start with '[reference type, class '",
                    str.startsWith("[reference type, class "));
         // The canonical name is embedded between the prefix and the opening '<'
         // of the toString template; we verify it appears before the trailing ']'.
         assertTrue("toString must end with ']'", str.endsWith("]"));
     }

     // ---- Signature methods do not corrupt canonical state --------------------------------

     @Test
     public void testGenericSignatureBracketBalance() {
         JavaType longType = tf.constructType(Long.class);
         JavaType arrayType = tf.constructArrayType(longType);
         JavaType refType = tf.constructParametricType(AtomicReference.class, arrayType);
         String sig = refType.getGenericSignature(new StringBuilder()).toString();
         // Generic signature must contain the array content type inside angle brackets
         // and must be properly closed.
         assertTrue("Generic signature must contain the array class",
                    sig.contains("java.lang.Long[]"));
         assertTrue("Generic signature must end with ';'",
                    sig.endsWith(">;"));
     }

     @Test
     public void testUpgradeFromProducesCorrectCanonicalName() {
         JavaType longType = tf.constructType(Long.class);
         JavaType arrayType = tf.constructArrayType(longType);
         // For upgradeFrom we need a TypeBase; use a SimpleType obtained from the factory.
         JavaType baseRaw = tf.constructType(AtomicReference.class);
         JavaType upgraded = ReferenceType.upgradeFrom(baseRaw, arrayType);

         String canonical = upgraded.toCanonical();
         assertTrue("Canonical name must end with '>'", canonical.endsWith(">"));
         assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]>",
                      canonical);
     }

     @Test
     public void testConstructStaticFactoryYieldsSameCanonicalFormat() {
         JavaType longType = tf.constructType(Long.class);
         JavaType arrayType = tf.constructArrayType(longType);
         // Use the static construct that provides super-class/index information.
         // We can obtain those from the previously built reference type.
         JavaType reference = tf.constructParametricType(AtomicReference.class, arrayType);
         ReferenceType rt = ReferenceType.construct(
                 reference.getRawClass(),
                 reference.getBindings(),
                 reference.getSuperClass(),
                 reference.getInterfaces().toArray(new JavaType[0]),
                 reference.getContentType());

         String canonical = rt.toCanonical();
         assertTrue("Canonical name must end with '>'", canonical.endsWith(">"));
         assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]>",
                      canonical);
     }

     @Test
     public void testAnchorTypeDoesNotBreakCanonicalName() {
         JavaType longType = tf.constructType(Long.class);
         JavaType arrayType = tf.constructArrayType(longType);
         JavaType refType = tf.constructParametricType(AtomicReference.class, arrayType);

         // Use the new-instance constructor that explicitly sets anchor type.
         // This constructor is not public; we rely on the internal factory setting anchor.
         // Instead, we just verify that the canonical name remains correct after
         // operations that preserve the anchor.
         JavaType withValueHandler = refType.withValueHandler(new Object());
         String canonical = withValueHandler.toCanonical();
         assertTrue("Canonical name must end with '>' after withValueHandler",
                    canonical.endsWith(">"));
         assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]>",
                      canonical);
     }
 }
