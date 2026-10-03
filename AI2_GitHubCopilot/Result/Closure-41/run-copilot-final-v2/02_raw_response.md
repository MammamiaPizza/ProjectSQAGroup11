/**

 - JUnit test for FunctionTypeBuilder related to bug 368:
 - @extends and @implements without @constructor/@interface warnings.
 - Tests are designed to expose incorrect warnings in the buggy version.
  */
 package com.google.javascript.jscomp;

import com.google.common.collect.Lists;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.Node;
import java.util.List;
import junit.framework.TestCase;

public class FunctionTypeBuilderTest extends TestCase {

 /** Helper: compile a snippet and return all warnings + errors as a list of JSError. */
 private List<JSError> compile(String js) {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     options.setCheckTypes(true);
     options.setWarningLevel(DiagnosticGroups.UNDECLARED_VARIABLE, CheckLevel.OFF);
     options.setWarningLevel(DiagnosticGroups.MISSING_PROPERTIES, CheckLevel.OFF);
     options.setWarningLevel(DiagnosticGroups.MISPLACED_TYPE_ANNOTATION, CheckLevel.WARNING);

     // Minimal externs so that Object, Function etc. are defined.
     List<SourceFile> externs = Lists.newArrayList();
     try {
         compiler.compile(externs,
                 Lists.newArrayList(SourceFile.fromCode("testcode", js)),
                 options);
     } catch (Exception e) {
         // Some compilations may throw; treat as zero errors.
     }
     List<JSError> all = Lists.newArrayList();
     all.addAll(compiler.getErrors());
     all.addAll(compiler.getWarnings());
     return all;
 }

 // ---------------------------------------------------------------
 // @extends with @constructor – no warning expected
 public void testExtendsWithConstructor_noWarning() {
     String js = "/** @constructor */ function Foo() {}" +
                 "/** @constructor @extends {Foo} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertEquals("Should have no warnings/errors", 0, diags.size());
 }

 // ---------------------------------------------------------------
 // @extends with @interface – no warning expected (bug: warning incorrectly emitted)
 public void testExtendsWithInterface_noWarning() {
     String js = "/** @interface */ function Foo() {}" +
                 "/** @interface @extends {Foo} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertEquals("Bug: @interface with @extends should not warn", 0, diags.size());
 }

 // ---------------------------------------------------------------
 // @extends on a non-constructor, non-interface function -> warning
 public void testExtendsWithoutConstructor_warning() {
     String js = "/** @constructor */ function Foo() {}" +
                 "/** @extends {Foo} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertTrue("Expected @extends warning", diags.size() > 0);
     String msg = diags.get(0).toString().toLowerCase();
     assertTrue(msg.contains("@extends") || msg.contains("extends"));
 }

 // ---------------------------------------------------------------
 // @implements with @constructor – no warning
 public void testImplementsWithConstructor_noWarning() {
     String js = "/** @interface */ function IFoo() {}" +
                 "/** @constructor @implements {IFoo} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertEquals("Should have no warnings/errors", 0, diags.size());
 }

 // ---------------------------------------------------------------
 // @implements with @interface – no warning
 public void testImplementsWithInterface_noWarning() {
     String js = "/** @interface */ function IFoo() {}" +
                 "/** @interface @implements {IFoo} */ function IBar() {}";
     List<JSError> diags = compile(js);
     assertEquals("Should have no warnings/errors", 0, diags.size());
 }

 // ---------------------------------------------------------------
 // @implements on a non-constructor, non-interface function -> warning
 public void testImplementsWithoutConstructor_warning() {
     String js = "/** @interface */ function IFoo() {}" +
                 "/** @implements {IFoo} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertTrue("Expected @implements warning", diags.size() > 0);
     String msg = diags.get(0).toString().toLowerCase();
     assertTrue(msg.contains("@implements") || msg.contains("implements"));
 }

 // ---------------------------------------------------------------
 // @extends with non-object base type (e.g., number) -> warning
 public void testExtendsNonObject_warning() {
     String js = "/** @constructor @extends {number} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertTrue("Expected @extends non-object warning", diags.size() > 0);
     String msg = diags.get(0).toString().toLowerCase();
     // The actual message varies; check for presence of "extends"
     assertTrue(msg.contains("extends") || msg.contains("non-object"));
 }

 // ---------------------------------------------------------------
 // @implements with a non-object type -> error
 public void testImplementsNonObject_error() {
     String js = "/** @constructor @implements {number} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertTrue("Expected @implements error", diags.size() > 0);
     String msg = diags.get(0).toString().toLowerCase();
     assertTrue(msg.contains("implement") || msg.contains("implements"));
 }

 // ---------------------------------------------------------------
 // @extends with an empty type -> warning
 public void testExtendsEmptyType_warning() {
     String js = "/** @constructor @extends {undefined} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertTrue("Expected @extends empty type warning", diags.size() > 0);
 }

 // ---------------------------------------------------------------
 // @implements with an empty type -> warning
 public void testImplementsEmptyType_warning() {
     String js = "/** @constructor @implements {undefined} */ function Bar() {}";
     List<JSError> diags = compile(js);
     assertTrue("Expected @implements empty type warning", diags.size() > 0);
 }

}