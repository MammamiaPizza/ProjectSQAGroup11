package com.google.javascript.jscomp;

 import static org.junit.Assert.*;

 import org.junit.Test;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;

 /**
  * Tests for dependency sorting performed by {@link Compiler#repartitionInputs()}.
  */
 public class CompilerDependencySortingTest {

     // ------------------------------------------------------------------
     // Normal acyclic chain
     // ------------------------------------------------------------------
     @Test
     public void testSimpleDependencyChain() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile inputA = SourceFile.fromCode("a.js",
                 "var a = 1; goog.provide('A');");
         SourceFile inputB = SourceFile.fromCode("b.js",
                 "var b = 2; goog.provide('B'); goog.require('A');");
         SourceFile inputC = SourceFile.fromCode("c.js",
                 "var c = 3; goog.require('B');");

         List<SourceFile> inputs = Arrays.asList(inputC, inputA, inputB);
         compiler.compile(new ArrayList<SourceFile>(), inputs, options);

         assertFalse("should not have errors", compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertEquals(3, sources.length);

         // Expected order: A (provides A), B (provides B, needs A), C (needs B)
         assertTrue("first should provide A", sources[0].contains("var a = 1"));
         assertTrue("second should provide B", sources[1].contains("var b = 2"));
         assertTrue("third should require B", sources[2].contains("var c = 3"));
     }

     // ------------------------------------------------------------------
     // Boundary: single input – no dependencies
     // ------------------------------------------------------------------
     @Test
     public void testSingleInputNoDependencies() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile input = SourceFile.fromCode("only.js",
                 "var x = 42;");
         compiler.compile(new ArrayList<SourceFile>(),
                 Arrays.asList(input), options);

         assertFalse(compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertEquals(1, sources.length);
         assertTrue(sources[0].contains("var x = 42"));
     }

     // ------------------------------------------------------------------
     // Boundary: empty input list
     // ------------------------------------------------------------------
     @Test
     public void testEmptyInputList() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         compiler.compile(new ArrayList<SourceFile>(),
                 new ArrayList<SourceFile>(), options);

         assertFalse(compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertEquals(0, sources.length);
     }

     // ------------------------------------------------------------------
     // Boundary: single input provides and requires same namespace
     // (self-cycle is not a real cycle, input stays alone)
     // ------------------------------------------------------------------
     @Test
     public void testSingleInputProvidingAndRequiringSameNamespace() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile input = SourceFile.fromCode("self.js",
                 "var s = 5; goog.provide('X'); goog.require('X');");
         compiler.compile(new ArrayList<SourceFile>(),
                 Arrays.asList(input), options);

         // A file that provides and requires the same symbol is usually fine
         // (it just depends on itself, no other file moves).
         assertFalse(compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertEquals(1, sources.length);
         assertTrue(sources[0].contains("var s = 5"));
     }

     // ------------------------------------------------------------------
     // Normal: multiple provides in one file satisfied by later inputs
     // ------------------------------------------------------------------
     @Test
     public void testMultipleProvidesInSingleFile() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile provider = SourceFile.fromCode("multi.js",
                 "var m = 10; goog.provide('P'); goog.provide('Q');");
         SourceFile reqP = SourceFile.fromCode("reqP.js",
                 "var rp = 20; goog.require('P');");
         SourceFile reqQ = SourceFile.fromCode("reqQ.js",
                 "var rq = 30; goog.require('Q');");

         List<SourceFile> inputs = Arrays.asList(reqQ, reqP, provider);
         compiler.compile(new ArrayList<SourceFile>(), inputs, options);

         assertFalse(compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertEquals(3, sources.length);
         // provider must appear before the two requesters
         assertTrue("provider first", sources[0].contains("var m = 10"));
         // order of requesters is not strictly defined; just check they come after
         int idxP = -1, idxQ = -1;
         for (int i = 0; i < sources.length; i++) {
             if (sources[i].contains("var rp = 20")) idxP = i;
             if (sources[i].contains("var rq = 30")) idxQ = i;
         }
         assertTrue(idxP > 0);
         assertTrue(idxQ > 0);
     }

     // ------------------------------------------------------------------
     // Error: circular dependency (A → B → A)
     // ------------------------------------------------------------------
     @Test
     public void testCircularDependencyReportsError() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile a = SourceFile.fromCode("a.js",
                 "goog.provide('A'); goog.require('B');");
         SourceFile b = SourceFile.fromCode("b.js",
                 "goog.provide('B'); goog.require('A');");

         compiler.compile(new ArrayList<SourceFile>(),
                 Arrays.asList(a, b), options);

         // A cycle must be reported as an error.
         assertTrue("circular dependency should be an error",
                 compiler.hasErrors());
         assertTrue(compiler.getErrorCount() > 0);
     }

     // ------------------------------------------------------------------
     // Error: missing provide (required namespace never provided)
     // ------------------------------------------------------------------
     @Test
     public void testMissingProvideReportsError() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile req = SourceFile.fromCode("req.js",
                 "var r = 1; goog.require('NonExistent');");

         compiler.compile(new ArrayList<SourceFile>(),
                 Arrays.asList(req), options);

         assertTrue("missing goog.provide should be an error",
                 compiler.hasErrors());
     }

     // ------------------------------------------------------------------
     // Normal: inputs with no goog statements preserve their original order
     // ------------------------------------------------------------------
     @Test
     public void testNoDependencyPreservesOriginalOrder() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile first = SourceFile.fromCode("first.js", "var f = 1;");
         SourceFile second = SourceFile.fromCode("second.js", "var s = 2;");
         SourceFile third = SourceFile.fromCode("third.js", "var t = 3;");

         List<SourceFile> inputs = Arrays.asList(first, second, third);
         compiler.compile(new ArrayList<SourceFile>(), inputs, options);

         assertFalse(compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertEquals(3, sources.length);
         assertTrue(sources[0].contains("var f = 1"));
         assertTrue(sources[1].contains("var s = 2"));
         assertTrue(sources[2].contains("var t = 3"));
     }

     // ------------------------------------------------------------------
     //  Boundary: externs do not influence input reordering
     // ------------------------------------------------------------------
     @Test
     public void testExternsDoNotAffectInputOrder() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile extern = SourceFile.fromCode("extern.js",
                 "var externalApi = {};");
         SourceFile a = SourceFile.fromCode("a.js",
                 "var a = 1; goog.provide('A');");
         SourceFile b = SourceFile.fromCode("b.js",
                 "var b = 2; goog.require('A');");

         compiler.compile(Arrays.asList(extern), Arrays.asList(b, a), options);

         assertFalse(compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertEquals(2, sources.length);
         // a must come before b
         assertTrue("provider A first", sources[0].contains("var a = 1"));
         assertTrue("requirer B second", sources[1].contains("var b = 2"));
     }

     // ------------------------------------------------------------------
     // Larger chain to exercise topological sorting
     // ------------------------------------------------------------------
     @Test
     public void testLargerAcyclicChain() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile[] files = new SourceFile[4];
         files[0] = SourceFile.fromCode("base.js",
                 "var v0 = 0; goog.provide('base');");
         files[1] = SourceFile.fromCode("mid.js",
                 "var v1 = 1; goog.provide('mid'); goog.require('base');");
         files[2] = SourceFile.fromCode("leaf.js",
                 "var v2 = 2; goog.require('mid');");
         files[3] = SourceFile.fromCode("other.js",
                 "var v3 = 3; goog.provide('other'); goog.require('base');");

         // deliberately scrambled
         List<SourceFile> inputs = Arrays.asList(
                 files[2], files[3], files[1], files[0]);
         compiler.compile(new ArrayList<SourceFile>(), inputs, options);

         assertFalse(compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertEquals(4, sources.length);

         // base must appear before mid and other
         String all = sources[0] + sources[1] + sources[2] + sources[3];
         int posBase = all.indexOf("var v0 = 0");
         int posMid  = all.indexOf("var v1 = 1");
         int posLeaf = all.indexOf("var v2 = 2");
         int posOther= all.indexOf("var v3 = 3");

         assertTrue(posBase < posMid);
         assertTrue(posBase < posOther);
         assertTrue(posMid < posLeaf);
     }

     // ------------------------------------------------------------------
     // Input with only goog.require (no goog.provide) still processed
     // and placed according to dependencies
     // ------------------------------------------------------------------
     @Test
     public void testInputWithOnlyGoogRequire() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile provider = SourceFile.fromCode("p.js",
                 "var p = 1; goog.provide('P');");
         SourceFile requirer = SourceFile.fromCode("r.js",
                 "var r = 2; goog.require('P');");

         compiler.compile(new ArrayList<SourceFile>(),
                 Arrays.asList(requirer, provider), options);

         assertFalse(compiler.hasErrors());
         String[] sources = compiler.toSourceArray();
         assertTrue(sources[0].contains("var p = 1"));
         assertTrue(sources[1].contains("var r = 2"));
     }

     // ------------------------------------------------------------------
     // Compile result indicates failure on error
     // ------------------------------------------------------------------
     @Test
     public void testResultFailureOnError() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile a = SourceFile.fromCode("a.js",
                 "goog.provide('A'); goog.require('B');");
         SourceFile b = SourceFile.fromCode("b.js",
                 "goog.provide('B'); goog.require('A');");

         Result result = compiler.compile(new ArrayList<SourceFile>(),
                 Arrays.asList(a, b), options);
         assertFalse("result success should be false on cycle",
                 result.success);
     }

     // ------------------------------------------------------------------
     // After compilation the input list can be retrieved and reflects
     // the sorted order (indirectly via toSourceArray)
     // ------------------------------------------------------------------
     @Test
     public void testToSourceArrayLengthMatchesInputsCount() {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         SourceFile one = SourceFile.fromCode("one.js", "var o=1;");
         SourceFile two = SourceFile.fromCode("two.js", "var t=2;");
         SourceFile three = SourceFile.fromCode("three.js", "var th=3;");

         compiler.compile(new ArrayList<SourceFile>(),
                 Arrays.asList(one, two, three), options);

         String[] sources = compiler.toSourceArray();
         assertEquals(3, sources.length);
     }
 }