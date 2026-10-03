package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerOptions.AliasTransformation;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformationHandler;
import com.google.javascript.rhino.SourcePosition;
import org.junit.Before;
import org.junit.Test;

public class ScopedAliasesBug24Test extends CompilerTestCase {

  private static final AliasTransformationHandler NO_OP_ALIAS_HANDLER =
      new AliasTransformationHandler() {
        @Override
        public AliasTransformation logAliasTransformation(
            String sourceFile, SourcePosition<AliasTransformation> position) {
          return new AliasTransformation() {
            @Override
            public void addAlias(String alias, String definition) {}
          };
        }
      };

  public ScopedAliasesBug24Test() {
    super("", true);
  }

  @Before
  public void initializeCompilerTestCase() throws Exception {
    super.setUp();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new ScopedAliases(compiler, null, NO_OP_ALIAS_HANDLER);
  }

  @Test
  public void rejectsUninitializedNonAliasLocal() {
    testSame(
        "goog.scope(function() { var local; });",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
  }

  @Test
  public void rejectsInitializedNonAliasLocal() {
    testSame(
        "goog.scope(function() { var local = 1; });",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
  }

  @Test
  public void rewritesAndRemovesValidAlias() {
    test(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  @Test
  public void rewritesTransitiveAliasUsage() {
    test(
        "goog.scope(function() {"
            + "  var g = goog;"
            + "  var dom = g.dom;"
            + "  dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  @Test
  public void rewritesAliasUseInNestedFunction() {
    test(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  function makeElement() { return dom.createElement('DIV'); }"
            + "});",
        "function makeElement() { return goog.dom.createElement('DIV'); }");
  }

  @Test
  public void rejectsAliasReassignmentAtScopeTopLevel() {
    testSame(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  dom = other;"
            + "});",
        ScopedAliases.GOOG_SCOPE_ALIAS_REDEFINED);
  }

  @Test
  public void rejectsScopeCallUsedAsExpressionValue() {
    testSame(
        "var value = goog.scope(function() {});",
        ScopedAliases.GOOG_SCOPE_USED_IMPROPERLY);
  }

  @Test
  public void rejectsScopeCallWithoutFunctionArgument() {
    testSame(
        "goog.scope();",
        ScopedAliases.GOOG_SCOPE_HAS_BAD_PARAMETERS);
  }

  @Test
  public void leavesOrdinaryGlobalFunctionsUntouched() {
    testSame("function f() { var local = 1; return local; }");
  }
}