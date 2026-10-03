package com.google.javascript.jscomp;

import com.google.javascript.jscomp.AbstractCompiler.LifeCycleStage;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.DefaultPassConfig;
import com.google.javascript.jscomp.CheckLevel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class DisambiguatePropertiesBugTest extends CompilerTestCase {

 private DisambiguateProperties<JSType> lastPass;

 @Override
 public CompilerPass getProcessor(Compiler compiler) {
     if (compiler.getLifeCycleStage() == LifeCycleStage.NORMALIZED) {
         DefaultPassConfig passConfig = (DefaultPassConfig) compiler.getPassConfig();
         lastPass = passConfig.disambiguateProperties();
         return lastPass;
     }
     return null;
 }

 @Override
 protected int getNumRepetitions() {
     return 1;
 }

 @Override
 protected void setUp() throws Exception {
     super.setUp();
     enableTypeCheck(CheckLevel.WARNING);
 }

 // Test 1: single type with only prototype-inherited property -> rootTypes empty
 public void testSingleTypeOnlyInherited() throws Exception {
     test("function Foo() {} Foo.prototype.a = 1; (new Foo).a;");
     Map<?, ?> rootTypes = getRootTypes("a");
     assertTrue("RootTypes should be empty for inherited property", rootTypes.isEmpty());
 }

 // Test 2: two types sharing inherited property -> no entries
 public void testTwoTypesSharedInherited() throws Exception {
     test("function Parent() {} Parent.prototype.a = 1;" +
          "function Child1() {} Child1.prototype = new Parent();" +
          "function Child2() {} Child2.prototype = new Parent();" +
          "(new Child1).a; (new Child2).a;");
     Map<?, ?> rootTypes = getRootTypes("a");
     assertTrue("RootTypes should be empty for shared inherited property", rootTypes.isEmpty());
 }

 // Test 3: type with own + inherited props -> map contains only own props
 public void testOwnAndInherited() throws Exception {
     test("function Foo() { this.b = 1; }" +
          "Foo.prototype.a = 1;" +
          "(new Foo).a; (new Foo).b;");
     Map<?, ?> rootTypesA = getRootTypes("a");
     assertTrue("Inherited property 'a' rootTypes must be empty", rootTypesA.isEmpty());
     Map<?, ?> rootTypesB = getRootTypes("b");
     assertFalse("Own property 'b' rootTypes must not be empty", rootTypesB.isEmpty());
 }

 // Test 4: empty object literal -> no entries
 public void testEmptyObjectLiteral() throws Exception {
     test("var x = {};");
     Field f = DisambiguateProperties.class.getDeclaredField("properties");
     f.setAccessible(true);
     Map<?, ?> props = (Map<?, ?>) f.get(lastPass);
     assertTrue("Properties map must be empty", props.size() == 0);
 }

 // Test 5: direct own property in constructor -> rootTypes not empty
 public void testDirectOwnProperty() throws Exception {
     test("function Foo() { this.c = 2; } (new Foo).c;");
     Map<?, ?> rootTypes = getRootTypes("c");
     assertFalse("Own property rootTypes should not be empty", rootTypes.isEmpty());
 }

 // Test 6: multi-level inherited property -> rootTypes empty
 public void testMultiLevelInherited() throws Exception {
     test("function Base() {} Base.prototype.a = 1;" +
          "function Derived() {} Derived.prototype = new Base();" +
          "(new Derived).a;");
     Map<?, ?> rootTypes = getRootTypes("a");
     assertTrue("RootTypes must be empty for multi-level inherited property", rootTypes.isEmpty());
 }

 // Helper method to retrieve rootTypes map for a given property name
 private Map<?, ?> getRootTypes(String propName) throws Exception {
     Method getProp = DisambiguateProperties.class.getDeclaredMethod("getProperty", String.class);
     getProp.setAccessible(true);
     Object prop = getProp.invoke(lastPass, propName);
     if (prop == null) {
         fail("Property '" + propName + "' not found.");
     }
     Field rootTypesField = prop.getClass().getDeclaredField("rootTypes");
     rootTypesField.setAccessible(true);
     return (Map<?, ?>) rootTypesField.get(prop);
 }

}