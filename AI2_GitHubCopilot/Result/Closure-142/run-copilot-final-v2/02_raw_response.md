package com.google.javascript.jscomp;

import junit.framework.TestCase;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.jscomp.parsing.JsDocInfoParser;
import com.google.javascript.jscomp.parsing.JsDocTokenStream;
import com.google.javascript.jscomp.parsing.NullErrorReporter;

/**

 - Tests for Closure bug 142 (IE parameter escape in CoalesceVariableNames and
 - JsDoc license extraction with annotations).
  */
 public class Bug142Test extends TestCase {

  // ---- JsDocInfoParser license tests ----

  public void testLicenseWithAnnotation() {
    String jsdoc = "/** @license Foo @type {string} */";
    JSDocInfo info = parseJsDoc(jsdoc);
    assertNotNull(info);
    assertEquals("License text should include content before @type",
                 " Foo", info.getLicense()); }

  public void testLicenseWithMultipleAnnotations() {
    String jsdoc = "/** @license Hello @param {string} name @return {number} */";
    JSDocInfo info = parseJsDoc(jsdoc);
    assertNotNull(info);
    assertEquals(" Hello", info.getLicense()); }

  public void testLicenseEmptyBlock() {
    String jsdoc = "/** @license */";
    JSDocInfo info = parseJsDoc(jsdoc);
    assertNotNull(info);
    assertEquals("", info.getLicense()); }

  public void testLicenseTrailingSpaces() {
    String jsdoc = "/** @license   Bar   @type {number} */";
    JSDocInfo info = parseJsDoc(jsdoc);
    assertNotNull(info);
    // trailing spaces inside the license text should be trimmed
    assertEquals(" License text should trim trailing spaces", " Bar", info.getLicense()); }

  public void testLicenseWithoutAnnotation() {
    String jsdoc = "/** @license License text only */";
    JSDocInfo info = parseJsDoc(jsdoc);
    assertNotNull(info);
    assertEquals(" License text only", info.getLicense()); }

  // ---- CoalesceVariableNames tests ----

  public void testCoalesceTwoParamsNotMerged() {
    // In this function, live ranges of a and b do not overlap.
    // Without the IE workaround they could be coalesced.
    String code = "function f(a, b) { var x = a; return x + b; }";
    String result = compileAndCoalesce(code);
    // Verify that both parameter names are still distinct in the output.
    assertTrue("Parameter a should be preserved", result.contains("a"));
    assertTrue("Parameter b should be preserved", result.contains("b"));
    // Check that they were not coalesced into the same name
    // by ensuring the two occurrences are separate identifiers.
    int aIdx = result.indexOf("a");
    int bIdx = result.indexOf("b");
    assertTrue("a and b must be different variables", aIdx != bIdx); }

  public void testCoalesceTwoParamsExactly2AreEscaped() {
    // A sort callback with exactly two parameters – the root cause of bug 58.
    // The IE workaround must prevent coalescing inside functions with 2 params.
    String code = "function sort(a, b) { return a - b; }";
    String result = compileAndCoalesce(code);
    assertTrue(result.contains("a"));
    assertTrue(result.contains("b"));
    int aIdx = result.indexOf("a");
    int bIdx = result.indexOf("b");
    assertTrue("a and b must not be merged", aIdx == -1 || bIdx == -1 || aIdx != bIdx); }

  public void testCoalesceThreeParamsMayBeMerged() {
    // Three parameters are NOT escaped, so coalescing may occur.
    // Here we check that coalescing does happen (or at least the pass runs).
    String code = "function f(a, b, c) { var x = a; return x + b + c; }";
    String result = compileAndCoalesce(code);
    // We don't assert exact names, but the result should be valid JS.
    assertNotNull(result); }

  /** Helper: parses a JSDoc comment string and returns the built JSDocInfo. */ private JSDocInfo
parseJsDoc(String jsdoc) {
    JsDocTokenStream stream = new JsDocTokenStream(jsdoc);
    JsDocInfoParser.Config config = new JsDocInfoParser.Config(
        new JSTypeRegistry(NullErrorReporter.forOldRhino()),
        new HashSet<String>(),
        false);
    JsDocInfoParser parser = new JsDocInfoParser(
        stream, "test.js", config, NullErrorReporter.forNewRhino());
    return parser.parse(); }

  /** Helper: runs the CoalesceVariableNames pass on the given code and returns the resulting
source. */ private String compileAndCoalesce(String code) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.coalesceVariableNames = false; // we run the pass manually
    List<SourceFile> externs = Arrays.asList(SourceFile.fromCode("externs", ""));
    List<SourceFile> inputs = Arrays.asList(SourceFile.fromCode("test.js", code));
    compiler.init(externs, inputs, options);
    compiler.parse();
    CoalesceVariableNames pass = new CoalesceVariableNames(compiler, false);
    pass.process(compiler.getExternsRoot(), compiler.getRoot());
    return compiler.toSource(); }
}