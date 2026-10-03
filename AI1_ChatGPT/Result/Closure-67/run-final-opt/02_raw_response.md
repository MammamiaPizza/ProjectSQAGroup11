package com.google.javascript.jscomp;

import org.junit.Test;

public class AnalyzePrototypePropertiesRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new RemoveUnusedPrototypeProperties(compiler, false);
  }

  @Test
  public void testPrototypePropertyUsedThroughPrototypeAliasIsRetained() {
    testSame(
        "function Foo() {}"
            + "Foo.prototype.foo = function() { this.bar(); };"
            + "Foo.prototype.bar = function() {};"
            + "var prototypeAlias = Foo.prototype;"
            + "prototypeAlias.foo();");
  }

  @Test
  public void testPrototypePropertyDependencyThroughAliasedThisIsRetained() {
    testSame(
        "function Foo() {}"
            + "Foo.prototype.foo = function() {"
            + "  var self = this;"
            + "  self.bar();"
            + "};"
            + "Foo.prototype.bar = function() {};"
            + "var instance = new Foo();"
            + "instance.foo();");
  }

  @Test
  public void testPrototypePropertyUsedThroughAliasChainIsRetained() {
    testSame(
        "function Foo() {}"
            + "Foo.prototype.foo = function() { this.bar(); };"
            + "Foo.prototype.bar = function() {};"
            + "var firstAlias = Foo.prototype;"
            + "var secondAlias = firstAlias;"
            + "secondAlias.foo();");
  }

  @Test
  public void testAliasedPrototypeMethodReferenceRetainsDependencies() {
    testSame(
        "function Foo() {}"
            + "Foo.prototype.foo = function() { this.bar(); };"
            + "Foo.prototype.bar = function() {};"
            + "var method = Foo.prototype.foo;"
            + "method();");
  }

  @Test
  public void testGlobalFunctionAssignmentParticipatesInPrototypeReferencePropagation() {
    testSame(
        "var Foo = function() {};"
            + "Foo.prototype.foo = function() { Foo.prototype.bar(); };"
            + "Foo.prototype.bar = function() {};"
            + "var prototypeAlias = Foo.prototype;"
            + "prototypeAlias.foo();");
  }

  @Test
  public void testUnusedPrototypePropertyIsRemoved() {
    test(
        "function Foo() {} Foo.prototype.unused = function() {};",
        "function Foo() {}");
  }
}