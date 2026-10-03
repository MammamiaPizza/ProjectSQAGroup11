package com.google.javascript.jscomp;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**

 - Tests for bug #600: CrossModuleMethodMotion should not move prototype methods
 - that are used across modules where the using module does not depend on the
 - defining module.
  */
 public class CrossModuleMethodMotionBug600Test extends TestCase {
 public void testIssue600_NoDependency_NoMove() throws Exception {
   // Two independent modules; method used in a sibling module.
   // The method should stay in the original module – no stub call.
   Compiler compiler = createCompiler();
   JSModule a = module("m_a",
   "/** @provides /" +
   "var Foo = function() {};" +
   "Foo.prototype.bar = function() { return 1; };"
   );
   JSModule b = module("m_b",
   "/ @provides
  */" +
   "var x = new Foo();" +
   "x.bar();"
   );
   // No dependency declared between a and b – they are siblings.
   JSModuleGraph graph = new JSModuleGraph(modules(a, b));
   runMotion(compiler, graph);
   String output = compiler.toSource();
   assertFalse("Must not contain stub call when method shouldn't move",
   output.contains("JSCompiler_stubMethod"));
   assertTrue("Original function body must stay",
   output.contains("return 1")); }
 public void testIssue600b_WithDependency_Moves() throws Exception {
   // Module a defines the method; module b depends on a.
   // The method should be moved – a stub is left and the body moves to b.
   Compiler compiler = createCompiler();
   JSModule a = module("m_a",
   "/** @provides /" +
   "var Foo = function() {};" +
   "Foo.prototype.bar = function() { return 1; };"
   );
   JSModule b = module("m_b",
   "/ @requires m_a
  */" +
   "var x = new Foo();" +
   "x.bar();"
   );
   // b depends on a → chain: a → b
   JSModuleGraph graph = new JSModuleGraph(modules(a, b));
   runMotion(compiler, graph);
   String output = compiler.toSource();
   // After movement the original module gets a stub call, and the
   // deeper module gets an unstub call containing the body.
   assertTrue("Stub call expected in moved case",
   output.contains("JSCompiler_stubMethod"));
   assertTrue("Unstub call expected in deeper module",
   output.contains("JSCompiler_unstubMethod"));
   assertTrue("Function body must still be present somewhere",
   output.contains("return 1")); }
 public void testIssue600e_ExternsPreventMove() throws Exception {
   // When a prototype property is referenced in externs, the property
   // is anchored and should never be moved, even if only used in one module.
   Compiler compiler = createCompiler();
   // Provide an extern that declares the prototype method.
   String externs = "function Foo(){}; Foo.prototype.bar = function(){};";
   JSModule a = module("m_a",
   "/** @provides
  */" +
   "var Foo = function() {};" +
   "Foo.prototype.bar = function() { return 1; };"
   );
   JSModuleGraph graph = new JSModuleGraph(modules(a));
   // Use compiler that accepts externs
   compiler = new Compiler();
   CompilerOptions opts = new CompilerOptions();
   opts.setCheckGlobalThisLevel(CheckLevel.OFF);
   compiler.initOptions(opts);
   compiler.initModules(SourceFile.fromCode("externs", externs), modules(a));
   compiler.parse();
   compiler.setModuleGraph(graph);
   CrossModuleMethodMotion pass = new CrossModuleMethodMotion(compiler,
   new CrossModuleMethodMotion.IdGenerator(), true);
   pass.process(compiler.getExternsRoot(), compiler.getRoot());
   String output = compiler.toSource();
   assertFalse("Extern reference should anchor property – no stub call",
   output.contains("JSCompiler_stubMethod")); }
 // ---------- helpers ----------
 private static Compiler createCompiler() {
   Compiler compiler = new Compiler();
   CompilerOptions opts = new CompilerOptions();
   opts.setCheckGlobalThisLevel(CheckLevel.OFF);
   compiler.initOptions(opts);
   return compiler; }
 private static JSModule module(String name, String source) {
   JSModule m = new JSModule(name);
   m.add(SourceFile.fromCode(name + ".js", source));
   return m; }
 @SuppressWarnings("unchecked") private static List<JSModule> modules(JSModule... modules) {
   return Arrays.asList(modules); }
 private static void runMotion(Compiler compiler, JSModuleGraph graph)
 throws Exception {
   // Ensure the compiler knows the module graph.
   compiler.setModuleGraph(graph);
   compiler.initModules(SourceFile.fromCode("externs", ""),
   toList(graph.getModules()));
   compiler.parse();
   CrossModuleMethodMotion pass = new CrossModuleMethodMotion(compiler,
   new CrossModuleMethodMotion.IdGenerator(), false);
   pass.process(compiler.getExternsRoot(), compiler.getRoot()); }
 private static <T> List<T> toList(Iterable<T> it) {
   java.util.List<T> l = new java.util.ArrayList<>();
   for (T t : it) l.add(t);
   return l; }

}