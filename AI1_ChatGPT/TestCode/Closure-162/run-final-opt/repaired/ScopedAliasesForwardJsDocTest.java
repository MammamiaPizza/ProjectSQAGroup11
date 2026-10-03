package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.jscomp.CompilerOptions.AliasTransformation;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformationHandler;
import com.google.javascript.rhino.SourcePosition;
import java.util.Collections;
import org.junit.Test;

public class ScopedAliasesForwardJsDocTest {

  private static final AliasTransformationHandler NULL_ALIAS_TRANSFORMATION_HANDLER =
      new AliasTransformationHandler() {
        @Override
        public AliasTransformation logAliasTransformation(
            String sourceFile, SourcePosition<AliasTransformation> position) {
          return new AliasTransformation() {
            @Override
            public void addAlias(String alias, String definition) {
            }
          };
        }
      };

  protected CompilerPass getProcessor(Compiler compiler) {
    return new ScopedAliases(
        compiler, null, NULL_ALIAS_TRANSFORMATION_HANDLER);
  }

  private void test(String input, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", input)),
        new CompilerOptions());
    compiler.parseInputs();

    assertEquals(0, compiler.getErrorCount());

    getProcessor(compiler).process(compiler.getExternsRoot(), compiler.getJsRoot());

    assertEquals(expected, compiler.toSource());
  }

  @Test
  public void testRewritesJsDocTypeWhenAliasDeclaredBeforeUse() {
    test(
        "goog.scope(function() {\n"
            + "  var Foo = foo.Foo;\n"
            + "  function f() {\n"
            + "    /** @type {Foo.Bar} */\n"
            + "    var value;\n"
            + "  }\n"
            + "});",
        "function f() {\n"
            + "  /** @type {foo.Foo.Bar} */\n"
            + "  var value;\n"
            + "}");
  }

  @Test
  public void testRewritesForwardJsDocTypeReference() {
    test(
        "goog.scope(function() {\n"
            + "  function f() {\n"
            + "    /** @type {Foo.Bar} */\n"
            + "    var value;\n"
            + "  }\n"
            + "  var Foo = foo.Foo;\n"
            + "});",
        "function f() {\n"
            + "  /** @type {foo.Foo.Bar} */\n"
            + "  var value;\n"
            + "}");
  }

  @Test
  public void testRewritesForwardJsDocThroughTransitiveAliases() {
    test(
        "goog.scope(function() {\n"
            + "  function f() {\n"
            + "    /** @param {Bar.Baz} value */\n"
            + "    function use(value) {}\n"
            + "  }\n"
            + "  var Foo = foo.Foo;\n"
            + "  var Bar = Foo.Bar;\n"
            + "});",
        "function f() {\n"
            + "  /** @param {foo.Foo.Bar.Baz} value */\n"
            + "  function use(value) {}\n"
            + "}");
  }
}
