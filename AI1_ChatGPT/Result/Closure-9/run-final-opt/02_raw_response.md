package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProcessCommonJSModulesBugTest {

  @Test
  public void testGuessModuleNameNormalizesWindowsSeparatorsBeforeRemovingPrefix() {
    ProcessCommonJSModules processor =
        new ProcessCommonJSModules(null, "foo");

    assertEquals("module$baz", processor.guessCJSModuleName("foo\\baz.js"));
  }

  @Test
  public void testGuessModuleNameNormalizesNestedWindowsSeparators() {
    ProcessCommonJSModules processor =
        new ProcessCommonJSModules(null, "src");

    assertEquals(
        "module$lib$my_module",
        processor.guessCJSModuleName("src\\lib\\my-module.js"));
  }

  @Test
  public void testToModuleNameConvertsStandardFilenameComponents() {
    assertEquals(
        "module$foo$bar_baz",
        ProcessCommonJSModules.toModuleName("./foo/bar-baz.js"));
  }

  @Test
  public void testToModuleNameResolvesRelativeRequireAgainstCurrentFile() {
    assertEquals(
        "module$dir$dependency",
        ProcessCommonJSModules.toModuleName("../dependency.js", "dir/sub/current.js"));
  }
}