package com.google.javascript.jscomp;

 import static org.junit.Assert.assertArrayEquals;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;

 import com.google.common.collect.Lists;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.rhino.SourceFile;

 import org.junit.Before;
 import org.junit.Test;

 import java.util.ArrayList;
 import java.util.List;

 public class CompilerTest {

     private Compiler compiler;
     private CompilerOptions options;
     private List<SourceFile> externs;

     @Before
     public void setUp() {
         compiler = new Compiler();
         options = new CompilerOptions();
         options.setWhitespaceOnly(true);
         externs = Lists.newArrayList(
                 SourceFile.fromCode("extern1", ""));
     }

     private static SourceFile src(String name, String code) {
         return SourceFile.fromCode(name, code);
     }

     private static JSModule module(String name, String code, JSModule... deps) {
         JSModule m = new JSModule(name);
         m.add(src(name + ".js", code));
         for (JSModule dep : deps) {
             m.addDependency(dep);
         }
         return m;
     }

     private static JSModule emptyModule(String name, JSModule... deps) {
         JSModule m = new JSModule(name);
         for (JSModule dep : deps) {
             m.addDependency(dep);
         }
         return m;
     }

     private List<JSModule> compileModules(JSModule... modules) {
         List<JSModule> modList = Lists.newArrayList(modules);
         CompilerOptions opts = new CompilerOptions();
         opts.setWhitespaceOnly(true);
         Result r = compiler.compileModules(externs, modList, opts);
         assertTrue("Compilation should succeed", r.success);
         return modList;
     }

     private String[] getModuleOutput(JSModule module) {
         return compiler.toSourceArray(module);
     }

     private static String[] strings(String... strings) {
         return strings;
     }

     // 1. Single module outputs its source
     @Test
     public void testSingleModuleWhitespace() {
         JSModule m = module("mod1", "var x = 1;");
         compileModules(m);
         String[] out = getModuleOutput(m);
         assertEquals(1, out.length);
         assertEquals("var x=1;", out[0].trim());
     }

     // 2. Two modules with dependency: dependent module must appear after its dependency
     @Test
     public void testMultiModuleChainOrder() {
         JSModule a = module("A", "var a = 1;");
         JSModule b = module("B", "var b = 2;", a);
         compileModules(a, b);

         // In the global array, B's input should be after A's because B depends on A
         String[] global = compiler.toSourceArray();
         assertEquals(2, global.length);
         assertEquals("var a=1;", global[0].trim());
         assertEquals("var b=2;", global[1].trim());

         // Per-module output should still be correct
         assertArrayEquals(strings("var a=1;"), getModuleOutput(a));
         assertArrayEquals(strings("var b=2;"), getModuleOutput(b));
     }

     // 3. Empty module produces empty output
     @Test
     public void testEmptyModule() {
         JSModule empty = emptyModule("empty");
         compileModules(empty);
         assertArrayEquals(new String[0], getModuleOutput(empty));
     }

     // 4. Three modules with chain dependency: verify correct topological order
     @Test
     public void testDependencySorting() {
         JSModule m1 = module("M1", "1");
         JSModule m2 = module("M2", "2", m1);
         JSModule m3 = module("M3", "3", m2);
         compileModules(m1, m2, m3);

         String[] global = compiler.toSourceArray();
         assertEquals(3, global.length);
         assertEquals("1", global[0].trim());
         assertEquals("2", global[1].trim());
         assertEquals("3", global[2].trim());
     }

     // 5. Modules with no explicit dependencies retain original registration order
     @Test
     public void testNoDependencyOrderUnchanged() {
         JSModule a = module("A", "a");
         JSModule b = module("B", "b");
         compileModules(a, b);
         String[] global = compiler.toSourceArray();
         assertEquals("a", global[0].trim());
         assertEquals("b", global[1].trim());
     }

     // 6. Dependency reversal: if B depends on A but B is given first, output must still be A then
B
     @Test
     public void testDependencyReversal() {
         JSModule a = module("A", "a");
         JSModule b = module("B", "b", a);
         // Pass B first, A second – compiler must sort correctly
         compileModules(b, a);
         String[] global = compiler.toSourceArray();
         assertEquals(2, global.length);
         assertEquals("a", global[0].trim());
         assertEquals("b", global[1].trim());
     }

     // 7. Multiple inputs per module; internal order should be preserved, modules sorted by deps
     @Test
     public void testMultipleInputsPerModule() {
         JSModule m = new JSModule("multi");
         m.add(src("f1.js", "1"));
         m.add(src("f2.js", "2"));
         compileModules(m);
         String[] out = compiler.toSourceArray();
         assertEquals(2, out.length);
         assertEquals("1", out[0].trim());
         assertEquals("2", out[1].trim());
     }

     // 8. Per-module toSourceArray returns correct code
     @Test
     public void testToSourceForModule() {
         JSModule a = module("A", "var a=1;");
         JSModule b = module("B", "var b=2;", a);
         compileModules(a, b);

         assertArrayEquals(strings("var a=1;"), getModuleOutput(a));
         assertArrayEquals(strings("var b=2;"), getModuleOutput(b));
     }

     // 9. Whitespace mode removes unnecessary whitespace
     @Test
     public void testWhitespaceModeOutputCompact() {
         JSModule m = module("m", "var   x   =   1;  \n  ");
         compileModules(m);
         String[] out = getModuleOutput(m);
         assertEquals("var x=1;", out[0].trim());
     }

     // 10. No errors after successful compile
     @Test
     public void testNoErrorsAfterCompile() {
         JSModule a = module("A", "var a=1;");
         JSModule b = module("B", "var b=2;", a);
         compileModules(a, b);
         assertEquals(0, compiler.getErrorCount());
         assertEquals(0, compiler.getWarningCount());
     }

     // 11. CompileModules with no modules returns Result with success?
     @Test
     public void testNoModules() {
         List<JSModule> emptyList = Lists.newArrayList();
         Result r = compiler.compileModules(externs, emptyList, options);
         assertTrue(r.success);
     }

     // 12. ToSourceArray on uninitialized compiler throws appropriate error (defensive)
     @Test(expected = IllegalStateException.class)
     public void testToSourceArrayBeforeCompileThrows() {
         compiler.toSourceArray();
     }
 }