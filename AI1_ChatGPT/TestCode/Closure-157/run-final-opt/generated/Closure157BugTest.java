package com.google.javascript.jscomp;

import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.jscomp.SourceFile;
import java.util.Collections;
import org.junit.Test;

public class Closure157BugTest {

  private String print(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setLanguageIn(LanguageMode.ECMASCRIPT5);
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input.js", source)),
        options);
    return compiler.toSource();
  }

  @Test
  public void testNumericObjectLiteralKeyIsPrintedAsNumericComputedKey() {
    String output = print("var x={1:1};");

    assertTrue(
        "Numeric object literal keys must not be converted into quoted strings: " + output,
        output.contains("{[1]:1}"));
  }

  @Test
  public void testExponentObjectLiteralKeyPreservesItsLexicalForm() {
    String output = print("var x={3E9:1};");

    assertTrue(
        "Exponent-form object literal keys must not be canonicalized or quoted: " + output,
        output.contains("{[3E9]:1}"));
  }

  @Test
  public void testQuotedGetterKeyRetainsBracketsAndQuotes() {
    String output = print("var x={get \"a\"(){return 1}};");

    assertTrue(
        "Quoted getter names must remain quoted computed keys: " + output,
        output.contains("get [\"a\"](){return 1}"));
  }

  @Test
  public void testQuotedSetterKeyRetainsBracketsAndQuotes() {
    String output = print("var x={set \"a\"(v){}};");

    assertTrue(
        "Quoted setter names must remain quoted computed keys: " + output,
        output.contains("set [\"a\"](v){}"));
  }

  @Test
  public void testOrdinaryIdentifierObjectLiteralKeyRemainsIdentifier() {
    String output = print("var x={plain:1};");

    assertTrue(
        "Identifier object literal keys should continue to print without brackets: " + output,
        output.contains("{plain:1}"));
  }
}
