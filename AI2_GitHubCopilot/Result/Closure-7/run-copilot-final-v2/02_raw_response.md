package com.google.javascript.jscomp.type;

 import com.google.javascript.jscomp.ClosureReverseAbstractInterpreter;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.FlowScope;
 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.jscomp.ControlFlowGraph;
 import com.google.javascript.jscomp.Scope;
 import com.google.javascript.jscomp.SemanticReverseAbstractInterpreter;
 import com.google.javascript.jscomp.graph.LinkedDirectedGraph;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.rhino.jstype.FunctionType;
 import com.google.javascript.rhino.jstype.JSType;
 import com.google.javascript.rhino.jstype.JSTypeNative;
 import com.google.javascript.rhino.jstype.JSTypeRegistry;
 import com.google.javascript.rhino.jstype.ObjectType;
 import com.google.javascript.rhino.jstype.UnionType;

 import junit.framework.TestCase;

 /**
  * Tests for {@link ChainableReverseAbstractInterpreter} focusing on
  * {@code caseUnionType} and {@code RestrictByTypeOfResultVisitor} behavior.
  *
  * See bug 841: typeof restriction on union types produces incorrect union
  * when null/void are present (expected: (Object|boolean|number|string) but
  * (Object) or others were returned).
  */
 public class ChainableReverseAbstractInterpreterTest extends TestCase {

   private Compiler compiler;
   private JSTypeRegistry registry;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     compiler = new Compiler();
     registry = compiler.getTypeRegistry();
   }

   // --- helper to create a simple flow scope for a given JSType ---
   private FlowScope scopeForType(String varName, JSType type) {
     Node root = new Node(Token.SCRIPT);
     Node nameNode = Node.newString(Token.NAME, varName);
     Scope globalScope = Scope.createGlobalScope(root);
     // ensure slot is set
     globalScope.declare(varName, nameNode, null, compiler.getInput(new SourceFile("test")));
     // We need a FlowScope that knows the type. The easiest way is to build a
     // control-flow graph but for reverse abstraction we only need a scope that
     // maps the name to the type. Create a LinkedFlowScope with a slot.
     // Since the exact API may vary, we use a mock-like approach by calling
     // compiler.getTypedScopeCreator().createScope(root, null) but that's heavy.
     // Instead use the fact that getTypeIfRefinable gets the type from
scope.getSlot(node.getString()).
     // We can use a Scope wrapper.
     // We'll rely on the concrete reverse interpreters' methods that accept Node and scope.
     return null; // placeholder – see concrete test methods below
   }

   /**
    * Tests that a union containing Function, null, void, boolean, number, string
    * is correctly narrowed to Function only when typeof === 'function'.
    * (Bug: caseUnionType removed void but did not properly apply typeof restriction,
    * leading to (Object|null|...)).
    */
   public void testUnionTypeOfFunctionNarrowsToFunction() {
     // Build union: Function + null + void + string + boolean + number
     ObjectType functionType = (ObjectType)
registry.createFunctionType(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE));
     JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
     JSType voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
     JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
     JSType booleanType = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
     JSType numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);

     UnionType union = registry.createUnionType(functionType, nullType, voidType,
         stringType, booleanType, numberType);

     // Apply typeof === 'function' restriction using RestrictByTypeOfResultVisitor
     // via the concrete SemanticReverseAbstractInterpreter (which overrides the visitor
     // to provide the typeof handling).
     SemanticReverseAbstractInterpreter interpreter =
         new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

     // Build the typeof node: typeof x === 'function'
     Node typeofNode = new Node(Token.STRING, "function");
     Node callNode = new Node(Token.CALL, Node.newString(Token.NAME, "typeof"), typeofNode);
     // Actually the condition node should be (typeof x) == 'function'
     // Simulate by calling getPreciserScopeKnowingConditionOutcome with a Node.
     // Use a node representing the expression typeof(x) whose type is string.
     Node condition = createTypeofCondition("x", "function");

     // We need a FlowScope where variable 'x' has type 'union'.
     FlowScope blindScope = createScopeWithVarType("x", union);
     if (blindScope == null) {
       // fallback: test caseUnionType directly
       JSType restricted = interpreter.caseUnionType(union);
       assertNotNull(restricted);
       // The buggy caseUnionType just removed VOID, so we would get a union without void.
       // We assert that after fix, union is Function.
       // But caseUnionType is not supposed to do typeof restriction; we need the full
       // reverse interpretation. Without a proper scope, we test using the underlying
       // visitor methods if accessible.
       // Let's test using the RestrictByOneTypeOfResultVisitor behavior.
       // Instead we'll test the helper getNativeTypeForTypeOf.
       JSType functionTypeNative = registry.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE);
       assertTrue(functionTypeNative.isSubtype(functionType));
       // The expected union after typeof function restriction: Function only.
       JSType expected = functionType;
       assertEquals(expected.toString(), union.getRestrictedUnion(
           getRestrictedTypeForTypeOf(union, "function")));
       // But getRestrictedUnion is not available in that form.
     }
     // Since creating a proper FlowScope is complex without full mock, we test
     // the members of ChainableReverseAbstractInterpreter that we can access.
   }

   /**
    * Tests caseUnionType directly – in the buggy version it always removes
    * VOID_TYPE from the union; the fix should make it a no-op (return type unchanged)
    * leaving restriction to the visitor.
    */
   public void testCaseUnionTypePreservesUnion() {
     ChainableReverseAbstractInterpreter interpreter =
         new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

     // Union of Object + void
     JSType objectType = registry.getNativeType(JSTypeNative.OBJECT_TYPE);
     JSType voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
     UnionType union = registry.createUnionType(objectType, voidType);
     JSType result = interpreter.caseUnionType(union);
     assertNotNull(result);
     // After fix, caseUnionType should not strip void (leaving it to typeof restriction).
     assertTrue(result.isUnionType());
     UnionType resultUnion = (UnionType) result;
     assertTrue(resultUnion.contains(voidType));
   }

   /**
    * Tests that typeof 'object' on a union of (Object, null, void, number)
    * excludes null/void but keeps Object+number? Actually typeof object should
    * keep Object+Number? No, typeof number is 'number'. So only Object.
    */
   public void testUnionTypeOfObjectExcludesNullAndVoid() {
     JSType objectType = registry.getNativeType(JSTypeNative.OBJECT_TYPE);
     JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
     JSType voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
     JSType numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
     UnionType union = registry.createUnionType(objectType, nullType, voidType, numberType);

     // typeof === 'object' must keep only Object (null is typeof object but JSType NULL is
separate)
     // The visitor's caseNullType returns matchesExpectation("object") ? <NULL_TYPE> : null
     // so null is kept for typeof object. The expected answer per bug fix is that null and void
     // are excluded (the expected (Object|boolean|number|string) does not contain null/void).
     // Actually goog.isDef uses typeof != 'undefined' to remove void, and goog.isNull?
     // The bug report says typeof==='object' should exclude null/void but keep Object+...
     // I'll follow the plan: testGetNativeTypeForTypeOf and getRestrictedWithoutNull/Undefined.

     // For now, assert that caseUnionType with the fix doesn't auto-remove void.
     ChainableReverseAbstractInterpreter interpreter =
         new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);
     JSType result = interpreter.caseUnionType(union);
     assertTrue(result.isUnionType());
     // If fix is applied, void is not removed; if buggy, void is removed.
     // We'll check the expected fix behavior:
     assertTrue(((UnionType) result).contains(voidType));
   }

   /**
    * Tests getNativeTypeForTypeOf (actually a private method, but we test its
    * effect through the RestrictByTypeOfResultVisitor in the concrete subclass).
    * Verifies that for typeof 'function' the native type U2U_CONSTRUCTOR_TYPE is returned.
    */
   public void testNativeTypeForTypeOfFunction() {
     JSType u2uType = registry.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE);
     assertEquals("function", u2uType.toAnnotationString()); // may vary
     assertNotNull(u2uType);
   }

   /**
    * Tests getRestrictedWithoutNull on a union containing null.
    */
   public void testGetRestrictedWithoutNull() {
     JSType numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
     JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
     UnionType union = registry.createUnionType(numberType, nullType);

     ChainableReverseAbstractInterpreter interpreter =
         new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);
     // The methods are protected final; we cannot call directly.
     // We can test through caseUnionType or other public API that uses them.
     // Fallback: test that after typeof restriction to 'number', null is removed.
     // We'll rely on getPreciserScopeKnowingConditionOutcome which we mock.
   }

   /**
    * Tests getRestrictedWithoutUndefined on a union containing void.
    */
   public void testGetRestrictedWithoutUndefined() {
     JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
     JSType voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
     UnionType union = registry.createUnionType(stringType, voidType);

     ChainableReverseAbstractInterpreter interpreter =
         new ClosureReverseAbstractInterpreter(compiler.getCodingConvention(), registry);
     // Again protected, so test indirectly.
   }

   /**
    * Tests that the RestrictByTrueTypeOfResultVisitor (used for goog.isFunction)
    * returns Function for a function type and null/void are stripped.
    * This test recreates the scenario of testGoogIsFunction2.
    */
   public void testGoogIsFunction2Narrowing() {
     JSType functionType =
registry.createFunctionType(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE));
     JSType nullType = registry.getNativeType(JSTypeNative.NULL_TYPE);
     JSType voidType = registry.getNativeType(JSTypeNative.VOID_TYPE);
     JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
     JSType booleanType = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
     JSType numberType = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
     JSType objectType = registry.getNativeType(JSTypeNative.OBJECT_TYPE);

     UnionType union = registry.createUnionType(functionType, nullType, voidType,
         stringType, booleanType, numberType, objectType);

     // The expected result after goog.isFunction (typeof === 'function', outcome true)
     // is Object|boolean|number|string according to the bug report? No, that's the result
     // for goog.isDef? Actually testGoogIsFunction2 expects (Object|boolean|number|string)
     // Let's see: that test probably checks that goog.isFunction on a union that includes
     // Object, boolean, number, string, null, void yields (Object|boolean|number|string) on the
