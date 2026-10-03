package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class RemoveConstantExpressionsRegressionTest extends CompilerTestCase {

  public RemoveConstantExpressionsRegressionTest() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new RemoveConstantExpressions(compiler);
  }

  @Test
  public void testCallInDiscardedAdditionIsPreserved() {
    test("foo() + 1;", "foo();");
  }

  @Test
  public void testNewInDiscardedAdditionIsPreserved() {
    test("new foo() + 1;", "new foo();");
  }

  @Test
  public void testMultipleSideEffectingCallsAreRetainedInOrder() {
    test("1 + foo() + bar();", "foo();bar();");
  }

  @Test
  public void testPureExpressionStatementIsRemoved() {
    test("1 + 2;", "");
  }

  @Test
  public void testCallArgumentsNeededByCallAreNotRemoved() {
    testSame("foo(bar());");
  }

  @Test
  public void testDirectNewExpressionIsNotRemoved() {
    testSame("new Foo();");
  }
}

abstract class CompilerTestCase extends Assert {
  CompilerTestCase(String name) {
  }

  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void test(String input, String expected) {
    Assert.assertEquals(parse(expected), transform(input));
  }

  protected void testSame(String input) {
    test(input, input);
  }

  private String transform(String source) {
    Compiler compiler = new Compiler();
    Node root = parse(compiler, source);
    Node externs = root.getFirstChild();
    Node js = root.getLastChild();
    getProcessor(compiler).process(externs, js);
    return compiler.toSource(js);
  }

  private String parse(String source) {
    Compiler compiler = new Compiler();
    Node root = parse(compiler, source);
    return compiler.toSource(root.getLastChild());
  }

  private Node parse(Compiler compiler, String source) {
    List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
    List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
    inputs.add(JSSourceFile.fromCode("testcode", source));
    compiler.init(externs, inputs, new CompilerOptions());
    return compiler.parseInputs();
  }

@Test
public void testCallNestedInDiscardedArithmeticIsPreserved() {
  String input = "1 + 2 * foo();";
  String expected = "foo();";
  Assert.assertEquals(parse(expected), transform(input));
}

@Test
public void testNewNestedInDiscardedArithmeticIsPreserved() {
  String input = "1 + 2 * new Foo();";
  String expected = "new Foo();";
  Assert.assertEquals(parse(expected), transform(input));
}
}
