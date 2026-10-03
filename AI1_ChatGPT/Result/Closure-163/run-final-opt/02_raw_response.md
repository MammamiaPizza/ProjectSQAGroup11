package com.google.javascript.jscomp;

public class CrossModuleMethodMotionIssue600Test extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    CrossModuleMethodMotion.IdGenerator idGenerator =
        new CrossModuleMethodMotion.IdGenerator();
    idGenerator.newId();
    return new CrossModuleMethodMotion(compiler, idGenerator, false);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  public void testMovesPrototypeMethodReferencedDirectlyInDependentModule() {
    test(
        new String[] {
          "function Foo() {} Foo.prototype.bar = function() {};",
          "Foo.prototype.bar();"
        },
        new String[] {
          "function Foo() {} Foo.prototype.bar = JSCompiler_stubMethod(1);",
          "Foo.prototype.bar = JSCompiler_unstubMethod(1, function() {});"
              + "Foo.prototype.bar();"
        });
  }

  public void testMovesPrototypeMethodReferencedByGlobalFunctionCalledInDependentModule() {
    test(
        new String[] {
          "function Foo() {}"
              + "Foo.prototype.bar = function() {};"
              + "function useBar() { Foo.prototype.bar(); }",
          "useBar();"
        },
        new String[] {
          "function Foo() {}"
              + "Foo.prototype.bar = JSCompiler_stubMethod(1);"
              + "function useBar() { Foo.prototype.bar(); }",
          "Foo.prototype.bar = JSCompiler_unstubMethod(1, function() {});"
              + "useBar();"
        });
  }

  public void testMovesPrototypeMethodReferencedByGlobalFunctionExpressionCalledInDependentModule() {
    test(
        new String[] {
          "function Foo() {}"
              + "Foo.prototype.bar = function() {};"
              + "var useBar = function() { Foo.prototype.bar(); };",
          "useBar();"
        },
        new String[] {
          "function Foo() {}"
              + "Foo.prototype.bar = JSCompiler_stubMethod(1);"
              + "var useBar = function() { Foo.prototype.bar(); };",
          "Foo.prototype.bar = JSCompiler_unstubMethod(1, function() {});"
              + "useBar();"
        });
  }

  public void testDoesNotMoveMethodWhenItIsUsedInItsOriginalModule() {
    test(
        new String[] {
          "function Foo() {}"
              + "Foo.prototype.bar = function() {};"
              + "Foo.prototype.bar();",
          "Foo.prototype.bar();"
        },
        new String[] {
          "function Foo() {}"
              + "Foo.prototype.bar = function() {};"
              + "Foo.prototype.bar();",
          "Foo.prototype.bar();"
        });
  }
}