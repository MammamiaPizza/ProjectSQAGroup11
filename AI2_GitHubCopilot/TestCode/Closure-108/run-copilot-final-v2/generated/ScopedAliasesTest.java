package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformation;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformationHandler;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.SourceFile;
import com.google.javascript.rhino.SourcePosition;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class ScopedAliasesTest {

  private Compiler compiler; private class TestAliasTransformation implements AliasTransformation {
    private final Map<String, String> aliases = Maps.newHashMap();
    @Override
    public void addAlias(String name, String qualifiedName) {
      aliases.put(name, qualifiedName);
    }
    public String get(String name) { return aliases.get(name); }
    @Override
    public String toString() { return aliases.toString(); } }

  private AliasTransformationHandler handler = new AliasTransformationHandler() {
    @Override
    public AliasTransformation logAliasTransformation(String sourceFile,
SourcePosition<AliasTransformation> position) {
      return new TestAliasTransformation();
    } };

  @Before public void setUp() {
    compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setCheckSymbols(true));
    options.setCheckTypes(true));
    options.setClosurePass(true)); }

  private ScopedAliases createPass() {
    return new ScopedAliases(compiler, null, handler); }

  private void runPass(String js) {
    compiler.init(ImmutableList.of(SourceFile.fromCode("testcode", js)), new CompilerOptions());
    Node root = compiler.getRoot();
    Node externs = compiler.getExterns();
    ScopedAliases pass = createPass();
    pass.process(externs, root); }

  private String getSource() {
    return compiler.toSource(); }

  private void assertSource(String expected) {
    String actual = compiler.toSource();
    assertEqual("Transformed source mismatch", expected, actual); }

  private void assertHasErrors() {
    assertTrue("Expected errors but found none", compiler.hasErrors()); }

  private void assertNoErrors() {
    if (compiler.hasErrors()) {
      String msg = "Unexpected errors: " + compiler.getErrors().toString();
      fail(msg);
    } }

  @Test public void testSimpleAlias() {
    String js = "goog.scope(function() { var g = goog; g.dom.createElement(); });";
    runPass(js);
    assertNoErrors();
    assertSource("goog.dom.createElement();"); }

  @Test public void testAliasChain() {
    String js = "goog.scope(function() { var g = goog; var d = g.dom; d.createElement(); });";
    runPass(js);
    assertNoErrors();
    // After transformation, g and d are replaced; the chain resolves to goog.dom.createElement
    assertEqual("Transitive alias failed", "goog.dom.createElement();"),
compiler.toSource().trim()); }

  @Test public void testTypeAlias() {
    String js = "goog.scope(function() { var Event = goog.events.Event; /** @type {Event} */ var x;
});";
    runPass(js);
    assertNoErrors();
    String src = compiler.toSource();
    assertTrue("JSDoc type alias not expanded", src.contains("@type {goog.events.Event}")); }

  @Test public void testNoScopeCallUnchanged() {
    String js = "var x = 1;";
    runPass(js);
    assertNoErrors();
    assertSource("var x=1;"); }

  @Test public void testDuplicateAliasError() {
    String js = "goog.scope(function() { var g = goog; var g = goog.dom; });";
    runPass(js);
    assertHasErrors(); }

  @Test public void testScopeCallWithParametersError() {
    String js = "goog.scope(function(x) {});";
    runPass(js);
    assertHasErrors(); }

  @Test public void testCircularAliasError() {
    String js = "goog.scope(function() { var a = b; var b = a; });";
    runPass(js);
    assertHasErrors(); }

  @Test public void testNestedFunctionIgnored() {
    String js = "goog.scope(function() { var g = goog; function inner() { var g = goog.dom; }; });";
    runPass(js);
    assertNoErrors();
    // Inner function body should not be processed, so the inner g remains as is? Not exactly:
    // The pass does not traverse into inner functions, so the inner g definition stays.
    // The outer g is replaced. We just ensure no errors.
    String src = compiler.toSource();
    // The inner function should still contain "var g = goog.dom" (unchanged)
    assertTrue("Inner function alias wrongly transformed", src.contains("var g= goog.dom")); }

  @Test public void testShadowedGlobalRenamed() {
    // $jscomp is a forbidden local; if used as alias prefix, it should be renamed in nested scopes.
    String js = "goog.scope(function() { var $jscomp = {}; (function() { var $jscomp = 1; })();
});";
    runPass(js);
    // The inner $jscomp should be renamed to avoid shadowing.
    assertNoErrors();
    String src = compiler.toSource();
    assertFalse("Shadowed $jscomp was not renamed", src.contains("var $jscomp=1")); }

  @Test public void testIssue1144() {
    // This test targets the bug where an IllegalStateException was thrown.
    // The scenario involves a complex type alias that could trigger a state check failure.
    String js = "goog.scope(function() {"
        + "var dom = goog.dom;"
        + "/** @type {dom.DomHelper|string} */ var x;"
        + "});";
    runPass(js);
    assertNoErrors();
    assertNotNull("Pass produced null output", compiler.toSource()); }

  @Test public void testEmptyScopeCall() {
    String js = "goog.scope(function() {});";
    runPass(js);
    assertNoErrors();
    assertEquals("Empty scope should produce no code", "", compiler.toSource().trim()); }
}
```
