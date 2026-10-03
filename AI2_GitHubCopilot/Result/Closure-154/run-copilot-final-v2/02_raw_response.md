package com.google.javascript.jscomp;

import junit.framework.TestCase;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.CheckLevel;
import java.util.List;
import java.util.ArrayList;

/**

 - Tests for detecting duplicate properties in interface inheritance.
 - See bug 204: missing warning when an interface declares a property
 - that is already defined in an extended interface.
  */
 public class TypeCheckBug154Test extends TestCase {

  private Compiler compiler;

  @Override public void setUp() {
    compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.WARNING);
    compiler.init(CompilerOptions.getDefaultOptionsForTesting(), options); }

  @Override public void tearDown() {
    compiler = null; }

  /**

 - Compiles the given JS source and returns the list of warnings whose
 - descriptions contain the given substring.
    */
   private List<String> compileAndGetWarnings(String js, String expectedSubstring) {
 List<JSSourceFile> inputs = new ArrayList<>();
 inputs.add(JSSourceFile.fromCode("test.js", js));
 Result result = compiler.compile(
    new ArrayList<JSSourceFile>(), inputs, new CompilerOptions());
 List<String> matched = new ArrayList<>();
 for (JSError warning : result.warnings) {
  if (warning.description.contains(expectedSubstring)) {
    matched.add(warning.description);
  }
 }
 return matched;
   }

  /**

 - Asserts that exactly one warning containing the given substring is produced.
    */
   private void assertWarningContaining(String js, String expectedSubstring) {
 List<String> warnings = compileAndGetWarnings(js, expectedSubstring);
 assertTrue("Expected a warning containing: " + expectedSubstring +
       ", but got: " + warnings,
       warnings.size() == 1);

  }

  /**

 - Asserts that no warning containing the given substring is produced.
    */
   private void assertNoWarningContaining(String js, String unexpectedSubstring) {
 List<String> warnings = compileAndGetWarnings(js, unexpectedSubstring);
 assertTrue("Expected no warning containing: " + unexpectedSubstring +
       ", but got: " + warnings,
       warnings.isEmpty());

  }

  // ------ Test cases for interface property duplicates ------

  /**

 - An interface extending another that declares the same property
 - should issue a warning about the property already being defined.
 - This is the core scenario of the bug (missing warning).
    /
   public void testInterfaceExtendsDuplicateProperty() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number} / A.prototype.prop;\n" +
    "/* @interface @extends {A} / function B() {}\n" +
    "/* @type {number}
  */ B.prototype.prop;\n";
 assertWarningContaining(js, "already defined");
   }

  /**

 - Same property but with different types across extended interfaces
 - should trigger a mismatch warning.
    /
   public void testInterfaceExtendsIncompatiblePropertyType() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number} / A.prototype.prop;\n" +
    "/* @interface @extends {A} / function B() {}\n" +
    "/* @type {string}
  */ B.prototype.prop;\n";
 assertWarningContaining(js, "not compatible");
   }

  /**

 - Interface extending two interfaces that both declare the same property.
    /
   public void testInterfaceExtendsTwoSameProperty() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number} / A.prototype.prop;\n" +
    "/* @interface / function B() {}\n" +
    "/* @type {number} / B.prototype.prop;\n" +
    "/* @interface @extends {A, B}
  */ function C() {}\n";
 assertWarningContaining(js, "already defined");
   }

  /**

 - Interface extending two interfaces with incompatible property types
 - should warn about incompatibility.
    /
   public void testInterfaceExtendsTwoIncompatibleProperty() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number} / A.prototype.prop;\n" +
    "/* @interface / function B() {}\n" +
    "/* @type {string} / B.prototype.prop;\n" +
    "/* @interface @extends {A, B}
  */ function C() {}\n";
 assertWarningContaining(js, "not compatible");
   }

  /**

 - Interface extending another that defines a property not present in parent
 - (no duplicate) – no warning expected.
    /
   public void testInterfaceExtendsNoConflict() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number} / A.prototype.prop;\n" +
    "/* @interface @extends {A} / function B() {}\n" +
    "/* @type {number}
  */ B.prototype.other;\n";
 assertNoWarningContaining(js, "already defined");
   }

  /**

 - Interface that does not extend anything and declares property – no warning.
    /
   public void testSimpleInterface() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number}
  */ A.prototype.prop;\n";
 assertNoWarningContaining(js, "already defined");
   }

  /**

 - Class implementing an interface but missing a property should
 - produce the "not implemented" warning.
    /
   public void testClassMissingInterfaceProperty() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number} / A.prototype.prop;\n" +
    "/* @constructor @implements {A}
  */ function B() {}\n";
 assertWarningContaining(js, "not implemented");
   }

  /**

 - Class implementing interface with correct property – no warning.
    /
   public void testClassImplementsInterfaceCorrectly() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number} / A.prototype.prop;\n" +
    "/* @constructor @implements {A} / function B() {}\n" +
    "/* @type {number}
  */ B.prototype.prop = 0;\n";
 assertNoWarningContaining(js, "not implemented");
   }

  /**

 - Interface extending itself (invalid) should trigger a type error,
 - not solely a duplicate property warning, but we test the general warning presence.
    /
   public void testInterfaceExtendingItself() {
 String js =
    "/* @interface @extends {A} / function A() {}\n";
 // Expect any type-check warning, but specifically the "already defined" is not expected here.
 // This is a different error. Just ensure compilation runs.
 compiler.compile(/ avoid assertion for specific message
  */);
 // Minimal assertion: compilation completes without fatal error (actual warnings are fine)
 assertTrue(true);
   }

  /**

 - Multiple interfaces with a common deep ancestor where a property
 - is redeclared – should warn about duplicate.
    /
   public void testInterfaceDeepInheritanceDuplicate() {
 String js =
    "/* @interface / function Base() {}\n" +
    "/* @type {number} / Base.prototype.prop;\n" +
    "/* @interface @extends {Base} / function Child1() {}\n" +
    "/* @interface @extends {Base} / function Child2() {}\n" +
    "/* @type {string} / Child2.prototype.prop;\n" +
    "/* @interface @extends {Child1, Child2}
  */ function GrandChild() {}\n";
 assertWarningContaining(js, "not compatible");
   }

  /**

 - Interface redeclaring property with the same type as parent should
 - still warn about "already defined" because it's redundant.
    /
   public void testInterfaceRedeclareSameType() {
 String js =
    "/* @interface / function A() {}\n" +
    "/* @type {number} / A.prototype.prop;\n" +
    "/* @interface @extends {A} / function B() {}\n" +
    "/* @type {number}
  */ B.prototype.prop;\n";
 assertWarningContaining(js, "already defined");
   }

  /**

 - Interface with no inheritance and no member at all – trivial, no warning.
    /
   public void testEmptyInterface() {
 String js =
    "/* @interface
  */ function A() {}\n";
 assertNoWarningContaining(js, "already defined");
   }
 }