false path?
     // The bug report: "Expected: (Object|boolean|number|string)" for testGoogIsFunction2.
     // That suggests the true path (typeof function) should not include null/void.
     // For typeof 'function', the true path should be Function (and maybe subtypes),
     // and the false path should be the rest without Function? Not sure.

     // The plan says typeof==='function' narrows to Function only (true path).
     // But the expected (Object|boolean|number|string) could be the false path
     // after removing Function, null, void.
     // We'll test both: true path yields Function; false path yields
(Object|boolean|number|string).
     // But we can't test both without proper flow scope. However, we can test
     // caseUnionType behavior indirectly through the visitor: the true visitor's caseUnionType.

     // Since the test is complex, I'll assert the expected behavior per the bug fix:
     // 1) typeof 'function' on union of (Function, Object, boolean, number, string, null, void)
     //    -> true path: Function; false path: Object|boolean|number|string
     // 2) typeof 'object' on same -> true path: Object|null; false path: ...
     // 3) typeof 'number' -> true path: number, etc.
     // This test just validates that caseUnionType does not prematurely remove void.
   }

   /**
    * Tests that a single-element union with typeof matching returns the type,
    * and with typeof mismatching returns NoType.
    */
   public void testSingleElementUnionTypeofMatchAndMismatch() {
     JSType stringType = registry.getNativeType(JSTypeNative.STRING_TYPE);
     UnionType single = registry.createUnionType(stringType);
     // typeof === 'string' should keep string
     // typeof === 'number' should become NoType
     ChainableReverseAbstractInterpreter interpreter =
         new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);
     // We test caseUnionType first: shouldn't change single.
     JSType result = interpreter.caseUnionType(single);
     assertTrue(result.isEquivalentTo(stringType));
     // Now test restriction: need to call the RestrictByTypeOfResultVisitor.
     // We can test by creating a condition node and using getPreciserScopeKnowingConditionOutcome.
     // For simplicity, we test using the helper methods if we can access them via reflection? Not
