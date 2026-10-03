package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import org.junit.Test;

public final class NameAnalyzerAssignmentCallRegressionTest {

  private Compiler createCompiler(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("test", source)),
        new CompilerOptions());
    return compiler;
  }

  private String compile(String source) {
    Compiler compiler = createCompiler(source);
    compiler.parseInputs();
    return compiler.toSource();
  }

  private String analyze(String source) {
    Compiler compiler = createCompiler(source);
    compiler.parseInputs();
    new NameAnalyzer(compiler, true)
        .process(compiler.getExternsRoot(), compiler.getJsRoot());
    return compiler.toSource();
  }

  @Test
  public void testUnusedTopLevelAssignmentWithCallPreservesCall() {
    assertEquals(
        compile("foo();"),
        analyze("var x; x = foo();"));
  }

  @Test
  public void testAssignmentWithCallUsedAsInitializerPreservesValue() {
    assertEquals(
        compile("var y = foo(); use(y);"),
        analyze("var x; var y = (x = foo()); use(y);"));
  }
}
