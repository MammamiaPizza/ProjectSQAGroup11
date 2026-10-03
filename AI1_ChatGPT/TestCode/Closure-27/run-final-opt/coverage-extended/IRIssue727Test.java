package com.google.javascript.rhino;

import junit.framework.TestCase;

public class IRIssue727Test extends TestCase {

  public void testTryFinallyAcceptsEmptyBlockBodiesAndCreatesCatchPlaceholder() {
    Node tryBody = IR.block();
    Node finallyBody = IR.block();

    Node tryNode = IR.tryFinally(tryBody, finallyBody);

    assertTrue(tryNode.isTry());
    assertEquals(3, tryNode.getChildCount());
    assertSame(tryBody, tryNode.getFirstChild());

    Node catchPlaceholder = tryBody.getNext();
    assertTrue(catchPlaceholder.isBlock());
    assertNull(catchPlaceholder.getFirstChild());

    assertSame(finallyBody, catchPlaceholder.getNext());
    assertSame(finallyBody, tryNode.getLastChild());
  }

  public void testTryCatchFinallyRetainsAllSuppliedBodies() {
    Node tryBody = IR.block(IR.exprResult(IR.name("work")));
    Node catchBody = IR.block(IR.exprResult(IR.name("recover")));
    Node catchNode = IR.catchNode(IR.name("e"), catchBody);
    Node finallyBody = IR.block(IR.exprResult(IR.name("cleanup")));

    Node tryNode = IR.tryCatchFinally(tryBody, catchNode, finallyBody);

    assertTrue(tryNode.isTry());
    assertEquals(3, tryNode.getChildCount());
    assertSame(tryBody, tryNode.getFirstChild());

    Node catchContainer = tryBody.getNext();
    assertTrue(catchContainer.isBlock());
    assertSame(catchNode, catchContainer.getFirstChild());
    assertSame(finallyBody, catchContainer.getNext());
  }

  public void testTryFinallyRejectsLabelNamesInsteadOfBlockBodies() {
    try {
      IR.tryFinally(IR.labelName("notATryBody"), IR.labelName("notAFinallyBody"));
      fail("try/finally bodies must be blocks");
    } catch (IllegalStateException expected) {
      // Expected.
    }
  }
}
