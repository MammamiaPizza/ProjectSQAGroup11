package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerOptions.AliasTransformation;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformationHandler;
import com.google.javascript.rhino.SourcePosition;
import org.junit.Test;

public class ScopedAliasesIssue1144Test extends CompilerTestCase {

  private static final AliasTransformationHandler ALIAS_TRANSFORMATION_HANDLER =
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

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new ScopedAliases(compiler, null, ALIAS_TRANSFORMATION_HANDLER);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testExpandsSimpleAliasAndRemovesScope() {
    test(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  @Test
  public void testExpandsTransitiveAliasesInDependencyOrder() {
    test(
        "goog.scope(function() {"
            + "  var g = goog;"
            + "  var dom = g.dom;"
            + "  dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  @Test
  public void testExpandsAliasInTypeAnnotation() {
    test(
        "goog.scope(function() {"
            + "  var EventType = goog.ui.Component.EventType;"
            + "  /** @type {EventType} */"
            + "  var value;"
            + "});",
        "/** @type {goog.ui.Component.EventType} */ var value;");
  }

  @Test
  public void testExpandsQualifiedAliasInTypeAnnotation() {
    test(
        "goog.scope(function() {"
            + "  var Component = goog.ui.Component;"
            + "  /** @type {Component.EventType} */"
            + "  var value;"
            + "});",
        "/** @type {goog.ui.Component.EventType} */ var value;");
  }

  @Test
  public void testExpandsAliasReferencesInsideNestedFunctions() {
    test(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  function create() {"
            + "    return dom.createElement('DIV');"
            + "  }"
            + "  create();"
            + "});",
        "function create() {"
            + "  return goog.dom.createElement('DIV');"
            + "}"
            + "create();");
  }

  @Test
  public void testScopeWithoutAliasesIsStillCollapsed() {
    test(
        "goog.scope(function() {"
            + "  goog.dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  @Test
  public void testScopeFunctionWithParametersIsRejected() {
    testError(
        "goog.scope(function(value) {"
            + "  goog.dom.createElement(value);"
            + "});",
        ScopedAliases.GOOG_SCOPE_HAS_BAD_PARAMETERS);
  }

  @Test
  public void testReturnInsideScopeIsRejected() {
    testError(
        "goog.scope(function() {"
            + "  return;"
            + "});",
        ScopedAliases.GOOG_SCOPE_USES_RETURN);
  }
}
