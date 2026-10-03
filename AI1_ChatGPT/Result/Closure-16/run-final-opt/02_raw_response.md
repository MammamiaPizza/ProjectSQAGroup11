package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerOptions.AliasTransformation;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformationHandler;
import com.google.javascript.rhino.SourcePosition;

public class ScopedAliasesGeneratedTest extends CompilerTestCase {

  private static final AliasTransformationHandler NO_OP_ALIAS_TRANSFORMATION_HANDLER =
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

  public ScopedAliasesGeneratedTest() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new ScopedAliases(
        compiler, null, NO_OP_ALIAS_TRANSFORMATION_HANDLER);
  }

  public void testRewritesSimpleAliasAndRemovesScope() {
    test(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  public void testRewritesTransitiveAliases() {
    test(
        "goog.scope(function() {"
            + "  var g = goog;"
            + "  var dom = g.dom;"
            + "  dom.createElement('DIV');"
            + "});",
        "goog.dom.createElement('DIV');");
  }

  public void testRewritesAliasesInJSDocTypes() {
    test(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  /** @param {dom.Helper} helper */"
            + "  dom.use = function(helper) {};"
            + "});",
        "/** @param {goog.dom.Helper} helper */"
            + "goog.dom.use = function(helper) {};");
  }

  public void testRejectsNonAliasLocalInScope() {
    testError(
        "goog.scope(function() {"
            + "  var local = 1;"
            + "});",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
  }

  public void testRejectsAliasRedefinition() {
    testError(
        "goog.scope(function() {"
            + "  var dom = goog.dom;"
            + "  dom = goog.other;"
            + "});",
        ScopedAliases.GOOG_SCOPE_ALIAS_REDEFINED);
  }

  public void testRejectsScopeFunctionWithParameters() {
    testError(
        "goog.scope(function(value) {"
            + "});",
        ScopedAliases.GOOG_SCOPE_HAS_BAD_PARAMETERS);
  }
}