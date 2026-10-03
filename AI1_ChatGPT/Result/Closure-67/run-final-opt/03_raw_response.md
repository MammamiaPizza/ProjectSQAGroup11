package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public class AnalyzePrototypePropertiesRegressionTest {

  private Compiler createCompiler(String source) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    return compiler;
  }

  private String print(String source) {
    Compiler compiler = createCompiler(source);
    compiler.parseInputs();
    return compiler.toSource();
  }

  private String process(String source) {
    Compiler compiler = createCompiler(source);
    Node root = compiler.parseInputs();
    new RemoveUnusedPrototypeProperties(compiler, false)
        .process(compiler.getExternsRoot(), root);
    return compiler.toSource();
  }

  private void testSame(String source) {
    assertEquals(print(source), process(source));
  }

  private void test(String source, String expected) {
    assertEquals(print(expected), process(source));
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