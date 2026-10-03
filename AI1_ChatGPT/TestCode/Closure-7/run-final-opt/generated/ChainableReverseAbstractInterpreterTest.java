package com.google.javascript.jscomp.type;

import static com.google.javascript.rhino.jstype.JSTypeNative.ALL_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.BOOLEAN_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.OBJECT_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.VOID_TYPE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.jscomp.Scope;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import org.junit.Test;

public class ChainableReverseAbstractInterpreterTest {

  private JSTypeRegistry newRegistry() {
    return new JSTypeRegistry(null);
  }

  private FlowScope scopeWithAllType(JSTypeRegistry registry, String name) {
    FlowScope scope =
        LinkedFlowScope.createEntryLattice(
            Scope.createGlobalScope(new Node(Token.SCRIPT)));
    return scope.inferSlotType(name, registry.getNativeType(ALL_TYPE));
  }

  private Node typeofComparison(String name, int comparisonToken, String value) {
    Node typeof = new Node(Token.TYPEOF, Node.newString(Token.NAME, name));
    return new Node(
        comparisonToken, typeof, Node.newString(Token.STRING, value));
  }

  private void assertEquivalent(JSType expected, JSType actual) {
    assertNotNull(actual);
    assertTrue(
        "Expected: " + expected + " but was: " + actual,
        expected.isEquivalentTo(actual));
  }

  @Test
  public void testTypeofNotUndefinedRestrictsAllTypeToDefinedValues() {
    JSTypeRegistry registry = newRegistry();
    SemanticReverseAbstractInterpreter interpreter =
        new SemanticReverseAbstractInterpreter(new ClosureCodingConvention(), registry);
    FlowScope scope = scopeWithAllType(registry, "x");

    FlowScope refined =
        interpreter.getPreciserScopeKnowingConditionOutcome(
            typeofComparison("x", Token.SHNE, "undefined"), scope, true);

    JSType expected =
        registry.createUnionType(
            registry.getNativeType(OBJECT_TYPE),
            registry.getNativeType(BOOLEAN_TYPE),
            registry.getNativeType(NUMBER_TYPE),
            registry.getNativeType(STRING_TYPE));
    assertEquivalent(expected, refined.getSlot("x").getType());
  }

  @Test
  public void testTypeofEqualsUndefinedKeepsOnlyVoidForAllType() {
    JSTypeRegistry registry = newRegistry();
    SemanticReverseAbstractInterpreter interpreter =
        new SemanticReverseAbstractInterpreter(new ClosureCodingConvention(), registry);
    FlowScope scope = scopeWithAllType(registry, "x");

    FlowScope refined =
        interpreter.getPreciserScopeKnowingConditionOutcome(
            typeofComparison("x", Token.SHEQ, "undefined"), scope, true);

    assertEquivalent(registry.getNativeType(VOID_TYPE), refined.getSlot("x").getType());
  }

  @Test
  public void testGoogIsFunctionFalseBranchExcludesUndefinedFromAllType() {
    JSTypeRegistry registry = newRegistry();
    ClosureReverseAbstractInterpreter interpreter =
        new ClosureReverseAbstractInterpreter(new ClosureCodingConvention(), registry);
    FlowScope scope = scopeWithAllType(registry, "x");

    Node callee =
        new Node(
            Token.GETPROP,
            Node.newString(Token.NAME, "goog"),
            Node.newString(Token.STRING, "isFunction"));
    Node condition = new Node(Token.CALL, callee, Node.newString(Token.NAME, "x"));

    FlowScope refined =
        interpreter.getPreciserScopeKnowingConditionOutcome(condition, scope, false);

    JSType expected =
        registry.createUnionType(
            registry.getNativeType(OBJECT_TYPE),
            registry.getNativeType(BOOLEAN_TYPE),
            registry.getNativeType(NUMBER_TYPE),
            registry.getNativeType(STRING_TYPE));
    assertEquivalent(expected, refined.getSlot("x").getType());
  }
}