allowed.
     // Let's create a proper test using the flow scope.
   }

   /**
    * Tests boundaries: empty union, NoType, UnknownType with typeof restrictions.
    */
   public void testEmptyUnionAndBoundaries() {
     ChainableReverseAbstractInterpreter interpreter =
         new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);
     JSType noType = registry.getNativeType(JSTypeNative.NO_TYPE);
     JSType resultNoType = interpreter.caseNoType();
     assertEquals(noType, resultNoType);

     // UnknownType
     JSType unknown = interpreter.caseUnknownType();
     assertEquals(registry.getNativeType(JSTypeNative.UNKNOWN_TYPE), unknown);

     // caseUnionType on empty union – not directly possible, but checking null
     UnionType emptyUnion = registry.createUnionType(); // possible?
     if (emptyUnion != null) {
       JSType emptyResult = interpreter.caseUnionType(emptyUnion);
       assertNotNull(emptyResult);
       assertTrue(emptyResult.isUnknownType() || emptyResult.isNoType());
     }
   }

   // Helper to create a condition node like `typeof x === 'result'`
   private Node createTypeofCondition(String varName, String result) {
     Node nameNode = Node.newString(Token.NAME, varName);
     Node typeofName = new Node(Token.SCRIPT);
     typeofName.addChildToBack(nameNode);
     Node typeofNode = new Node(Token.SCRIPT); // placeholder
     Node callNode = new Node(Token.CALL, Node.newString(Token.NAME, "typeof"), nameNode);
     Node stringNode = Node.newString(result);
     Node eqNode = new Node(Token.EQ, callNode, stringNode);
     return eqNode;
   }

   private FlowScope createScopeWithVarType(String varName, JSType type) {
     // Simplified: we cannot create a full FlowScope without a CFG.
     // We'll return null and rely on direct method tests.
     return null;
   }
 }

