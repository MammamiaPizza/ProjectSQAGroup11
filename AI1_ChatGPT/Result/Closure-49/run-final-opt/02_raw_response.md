package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.base.Supplier;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class MakeDeclaredNamesUniqueGeneratedTest {

  private static final class CountingSupplier implements Supplier<String> {
    private int next = 1;

    @Override
    public String get() {
      return String.valueOf(next++);
    }
  }

  private static Node name(String value) {
    return Node.newString(Token.NAME, value);
  }

  private static Node function(Node functionName, Node params, Node body) {
    Node function = new Node(Token.FUNCTION);
    function.addChildToBack(functionName);
    function.addChildToBack(params);
    function.addChildToBack(body);
    return function;
  }

  @Test
  public void testInlineRenamerRenamesDeclarationAndReferencesConsistently() {
    MakeDeclaredNamesUnique.InlineRenamer renamer =
        new MakeDeclaredNamesUnique.InlineRenamer(
            new CountingSupplier(), "inline", false);

    renamer.addDeclaredName("value");

    String replacement = renamer.getReplacementName("value");
    assertTrue(replacement.startsWith("value"));
    assertTrue(replacement.endsWith("inline1"));
    assertEquals(replacement, renamer.getReplacementName("value"));
  }

  @Test
  public void testInlineRenamerUsesDifferentNamesForChildScopeDeclarations() {
    MakeDeclaredNamesUnique.InlineRenamer parent =
        new MakeDeclaredNamesUnique.InlineRenamer(
            new CountingSupplier(), "inline", false);
    parent.addDeclaredName("x");

    MakeDeclaredNamesUnique.Renamer child = parent.forChildScope();
    child.addDeclaredName("x");

    assertFalse(parent.getReplacementName("x").equals(child.getReplacementName("x")));
    assertTrue(parent.getReplacementName("x").endsWith("inline1"));
    assertTrue(child.getReplacementName("x").endsWith("inline2"));
  }

  @Test
  public void testInlineRenamerStripsExistingContextualSuffixBeforeAddingInlineSuffix() {
    MakeDeclaredNamesUnique.InlineRenamer renamer =
        new MakeDeclaredNamesUnique.InlineRenamer(
            new CountingSupplier(), "inline", false);

    renamer.addDeclaredName("local$$17");

    assertEquals("local$$inline1", renamer.getReplacementName("local$$17"));
  }

  @Test
  public void testInlineRenamerLeavesEmptyFunctionExpressionNameEmpty() {
    MakeDeclaredNamesUnique.InlineRenamer renamer =
        new MakeDeclaredNamesUnique.InlineRenamer(
            new CountingSupplier(), "inline", false);

    renamer.addDeclaredName("");

    assertEquals("", renamer.getReplacementName(""));
    assertNull(renamer.getReplacementName("notDeclared"));
  }

  @Test
  public void testInlineRenamerRejectsArgumentsAsDeclaredName() {
    MakeDeclaredNamesUnique.InlineRenamer renamer =
        new MakeDeclaredNamesUnique.InlineRenamer(
            new CountingSupplier(), "inline", false);

    try {
      renamer.addDeclaredName(MakeDeclaredNamesUnique.ARGUMENTS);
      fail("arguments is not a renameable declaration");
    } catch (IllegalStateException expected) {
      assertTrue(true);
    }
  }

  @Test
  public void testOriginalNameExtractionHandlesGeneratedAndOrdinaryNames() {
    assertEquals("plain", MakeDeclaredNamesUnique.ContextualRenameInverter.getOrginalName("plain"));
    assertEquals("x", MakeDeclaredNamesUnique.ContextualRenameInverter.getOrginalName("x$$1"));
    assertEquals("", MakeDeclaredNamesUnique.ContextualRenameInverter.getOrginalName("$$inline1"));
  }

  @Test
  public void testContextualRenameInverterRestoresLocalParameterAndVariableNames() {
    Node script = new Node(Token.SCRIPT);
    Node params = new Node(Token.PARAM_LIST);
    Node body = new Node(Token.BLOCK);

    Node parameter = name("arg$$1");
    params.addChildToBack(parameter);

    Node localDeclaration = name("local$$2");
    Node var = new Node(Token.VAR);
    var.addChildToBack(localDeclaration);
    body.addChildToBack(var);

    Node localUse = name("local$$2");
    Node expression = new Node(Token.EXPR_RESULT);
    expression.addChildToBack(localUse);
    body.addChildToBack(expression);

    Node parameterUse = name("arg$$1");
    Node ret = new Node(Token.RETURN);
    ret.addChildToBack(parameterUse);
    body.addChildToBack(ret);

    script.addChildToBack(function(name("f"), params, body));

    Compiler compiler = new Compiler();
    CompilerPass inverter = MakeDeclaredNamesUnique.getContextualRenameInverter(compiler);
    inverter.process(null, script);

    assertEquals("arg", parameter.getString());
    assertEquals("local", localDeclaration.getString());
    assertEquals("local", localUse.getString());
    assertEquals("arg", parameterUse.getString());
  }
}