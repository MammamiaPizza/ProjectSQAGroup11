package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.MaybeReachingVariableUse.ReachingUses;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import org.junit.Test;

public class MaybeReachingVariableUseTest {

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
  public void testReachingUsesCopyPreservesEntriesAndIsIndependent() throws Exception {
    ReachingUses original = new ReachingUses();
    Node use = new Node(Token.NAME);
    put(original, null, use);

    ReachingUses copy = new ReachingUses(original);

    assertEquals(original, copy);
    assertEquals(original.hashCode(), copy.hashCode());
    assertTrue(containsEntry(copy, null, use));

    remove(copy, null, use);

    assertFalse(original.equals(copy));
    assertTrue(containsEntry(original, null, use));
    assertFalse(containsEntry(copy, null, use));
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

  private void testSame(String source) {
    Compiler baselineCompiler = new Compiler();
    baselineCompiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("test", source)),
        new CompilerOptions());
    Node baselineRoot = baselineCompiler.parseInputs();
    String expected = baselineCompiler.toSource(baselineRoot);

    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("test", source)),
        new CompilerOptions());
    Node root = compiler.parseInputs();
    new FlowSensitiveInlineVariables(compiler).process(null, root);

    assertEquals(expected, compiler.toSource(root));
  }

  private static void put(ReachingUses uses, Object key, Node value) throws Exception {
    mapMethod(uses, "put").invoke(map(uses), key, value);
  }

  private static void remove(ReachingUses uses, Object key, Node value) throws Exception {
    mapMethod(uses, "remove").invoke(map(uses), key, value);
  }

  private static boolean containsEntry(ReachingUses uses, Object key, Node value) throws Exception {
    return ((Boolean) mapMethod(uses, "containsEntry").invoke(map(uses), key, value)).booleanValue();
  }

  private static Object map(ReachingUses uses) throws Exception {
    Field field = ReachingUses.class.getDeclaredField("mayUseMap");
    field.setAccessible(true);
    return field.get(uses);
  }

  private static Method mapMethod(ReachingUses uses, String name) throws Exception {
    return map(uses).getClass().getMethod(name, Object.class, Object.class);
  }
}
