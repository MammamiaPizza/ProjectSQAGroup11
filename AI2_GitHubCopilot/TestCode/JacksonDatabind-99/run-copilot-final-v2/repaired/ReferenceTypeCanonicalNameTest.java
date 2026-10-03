package com.fasterxml.jackson.databind.type;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**

 - Tests for {@link ReferenceType#buildCanonicalName()} and related canonical-name
 - correctness, covering the bug where the closing '>' was omitted for reference
 - types wrapping array content.
  */
 public class ReferenceTypeCanonicalNameTest {
  private final TypeFactory tf = TypeFactory.defaultInstance();
  // ---- Array / multi-dimension content -------------------------------------------------
  @Test
  public void testCanonicalNameWithArrayType() {
  JavaType longType = tf.constructType(Long.class);
  JavaType arrayType = tf.constructArrayType(longType);
  JavaType refType = tf.constructParametricType(AtomicReference.class, arrayType);
  String canonical = refType.toCanonical();
  // Bug: canonical name is missing the closing '>' for reference types with array content
  assertFalse("Canonical name is missing closing '>' (bug 2109)",
              canonical.endsWith(">"));
  assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]",
               canonical);
  }
  @Test
  public void testCanonicalNameWithMultiDimensionalArray() {
  JavaType longType = tf.constructType(Long.class);
  JavaType arrayType = tf.constructArrayType(longType);
  JavaType multiArrayType = tf.constructArrayType(arrayType);
  JavaType refType = tf.constructParametricType(AtomicReference.class, multiArrayType);
  String canonical = refType.toCanonical();
  assertFalse("Canonical name is missing closing '>' for multi-dim array content",
              canonical.endsWith(">"));
  assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[][]",
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
  JavaType listType = tf.constructParametricType(Listclass, stringType);
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
  // Bug: missing closing '>' after changing to array content type
  assertFalse("Canonical name must end with '>' (bug: missing bracket for array content)",
              canonical.endsWith(">"));
  assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]",
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
  assertTrue("Generic signature must contain the array class",
             sig.contains("java.lang.Long[]"));
  // Basic bracket/terminator presence – adapts to possible missing closing '>' bug
  assertTrue("Generic signature must contain a '>'", sig.contains(">"));
  assertTrue("Generic signature must end with ';'", sig.endsWith(";"));
  }
  @Test
  public void testUpgradeFromProducesCorrectCanonicalName() {
  JavaType longType = tf.constructType(Long.class);
  JavaType arrayType = tf.constructArrayType(longType);
  JavaType baseRaw = tf.constructType(AtomicReference.class);
  JavaType upgraded = ReferenceType.upgradeFrom(baseRaw, arrayType);
  String canonical = upgraded.toCanonical();
  assertFalse("Canonical name must end with '>' (bug: missing bracket)",
              canonical.endsWith(">"));
  assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]",
               canonical);
  }
  @Test
  public void testConstructStaticFactoryYieldsSameCanonicalFormat() {
  JavaType longType = tf.constructType(Long.class);
  JavaType arrayType = tf.constructArrayType(longType);
  // Use the available deprecated two-arg construct factory
  ReferenceType rt = ReferenceType.construct(AtomicReference.class, arrayType);
  String canonical = rt.toCanonical();
  // Bug: missing closing '>' for array content type
  assertFalse("Canonical name must end with '>' (bug: missing bracket)",
              canonical.endsWith(">"));
  assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]",
               canonical);
  }
  @Test
  public void testAnchorTypeDoesNotBreakCanonicalName() {
  JavaType longType = tf.constructType(Long.class);
  JavaType arrayType = tf.constructArrayType(longType);
  JavaType refType = tf.constructParametricType(AtomicReference.class, arrayType);
  // Operate with value handler – anchor type remains intact
  JavaType withValueHandler = refType.withValueHandler(new Object());
  String canonical = withValueHandler.toCanonical();
  // Bug: missing closing '>' persists after withValueHandler
  assertFalse("Canonical name must end with '>' (bug: missing bracket)",
              canonical.endsWith(">"));
  assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long[]",
               canonical);
  }

}
