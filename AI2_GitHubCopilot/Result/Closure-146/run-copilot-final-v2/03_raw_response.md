package com.google.javascript.rhino.jstype;

 import com.google.javascript.rhino.ErrorReporter;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;
 import com.google.javascript.rhino.jstype.JSTypeNative;
 import com.google.javascript.rhino.jstype.TernaryValue;
 import com.google.javascript.rhino.jstype.UnionType;
 import junit.framework.TestCase;

 /**

 - Tests for the void/undefined narrowing bug (Closure-146b).
 - The bug causes getTypesUnderEquality and testForEquality to incorrectly
 - keep VoidType instead of narrowing to NoType when comparing void with an
 - incompatible type.
   */
  public class JSTypeBugTest extends TestCase {

   private JSTypeRegistry registry; private JSType voidType; private JSType numberType; private
JSType stringType; private JSType nullType; private JSType noType;

   @Override protected void setUp() throws Exception {
     super.setUp();
     ErrorReporter reporter = new ErrorReporter() {
       @Override public void error(String msg, String sourceName, int line, int lineOffset) {}
       @Override public void runtimeError(String msg, String sourceName, int line, String
lineSource, int lineOffset) {}
       @Override public void warning(String msg, String sourceName, int line, int lineOffset) {}
     };
     registry = new JSTypeRegistry(reporter, null, null);
     voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
     numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
     stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
     nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
     noType = registry.getNativeType(JSTypeNative.NO_TYPE); }

   // ---- getTypesUnderEquality ----

   public void testVoidEqualityWithNumber() {
     JSType.TypePair pair = voidType.getTypesUnderEquality(numberType);
     assertNotNull("pair should not be null", pair);
     assertTrue("void eq number should narrow void to NoType", pair.typeA.isNoType()); }

   public void testVoidEqualityWithVoid() {
     JSType.TypePair pair = voidType.getTypesUnderEquality(voidType);
     assertNotNull(pair);
     assertTrue("void eq void should keep void", pair.typeA.isVoidType());
     assertTrue("void eq void should keep void on both sides", pair.typeB.isVoidType()); }

   public void testVoidEqualityWithNull() {
     // undefined == null is true in JavaScript, so both sides should remain
     JSType.TypePair pair = voidType.getTypesUnderEquality(nullType);
     assertNotNull(pair);
     // VoidType and NullType are both kept because the equality can hold.
     assertFalse("void eq null should not narrow to NoType", pair.typeA.isNoType());
     assertFalse("null side should not be NoType", pair.typeB.isNoType()); }

   public void testVoidEqualityWithUnionContainingVoid() {
     UnionType union = registry.createUnionType(voidType, numberType);
     JSType.TypePair pair = voidType.getTypesUnderEquality(union);
     assertNotNull(pair);
     assertTrue("void eq (void|number) should keep void", pair.typeA.isVoidType()); }

   public void testVoidEqualityWithUnionNotContainingVoid() {
     UnionType union = registry.createUnionType(numberType, stringType);
     JSType.TypePair pair = voidType.getTypesUnderEquality(union);
     assertNotNull(pair);
     assertTrue("void eq (number|string) should narrow void to NoType", pair.typeA.isNoType()); }

   // ---- restrictByNotNullOrUndefined ----

   public void testRestrictByNotNullOrUndefinedOnVoid() {
     JSType restricted = voidType.restrictByNotNullOrUndefined();
     assertTrue("restricting void should give NoType", restricted.isNoType()); }

   public void testRestrictByNotNullOrUndefinedOnNumber() {
     JSType restricted = numberType.restrictByNotNullOrUndefined();
     assertSame("restricting number should return number", numberType, restricted); }

   // ---- isVoidType ----

   public void testIsVoidType() {
     assertTrue("void should be void", voidType.isVoidType());
     assertFalse("number is not void", numberType.isVoidType());
     assertFalse("NoType is not void", noType.isVoidType()); }

   // ---- testForEquality ----

   public void testTestForEqualityVoidNumber() {
     assertEquals("void eq number should be FALSE",
         TernaryValue.FALSE, voidType.testForEquality(numberType)); }

   public void testTestForEqualityVoidVoid() {
     assertEquals("void eq void should be TRUE",
         TernaryValue.TRUE, voidType.testForEquality(voidType)); }

   public void testTestForEqualityVoidNull() {
     // undefined == null evaluates to TRUE in JS; the type system should reflect that
     assertEquals("void eq null should be TRUE",
         TernaryValue.TRUE, voidType.testForEquality(nullType)); }

   public void testTestForEqualityVoidUnionNotContainingVoid() {
     UnionType union = registry.createUnionType(numberType, stringType);
     assertEquals("void eq (number|string) should be FALSE",
         TernaryValue.FALSE, voidType.testForEquality(union)); }
 }