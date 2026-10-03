package com.google.javascript.jscomp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.ReferenceCollectingCallback.ReferenceCollection;
import com.google.javascript.rhino.Node;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ReferenceCollectingCallbackGeneratedTest {

  private final Map<String, ReferenceCollection> collections =
      new HashMap<String, ReferenceCollection>();

  private ReferenceCollection collect(String source, String name) {
    collections.clear();

    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    compiler.parseInputs();

    new ReferenceCollectingCallback(
        compiler,
        new ReferenceCollectingCallback.Behavior() {
          @Override
          public void afterExitScope(
              NodeTraversal t,
              Map<Scope.Var, ReferenceCollection> referenceMap) {
            for (Map.Entry<Scope.Var, ReferenceCollection> entry
                : referenceMap.entrySet()) {
              collections.put(entry.getKey().getName(), entry.getValue());
            }
          }
        }).process(null, compiler.getRoot());

    ReferenceCollection collection = collections.get(name);
    assertNotNull("Expected references for " + name, collection);
    return collection;
  }

  @Test
  public void testInitializedVariableHasInitializationReference() {
    ReferenceCollection collection = collect("var value = 1; value;", "value");

    assertNotNull(collection.getInitializingReference());
    assertTrue(collection.firstReferenceIsAssigningDeclaration());
    assertTrue(collection.isAssignedOnceInLifetime());
    assertFalse(collection.isNeverAssigned());
  }

  @Test
  public void testUninitializedDeclarationIsNotAnAssignment() {
    ReferenceCollection collection = collect("var value; value;", "value");

    assertNull(collection.getInitializingReference());
    assertFalse(collection.firstReferenceIsAssigningDeclaration());
    assertTrue(collection.isNeverAssigned());
    assertFalse(collection.isAssignedOnceInLifetime());
  }

  @Test
  public void testAssignmentInLoopIsNotAssignedOnceInLifetime() {
    ReferenceCollection collection = collect(
        "var value; while (true) { value = 1; break; }", "value");

    assertNotNull(collection.getInitializingReference());
    assertFalse(collection.isNeverAssigned());
    assertFalse(collection.isAssignedOnceInLifetime());
  }

  @Test
  public void testLoopAliasDeclarationIsNotTreatedAsLifetimeSingleAssignment() {
    ReferenceCollection collection = collect(
        "var value; while (true) { var alias = value; value = alias; break; }",
        "alias");

    assertNotNull(collection.getInitializingReference());
    assertTrue(collection.firstReferenceIsAssigningDeclaration());
    assertFalse(collection.isNeverAssigned());
    assertFalse(collection.isAssignedOnceInLifetime());
  }
}
