package com.google.javascript.jscomp;

import com.google.common.base.Supplier;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import java.util.Collections;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionInjectorRegressionTest {

  @Test
  public void mutableArgumentReferencedOnceCanBeDirectlyInlined() {
    Node function = functionReturning("f", "a", name("a"));
    Node call = call("f", assignment("x", number(1)));

    assertEquals(
        FunctionInjector.CanInlineResult.YES,
        canInline(call, function));
  }

  @Test
  public void mutableArgumentCapturedByReturnedFunctionCannotBeDirectlyInlined() {
    Node capturedFunction = new Node(Token.FUNCTION);
    capturedFunction.addChildToBack(name(""));
    capturedFunction.addChildToBack(new Node(Token.PARAM_LIST));
    Node capturedBody = new Node(Token.BLOCK);
    capturedBody.addChildToBack(new Node(Token.RETURN, name("a")));
    capturedFunction.addChildToBack(capturedBody);

    Node function = functionReturning("f", "a", capturedFunction);
    Node call = call("f", assignment("x", number(1)));

    assertEquals(
        FunctionInjector.CanInlineResult.NO,
        canInline(call, function));
  }

  @Test
  public void mutableArgumentCapturedByReturnedFunctionWithIncrementCannotBeDirectlyInlined() {
    Node capturedFunction = new Node(Token.FUNCTION);
    capturedFunction.addChildToBack(name(""));
    capturedFunction.addChildToBack(new Node(Token.PARAM_LIST));
    Node capturedBody = new Node(Token.BLOCK);
    capturedBody.addChildToBack(new Node(Token.RETURN, name("a")));
    capturedFunction.addChildToBack(capturedBody);

    Node function = functionReturning("f", "a", capturedFunction);
    Node call = call("f", new Node(Token.INC, name("x")));

    assertEquals(
        FunctionInjector.CanInlineResult.NO,
        canInline(call, function));
  }

  @Test
  public void mutableArgumentReferencedMoreThanOnceCannotBeDirectlyInlined() {
    Node function = functionReturning("f", "a", new Node(Token.ADD, name("a"), name("a")));
    Node call = call("f", assignment("x", number(1)));

    assertEquals(
        FunctionInjector.CanInlineResult.NO,
        canInline(call, function));
  }

  @Test
  public void sideEffectingExtraArgumentPreventsDirectInlining() {
    Node function = functionReturning("f", "a", name("a"));
    Node call = call("f", number(0), assignment("x", number(1)));

    assertEquals(
        FunctionInjector.CanInlineResult.NO,
        canInline(call, function));
  }

  @Test
  public void functionObjectCallWithNonThisReceiverCannotBeDirectlyInlined() {
    Node function = functionReturning("f", "a", name("a"));
    Node callee = new Node(Token.GETPROP, name("f"), Node.newString("call"));
    Node call = new Node(Token.CALL);
    call.addChildToBack(callee);
    call.addChildToBack(name("receiver"));
    call.addChildToBack(number(1));

    assertEquals(
        FunctionInjector.CanInlineResult.NO,
        canInline(call, function));
  }

  private static FunctionInjector.CanInlineResult canInline(Node call, Node function) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.<SourceFile>emptyList(),
        new CompilerOptions());

    FunctionInjector injector =
        new FunctionInjector(
            compiler,
            new Supplier<String>() {
              @Override
              public String get() {
                return "inline";
              }
            },
            true,
            false,
            false);

    return injector.canInlineReferenceToFunction(
        null,
        call,
        function,
        Collections.<String>emptySet(),
        FunctionInjector.InliningMode.DIRECT,
        false,
        false);
  }

  private static Node functionReturning(String functionName, String parameterName, Node expression) {
    Node function = new Node(Token.FUNCTION);
    function.addChildToBack(name(functionName));

    Node parameters = new Node(Token.PARAM_LIST);
    parameters.addChildToBack(name(parameterName));
    function.addChildToBack(parameters);

    Node body = new Node(Token.BLOCK);
    body.addChildToBack(new Node(Token.RETURN, expression));
    function.addChildToBack(body);
    return function;
  }

  private static Node call(String functionName, Node... arguments) {
    Node call = new Node(Token.CALL);
    call.addChildToBack(name(functionName));
    for (Node argument : arguments) {
      call.addChildToBack(argument);
    }
    return call;
  }

  private static Node assignment(String variable, Node value) {
    return new Node(Token.ASSIGN, name(variable), value);
  }

  private static Node name(String value) {
    return new Node(Token.NAME, value);
  }

  private static Node number(double value) {
    return Node.newNumber(value);
  }
}