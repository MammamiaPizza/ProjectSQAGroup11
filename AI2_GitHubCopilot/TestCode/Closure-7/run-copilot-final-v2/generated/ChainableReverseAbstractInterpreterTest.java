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
