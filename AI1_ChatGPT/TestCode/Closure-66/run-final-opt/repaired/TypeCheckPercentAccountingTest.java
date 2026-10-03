package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public class TypeCheckPercentAccountingTest {
  private TypeCheck typeCheck;

  private void testSame(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.singletonList(
            SourceFile.fromCode("externs.js", "/** @constructor */ function Object() {}")),
        Collections.singletonList(SourceFile.fromCode("test.js", source)),
        new CompilerOptions());

    Node root = compiler.parseInputs();
    typeCheck =
        new TypeCheck(
            compiler,
            new SemanticReverseAbstractInterpreter(
                compiler.getCodingConvention(), compiler.getTypeRegistry()),
            compiler.getTypeRegistry(),
            CheckLevel.WARNING,
            CheckLevel.OFF);
    typeCheck.process(root.getFirstChild(), root.getLastChild());
  }

  @Test
  public void typedPercentIsCompleteForDotPropertyAssignment() {
    testSame("/** @type {Object} */ var x = {}; x.foo = 1;");

    assertEquals(100.0, typeCheck.getTypedPercent(), 0.0);
  }

  @Test
  public void typedPercentIsCompleteForBracketPropertyAssignment() {
    testSame("/** @type {Object} */ var x = {}; x['foo'] = 1;");

    assertEquals(100.0, typeCheck.getTypedPercent(), 0.0);
  }
}
