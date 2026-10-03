package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class NormalizeIssue115Test {

  private Compiler createCompiler(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        new JSSourceFile[0],
        new JSSourceFile[] {JSSourceFile.fromCode("test.js", source)},
        options);
    compiler.parseInputs();
    return compiler;
  }

  private Compiler normalize(String source) {
    Compiler compiler = createCompiler(source);
    new Normalize(compiler, false).process(
        compiler.getExternsRoot(), compiler.getJsRoot());
    return compiler;
  }

  private Node findFirst(Node node, int type) {
    if (node.getType() == type) {
      return node;
    }
    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      Node result = findFirst(child, type);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private int countType(Node node, int type) {
    int count = node.getType() == type ? 1 : 0;
    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      count += countType(child, type);
    }
    return count;
  }

  private boolean isInsideFunction(Node node) {
    for (Node current = node.getParent(); current != null; current = current.getParent()) {
      if (current.getType() == Token.FUNCTION) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void testSplitsMultipleVariableDeclarationIntoSingleDeclarations() {
    Compiler compiler = normalize("var first = 1, second = 2, third;");
    Node jsRoot = compiler.getJsRoot();

    assertEquals(3, countType(jsRoot, Token.VAR));

    int namesInVars = 0;
    for (Node node = jsRoot.getFirstChild(); node != null; node = node.getNext()) {
      namesInVars += countNamesDirectlyUnderVars(node);
    }
    assertEquals(3, namesInVars);
  }

  private int countNamesDirectlyUnderVars(Node node) {
    int count = 0;
    if (node.getType() == Token.VAR) {
      for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
        assertEquals(Token.NAME, child.getType());
        count++;
      }
    }
    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      count += countNamesDirectlyUnderVars(child);
    }
    return count;
  }

  @Test
  public void testWrapsSimpleLabeledStatementInBlock() {
    Compiler compiler = normalize("target: work();");
    Node label = findFirst(compiler.getJsRoot(), Token.LABEL);

    assertNotNull(label);
    Node body = label.getLastChild();
    assertEquals(Token.BLOCK, body.getType());
    assertNotNull(body.getFirstChild());
    assertEquals(Token.EXPR_RESULT, body.getFirstChild().getType());
    assertEquals(body.getFirstChild(), body.getLastChild());
  }

  @Test
  public void testDoesNotWrapLabeledLoopInAdditionalBlock() {
    Compiler compiler = normalize("retry: while (condition) { break retry; }");
    Node label = findFirst(compiler.getJsRoot(), Token.LABEL);

    assertNotNull(label);
    assertEquals(Token.WHILE, label.getLastChild().getType());
  }

  @Test
  public void testMovesNestedFunctionDeclarationsBeforeExecutableStatements() {
    Compiler compiler = normalize(
        "function outer() {"
            + " before();"
            + " function later() {}"
            + " after();"
            + " function last() {}"
            + "}");
    Node outer = findFirst(compiler.getJsRoot(), Token.FUNCTION);
    assertNotNull(outer);

    Node body = outer.getLastChild();
    Node first = body.getFirstChild();
    Node second = first.getNext();

    assertEquals(Token.FUNCTION, first.getType());
    assertEquals("later", first.getFirstChild().getString());
    assertEquals(Token.FUNCTION, second.getType());
    assertEquals("last", second.getFirstChild().getString());
    assertEquals(Token.EXPR_RESULT, second.getNext().getType());
  }

  @Test
  public void testRemovesDuplicateVarDeclarationByConvertingInitializerToAssignment() {
    Compiler compiler = normalize("var value = 1; var value = 2;");
    Node jsRoot = compiler.getJsRoot();

    assertEquals(1, countType(jsRoot, Token.VAR));
    assertEquals(1, countType(jsRoot, Token.ASSIGN));
  }

  @Test
  public void testConstantAnnotationsRespectShadowingAcrossScopes() {
    Compiler compiler = normalize(
        "/** @const */ var shadow = 1;"
            + "function f() { var shadow = 2; return shadow; }"
            + "shadow;");

    int globalNames = 0;
    int localNames = 0;
    for (Node node = firstName(compiler.getJsRoot(), "shadow");
        node != null;
        node = nextName(node, "shadow")) {
      if (isInsideFunction(node)) {
        localNames++;
        assertFalse(node.getBooleanProp(Node.IS_CONSTANT_NAME));
      } else {
        globalNames++;
        assertTrue(node.getBooleanProp(Node.IS_CONSTANT_NAME));
      }
    }

    assertEquals(2, globalNames);
    assertEquals(2, localNames);
  }

  @Test
  public void testVerifyConstantsAllowsConstantAndNonConstantShadowedNames() {
    Compiler compiler = normalize(
        "/** @const */ var shadow = 1;"
            + "function f() { var shadow = 2; return shadow; }"
            + "shadow;");

    new Normalize.VerifyConstants(compiler, true).process(
        compiler.getExternsRoot(), compiler.getJsRoot());
  }

  @Test(expected = IllegalStateException.class)
  public void testAssertOnChangeRejectsNewConstantAnnotation() {
    Compiler compiler = createCompiler("/** @const */ var annotated = 1; annotated;");

    new Normalize(compiler, true).process(
        compiler.getExternsRoot(), compiler.getJsRoot());
  }

  private Node firstName(Node node, String name) {
    if (node.getType() == Token.NAME && name.equals(node.getString())) {
      return node;
    }
    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      Node result = firstName(child, name);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private Node nextName(Node node, String name) {
    Node parent = node.getParent();
    for (Node sibling = node.getNext(); sibling != null; sibling = sibling.getNext()) {
      Node result = firstName(sibling, name);
      if (result != null) {
        return result;
      }
    }
    while (parent != null) {
      for (Node sibling = parent.getNext(); sibling != null; sibling = sibling.getNext()) {
        Node result = firstName(sibling, name);
        if (result != null) {
          return result;
        }
      }
      parent = parent.getParent();
    }
    return null;
  }
}