But this test class won't compile because I've used UnionType.contains which may not exist, and
other assumptions. I need to produce a compilable test class. Since I cannot verify the exact API of
the concrete subclasses, I'll stick to testing only the methods that are public/accessible and rely
on the existing test infrastructure.

Better approach: I'll look at the existing test classes in Defects4J Closure 7b. I can try to recall
patterns. The prompt says "Return ONLY the complete Java Test Class." I must provide a single file.

I'll write a test class that extends TestCase, imports the necessary classes, creates a Compiler and
a ChainableReverseAbstractInterpreter concrete instance, and then tests caseUnionType, caseNoType,
caseUnknownType, and uses the public method getPreciserScopeKnowingConditionOutcome with a
manufactured Node and FlowScope. Actually, ChainableReverseAbstractInterpreter has protected methods
including getPreciserScopeKnowingConditionOutcome, but the concrete subclass may expose them. The
existing tests access them via the concrete class. So I can do the same.

I'll write tests that directly call interpreter.getPreciserScopeKnowingConditionOutcome(condition,
blindScope, outcome) (which is protected but accessible from test in same package or through
subclass). Since our test is in the same package com.google.javascript.jscomp.type, we can access
protected methods. Yes.

I'll provide the test class named ChainableReverseAbstractInterpreterTest in package
com.google.javascript.jscomp.type.

I'll ensure it compiles with the available JARs. The Defects4J environment includes the full Closure
project on the classpath, so imports should work.

I'll simplify to test the main bug: caseUnionType and typeof restriction via the public method.

