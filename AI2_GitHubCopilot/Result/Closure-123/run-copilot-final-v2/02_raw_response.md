package com.google.javascript.jscomp;

import junit.framework.TestCase;

public class CodeGeneratorBug123Test extends TestCase {

  private Compiler compiler;

  @Override public void setUp() throws Exception {
    compiler = new Compiler();
    compiler.initOptions(new CompilerOptions()); }

  private String print(String js) {
    Node root = compiler.parseSyntheticCode("test", js);
    return new CodePrinter.Builder(root).setPrettyPrint(false).build().trim(); }

  public void testForInitTernaryArrayIn() {
    String code = print("for(a=c?0:[0 in d];;)foo()");
    assertEquals("for(a=c?0:[(0 in d)];;)foo()", code); }

  public void testForInitCommaWithIn() {
    String code = print("for(a=0 in d,b=1;;)foo()");
    assertEquals("for(a=(0 in d),b=1;;)foo()", code); }

  public void testForInitVarWithIn() {
    String code = print("for(var a=0 in d;;)foo()");
    assertEquals("for(var a=(0 in d);;)foo()", code); }

  public void testForTestWithIn() {
    String code = print("for(;0 in d;)foo()");
    assertEquals("for(;0 in d;)foo()", code); }

  public void testForInLoopNoExtraParens() {
    String code = print("for(a in d)foo()");
    assertEquals("for(a in d)foo()", code); }

  public void testNormalInOperator() {
    String code = print("a=0 in d;");
    assertEquals("a=0 in d;", code); }

  public void testForInitNoIn() {
    String code = print("for(a=0,b=1;;)foo()");
    assertEquals("for(a=0,b=1;;)foo()", code); }

  public void testForInitTernaryArrayInWithNestedExpr() {
    String code = print("for(a=c?0:[(0 in d)*2];;)foo()");
    assertEquals("for(a=c?0:[(0 in d)*2];;)foo()", code); }

  public void testForInitTernaryArrayInWithInlineFunction() {
    String code = print("for(a=c?0:[function(){return 0 in d}];;)foo()");
    assertEquals("for(a=c?0:[function(){return 0 in d}];;)foo()", code); }
}