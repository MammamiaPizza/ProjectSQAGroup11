package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import java.util.ArrayList;
 import java.util.List;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.SourceFile;
 import com.google.javascript.jscomp.JSError;
 import com.google.javascript.jscomp.CheckLevel;
 import com.google.javascript.jscomp.ErrorManager;

 public class TypeCheckInterfaceExtendsTest extends TestCase {

  private static final String EXTERNS =
      "/** @constructor */ function Object() {}\n" +
      "/** @constructor */ function Function() {}\n" +
      "/** @constructor */ function Array() {}\n" +
      "/** @constructor */ function String() {}\n" +
      "/** @constructor */ function Number() {}\n" +
      "/** @constructor */ function Boolean() {}\n" +
      "var undefined;\n" +
      "var null;\n";

  private static class CollectingErrorManager implements ErrorManager {
      private final List<JSError> errors = new ArrayList<JSError>();
      private final List<JSError> warnings = new ArrayList<JSError>();
      private double typedPercent = 0.0;

      @Override
      public void report(CheckLevel level, JSError error) {
          if (level == CheckLevel.ERROR) {
              errors.add(error);
          } else if (level == CheckLevel.WARNING) {
              warnings.add(error);
          }
      }

      @Override
      public void generateReport() {}

      @Override
      public int getErrorCount() { return errors.size(); }

      @Override
      public int getWarningCount() { return warnings.size(); }

      @Override
      public JSError[] getErrors() { return errors.toArray(new JSError[0]); }

      @Override
      public JSError[] getWarnings() { return warnings.toArray(new JSError[0]); }

      @Override
      public void setTypedPercent(double typedPercent) { this.typedPercent = typedPercent; }

      @Override
      public double getTypedPercent() { return typedPercent; }
  }

  private List<JSError> compileAndGetWarnings(String js) {
      Compiler compiler = new Compiler();
      CollectingErrorManager errorManager = new CollectingErrorManager();
      compiler.setErrorManager(errorManager);
      CompilerOptions options = new CompilerOptions();
      options.setCheckTypes(true);
      SourceFile externFile = SourceFile.fromCode("externs", EXTERNS);
      SourceFile inputFile = SourceFile.fromCode("input", js);
      try {
          compiler.compile(externFile, inputFile, options);
      } catch (NullPointerException npe) {
          fail("NullPointerException thrown: " + npe.getMessage());
      } catch (Exception e) {
          fail("Unexpected exception: " + e.getMessage());
      }
      List<JSError> result = new ArrayList<JSError>();
      for (JSError e : errorManager.getWarnings()) {
          result.add(e);
      }
      for (JSError e : errorManager.getErrors()) {
          result.add(e);
      }
      return result;
  }

  private boolean containsDiagnosticFor(List<JSError> diagnostics, String typeName) {
      for (JSError e : diagnostics) {
          if (e.description.contains(typeName)) {
              return true;
          }
      }
      return false;
  }

  /**
   * Interface extends a mix of existing and non-existent interfaces.
   * Must not throw NPE and must emit warning mentioning the missing one.
   */
  public void testBadInterfaceExtendsNonExistentInterfaces() {
      String js = "/** @interface */ function A() {}\n" +
                  "/** @interface @extends {A, B} */ function C() {}";
      List<JSError> diagnostics = compileAndGetWarnings(js);
      assertFalse("Expected at least one diagnostic for missing interface B",
diagnostics.isEmpty());
      assertTrue("Expected diagnostic mentioning missing interface 'B'",
              containsDiagnosticFor(diagnostics, "B"));
  }

  /**
   * Interface extends a single non-existent interface.
   */
  public void testInterfaceExtendsSingleNonExistent() {
      String js = "/** @interface @extends {Missing} */ function I() {}";
      List<JSError> diagnostics = compileAndGetWarnings(js);
      assertFalse("Expected diagnostic for missing interface Missing", diagnostics.isEmpty());
      assertTrue("Expected diagnostic mentioning 'Missing'",
              containsDiagnosticFor(diagnostics, "Missing"));
  }

  /**
   * Interface extends a valid interface with no missing ones – no error about missing.
   */
  public void testInterfaceExtendsValidInterface() {
      String js = "/** @interface */ function Base() {}\n" +
                  "/** @interface @extends {Base} */ function Sub() {}";
      List<JSError> diagnostics = compileAndGetWarnings(js);
      assertTrue("Expected no diagnostic mentioning Base as missing",
              !containsDiagnosticFor(diagnostics, "Base"));
  }

  /**
   * Interface with no extends clause – nothing to check.
   */
  public void testInterfaceNoExtends() {
      String js = "/** @interface */ function I() {}";
      List<JSError> diagnostics = compileAndGetWarnings(js);
      // May contain no diagnostic about any missing interface
  }

  /**
   * Interface extends multiple non-existent interfaces.
   */
  public void testInterfaceExtendsMultipleNonexistent() {
      String js = "/** @interface @extends {X, Y} */ function I() {}";
      List<JSError> diagnostics = compileAndGetWarnings(js);
      assertFalse("Expected diagnostic for missing interfaces", diagnostics.isEmpty());
      assertTrue("Expected diagnostic mentioning X or Y",
              containsDiagnosticFor(diagnostics, "X") || containsDiagnosticFor(diagnostics, "Y"));
  }

  /**
   * Interface extending itself – should not throw NPE and may emit a diagnostic.
   */
  public void testInterfaceExtendsSelf() {
      String js = "/** @interface @extends {Self} */ function Self() {}";
      List<JSError> diagnostics = compileAndGetWarnings(js);
      // At minimum, no NPE. A diagnostic about recursive extends may appear.
      // Just verify no NPE (handled in helper). No further assertion required.
  }

  /**
   * Class extending a non-existent class – ensure no NPE in similar path.
   * (Risk: NPE may also affect class extends)
   */
  public void testClassExtendsNonExistent() {
      String js = "/** @constructor @extends {NotFound} */ function Sub() {}";
      List<JSError> diagnostics = compileAndGetWarnings(js);
      // No NPE, may produce a warning about missing type.
  }

 }
