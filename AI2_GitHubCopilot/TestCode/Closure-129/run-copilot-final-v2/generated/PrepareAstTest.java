package com.google.javascript.jscomp;

import com.google.javascript.rhino.IR;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import junit.framework.TestCase;

/**

 - Tests for {@link PrepareAst}.
 - Focus on annotation normalization (object literals, calls, dispatchers),
 - block normalization, and edge cases (null inputs, check-only mode).
  */
 public class PrepareAstTest extends TestCase {

  /**

 - Helper: build a SCRIPT root with an optional externs root
 - and run PrepareAst.process, returning the (possibly modified) root.
    */
   private Node runProcess(Node root) {
 return runProcess(null, root);
   }

  private Node runProcess(Node externs, Node root) {
    PrepareAst pass = new PrepareAst(new Compiler() /* dummy compiler */);
    pass.process(externs, root);
    return root; }

  /**

 -
  1. Object-literal key JSDoc should be copied to the value when it is a function.
 - (This is the documented behavior of normalizeObjectLiteralKeyAnnotations.)
 */
   public void testObjectLiteralKeyAnnotationTransfer() {
  Node objLit = IR.objectLit();
  Node key = IR.stringKey("method", IR.function(IR.name(""), IR.paramList(), IR.block()));
  JSDocInfo info = new JSDocInfo(true);
  key.setJSDocInfo(info);
  objLit.addChildToBack(key);
  Node script = IR.script(objLit);
  runProcess(script);
  Node func = key.getFirstChild();
  assertTrue(func.isFunction());
  assertNotNull("Function should receive the JSDoc from the key",
            func.getJSDocInfo());
  assertSame("JSDoc should be the originally attached instance",
         info, func.getJSDocInfo()); }

  /**

 -
  2. Getter/Setter keys (GETTER_DEF / SETTER_DEF) with JSDoc and function value
 - should also transfer JSDoc to the function.
 */
   public void testGetterSetterAnnotationTransfer() {
  // getter
  Node objLit = IR.objectLit();
  Node getter = IR.getterDef(IR.function(IR.name(""), IR.paramList(), IR.block()));
  getter.setJSDocInfo(new JSDocInfo(true));
  objLit.addChildToBack(getter);
  Node script = IR.script(objLit);
  runProcess(script);
  assertNotNull(getter.getFirstChild().getJSDocInfo());
  // setter
  Node objLit2 = IR.objectLit();
  Node setter = IR.setterDef(IR.function(IR.name(""), IR.paramList(
  IR.name("x")), IR.block()));
  setter.setJSDocInfo(new JSDocInfo(true));
  objLit2.addChildToBack(setter);
  Node script2 = IR.script(objLit2);
  runProcess(script2);
  assertNotNull(setter.getFirstChild().getJSDocInfo()); }

  /**

 -
  3. Calls without explicit this (free calls) are marked with FREE_CALL property.
  */
    public void testFreeCallAnnotation() {
   // call: foo()
   Node call = IR.call(IR.name("foo"));
   Node expr = IR.exprResult(call);
   Node script = IR.script(expr);
   runProcess(script);
   assertTrue("Call should be marked FREE_CALL",
     call.getBooleanProp(Node.FREE_CALL));

  }

  /**

 -
  4. Direct calls to eval() set DIRECT_EVAL on the NAME node.
  */
    public void testDirectEvalAnnotation() {
   Node call = IR.call(IR.name("eval"));
   Node expr = IR.exprResult(call);
   Node script = IR.script(expr);
   runProcess(script);
   assertTrue("eval NAME should be marked DIRECT_EVAL",
     call.getFirstChild().getBooleanProp(Node.DIRECT_EVAL));

  }

  /**

 -
  5. Dispatcher pattern: an assignment with @javadispatch JSDoc
 - marks the function with IS_DISPATCHER.
 */
   public void testDispatcherAnnotation() {
  Node func = IR.function(IR.name(""), IR.paramList(), IR.block());
  Node assign = IR.assign(IR.name("dispatcher"), func);
  JSDocInfo dispatchInfo = new JSDocInfo(true);
  dispatchInfo.setJavaDispatch(true);
  assign.setJSDocInfo(dispatchInfo);
  Node expr = IR.exprResult(assign);
  Node script = IR.script(expr);
  runProcess(script);
  assertTrue("Function should be marked IS_DISPATCHER",
         func.getBooleanProp(Node.IS_DISPATCHER));

  }

  /**

 -
  6. IF statement without BLOCK body  the body is wrapped in a BLOCK.
  */
    public void testIfBlockNormalization() {
   Node ifStmt = IR.ifStmt(IR.trueNode(), IR.name("x"));
   Node expr = IR.script(ifStmt);
   runProcess(expr);
   Node body = ifStmt.getSecondChild();
   assertTrue("IF body should become BLOCK", body.isBlock());
   assertTrue("BLOCK should contain original statement",
     body.hasChildren());

  }

  /**

 -
  7. Null-externs and null-root should not throw.
  */
    public void testNullExternsAndRoot() {
   try {
   PrepareAst pass = new PrepareAst(new Compiler());
   pass.process(null, null);
   } catch (Exception e) {
   fail("Should handle null externs and root gracefully");
   }
    }

  /**

 -
  8. Check-only mode with null root should not throw
 - (it exercises the same normalization path with null safety).
 /
   public void testNullRootCheckOnlyMode() {
  try {
    PrepareAst checkPass = new PrepareAst(new Compiler(), true / checkOnly
  */);
    checkPass.process(null, null);
  } catch (Exception e) {
    fail("Check-only mode should handle null root gracefully: " + e.getMessage());
  }
   }

  /**

 -
  9. Empty script (no children) should be handled without error.
  */
    public void testEmptyScript() {
   Node script = IR.script();
   runProcess(script);
   // no action expected; pass should not crash
    }

  /**

 -
  10. Nested object literals: annotations on multiple levels should be processed.
     */
    public void testNestedObjectLiterals() {
  Node innerFunc = IR.function(IR.name(""), IR.paramList(), IR.block());
  Node innerKey = IR.stringKey("inner", innerFunc);
  innerKey.setJSDocInfo(new JSDocInfo(true));
  Node innerObjLit = IR.objectLit();
  innerObjLit.addChildToBack(innerKey);

 Node outerKey = IR.stringKey("outer", innerObjLit);
 Node outerObjLit = IR.objectLit();
 outerObjLit.addChildToBack(outerKey);
 Node script = IR.script(IR.exprResult(outerObjLit));
 runProcess(script);
 assertNotNull("Inner function should get its key's JSDoc",
               innerFunc.getJSDocInfo()); }

  /**

 -
  11. Only function values should receive key JSDoc; non-function values should not.
     */
    public void testNonFunctionValueDoesNotReceiveJSDoc() {
  Node objLit = IR.objectLit();
  Node key = IR.stringKey("prop", IR.number(42));
  key.setJSDocInfo(new JSDocInfo(true));
  objLit.addChildToBack(key);
  Node script = IR.script(IR.exprResult(objLit));
  runProcess(script);
  // The number node should not have jsdoc (should remain null)
  assertNull("Non-function value should not get JSDoc",
    key.getFirstChild().getJSDocInfo());

  }

  /**

 -
  12. Combined test: object literal with annotated key (function)
 - and an eval call in the same script.
 */
   public void testCombinedAnnotations() {
  Node func = IR.function(IR.name(""), IR.paramList(), IR.block());
  Node key = IR.stringKey("fn", func);
  key.setJSDocInfo(new JSDocInfo(true));
  Node objLit = IR.objectLit();
  objLit.addChildToBack(key);
  Node call = IR.call(IR.name("eval"));
  Node expr1 = IR.exprResult(objLit);
  Node expr2 = IR.exprResult(call);
  Node script = IR.script(expr1, expr2);
  runProcess(script);
  assertNotNull("Function should have JSDoc", func.getJSDocInfo());
  assertTrue("Call should be FREE_CALL", call.getBooleanProp(Node.FREE_CALL));
  assertTrue("eval should be DIRECT_EVAL",
         call.getFirstChild().getBooleanProp(Node.DIRECT_EVAL)); }

}
