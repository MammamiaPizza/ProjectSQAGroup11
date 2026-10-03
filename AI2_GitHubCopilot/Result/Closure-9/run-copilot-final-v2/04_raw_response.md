@Test
    public void testToModuleNameTwoArgsSimple() {
        assertEquals("module$bar",
            com.google.javascript.jscomp.ProcessCommonJSModules.toModuleName(
                "bar.js", "foo.js"));
    }

 @Test
 public void testToModuleNameTwoArgsRelativeDotSlash() {
     assertEquals("module$a$bar",
         com.google.javascript.jscomp.ProcessCommonJSModules.toModuleName(
             "./bar.js", "a/b.js"));
 }

 @Test
 public void testToModuleNameTwoArgsRelativeDotDotSlash() {
     assertEquals("module$bar",
com.google.javascript.jscomp.ProcessCommonJSModules.toModuleName(                "../bar.js",
"a/b.js"));}

 @Test
 public void testToModuleNameTwoArgsRelativeBackslash() {
     assertEquals("module$foo\\bar",
         com.google.javascript.jscomp.ProcessCommonJSModules.toModuleName(
             ".\\bar.js", "foo\\baz.js"));
 }