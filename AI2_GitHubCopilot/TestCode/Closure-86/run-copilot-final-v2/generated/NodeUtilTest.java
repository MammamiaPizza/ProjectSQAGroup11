package com.google.javascript.jscomp;

 import com.google.common.base.Predicate;
 import com.google.common.base.Predicates;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 import junit.framework.TestCase;

 public class NodeUtilTest extends TestCase {

     // Positive control: a simple local variable is indeed a local value.
     public void testLocalNameEvaluatesToLocalValue() {
         Node nameNode = Node.newString(Token.NAME, "localVar", 0, 0);
         boolean result = NodeUtil.evaluatesToLocalValue(nameNode,
             Predicates.<Node>alwaysFalse());
         // Even with a predicate that treats nothing as local, an unqualified
         // NAME node should not be considered a local value on its own.
         assertFalse("Bare NAME should not be local without predicate", result);
     }

     public void testLocalNameWithMatchingPredicate() {
         Node nameNode = Node.newString(Token.NAME, "x", 0, 0);
         final String expectedName = "x";
         Predicate<Node> locals = new Predicate<Node>() {
             public boolean apply(Node n) {
                 return Token.NAME == n.getType()
                     && expectedName.equals(n.getString());
             }
         };
         assertTrue("Local NAME with predicate should be local",
             NodeUtil.evaluatesToLocalValue(nameNode, locals));
     }

     // The core bug: NEW nodes are not recognised as local values.
     public void testNewStringEvaluatesToLocalValue() {
         Node constructor = Node.newString(Token.NAME, "String", 0, 0);
         Node newExpr = new Node(Token.NEW, constructor, 0, 0);
         assertTrue("new String() should evaluate to a local value",
             NodeUtil.evaluatesToLocalValue(newExpr, Predicates.<Node>alwaysFalse()));
     }

     public void testNewStringWithLocalsPredicate() {
         Node constructor = Node.newString(Token.NAME, "String", 0, 0);
         Node newExpr = new Node(Token.NEW, constructor, 0, 0);
         Predicate<Node> locals = Predicates.alwaysTrue();
         assertTrue("new String() should be local regardless of predicate",
             NodeUtil.evaluatesToLocalValue(newExpr, locals));
     }

     // Boundary: NEW with a single local variable argument.
     public void testNewWithLocalVarArg() {
         Node constructor = Node.newString(Token.NAME, "String", 0, 0);
         Node arg = Node.newString(Token.NAME, "localVar", 0, 0);
         Node newExpr = new Node(Token.NEW, constructor, arg, 0, 0);
         Predicate<Node> locals = new Predicate<Node>() {
             public boolean apply(Node n) {
                 return "localVar".equals(n.getString());
             }
         };
         assertTrue("new String(localVar) should be local",
             NodeUtil.evaluatesToLocalValue(newExpr, locals));
     }

     // Boundary: NEW with multiple arguments.
     public void testNewWithMultipleArgs() {
         Node constructor = Node.newString(Token.NAME, "Array", 0, 0);
         Node arg1 = Node.newString(Token.NAME, "a", 0, 0);
         Node arg2 = Node.newString(Token.NAME, "b", 0, 0);
         Node newExpr = new Node(Token.NEW, constructor, arg1, arg2, 0, 0);
         assertTrue("new Array(a,b) should be local",
             NodeUtil.evaluatesToLocalValue(newExpr, Predicates.<Node>alwaysFalse()));
     }

     // Boundary: nested NEW (inner result is an argument to outer NEW).
     public void testNestedNewEvaluatesToLocalValue() {
         Node innerConstructor = Node.newString(Token.NAME, "String", 0, 0);
         Node innerNew = new Node(Token.NEW, innerConstructor, 0, 0);
         Node outerConstructor = Node.newString(Token.NAME, "Object", 0, 0);
         Node outerNew = new Node(Token.NEW, outerConstructor, innerNew, 0, 0);
         assertTrue("new Object(new String()) outer should be local",
             NodeUtil.evaluatesToLocalValue(outerNew, Predicates.<Node>alwaysFalse()));
     }

     // Error handling: the method must not throw when encountering a NEW node.
     public void testNewDoesNotThrow() {
         Node newExpr = new Node(Token.NEW,
             Node.newString(Token.NAME, "String", 0, 0), 0, 0);
         try {
             NodeUtil.evaluatesToLocalValue(newExpr, Predicates.<Node>alwaysFalse());
         } catch (Exception e) {
             fail("evaluatesToLocalValue should not throw for NEW: " + e.getMessage());
         }
     }

     // Side-effect detection: a script consisting only of "new String()" has
     // no observable side effects (the constructor is known to be side-effect-free).
     public void testNewMayNotHaveSideEffects() {
         Node newExpr = new Node(Token.NEW,
             Node.newString(Token.NAME, "String", 0, 0), 0, 0);
         Node exprStatement = NodeUtil.newExpr(newExpr);
         Node script = new Node(Token.SCRIPT, exprStatement, 0, 0);
         assertFalse("new String() alone should not have side effects",
             NodeUtil.mayHaveSideEffects(script));
     }

     // var x = new String()  --  also not a side effect.
     public void testNewInVarDeclarationMayNotHaveSideEffects() {
         Node newExpr = new Node(Token.NEW,
             Node.newString(Token.NAME, "String", 0, 0), 0, 0);
         Node varName = Node.newString(Token.NAME, "x", 0, 0);
         // VAR node: first child is the NAME, second is the initialiser.
         Node varDecl = new Node(Token.VAR, varName, newExpr, 0, 0);
         Node script = new Node(Token.SCRIPT, varDecl, 0, 0);
         assertFalse("var x = new String() should not have side effects",
             NodeUtil.mayHaveSideEffects(script));
     }

     // NEW with a literal argument should still be local.
     public void testNewWithStringLiteralArg() {
         Node constructor = Node.newString(Token.NAME, "String", 0, 0);
         Node arg = Node.newString(Token.STRING, "hello", 0, 0);
         Node newExpr = new Node(Token.NEW, constructor, arg, 0, 0);
         assertTrue("new String('hello') should be local",
             NodeUtil.evaluatesToLocalValue(newExpr, Predicates.<Node>alwaysFalse()));
     }

     // Regression control: immutable values are correctly identified.
     public void testImmutableValuesAreLocal() {
         assertTrue(NodeUtil.evaluatesToLocalValue(
             Node.newString(Token.STRING, "immutable", 0, 0),
             Predicates.<Node>alwaysFalse()));
         assertTrue(NodeUtil.evaluatesToLocalValue(
             Node.newNumber(42.0, 0, 0),
             Predicates.<Node>alwaysFalse()));
     }
 }
