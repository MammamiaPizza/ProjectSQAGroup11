package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import org.junit.Test;

public class CompilerReportCodeChangeTest {

  private static final class ChangeCountingCompiler extends Compiler {
    int codeChangeCount;

    @Override
    public void reportCodeChange() {
      codeChangeCount++;
      super.reportCodeChange();
    }
  }

  @Test
  public void testEmptyLeadingModuleDoesNotReportCodeChange() {
    JSModule empty = module("empty", null);
    JSModule source = module("source", "var untouched = 1;");
    source.addDependency(empty);

    ChangeCountingCompiler compiler = runPass(empty, source);

    assertEquals(
        "A module with no inputs must not cause CrossModuleCodeMotion to report a change.",
        0,
        compiler.codeChangeCount);
  }

  @Test
  public void testEmptyTrailingModuleDoesNotReportCodeChange() {
    JSModule source = module("source", "var untouched = 1;");
    JSModule empty = module("empty", null);
    empty.addDependency(source);

    ChangeCountingCompiler compiler = runPass(source, empty);

    assertEquals(
        "An empty dependent module must not make a no-op optimization report a change.",
        0,
        compiler.codeChangeCount);
  }

  @Test
  public void testOnlyEmptyModulesDoNotReportCodeChange() {
    JSModule first = module("first", null);
    JSModule second = module("second", null);
    second.addDependency(first);

    ChangeCountingCompiler compiler = runPass(first, second);

    assertEquals(
        "Running CrossModuleCodeMotion over modules with no inputs is a no-op.",
        0,
        compiler.codeChangeCount);
  }

  @Test
  public void testActualCrossModuleMotionReportsCodeChange() {
    JSModule declaration = module("declaration", "function moved() { return 1; }");
    JSModule use = module("use", "moved();");
    use.addDependency(declaration);

    ChangeCountingCompiler compiler = runPass(declaration, use);

    assertTrue(
        "Moving a declaration needed only by a dependent module must report a code change.",
        compiler.codeChangeCount > 0);
  }

  private static JSModule module(String name, String source) {
    JSModule module = new JSModule(name);
    if (source != null) {
      module.add(new JSSourceFile(name + ".js", source));
    }
    return module;
  }

  private static ChangeCountingCompiler runPass(JSModule... modules) {
    ChangeCountingCompiler compiler = new ChangeCountingCompiler();
    compiler.init(new JSSourceFile[0], modules, new CompilerOptions());
    compiler.parse();

    assertEquals("Test inputs must parse without compiler errors.", 0, compiler.getErrorCount());

    Node root = compiler.getRoot();
    new CrossModuleCodeMotion(compiler, false)
        .process(root.getFirstChild(), root.getLastChild());
    return compiler;
  }
}
