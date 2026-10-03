package com.google.javascript.jscomp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.junit.Test;

public class Closure134RegressionTest extends TestCase {

  @Test
  public void testAmbiguatePropertiesKeepsImplementedAndInheritedPropertiesDistinct() {
    String source =
        "/** @interface */ function I() {}\n"
            + "/** @return {number} */ I.prototype.foo = function() { return 1; };\n"
            + "/** @constructor @implements {I} */ function A() {}\n"
            + "/** @return {number} */ A.prototype.foo = function() { return 1; };\n"
            + "/** @return {number} */ A.prototype.bar = function() { return 2; };\n"
            + "/** @constructor @extends {A} */ function B() {}\n"
            + "/** @return {number} */ B.prototype.baz = function() { return 3; };\n"
            + "var b = new B();\n"
            + "b.foo(); b.bar(); b.baz();\n";

    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    options.ambiguateProperties = true;

    Compiler compiler = new Compiler();
    Result result =
        compiler.compile(
            JSSourceFile.fromCode("externs.js", ""),
            JSSourceFile.fromCode("input.js", source),
            options);

    assertEquals(0, result.errors.length);

    Matcher matcher =
        Pattern.compile("(?:I|A|B)\\.prototype\\.([A-Za-z$_][A-Za-z0-9$_]*)=")
            .matcher(compiler.toSource());
    List<String> declarationNames = new ArrayList<String>();
    while (matcher.find()) {
      declarationNames.add(matcher.group(1));
    }

    assertEquals(4, declarationNames.size());
    assertEquals(declarationNames.get(0), declarationNames.get(1));

    Set<String> distinctNames = new HashSet<String>();
    distinctNames.add(declarationNames.get(0));
    distinctNames.add(declarationNames.get(2));
    distinctNames.add(declarationNames.get(3));
    assertEquals(3, distinctNames.size());
  }

  @Test
  public void testTypeCheckWarnsWhenBaseInstanceIsAssignedToExtendedType() {
    String source =
        "/** @constructor */ function A() {}\n"
            + "/** @constructor @extends {A} */ function B() {}\n"
            + "/** @type {B} */ var b = new A();\n";

    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;

    Compiler compiler = new Compiler();
    Result result =
        compiler.compile(
            JSSourceFile.fromCode("externs.js", ""),
            JSSourceFile.fromCode("input.js", source),
            options);

    assertEquals(0, result.errors.length);
    assertTrue(
        "Assigning an A instance to a B variable must produce a type warning",
        result.warnings.length > 0);
  }
}