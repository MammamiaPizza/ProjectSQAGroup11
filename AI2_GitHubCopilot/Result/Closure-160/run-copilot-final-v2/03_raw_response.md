package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.CheckLevel;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.JSSourceFile;
 import com.google.javascript.jscomp.WarningsGuard;

 import junit.framework.TestCase;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;

 public class CompilerCheckSymbolsQuietTest extends TestCase {

     private static class SuppressAllWarningsGuard extends WarningsGuard {
         @Override
         public CheckLevel level(JSError error) {
             return CheckLevel.OFF;
         }
     }

     public void testQuietWithoutCheckSymbols() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(CheckLevel.OFF);
         options.setWarningsGuard(new SuppressAllWarningsGuard());
         List<JSSourceFile> externs = Arrays.asList(JSSourceFile.fromCode("externs.js", "");
         List<JSSourceFile> inputs = Arrays.asList(JSSourceFile.fromCode("test.js", "var x = y;"));
         compiler.compile(externs, inputs, options);
         assertEquals(0, compiler.getErrorCount() + compiler.getWarningCount());
     }

     public void testCheckSymbolsOverridesQuiet() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(CheckLevel.WARNING);
         options.setWarningsGuard(new SuppressAllWarningsGuard());
         List<JSSourceFile> externs = Arrays.asList(JSSourceFile.fromCode("externs.js", ""));
         List<JSSourceFile> inputs = Arrays.asList(JSSourceFile.fromCode("test.js", "var x = y;"));
         compiler.compile(externs, inputs, options);
         int total = compiler.getErrorCount() + compiler.getWarningCount();
         assertEquals("Quiet + --check_symbols should produce exactly one warning or error",
                      1, total);
     }

     public void testCheckSymbolsWithoutQuiet() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(CheckLevel.WARNING);
         List<JSSourceFile> externs = Arrays.asList(JSSourceFile.fromCode("externs.js", ""));
         List<JSSourceFile> inputs = Arrays.asList(JSSourceFile.fromCode("test.js", "var x = y;"));
         compiler.compile(externs, inputs, options);
         assertTrue("--check_symbols without quiet should report at least one warning or error",
                    compiler.getErrorCount() + compiler.getWarningCount() > 0);
     }

     public void testQuietWithCleanCode() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(CheckLevel.OFF);
         options.setWarningsGuard(new SuppressAllWarningsGuard());
         List<JSSourceFile> externs = Arrays.asList(JSSourceFile.fromCode("externs.js", ""));
         List<JSSourceFile> inputs = Arrays.asList(JSSourceFile.fromCode("test.js", "var x = 1;"));
         compiler.compile(externs, inputs, options);
         assertEquals(0, compiler.getErrorCount() + compiler.getWarningCount());
     }

     public void testCheckSymbolsQuietCleanCode() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(CheckLevel.WARNING);
         options.setWarningsGuard(new SuppressAllWarningsGuard());
         List<JSSourceFile> externs = Arrays.asList(JSSourceFile.fromCode("externs.js", ""));
         List<JSSourceFile> inputs = Arrays.asList(JSSourceFile.fromCode("test.js", "var x = 1;"));
         compiler.compile(externs, inputs, options);
         assertEquals(0, compiler.getErrorCount() + compiler.getWarningCount());
     }

     public void testCheckSymbolsMultipleErrorsWithoutQuiet() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(CheckLevel.WARNING);
         List<JSSourceFile> externs = Arrays.asList(JSSourceFile.fromCode("externs.js", ""));
         List<JSSourceFile> inputs = Arrays.asList(JSSourceFile.fromCode("test.js",
                 "var a = b; var c = d;"));
         compiler.compile(externs, inputs, options);
         assertTrue("Multiple errors with --check_symbols should appear",
                 compiler.getErrorCount() + compiler.getWarningCount() >= 2);
     }

     public void testCheckSymbolsMultipleErrorsWithQuiet() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(CheckLevel.WARNING);
         options.setWarningsGuard(new SuppressAllWarningsGuard());
         List<JSSourceFile> externs = Arrays.asList(JSSourceFile.fromCode("externs.js", ""));
         List<JSSourceFile> inputs = Arrays.asList(JSSourceFile.fromCode("test.js",
                 "var a = b; var c = d;"));
         compiler.compile(externs, inputs, options);
         int total = compiler.getErrorCount() + compiler.getWarningCount();
         assertTrue("Quiet should not suppress --check_symbols override; got " + total,
                 total >= 1);
     }

     public void testGetMessagesReturnsOneWithCheckSymbolsQuiet() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(CheckLevel.WARNING);
         options.setWarningsGuard(new SuppressAllWarningsGuard());
         List<JSSourceFile> externs = Arrays.asList(JSSourceFile.fromCode("externs.js", ""));
         List<JSSourceFile> inputs = Arrays.asList(JSSourceFile.fromCode("test.js", "var x = y;"));
         compiler.compile(externs, inputs, options);
         JSError[] messages = compiler.getMessages();
         assertEquals(1, messages.length);
         assertEquals(1, compiler.getErrors().length + compiler.getWarnings().length);
     }
 }