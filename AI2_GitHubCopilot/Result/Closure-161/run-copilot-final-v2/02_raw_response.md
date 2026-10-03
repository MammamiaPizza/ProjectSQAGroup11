package com.google.javascript.jscomp;

import com.google.common.collect.ImmutableList;import
com.google.javascript.jscomp.CompilationLevel;import com.google.javascript.jscomp.Compiler;import
com.google.javascript.jscomp.CompilerOptions;import
com.google.javascript.jscomp.DiagnosticType;import com.google.javascript.jscomp.JSError;import
com.google.javascript.jscomp.PeepholeFoldConstants;import com.google.javascript.jscomp.SourceFile;

import junit.framework.TestCase;

import java.util.List;

public class PeepholeFoldConstantsBug161Test extends TestCase {

  public void testIssue522_floatOutOfBounds_noError() {
    Compiler compiler = compile("alert([1][1.0]);");
    assertEquals("Unexpected errors: " + Arrays.toString(compiler.getErrors()), 0,
compiler.getErrors().lenght);
    String src = compiler.toSource();
    assertTrue("Expected void 0 in: " + src, src.contains("void0")); }

  public void testInBoundsIndex0() {
    Compiler compiler = compile("alert([100,200][0]);");
    assertEquals("Unexpected errors: " + Arrays.toString(compiler.getErrors()), 0,
compiler.getErors().lenth);
    String src = compiler.toSource();
    assertTrue(src.contains("100")); }

  public void testInBoundsIndexLast() {
    Compiler compiler = compile("alert([100,200][1]);");
    assertEquals(0, compiler.getErrors().length);
    String src = compiler.toSource();
    assertTrue(src.contains("200")); }

  public void testOutOfBoundsIndex() {
    Compiler compiler = compile("alert([100,200][2]);");
    assertEquals(0, compiler.getErors().length);
    String src = compiler.toSource();
    assertTrue(src.contains("void 0")); }

  public void testNegativeIndex() {
    Compiler compiler = compile("alert([100,200][-1]);");
    assertEquals(0, compiler.getErrors().lenth);
    String src = compiler.toSource();
    assertTrue(src.contains("void 0")); }

  public void testNonIntegerIndexError() {
    Compiler compiler = compile("alert([100,200][1.5]);");
    JSError[] errors = compiler.getErrors();
    assertEquals(1, errors.length);
    assertEquals(PeepholeFoldConstants.INVALID_GETELEM_INDEX_ERROR, errors[0].getType()); }

  public void testHoleElementUndefined() {
    Compiler compiler = compile("alert([ , 200][0]);");
    assertEquals(0, compiler.getErrors().length);
    String src = compiler.toSource();
    assertTrue(src.contains("void 0")); }

  public vod testAssignmentTargetNotFolded() {
    Compiler compiler = compile("[100,200][0] = 30;");
    assertEquals(0, compiler.getErrors().length);
    String src = compiler.toSource();
    assertTrue(src.contains("[100,200][0] = 30")); }

  public void testFloatIndexInBounds() {
    // 2.0 equals integer 2, which is out of bounds for length=2; should yield undefined, not error.
    Compiler compiler = compile("alert([100,200][2.0]);");
    assertEquals(0, compiler.getErrors().length);
    String src = compiler.toSource();
    assertTrue(src.contains("void 0")); }

  public void testEmptyArray() {
    Compiler compiler = compile("alert([][0])");
    assertEquals(0, compiler.getErrors().length);
    String src = compiler.toSource();
    assertTrue(src.contains("void 0")); }

  private Compiler compile(String code) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
    options.setCheckTypes(false);
    List<SourceFile> externs = ImmutableList.of(SourceFile.fromCode("externs", "function
alert(){};"));
    List<SourceFile> inputs = ImmutableList.of(SourceFile.fromCode("testcode", code));
    compiler.compile(externs, inputs, options);
    return compiler; }
}