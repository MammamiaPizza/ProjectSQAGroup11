package com.google.javascript.jscomp.type;

import static com.google.javascript.rhino.jstype.JSTypeNative.NUMBER_TYPE;
import static com.google.javascript.rhino.jstype.JSTypeNative.STRING_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.google.javascript.jscomp.CodingConvention;
import com.google.javascript.jscomp.FlowScope;
import com.google.javascript.jscomp.Scope;
import com.google.javascript.jscomp.TestCase;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.StaticSlot;
import com.google.javascript.rhino.jstype.StaticSourceFile;

import java.util.HashMap;
import java.util.Map;

public class ChainableReverseAbstractInterpreterTest extends TestCase {

  // ----------------------------------------------------------------- // Bug-regression:
getTypeIfRefinable must not throw for THIS nodes. //
----------------------------------------------------------------- public void
testThisNodeDoesNotThrow() {
    TestHelper helper = new TestHelper(getCodingConvention(), getTypeRegistry());
    Node thisNode = new Node(Token.THIS);
    try {
      helper.callGetTypeIfRefinable(thisNode, null);
    } catch (IllegalArgumentException e) {
      fail("IllegalArgumentException thrown for 'this' node: " + e.getMessage());
    } }

  // ----------------------------------------------------------------- // getTypeIfRefinable –
normal behaviour // ----------------------------------------------------------------- public void
testGetTypeIfRefinableWithNameNode() {
    JSTypeRegistry registry = getTypeRegistry();
    JSType numberType = registry.getType(NUMBER_TYPE);
    JSType stringType = registry.getType(STRING_TYPE);

 TestHelper helper = new TestHelper(getCodingConvention(), registry);
 MockScope scope = new MockScope();
 scope.putSlot("x", numberType);

 // scope has a slot → use that type
 Node nameNode = Node.newString(Token.NAME, "x");
 assertEquals(numberType, helper.callGetTypeIfRefinable(nameNode, scope));

 // slot is null → fall back to node.getJSType()
 Node nameNode2 = Node.newString(Token.NAME, "y");
 nameNode2.setJSType(stringType);
 assertEquals(stringType, helper.callGetTypeIfRefinable(nameNode2, scope));

 // both null → null
 Node nameNode3 = Node.newString(Token.NAME, "z");
 assertNull(helper.callGetTypeIfRefinable(nameNode3, scope)); }

  public void testGetTypeIfRefinableWithGetPropNode() {
    JSTypeRegistry registry = getTypeRegistry();
    JSType stringType = registry.getType(STRING_TYPE);

 TestHelper helper = new TestHelper(getCodingConvention(), registry);
 MockScope scope = new MockScope();
 scope.putSlot("a.b", stringType);

 // qualified name present, slot exists
 Node getprop = Node.newString(Token.GETPROP, "a.b");
 assertEquals(stringType, helper.callGetTypeIfRefinable(getprop, scope));

 // qualified name present, slot missing → fall back to node type
 Node getprop2 = Node.newString(Token.GETPROP, "c.d");
 getprop2.setJSType(stringType);
 assertEquals(stringType, helper.callGetTypeIfRefinable(getprop2, scope));

 // no qualified name → null
 Node getprop3 = new Node(Token.GETPROP); // no children, getQualifiedName() == null
 assertNull(helper.callGetTypeIfRefinable(getprop3, scope)); }

  public void testGetTypeIfRefinableForOtherNodeTypeReturnsNull() {
    TestHelper helper = new TestHelper(getCodingConvention(), getTypeRegistry());
    Node stringNode = Node.newString(Token.STRING, "hello");
    assertNull(helper.callGetTypeIfRefinable(stringNode, null)); }

  // ----------------------------------------------------------------- // typeof refinement –
exercised via the whole compiler chain //
----------------------------------------------------------------- public void
testTypeofRefinementNumber() {
    testTypes("var x; if (typeof x === 'number') { var y = x; }",
              "y: number"); }

  public void testTypeofRefinementString() {
    testTypes("var x; if (typeof x === 'string') { var y = x; }",
              "y: string"); }

  public void testTypeofRefinementBoolean() {
    testTypes("var x; if (typeof x === 'boolean') { var y = x; }",
              "y: boolean"); }

  public void testTypeofRefinementObject() {
    testTypes("var x; if (typeof x === 'object') { var y = x; }",
              "y: Object"); }

  public void testTypeofRefinementFunction() {
    testTypes("var x; if (typeof x === 'function') { var y = x; }",
              "y: Function"); }

  public void testTypeofRefinementUndefined() {
    testTypes("var x; if (typeof x === 'undefined') { var y = x; }",
              "y: undefined"); }

  // ----------------------------------------------------------------- // Helper classes //
----------------------------------------------------------------- private static class TestHelper
extends ChainableReverseAbstractInterpreter {
    TestHelper(CodingConvention convention, JSTypeRegistry registry) {
      super(convention, registry);
    }

 @Override
 protected FlowScope getPreciserScopeKnowingConditionOutcome(
     Node condition, FlowScope blindScope, boolean outcome) {
   return blindScope;
 }

 /** Exposes the protected method for testing. */
 JSType callGetTypeIfRefinable(Node node, FlowScope scope) {
   return getTypeIfRefinable(node, scope);
 } }

  /** Minimal FlowScope that only provides getSlot. */ private static class MockScope implements
FlowScope {
    private final Map<String, JSType> slots = new HashMap<String, JSType>();

 void putSlot(String name, JSType type) {
   slots.put(name, type);
 }

 @Override
 public StaticSlot<JSType> getSlot(final String name) {
   final JSType type = slots.get(name);
   if (type == null) return null;
   return new StaticSlot<JSType>() {
     @Override public String getName() { return name; }
     @Override public JSType getType() { return type; }
     @Override public boolean isTypeInferred() { return true; }
     @Override public StaticSourceFile getSourceFile() { return null; }
     @Override public int getLineno() { return -1; }
     @Override public int getCharno() { return -1; }
   };
 }

 // other FlowScope methods – stubs that are not needed for the current tests
 @Override public JSType getTypeOfThis() { return null; }
 @Override public FlowScope getParentScope() { return null; }
 @Override public String toString() { return "MockScope"; }
 @Override public Scope getScope() { return null; }
 @Override public JSType getPrimitiveType() { return null; }
 @Override public StaticSlot<JSType> getSlot(String name, boolean recurse) {
   return getSlot(name);
 }
 @Override public FlowScope withPosition(Node node) { return this; }
 @Override public FlowScope withType(JSType type) { return this; } }

}
