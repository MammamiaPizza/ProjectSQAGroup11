package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.CollapseProperties;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerPass;
 import com.google.javascript.jscomp.CompilerTestCase;

 public class CollapsePropertiesBug156Test extends CompilerTestCase {

   @Override protected void setUp() throws Exception {
     super.setUp();
   }

   @Override protected CompilerPass getProcessor(Compiler compiler) {
     return new CollapseProperties(compiler, false, true);
   }

   public void testAliasedTopLevelEnum() {
     test(
       "var MyEnum = {ACTIVE:1, INACTIVE:0}; var theAlias = MyEnum; var state = theAlias.ACTIVE;",
       "var MyEnum$ACTIVE=1;var MyEnum$INACTIVE=0;var theAlias=MyEnum;var state=MyEnum$ACTIVE"
     );
   }

   public void testIssue389() {
     test(
       "var lib = {}; lib.Order = {ASC:1, DESC:0}; var ord = lib.Order; var dir = ord.ASC;",
       "var lib$Order$ASC=1;var lib$Order$DESC=0;var ord=lib.Order;var dir=lib$Order$ASC"
     );
   }

   public void testAliasedNestedEnum() {
     test(
       "var root = {}; root.Constants = {ONE:1, TWO:2}; var c = root.Constants; function f() {
return c.TWO; }",
       "var root$Constants$ONE=1;var root$Constants$TWO=2;var c=root.Constants;function f(){return
root$Constants$TWO}"
     );
   }

   public void testChainAliasing() {
     test(
       "var A = {}; A.B = {}; A.B.C = 42; var alias1 = A.B; var alias2 = alias1.C; alert(alias2);",
       "var A$B$C=42;var alias1=A.B;var alias2=A$B$C;alert(alias2)"
     );
   }

   public void testAliasedNamespaceProperty() {
     test(
       "var ns = {}; ns.value = 10; var v = ns; alert(v.value);",
       "var ns$value=10;var v=ns;alert(ns$value)"
     );
   }

   public void testEmptyAliasPath() {
     test(
       "var a = {}; a.b = 5; var c = a;",
       "var a$b=5;var c=a"
     );
   }

   public void testAliasUsedBeforeDeclaration() {
     test(
       "var c = a; var a = {}; a.b = 5;",
       "var c=a;var a$b=5"
     );
   }

   public void testAliasAfterRedefinition() {
     test(
       "var a = {}; a.b = 5; var c = a; a.b = 10; alert(c.b);",
       "var a$b=5;var c=a;a$b=10;alert(a$b)"
     );
   }

   public void testMultipleAliases() {
     test(
       "var config = {slow:false}; var c1 = config; var c2 = config; alert(c1.slow && c2.slow);",
       "var config$slow=false;var c1=config;var c2=config;alert(config$slow&&config$slow)"
     );
   }
 }
