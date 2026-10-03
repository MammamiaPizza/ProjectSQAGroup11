package com.google.javascript.jscomp.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.google.javascript.jscomp.CodingConvention;
import com.google.javascript.jscomp.CodingConventions;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeNative;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.StaticSlot;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.junit.Test;

public class ChainableReverseAbstractInterpreterTest {

  private static final class TestInterpreter
      extends ChainableReverseAbstractInterpreter {
    TestInterpreter(CodingConvention convention, JSTypeRegistry registry) {
      super(convention, registry);
    }

    @Override
    public FlowScope getPreciserScopeKnowingConditionOutcome(
        Node condition, FlowScope blindScope, boolean outcome) {
      return blindScope;
    }
  }

  private TestInterpreter newInterpreter(JSTypeRegistry registry) {
    return new TestInterpreter(CodingConventions.getDefault(), registry);
  }

  private FlowScope scopeWithSlot(final String expectedName, final JSType type) {
    final StaticSlot<JSType> slot =
        (StaticSlot<JSType>)
            Proxy.newProxyInstance(
                StaticSlot.class.getClassLoader(),
                new Class<?>[] {StaticSlot.class},
                new InvocationHandler() {
                  @Override
                  public Object invoke(Object proxy, Method method, Object[] args) {
                    if ("getType".equals(method.getName())) {
                      return type;
                    }
                    if ("getName".equals(method.getName())) {
                      return expectedName;
                    }
                    return null;
                  }
                });

    return (FlowScope)
        Proxy.newProxyInstance(
            FlowScope.class.getClassLoader(),
            new Class<?>[] {FlowScope.class},
            new InvocationHandler() {
              @Override
              public Object invoke(Object proxy, Method method, Object[] args) {
                if ("getSlot".equals(method.getName())
                    && args != null
                    && args.length == 1
                    && expectedName.equals(args[0])) {
                  return slot;
                }
                return null;
              }
            });
  }

  private FlowScope scopeWithoutSlots() {
    return (FlowScope)
        Proxy.newProxyInstance(
            FlowScope.class.getClassLoader(),
            new Class<?>[] {FlowScope.class},
            new InvocationHandler() {
              @Override
              public Object invoke(Object proxy, Method method, Object[] args) {
                return null;
              }
            });
  }

  @Test
  public void nonRefinableThisDoesNotRequireAScope() {
    JSTypeRegistry registry = new JSTypeRegistry(null);
    TestInterpreter interpreter = newInterpreter(registry);

    assertNull(interpreter.getTypeIfRefinable(new Node(Token.THIS), null));
    assertNull(interpreter.getTypeIfRefinable(new Node(Token.GETELEM), null));
  }

  @Test
  public void nameUsesTypeFromFlowScope() {
    JSTypeRegistry registry = new JSTypeRegistry(null);
    JSType number = registry.getNativeType(JSTypeNative.NUMBER_TYPE);
    Node name = Node.newString(Token.NAME, "value");
    name.setJSType(registry.getNativeType(JSTypeNative.STRING_TYPE));

    assertSame(
        number,
        newInterpreter(registry).getTypeIfRefinable(name, scopeWithSlot("value", number)));
  }

  @Test
  public void nameFallsBackToNodeTypeWhenScopeSlotHasNoType() {
    JSTypeRegistry registry = new JSTypeRegistry(null);
    JSType string = registry.getNativeType(JSTypeNative.STRING_TYPE);
    Node name = Node.newString(Token.NAME, "value");
    name.setJSType(string);

    assertSame(
        string,
        newInterpreter(registry).getTypeIfRefinable(name, scopeWithSlot("value", null)));
  }

  @Test
  public void qualifiedPropertyUsesTypeFromFlowScope() {
    JSTypeRegistry registry = new JSTypeRegistry(null);
    JSType booleanType = registry.getNativeType(JSTypeNative.BOOLEAN_TYPE);
    Node property =
        new Node(
            Token.GETPROP,
            Node.newString(Token.NAME, "object"),
            Node.newString(Token.STRING, "flag"));

    assertSame(
        booleanType,
        newInterpreter(registry)
            .getTypeIfRefinable(property, scopeWithSlot("object.flag", booleanType)));
  }

  @Test
  public void qualifiedPropertyWithoutAnyTypeFallsBackToUnknown() {
    JSTypeRegistry registry = new JSTypeRegistry(null);
    Node property =
        new Node(
            Token.GETPROP,
            Node.newString(Token.NAME, "object"),
            Node.newString(Token.STRING, "missing"));

    assertSame(
        registry.getNativeType(JSTypeNative.UNKNOWN_TYPE),
        newInterpreter(registry).getTypeIfRefinable(property, scopeWithoutSlots()));
  }

  @Test
  public void compilerCanInferThroughAThisConditionWithoutThrowing() {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setCheckTypes(true);

    Result result =
        compiler.compile(
            SourceFile.fromCode("externs.js", ""),
            SourceFile.fromCode(
                "input.js",
                "function noThisInference() {"
                    + "  if (this) { return 1; }"
                    + "  return 0;"
                    + "}"),
            options);

    assertEquals(0, result.errors.length);
  }
}