package com.google.javascript.jscomp;

import com.google.javascript.jscomp.Scope.Var;
import com.google.javascript.rhino.Node;
import java.util.Collections;
import java.util.Iterator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReferenceCollectingCallbackRegressionTest {
  private String symbolName;
  private ReferenceCollectingCallback.ReferenceCollection references;

  protected CompilerPass getProcessor(final Compiler compiler) {
    return new CompilerPass() {
      @Override
      public void process(Node externs, Node root) {
        ReferenceCollectingCallback callback =
            new ReferenceCollectingCallback(
                compiler,
                new ReferenceCollectingCallback.Behavior() {
                  @Override
                  public void afterExitScope(
                      NodeTraversal traversal,
                      ReferenceCollectingCallback.ReferenceMap referenceMap) {
                    Var var = traversal.getScope().getVar(symbolName);
                    if (var != null) {
                      ReferenceCollectingCallback.ReferenceCollection found =
                          referenceMap.getReferences(var);
                      if (found != null) {
                        references = found;
                      }
                    }
                  }
                });
        callback.process(externs, root);
      }
    };
  }

  private void testSame(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    getProcessor(compiler).process(null, compiler.parseInputs());
  }

  private ReferenceCollectingCallback.ReferenceCollection collect(
      String name, String source) {
    symbolName = name;
    references = null;
    testSame(source);
    assertNotNull("No references were collected for " + name, references);
    return references;
  }

  private static int size(ReferenceCollectingCallback.ReferenceCollection collection) {
    int count = 0;
    for (Iterator<ReferenceCollectingCallback.Reference> it = collection.iterator();
        it.hasNext();) {
      it.next();
      count++;
    }
    return count;
  }

  @Test
  public void testInitializingDeclarationIsRecognized() {
    ReferenceCollectingCallback.ReferenceCollection collection =
        collect("x", "function f(){var x=1;return x;}");

    assertEquals(2, size(collection));
    assertNotNull(collection.getInitializingReference());
    assertTrue(collection.firstReferenceIsAssigningDeclaration());
    assertTrue(collection.isAssignedOnceInLifetime());
    assertFalse(collection.isNeverAssigned());
    assertTrue(collection.isWellDefined());
  }

  @Test
  public void testAssignmentFollowingUninitializedDeclarationIsInitialization() {
    ReferenceCollectingCallback.ReferenceCollection collection =
        collect("x", "function f(){var x;x=1;return x;}");

    assertEquals(3, size(collection));
    assertNotNull(collection.getInitializingReference());
    assertFalse(collection.firstReferenceIsAssigningDeclaration());
    assertTrue(collection.isAssignedOnceInLifetime());
    assertTrue(collection.isWellDefined());
  }

  @Test
  public void testConditionalAssignmentDoesNotMakeVariableWellDefined() {
    ReferenceCollectingCallback.ReferenceCollection collection =
        collect("x", "function f(c){var x;if(c){x=1;}return x;}");

    assertEquals(3, size(collection));
    assertNotNull(collection.getInitializingReference());
    assertFalse(collection.isWellDefined());
  }

  @Test
  public void testAssignmentInsideLoopIsNotAssignedOnceInLifetime() {
    ReferenceCollectingCallback.ReferenceCollection collection =
        collect("x", "function f(c){var x;while(c){x=1;}return x;}");

    assertEquals(3, size(collection));
    assertFalse(collection.isAssignedOnceInLifetime());
  }

  @Test
  public void testMultipleAssignmentsAreNotAssignedOnce() {
    ReferenceCollectingCallback.ReferenceCollection collection =
        collect("x", "function f(){var x=0;x=1;return x;}");

    assertEquals(3, size(collection));
    assertFalse(collection.isAssignedOnceInLifetime());
    assertFalse(collection.isNeverAssigned());
  }

  @Test
  public void testUninitializedVariableIsNeverAssigned() {
    ReferenceCollectingCallback.ReferenceCollection collection =
        collect("x", "function f(){var x;return x;}");

    assertEquals(2, size(collection));
    assertNull(collection.getInitializingReference());
    assertTrue(collection.isNeverAssigned());
    assertFalse(collection.isAssignedOnceInLifetime());
  }

  @Test
  public void testReferenceFromNestedFunctionIsEscaped() {
    ReferenceCollectingCallback.ReferenceCollection collection =
        collect("x", "function f(){var x=1;function g(){return x;}return x;}");

    assertEquals(3, size(collection));
    assertTrue(collection.isEscaped());
  }

  @Test
  public void testReferenceInLogicalRightHandSideIsCollectedAcrossBlockBoundary() {
    ReferenceCollectingCallback.ReferenceCollection collection =
        collect("x", "function f(c){var x=0;c&&(x=1);return x;}");

    assertEquals(3, size(collection));
    assertFalse(collection.isAssignedOnceInLifetime());
    assertFalse(collection.isNeverAssigned());
  }
}
