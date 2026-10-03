package com.google.javascript.jscomp;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class CompilerReportCodeChangeTest {

 private CompilerSpy compiler;
 private CompilerOptions options;
 private JSSourceFile[] externs;

 private static class CompilerSpy extends Compiler {
     final AtomicInteger reportCodeChangeCount = new AtomicInteger(0);

     @Override
     public void reportCodeChange() {
         reportCodeChangeCount.incrementAndGet();
         super.reportCodeChange();
     }
 }

 @Before
 public void setUp() {
     compiler = new CompilerSpy();
     options = new CompilerOptions();
     externs = new JSSourceFile[0];
 }

 @Test
 public void testEmptyModulesNoReportCodeChange() {
     JSModule[] modules = new JSModule[0];
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals("reportCodeChange must not be called for empty modules",
             0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testSingleEmptyModuleNoReportCodeChange() {
     JSModule empty = new JSModule("empty");
     JSModule[] modules = new JSModule[]{empty};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testEmptyModulesWithCrossModuleMotionNoReportCodeChange() {
     options.crossModuleCodeMotion = true;
     JSModule[] modules = new JSModule[0];
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testSingleEmptyModuleWithCrossModuleMotionNoReportCodeChange() {
     options.crossModuleCodeMotion = true;
     JSModule empty = new JSModule("empty");
     JSModule[] modules = new JSModule[]{empty};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testModulesWithEmptyInputNoReportCodeChange() {
     JSSourceFile emptyInput = JSSourceFile.fromCode("empty.js", "");
     JSModule module = new JSModule("mod");
     module.add(emptyInput);
     JSModule[] modules = new JSModule[]{module};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testProcessDefinesWithoutDefinesNoReportCodeChange() {
     options.processDefines = true;
     JSModule mod = new JSModule("mod");
     mod.add(JSSourceFile.fromCode("test.js", "var x = 1;"));
     JSModule[] modules = new JSModule[]{mod};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testOptimizeWithNothingToOptimizeNoReportCodeChange() {
     JSModule mod = new JSModule("mod");
     mod.add(JSSourceFile.fromCode("test.js", "var x=1;"));
     JSModule[] modules = new JSModule[]{mod};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testNormalUnchangedRunNoReportCodeChange() {
     JSModule mod = new JSModule("mod");
     mod.add(JSSourceFile.fromCode("test.js", "var a = 1;"));
     JSModule[] modules = new JSModule[]{mod};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testMultipleEmptyModulesNoReportCodeChange() {
     JSModule empty1 = new JSModule("empty1");
     JSModule empty2 = new JSModule("empty2");
     JSModule[] modules = new JSModule[]{empty1, empty2};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testMixedModulesWithTrivialInputsNoReportCodeChange() {
     JSModule mod1 = new JSModule("mod1");
     mod1.add(JSSourceFile.fromCode("a.js", "var a=1;"));
     JSModule mod2 = new JSModule("mod2");
     mod2.add(JSSourceFile.fromCode("b.js", "var b=2;"));
     JSModule[] modules = new JSModule[]{mod1, mod2};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertEquals(0, compiler.reportCodeChangeCount.get());
 }

 @Test
 public void testCodeChangeIsReportedWhenMotionMovesCode() {
     options.crossModuleCodeMotion = true;
     JSModule mod1 = new JSModule("mod1");
     mod1.add(JSSourceFile.fromCode("a.js", "function f(){} var x = f();"));
     JSModule mod2 = new JSModule("mod2");
     mod2.add(JSSourceFile.fromCode("b.js", "f();"));
     JSModule[] modules = new JSModule[]{mod1, mod2};
     compiler.compile(externs, modules, options);
     assertFalse(compiler.hasErrors());
     assertTrue("Expected reportCodeChange to be called when code actually changes",
             compiler.reportCodeChangeCount.get() > 0);
 }

}
