package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 public class ProcessCommonJSModulesTest extends TestCase {

     public void testToModuleNameNormalPath() {
         assertEquals("module$foo$bar", ProcessCommonJSModules.toModuleName("foo/bar.js"));
     }

     public void testToModuleNameLeadingDotSlash() {
         assertEquals("module$bar", ProcessCommonJSModules.toModuleName("./bar.js"));
     }

     public void testToModuleNameBackslash() {
         assertEquals("module$foo$bar", ProcessCommonJSModules.toModuleName("foo\\bar.js"));
     }

     public void testToModuleNameMixedSlashes() {
         assertEquals("module$a$b$c", ProcessCommonJSModules.toModuleName("a/b\\c.js"));
     }

     public void testToModuleNameOnlyExtension() {
         assertEquals("module$", ProcessCommonJSModules.toModuleName(".js"));
     }

     public void testToModuleNameEmpty() {
         assertEquals("module$", ProcessCommonJSModules.toModuleName(""));
     }

     public void testToModuleNameDashReplacement() {
         assertEquals("module$my_var", ProcessCommonJSModules.toModuleName("my-var.js"));
     }

     public void testGuessModuleNameNormal() {
         ProcessCommonJSModules p = new ProcessCommonJSModules(null, "foo");
         assertEquals("module$baz", p.guessCJSModuleName("foo/baz.js"));
     }

     public void testGuessModuleNameWindows() {
         ProcessCommonJSModules p = new ProcessCommonJSModules(null, "foo");
         assertEquals("module$baz", p.guessCJSModuleName("foo\\baz.js"));
     }

     public void testGuessModuleNameRelative() {
         ProcessCommonJSModules p = new ProcessCommonJSModules(null, ".");
         assertEquals("module$bar", p.guessCJSModuleName("./foo/bar.js"));
     }

     public void testGuessModuleNameRelativeWindows() {
         ProcessCommonJSModules p = new ProcessCommonJSModules(null, ".");
         assertEquals("module$bar", p.guessCJSModuleName(".\\foo\\bar.js"));
     }

     public void testGuessModuleNameNull() {
         ProcessCommonJSModules p = new ProcessCommonJSModules(null, ".");
         try {
             p.guessCJSModuleName(null);
             fail("Expected NullPointerException");
         } catch (NullPointerException e) {
             // expected
         }
     }
 }
