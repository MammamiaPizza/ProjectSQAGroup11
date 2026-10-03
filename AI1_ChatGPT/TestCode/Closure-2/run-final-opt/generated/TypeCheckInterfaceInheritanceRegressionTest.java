package com.google.javascript.jscomp;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TypeCheckInterfaceInheritanceRegressionTest {

  @Test
  public void testNonExistentExtendedInterfacesDoNotCrashTypeChecking() {
    TypeCheckTest harnessTest = new TypeCheckTest();
    harnessTest.setName("testBadInterfaceExtendsNonExistentInterfaces");

    Throwable failure = null;
    try {
      harnessTest.runBare();
    } catch (Throwable t) {
      failure = t;
    }

    assertNull(
        "Type checking an interface that extends nonexistent interfaces must complete "
            + "and preserve the harness's expected diagnostic behavior.",
        failure);
  }
}
