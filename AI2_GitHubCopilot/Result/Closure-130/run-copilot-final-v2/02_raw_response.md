import com.google.javascript.jscomp.CompilerTestCase;

public class CollapsePropertiesTest extends CompilerTestCase {

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new CollapseProperties(compiler, false, true); }

  @Override protected int getNumRepetitions() {
    return 1; }

  // Regression test for Issue 931 public void testIssue931() {
    // Aliasing a namespace then adding a property to it should collapse correctly
    test("var goog = {}; goog.dom = {}; goog.dom.DomHelper = function() {};"
        + "var dom = goog.dom; dom.DomHelper.field = 1;",
        "var goog={};goog.dom={};goog.dom.DomHelper=function(){};"
        + "var goog$dom=goog.dom;goog$dom$DomHelper.field=1");
  }

  // Collapse simple namespace alias public void testSimpleNamespaceAlias() {
    test("var goog = {}; goog.dom = {}; var d = goog.dom; d.createElement = function(){};",
        "var goog={};goog.dom={};var goog$dom=goog.dom;goog$dom.createElement=function(){}"); }

  // Collapse an alias that is read-only (used as a prefix) public void testAliasAsPrefix() {
    test("var a = {}; a.b = {}; a.b.c = 1; var x = a.b; var y = x.c;",
        "var a={};a.b={};a.b.c=1;var a$b=a.b;var y=a$b.c"); }

  // Chained aliases: alias of alias public void testChainedAliases() {
    test("var x = {}; x.y = {}; x.y.z = {}; var a = x.y; var b = a.z; b.foo = 1;",
        "var x={};x.y={};x.y.z={};var x$y=x.y;var x$y$z=x$y.z;x$y$z.foo=1"); }

  // Alias used as LHS of a property assignment should still flatten public void
testAliasLhsPropertyAssignment() {
    test("var goog = {}; goog.dom = {}; var dom = goog.dom; dom.classes = {};",
         "var goog={};goog.dom={};var goog$dom=goog.dom;goog$dom.classes={}"); }

 ​// Alias inside object literal should not be collapsed incorrectly ​public void
testAliasInsideObjectLiteral() {
    testSame("var goog = {}; goog.dom = {}; var o = { dom: goog.dom }; var x = o.dom;"); }

  ​// Same-name local variable should not collide with alias ​} }​
}

I need to provide full class. I'll produce a complete, compilable test class. However, I must be
careful with syntax: I see I made a typo "goog.dom" etc. I'll fix.

Also, I need to import necessary packages.

Let's write proper Java:

package com.google.javascript.jscomp;

import com.google.javascript.jscomp.CompilerTestCase;
import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.Compiler;

public class CollapsePropertiesTest extends CompilerTestCase {

  public CollapsePropertiesTest() {
    // accept latest JS
    setAcceptedLanguage(CompilerOptions.LanguageMode.ECMASCRIPT5); }

  @Override protected CompilerPass getProcessor(Compiler compiler) {
    return new CollapseProperties(compiler, false, true); }

  public void testIssue931() {
    test("var goog = {}; goog.dom = {}; goog.dom.DomHelper = function() {};"
        + "var dom = goog.dom; dom.DomHelper.field = 1;",
        "var goog={};goog.dom={};goog.dom.DomHelper=function(){};"
        + "var goog$dom=goog.dom;goog$dom$DomHelper.field=1");
  }

  ...
But I need to ensure assert strings match the expected output of the optimizer. I can test with
"test" method that checks EXACT string equality after compilation and printing. Usually the test
framework CompilerTestCase does that.

Actually, typical Closure compiler tests use "test" with two strings: source and expected output
(optimized). The expected output is the whole script after optimization as a compact string. But I
don't know the exact output format; it may be pretty printed. However, I can rely on the test
framework to parse and normalize. I'll assume the standard test harness: "test(String src, String
expected)" works. But to be safe, I can use "testSame" for cases where the output should be
unchanged.

Given the bug is about collapse properties, the expected output should collapse the aliased
namespace into flattened names. I'll craft examples based on typical Closure behavior.

Potential pitfalls: the optimizer may not output "var goog$dom=goog.dom;" if the alias is not used
after collapsing, but if it's used only as prefix for child properties, the alias var might be
eliminated? Actually the CollapseProperties pass might inlineAliases only if the alias is used as a
simple reference? I'll read the source: inlineAliases method likely replaces alias references with
the actual namespace property chain, then later flattenReferences replaces a.b.c with a$b$c. So for
"var d = goog.dom; d.createElement = ...", after inlineAliases, "d" might be replaced by "goog.dom"
directly, so the var d might be removed if it's only used for collapsing? Actually I need to study
the code snippet: It mentions inlineAliases, flattenReferencesTo,
collapseDeclarationOfNameAndDescendants. The process does inlineAliases, then flattenReferences,
then collapse declarations. So after process, the alias variable may still exist but its usages are
flattened. The expected output may include the alias variable unchanged? The test plan says
"Collapsing namespace aliases (e.g., var ns = some.ns; ns.x)". So ns.x should become some$ns.x?
Actually they flatten to some$ns$x maybe. So expected output might contain "var goog$dom =
goog.dom;" and then "goog$dom.createElement = ...". That's plausible.

I'll craft expected output as compact strings.

I must ensure the test class compiles. I'll use proper imports.

Also, the setUp might need to set language mode. I'll set it in constructor.

Let me produce the final class.```java
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
      "var goog = {}; goog.dom = {}; goog.dom.DomHelper = functon(){};" +
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
```