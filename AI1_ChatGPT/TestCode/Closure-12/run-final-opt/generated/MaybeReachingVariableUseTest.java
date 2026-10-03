package com.google.javascript.jscomp;

import com.google.javascript.jscomp.MaybeReachingVariableUse.ReachingUses;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class MaybeReachingVariableUseTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new FlowSensitiveInlineVariables(compiler);
  }

  @Test
  public void testReachingUsesEmptyInstancesAreEqualAndHaveSameHashCode() {
    ReachingUses first = new ReachingUses();
    ReachingUses second = new ReachingUses();

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertFalse(first.equals(null));
    assertFalse(first.equals("uses"));
  }

  @Test
  public void testReachingUsesCopyPreservesEntriesAndIsIndependent() {
    ReachingUses original = new ReachingUses();
    Node use = new Node(Token.NAME);
    original.mayUseMap.put(null, use);

    ReachingUses copy = new ReachingUses(original);

    assertEquals(original, copy);
    assertEquals(original.hashCode(), copy.hashCode());
    assertTrue(copy.mayUseMap.containsEntry(null, use));

    copy.mayUseMap.remove(null, use);

    assertFalse(original.equals(copy));
    assertTrue(original.mayUseMap.containsEntry(null, use));
    assertFalse(copy.mayUseMap.containsEntry(null, use));
  }

  @Test
  public void testAssignmentThatCanThrowInTryIsNotInlinedIntoFinally() {
    testSame(
        "function mayThrow() {}"
            + "function f() {"
            + "  var x = 1;"
            + "  try {"
            + "    x = mayThrow();"
            + "  } finally {"
            + "    x;"
            + "  }"
            + "}");
  }

  @Test
  public void testAssignmentThatCanThrowInTryIsNotInlinedIntoCatch() {
    testSame(
        "function mayThrow() {}"
            + "function f() {"
            + "  var x = 1;"
            + "  try {"
            + "    x = mayThrow();"
            + "  } catch (e) {"
            + "    x;"
            + "  }"
            + "}");
  }
}
