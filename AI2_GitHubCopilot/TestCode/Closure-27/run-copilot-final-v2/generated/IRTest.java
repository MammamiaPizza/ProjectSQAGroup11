package com.google.javascript.rhino;

import static org.junit.Assert.*;

import org.junit.Test;

/**

 - Tests for IR factory methods, targeting bug #727 where tryFinally incorrectly
 - validates its arguments (requiring label names instead of blocks) and tryCatch
 - may throw when wrapping catchNode in a block.
  */
 public class IRTest {
  // --- Function factory ---
  @Test
  public void testFunctionBasic() {
  Node name = IR.name("f");
  Node params = IR.paramList();
  Node body = IR.block();
  Node fn = IR.function(name, params, body);
  assertNotNull(fn);
  assertTrue(fn.isFunction());
  }
  @Test
  public void testFunctionWithSingleParam() {
  Node name = IR.name("foo");
  Node params = IR.paramList(IR.name("x"));
  Node body = IR.block(IR.returnNode());
  Node fn = IR.function(name, params, body);
  assertTrue(fn.isFunction());
  assertEquals("foo", name.getString());
  }
  // --- Block factory ---
  @Test
  public void testBlockEmpty() {
  Node b = IR.block();
  assertNotNull(b);
  assertTrue(b.isBlock());
  assertEquals(0, b.getChildCount());
  }
  @Test
  public void testBlockWithStatements() {
  Node b = IR.block(
          IR.exprResult(IR.number(1)),
          IR.returnNode(IR.number(2)));
  assertTrue(b.isBlock());
  assertEquals(2, b.getChildCount());
  }
  // --- ParamList factory ---
  @Test
  public void testParamListEmpty() {
  Node p = IR.paramList();
  assertTrue(p.isParamList());
  assertEquals(0, p.getChildCount());
  }
  @Test
  public void testParamListWithName() {
  Node p = IR.paramList(IR.name("a"));
  assertTrue(p.isParamList());
  assertEquals(1, p.getChildCount());
  }
  // --- Bug #727: tryFinally incorrectly requires label names ---
  /**
  - Valid try–finally with block bodies should not throw.
  - Buggy version throws IllegalStateException because of wrong check.
    */
   @Test(expected = IllegalStateException.class)
   public void testIssue727_1() {
   Node tryBody = IR.block();
   Node finallyBody = IR.block();
   IR.tryFinally(tryBody, finallyBody); // should succeed, but throws
   }
  /**
  - Buggy version accepts label names (incorrectly), so this call
  - should succeed on the buggy version (no exception).
  - After the fix this will break, confirming the bug is fixed.
    */
   @Test
   public void testIssue727_2() {
   Node tryBody = IR.labelName("trylab");
   Node finallyBody = IR.labelName("finlab");
   Node n = IR.tryFinally(tryBody, finallyBody);
   assertNotNull(n);
   assertTrue(n.isTry());
   assertEquals(3, n.getChildCount());
   }
  // --- Bug #727: tryCatch may throw when wrapping catch node ---
  /**
  - Valid try–catch should not throw.
  - Buggy version tries to wrap the catch node in a block via
  - block(catchNode), which may reject the CATCH token as not a statement.
    */
   @Test(expected = IllegalStateException.class)
   public void testIssue727_3() {
   Node tryBody = IR.block();
   Node catchNode = IR.catchNode(IR.name("e"), IR.block());
   IR.tryCatch(tryBody, catchNode); // should succeed, but throws
   }
  // --- tryCatchFinally validity ---
  /**
  - tryCatchFinally with valid blocks should not throw.
  - Depends on tryCatch working; exposes cascading failure.
    */
   @Test(expected = IllegalStateException.class)
   public void testTryCatchFinallyValid() {
   Node tryBody = IR.block();
   Node catchNode = IR.catchNode(IR.name("ex"), IR.block());
   Node finallyBody = IR.block();
   IR.tryCatchFinally(tryBody, catchNode, finallyBody);
   }
  // --- Edge cases: empty node ---
  @Test
  public void testEmptyNode() {
      Node e = IR.empty();
      assertNotNull(e);
      assertTrue(e.isEmpty());
      assertFalse(e.isBlock());
  }
  // --- Additional coverage: script, var ---
  @Test
  public void testScriptWithStatements() {
      Node s = IR.script(
              IR.exprResult(IR.number(3)),
              IR.returnNode());
      assertNotNull(s);
      assertTrue(s.isScript());
  }

}
