package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import junit.framework.TestCase;

 public class ClosureCodingConventionTest extends TestCase {

     private final ClosureCodingConvention convention = new ClosureCodingConvention();

     public void testGoogRequireValidStringArgument() {
         assertEquals("a.B", extractRequire("goog.require", "a.B", Token.STRING));
     }

     public void testGoogProvideCalledViaRequire() {
         assertNull(extractRequire("goog.provide", "a.B", Token.STRING));
     }

     public void testUnqualifiedRequire() {
         assertNull(extractRequire("require", "foo", Token.STRING));
     }

     public void testDifferentNamespaceRequire() {
         assertNull(extractRequire("x.y.require", "foo", Token.STRING));
     }

     public void testGoogRequireNoArguments() {
         Node callee = createGetProp("goog", "require");
         Node call = new Node(Token.CALL);
         call.addChildToBack(callee);
         Node expr = new Node(Token.EXPR_RESULT);
         expr.addChildToBack(call);
         assertNull(convention.extractClassNameIfRequire(call, expr));
     }

     public void testGoogRequireNonStringArgument() {
         // Bug 530: extractClassNameIfRequire must return null for non‑string arguments.
         assertNull(extractRequire("goog.require", "foo", Token.NAME));
     }

     public void testGoogRequireNullParent() {
         Node callee = createGetProp("goog", "require");
         Node call = new Node(Token.CALL);
         call.addChildToBack(callee);
         call.addChildToBack(Node.newString(Token.STRING, "foo"));
         assertEquals("foo", convention.extractClassNameIfRequire(call, null));
     }

     public void testGoogRequireWithNonExprParent() {
         Node callee = createGetProp("goog", "require");
         Node call = new Node(Token.CALL);
         call.addChildToBack(callee);
         call.addChildToBack(Node.newString(Token.STRING, "a.B"));
         Node block = new Node(Token.BLOCK);
         assertNull(convention.extractClassNameIfRequire(call, block));
     }

     public void testGoogRequireWithMultipleArguments() {
         Node callee = createGetProp("goog", "require");
         Node arg1 = Node.newString(Token.STRING, "a.B");
         Node arg2 = Node.newString(Token.STRING, "c.D");
         Node call = new Node(Token.CALL);
         call.addChildToBack(callee);
         call.addChildToBack(arg1);
         call.addChildToBack(arg2);
         Node expr = new Node(Token.EXPR_RESULT);
         expr.addChildToBack(call);
         // First argument is used.
         assertEquals("a.B", convention.extractClassNameIfRequire(call, expr));
     }

     public void testGoogRequireWithNullFirstChild() {
         Node call = new Node(Token.CALL);
         call.addChildToBack(Node.newString(Token.STRING, "a.B"));
         Node expr = new Node(Token.EXPR_RESULT);
         expr.addChildToBack(call);
         assertNull(convention.extractClassNameIfRequire(call, expr));
     }

     // -------------------------------------------------------------------------
     // Helper methods
     // -------------------------------------------------------------------------

     private String extractRequire(String qualifiedName, String argValue, int argType) {
         String[] parts = qualifiedName.split("\\.");
         Node callee;
         if (parts.length == 2) {
             callee = createGetProp(parts[0], parts[1]);
         } else {
             callee = Node.newString(Token.NAME, parts[0]);
         }
         Node arg = Node.newString(argType, argValue);
         Node call = new Node(Token.CALL);
         call.addChildToBack(callee);
         call.addChildToBack(arg);
         Node expr = new Node(Token.EXPR_RESULT);
         expr.addChildToBack(call);
         return convention.extractClassNameIfRequire(call, expr);
     }

     private Node createGetProp(String objectName, String propertyName) {
         Node obj = Node.newString(Token.NAME, objectName);
         Node prop = Node.newString(Token.STRING, propertyName);
         Node getprop = new Node(Token.GETPROP);
         getprop.addChildToBack(obj);
         getprop.addChildToBack(prop);
         return getprop;
     }
 }