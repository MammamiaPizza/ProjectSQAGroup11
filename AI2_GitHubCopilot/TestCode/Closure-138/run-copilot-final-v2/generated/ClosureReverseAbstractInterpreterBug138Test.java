package com.google.javascript.jscomp;

import static com.google.javascript.rhino.jstype.JSTypeNative.*;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeNative;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.UnknownType;

import junit.framework.TestCase;

/**

 - Tests for ClosureReverseAbstractInterpreter bug 138.
 - Ensures that goog.isArray, goog.isFunction, and goog.isObject correctly narrow null/void types.
  */
 public class ClosureReverseAbstractInterpreterBug138Test extends TestCase {

  private Compiler compiler; private JSTypeRegistry registry; private
ClosureReverseAbstractInterpreter reverseInterpreter; private CodingConvention convention;

  // Commonly used native types private JSType nullType; private JSType voidType; private JSType
arrayType; private JSType functionType; private JSType objectType;

  @Override protected void setUp() throws Exception {
    super.setUp();
    compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.initOptions(options);
    registry = compiler.getTypeRegistry();
    convention = compiler.getCodingConvention();
    reverseInterpreter = new ClosureReverseAbstractInterpreter(convention, registry);

 nullType   = registry.getNativeType(NULL_TYPE);
 voidType   = registry.getNativeType(VOID_TYPE);
 arrayType  = registry.getNativeType(ARRAY_TYPE);
 functionType = registry.getNativeType(FUNCTION_INSTANCE_TYPE);
 objectType = registry.getNativeType(OBJECT_TYPE); }

  /* ---------- goog.isArray ---------- */

  /** goog.isArray on null with true outcome must yield Array. */ public void
testGoogIsArrayOnNullTrue() {
    JSType result = getRestrictedType("isArray", nullType, true);
    assertEquals(arrayType, result); }

  /** goog.isArray on null with false outcome should remain null (unchanged). */ public void
testGoogIsArrayOnNullFalse() {
    JSType result = getRestrictedType("isArray", nullType, false);
    assertEquals(nullType, result); }

  /** goog.isArray on void (undefined) with true outcome must yield Array. */ public void
testGoogIsArrayOnVoidTrue() {
    JSType result = getRestrictedType("isArray", voidType, true);
    // Expected: Array, because true outcome forces the value to be an array.
    assertEquals(arrayType, result); }

  /** goog.isArray on a union of null and Array with true outcome removes null. */ public void
testGoogIsArrayOnNullArrayUnionTrue() {
    JSType union = registry.createUnionType(nullType, arrayType);
    JSType result = getRestrictedType("isArray", union, true);
    assertEquals(arrayType, result); }

  /** goog.isArray on a non-null type (e.g. Array) with true outcome stays Array. */ public void
testGoogIsArrayOnArrayTrue() {
    JSType result = getRestrictedType("isArray", arrayType, true);
    assertEquals(arrayType, result); }

  /* ---------- goog.isFunction ---------- */

  /** goog.isFunction on null with true outcome must yield Function. */ public void
testGoogIsFunctionOnNullTrue() {
    JSType result = getRestrictedType("isFunction", nullType, true);
    assertEquals(functionType, result); }

  /** goog.isFunction on null with false outcome should stay null. */ public void
testGoogIsFunctionOnNullFalse() {
    JSType result = getRestrictedType("isFunction", nullType, false);
    assertEquals(nullType, result); }

  /** goog.isFunction on void with true outcome must yield Function. */ public void
testGoogIsFunctionOnVoidTrue() {
    JSType result = getRestrictedType("isFunction", voidType, true);
    assertEquals(functionType, result); }

  /* ---------- goog.isObject ---------- */

  /** goog.isObject on null with true outcome must yield Object. */ public void
testGoogIsObjectOnNullTrue() {
    JSType result = getRestrictedType("isObject", nullType, true);
    assertEquals(objectType, result); }

  /** goog.isObject on null with false outcome should stay null. */ public void
testGoogIsObjectOnNullFalse() {
    JSType result = getRestrictedType("isObject", nullType, false);
    assertEquals(nullType, result); }

  /** goog.isObject on void with true outcome must yield Object. */ public void
testGoogIsObjectOnVoidTrue() {
    JSType result = getRestrictedType("isObject", voidType, true);
    assertEquals(objectType, result); }

  /** goog.isObject on a union of null and Object with true outcome removes null. */ public void
testGoogIsObjectOnNullObjectUnionTrue() {
    JSType union = registry.createUnionType(nullType, objectType);
    JSType result = getRestrictedType("isObject", union, true);
    assertEquals(objectType, result); }

  // ---------- helper ----------

  /**

 - Builds a {@code goog.isXxx(x)} call node and a scope where {@code x} has the
 - given input type, then asks the reverse interpreter for the narrowed type of
 - {@code x} under the given outcome.
 -
 - @return the type of variable {@code x} after applying the condition, or
 -      {@code null} if the scope was not modified.

   */
  private JSType getRestrictedType(String googFuncName, JSType inputType, boolean outcome) {
    Node condition = new Node(Token.CALL,
        new Node(Token.GETPROP,
            Node.newString(Token.NAME, "goog"),
            Node.newString(Token.STRING, googFuncName)),
        Node.newString(Token.NAME, "x"));

 Node script = new Node(Token.SCRIPT);
 Scope functionScope = new Scope(script, null);
 functionScope.declare("x", null, inputType, null);

 FlowScope blindScope = LinkedFlowScope.createEntryLattice(functionScope);
 FlowScope precise = reverseInterpreter.getPreciserScopeKnowingConditionOutcome(
     condition, blindScope, outcome);
 if (precise == null) {
   return null;
 }
 StaticSlot<JSType> slot = precise.getSlot("x");
 return slot == null ? null : slot.getType(); }

}
```
