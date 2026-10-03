package com.google.javascript.jscomp.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class IRFactoryForEachTest {

  private Node parse(String source) {
    Compiler compiler = new Compiler();
    compiler.initOptions(new CompilerOptions());
    Node script = compiler.parseSyntheticCode("test.js", source);
    assertNotNull(script);
    return script;
  }

  private Node onlyStatement(Node script) {
    assertEquals(1, script.getChildCount());
    Node statement = script.getFirstChild();
    assertNotNull(statement);
    return statement;
  }

  @Test
  public void testForEachWithVarDeclarationPreservesForEachMarkerAndIterator() {
    Node loop = onlyStatement(parse("for each (var item in values) item;"));

    assertEquals(Token.FOR_IN, loop.getType());
    assertTrue(loop.getBooleanProp(Node.IS_FOR_EACH));

    Node iterator = loop.getFirstChild();
    assertEquals(Token.VAR, iterator.getType());
    assertEquals(1, iterator.getChildCount());
    assertEquals(Token.NAME, iterator.getFirstChild().getType());
    assertEquals("item", iterator.getFirstChild().getString());

    Node iterable = iterator.getNext();
    assertEquals(Token.NAME, iterable.getType());
    assertEquals("values", iterable.getString());
  }

  @Test
  public void testForEachWithAssignmentTargetPreservesTargetAndMarker() {
    Node loop = onlyStatement(parse("for each (item in values) item;"));

    assertEquals(Token.FOR_IN, loop.getType());
    assertTrue(loop.getBooleanProp(Node.IS_FOR_EACH));

    Node iterator = loop.getFirstChild();
    assertEquals(Token.NAME, iterator.getType());
    assertEquals("item", iterator.getString());

    Node iterable = iterator.getNext();
    assertEquals(Token.NAME, iterable.getType());
    assertEquals("values", iterable.getString());
  }

  @Test
  public void testOrdinaryForInIsNotMarkedAsForEach() {
    Node loop = onlyStatement(parse("for (var item in values) item;"));

    assertEquals(Token.FOR_IN, loop.getType());
    assertFalse(loop.getBooleanProp(Node.IS_FOR_EACH));

    Node iterator = loop.getFirstChild();
    assertEquals(Token.VAR, iterator.getType());
    assertEquals("item", iterator.getFirstChild().getString());
  }

  @Test
  public void testForEachWithPropertyAssignmentTargetKeepsPropertyAccess() {
    Node loop = onlyStatement(parse("for each (holder.item in values) holder.item;"));

    assertEquals(Token.FOR_IN, loop.getType());
    assertTrue(loop.getBooleanProp(Node.IS_FOR_EACH));

    Node iterator = loop.getFirstChild();
    assertEquals(Token.GETPROP, iterator.getType());
    assertEquals(Token.NAME, iterator.getFirstChild().getType());
    assertEquals("holder", iterator.getFirstChild().getString());
    assertEquals(Token.STRING, iterator.getFirstChild().getNext().getType());
    assertEquals("item", iterator.getFirstChild().getNext().getString());
  }
}