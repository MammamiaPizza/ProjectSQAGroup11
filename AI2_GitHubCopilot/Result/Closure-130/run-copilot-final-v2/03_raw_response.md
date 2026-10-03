package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerTestCase;

public class CollapsePropertiesTest extends CompilerTestCase {

  public CollapsePropertiesTest() {
    setAcceptedLanguage(CompilerOptions.LanguageMode.ECMASCRIPT5); }

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new CollapseProperties(compiler, false, true); }

  // Regression test for Issue 931: aliased namespace properties should collapse public void
testIssue931() {
    test(
      "var goog = {}; goog.dom = {}; goog.dom.DomHelper = function(){};" +
      "var dom = goog.dom; dom.DomHelper.field = 1;",
      "var goog={};goog.dom={};goog.dom.DomHelper=function(){};" +
      "var goog$dom=goog.dom;goog$dom$DomHelper.field=1"
    ); }

  // Simple namespace alias collapse: alias variable becomes prefix public void testSimpleAlias() {
    test(
      "var a = {}; a.b = {}; var x = a.b; x.c = 1;",
      "var a={};a.b={};var a$b=a.b;a$b.c=1"
    ); }

  // Chained aliases should collapse through the chain public void testChainedAliases() {
    test(
      "var x = {}; x.y = {}; x.y.z = {}; var a = x.y; var b = a.z; b.w = 0;",
      "var x={};x.y={};x.y.z={};var x$y=x.y;var x$y$z=x$y.z;x$y$z.w=0"
    ); }

  // Aliased namespace used as left-hand side of a property assignment public void
testAliasLeftHandSide() {
    test(
      "var ns = {}; ns.sub = {}; var sub = ns.sub; sub.val = 5;",
      "var ns={};ns.sub={};var ns$sub=ns.sub;ns$sub.val=5"
    ); }

  // Alias with dot access after being assigned should flatten public void testAliasDotAccess() {
    test(
      "var f = {}; f.g = {}; f.g.h = {}; var g = f.g; var v = g.h.x;",
      "var f={};f.g={};f.g.h={};var f$g=f.g;var v=f$g.h.x"
    ); }

  // Multiple aliases to same namespace public void testMultipleAliasesSameNamespace() {
    test(
      "var lib = {}; lib.core = {}; var c1 = lib.core; var c2 = lib.core;" +
      "c1.a = 1; c2.b = 2;",
      "var lib={};lib.core={};var lib$core=lib.core;" +
      "var lib$core=lib.core;lib$core.a=1;lib$core.b=2"
    ); }

  // Alias used as both read and write public void testAliasReadWrite() {
    test(
      "var p = {}; p.q = {}; p.q.r = 10; var q = p.q; var t = q.r; q.s = t;",
      "var p={};p.q={};p.q.r=10;var p$q=p.q;var t=p$q.r;p$q.s=t"
    ); }

  // Alias inside object literal should NOT collapse the key public void testAliasInObjectLiteral()
{
    testSame(
      "var top = {}; top.mid = {}; var m = top.mid; var o = {key: m}; var val = o.key;"
    ); }

  // Same-name local variable should not interfere with collapsed alias public void
testAliasSameNameLocal() {
    test(
      "var goog = {}; goog.ui = {}; var ui = goog.ui; function f() { var ui = 1; return ui; } ui.x =
f();",
      "var goog={};goog.ui={};var goog$ui=goog.ui;function f(){var ui=1;return ui}goog$ui.x=f()"
    ); }

  // Aliased namespace with nested assignments should collapse properly public void
testAliasNestedProperties() {
    test(
      "var a = {}; a.b = {}; a.b.c = {}; var b = a.b; b.c.d = 42;",
      "var a={};a.b={};a.b.c={};var a$b=a.b;a$b.c.d=42"
    ); }

  // Referencing aliased namespace in a function call public void testAliasInFunctionCall() {
    test(
      "var goog = {}; goog.array = {}; goog.array.forEach = function(){};" +
      "var arr = goog.array; arr.forEach([1,2,3]);",
      "var goog={};goog.array={};goog.array.forEach=function(){};" +
      "var goog$array=goog.array;goog$array.forEach([1,2,3]);"
    ); }

  // Alias declared but never used (stub) public void testAliasUnused() {
    testSame(
      "var goog = {}; goog.dom = {}; var dom = goog.dom;"
    ); }
}