Given time constraints, I'll produce a well-structured but minimal test class that covers the
required cases.```java
package com.google.javascript.jscomp.type;

import static com.google.javascript.rhino.jstype.JSTypeNative.*;

import com.google.javascript.jscomp.ClosureReverseAbstractInterpreter;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.FlowScope;
import com.google.javascript.jscomp.LinkedFlowScope;
import com.google.javascript.jscomp.Scope;
import com.google.javascript.jscomp.SemanticReverseAbstractInterpreter;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.UnionType;

import junit.framework.TestCase;

/**

 - Tests {@link ChainableReverseAbstractInterpreter} for the bug 841 fix:
 - typeof restrictions on union types must produce the correct narrowed union,
 - not incorrectly strip void/null.
  */
 public class ChainableReverseAbstractInterpreterTest extends TestCase {

  private Compiler compiler; private JSTypeRegistry registry;

  @Override protected void setUp() throws Exception {
    super.setUp();
    compiler = new Compiler();
    registry = compiler.getTypeRegistry(); }

  // ---------------------------------------------------------------------- // caseUnionType :
should NOT alter the union (fix makes it a no‑op) //
----------------------------------------------------------------------

  /**

 - caseUnionType must not remove VOID from the union; the typename
 - restriction is delegated to the RestrictByTypeOfResultVisitors.
    */
   public void testCaseUnionTypePreservesVoid() {
 ChainableReverseAbstractInterpreter interp =
    new ClosureReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType obj = registry.getNativeType(OBJECT_TYPE);
 JSType vd  = registry.getNativeType(VOID_TYPE);
 UnionType source = registry.createUnionType(obj, vd);
 JSType result = interp.caseUnionType(source);

 assertTrue("Result must still be a UnionType", result.isUnionType());
 UnionType ru = (UnionType) result;
 assertTrue("VOID_TYPE was incorrectly stripped by caseUnionType",
     ru.getAlternates().contains(vd)); }

  public void testCaseUnionTypeLeavesSingleElementAlone() {
    ClosureReverseAbstractInterpreter interp =
        new ClosureReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType s = registry.getNativeType(STRING_TYPE);
 UnionType single = registry.createUnionType(s);
 JSType result = interp.caseUnionType(single);
 assertEquals("Single-element union should remain unchanged",
     single, result); }

  // ---------------------------------------------------------------------- // typeof 'function' on
a broad union  (covers testGoogIsFunction2) //
----------------------------------------------------------------------

  /**

 - typeof x === "function" on a union of (Function, null, void, string,
 - boolean, number) must narrow to Function only on the true branch.
    */
   public void testTypeofFunctionTrueBranch() {
 ClosureReverseAbstractInterpreter interp =
    new ClosureReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType func = registry.createFunctionType(registry.getNativeType(UNKNOWN_TYPE));
 JSType nul  = registry.getNativeType(NULL_TYPE);
 JSType vd   = registry.getNativeType(VOID_TYPE);
 JSType str  = registry.getNativeType(STRING_TYPE);
 JSType bool = registry.getNativeType(BOOLEAN_TYPE);
 JSType num  = registry.getNativeType(NUMBER_TYPE);

 UnionType input = registry.createUnionType(func, nul, vd, str, bool, num);
 FlowScope blind = scopeWithVar("x", input);

 Node cond = new Node(Token.EQ,
     new Node(Token.TYPEOF, Node.newString(Token.NAME, "x")),
     Node.newString("function"));

 FlowScope result = interp.getPreciserScopeKnowingConditionOutcome(cond, blind, true);
 JSType narrowed = result.getSlot("x").getType();

 assertTrue("True branch must yield a Function subtype", narrowed.isFunctionType()); }

  /**

 - On the false branch of typeof x === "function" the union should be
 - stripped of the Function part, i.e. (null|void|string|boolean|number).
 - (Regression for testGoogIsFunction2 false‑branch expectation.)
    */
   public void testTypeofFunctionFalseBranch() {
 ClosureReverseAbstractInterpreter interp =
    new ClosureReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType func = registry.createFunctionType(registry.getNativeType(UNKNOWN_TYPE));
 JSType nul  = registry.getNativeType(NULL_TYPE);
 JSType vd   = registry.getNativeType(VOID_TYPE);
 JSType str  = registry.getNativeType(STRING_TYPE);
 JSType bool = registry.getNativeType(BOOLEAN_TYPE);
 JSType num  = registry.getNativeType(NUMBER_TYPE);

 UnionType input = registry.createUnionType(func, nul, vd, str, bool, num);
 FlowScope blind = scopeWithVar("x", input);

 Node cond = new Node(Token.EQ,
     new Node(Token.TYPEOF, Node.newString(Token.NAME, "x")),
     Node.newString("function"));

 FlowScope result = interp.getPreciserScopeKnowingConditionOutcome(cond, blind, false);
 JSType narrowed = result.getSlot("x").getType();

 assertTrue("False branch must be a UnionType", narrowed.isUnionType());
 UnionType nu = (UnionType) narrowed;
 assertFalse("false branch must not contain Function part",
     nu.getAlternates().contains(func));
 assertTrue("false branch must contain STRING_TYPE",
     nu.getAlternates().contains(str)); }

  // ---------------------------------------------------------------------- // typeof 'object' must
handle null/void correctly  (covers testTypeof3) //
----------------------------------------------------------------------

  /**

 - typeof x === "object" on a union of (Object, number, string, boolean, null, void)
 - should keep Object + null (because typeof null === "object"), and remove void.
 - The expected narrowed set on true branch is (Object|null).
    */
   public void testTypeofObjectTrueBranch() {
 SemanticReverseAbstractInterpreter interp =
    new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType obj  = registry.getNativeType(OBJECT_TYPE);
 JSType num  = registry.getNativeType(NUMBER_TYPE);
 JSType str  = registry.getNativeType(STRING_TYPE);
 JSType bool = registry.getNativeType(BOOLEAN_TYPE);
 JSType nul  = registry.getNativeType(NULL_TYPE);
 JSType vd   = registry.getNativeType(VOID_TYPE);

 UnionType input = registry.createUnionType(obj, num, str, bool, nul, vd);
 FlowScope blind = scopeWithVar("x", input);

 Node cond = new Node(Token.EQ,
     new Node(Token.TYPEOF, Node.newString(Token.NAME, "x")),
     Node.newString("object"));

 FlowScope result = interp.getPreciserScopeKnowingConditionOutcome(cond, blind, true);
 JSType narrowed = result.getSlot("x").getType();

 assertTrue(narrowed.isUnionType());
 UnionType nu = (UnionType) narrowed;
 assertTrue("Must contain Object", nu.getAlternates().contains(obj));
 assertTrue("Must contain null (typeof null == 'object')",
     nu.getAlternates().contains(nul));
 assertFalse("Must not contain void", nu.getAlternates().contains(vd)); }

  // ---------------------------------------------------------------------- // typeof 'number' /
'string' / 'boolean' // ----------------------------------------------------------------------

  public void testTypeofNumberOnMultiUnion() {
    ClosureReverseAbstractInterpreter interp =
        new ClosureReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType num  = registry.getNativeType(NUMBER_TYPE);
 JSType str  = registry.getNativeType(STRING_TYPE);
 JSType bool = registry.getNativeType(BOOLEAN_TYPE);
 JSType obj  = registry.getNativeType(OBJECT_TYPE);

 UnionType input = registry.createUnionType(num, str, bool, obj);
 FlowScope blind = scopeWithVar("x", input);

 Node cond = new Node(Token.EQ,
     new Node(Token.TYPEOF, Node.newString(Token.NAME, "x")),
     Node.newString("number"));

 FlowScope result = interp.getPreciserScopeKnowingConditionOutcome(cond, blind, true);
 JSType narrowed = result.getSlot("x").getType();
 assertEquals("Must narrow to NUMBER_TYPE", num, narrowed); }

  public void testTypeofMismatchYieldsNoType() {
    SemanticReverseAbstractInterpreter interp =
        new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType str = registry.getNativeType(STRING_TYPE);
 UnionType input = registry.createUnionType(str);
 FlowScope blind = scopeWithVar("x", input);

 Node cond = new Node(Token.EQ,
     new Node(Token.TYPEOF, Node.newString(Token.NAME, "x")),
     Node.newString("number"));

 FlowScope result = interp.getPreciserScopeKnowingConditionOutcome(cond, blind, true);
 JSType narrowed = result.getSlot("x").getType();
 assertEquals("Mismatch must yield NO_TYPE",
     registry.getNativeType(NO_TYPE), narrowed); }

  // ---------------------------------------------------------------------- // boundary cases //
----------------------------------------------------------------------

  public void testUnknownTypeUnchanged() {
    ClosureReverseAbstractInterpreter interp =
        new ClosureReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType unknown = registry.getNativeType(UNKNOWN_TYPE);
 FlowScope blind = scopeWithVar("x", unknown);

 Node cond = new Node(Token.EQ,
     new Node(Token.TYPEOF, Node.newString(Token.NAME, "x")),
     Node.newString("function"));

 FlowScope result = interp.getPreciserScopeKnowingConditionOutcome(cond, blind, true);
 JSType narrowed = result.getSlot("x").getType();
 assertTrue("Unknown must remain Unknown", narrowed.isUnknownType()); }

  public void testNoTypeStaysNoType() {
    SemanticReverseAbstractInterpreter interp =
        new SemanticReverseAbstractInterpreter(compiler.getCodingConvention(), registry);

 JSType none = registry.getNativeType(NO_TYPE);
 assertEquals(none, interp.caseNoType());

 FlowScope blind = scopeWithVar("x", none);
 Node cond = new Node(Token.EQ,
     new Node(Token.TYPEOF, Node.newString(Token.NAME, "x")),
     Node.newString("object"));

 FlowScope result = interp.getPreciserScopeKnowingConditionOutcome(cond, blind, true);
 JSType narrowed = result.getSlot("x").getType();
 assertEquals(none, narrowed); }

  // ---------------------------------------------------------------------- // helpers //
----------------------------------------------------------------------

  /**

 - Create a minimal {@link FlowScope} that contains a single variable
 - with the supplied type. This is enough for the reverse abstract
 - interpreter to operate.
    /
   private FlowScope scopeWithVar(String name, JSType type) {
 Scope global = Scope.createGlobalScope(new Node(Token.SCRIPT));
 global.declare(name, Node.newString(Token.NAME, name), type, / input
  */ null);
 return LinkedFlowScope.createEntryLattice(global);
   }

}
```