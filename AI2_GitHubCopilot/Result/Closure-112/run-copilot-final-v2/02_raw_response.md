package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 public class TypeInferenceBug1058Test extends TypeCheckTest {

     public void testIssue1058_InstanceofGeneric() {
         // Regression: instanceof with generic type must not warn
         testSame(
             "/** @template T\n" +
             " * @param {function(new:T)} ctor\n" +
             " * @param {*} obj */\n" +
             "function hasInstance(ctor, obj) {\n" +
             "  if (obj instanceof ctor) {\n" +
             "    var x = /** @type {T} */ (obj);\n" +
             "  }\n" +
             "}");
     }

     public void testTemplatized11_ReturnTemplateParam() {
         // Templatized function returning template param should infer correctly
         testSame(
             "/** @template T\n" +
             " * @param {T} x\n" +
             " * @return {T} */\n" +
             "function id(x) { return x; }\n" +
             "var /** string */ s = id('hello');");
     }

     public void testMultipleTypeParams() {
         // Multiple template type parameters
         testSame(
             "/** @template T,U\n" +
             " * @param {T} a\n" +
             " * @param {U} b\n" +
             " * @return {T} */\n" +
             "function first(a, b) { return a; }\n" +
             "var /** number */ n = first(1, 'str');");
     }

     public void testNestedGenerics() {
         // Function returning a generic type containing template
         testSame(
             "/** @template T\n" +
             " * @param {T} item\n" +
             " * @return {Array.<T>} */\n" +
             "function singleton(item) {\n" +
             "  return [item];\n" +
             "}\n" +
             "var /** Array.<string> */ arr = singleton('x');");
     }

     public void testNullUnionInput() {
         // Template function called with union type (including null)
         testSame(
             "/** @template T\n" +
             " * @param {T} x\n" +
             " * @return {T} */\n" +
             "function passthrough(x) { return x; }\n" +
             "var /** (string|undefined) */ y = " +
             "passthrough(/** @type {string|null} */ ('x'));");
     }

     public void testBackwardsInferenceFromCallSite() {
         // Backwards inference should infer type from call site
         testSame(
             "/** @template T */\n" +
             "function newArrayList() { return []; }\n" +
             "var /** Array.<string> */ list = newArrayList();");
     }

     public void testBindGeneric() {
         // Using bind on a generic function
         testSame(
             "/** @template T\n" +
             " * @param {T} x\n" +
             " * @return {T} */\n" +
             "function id(x) { return x; }\n" +
             "var /** function(string): string */ bound = id.bind(null);");
     }

     public void testTraverseNewTemplateConstructor() {
         // New on a templatized constructor
         testSame(
             "/** @constructor @template T */\n" +
             "function Container() {}\n" +
             "var /** Container.<number> */ c = new Container();");
     }

     public void testUnresolvedTemplateBoundary() {
         // Unresolved template type should not cause spurious warning
         testSame(
             "/** @template T\n" +
             " * @param {T} x */\n" +
             "function f(x) {\n" +
             "  var y = x;\n" +
             "}\n" +
             "f(42);");
     }

     public void testNarrowingAfterInstanceof() {
         // instanceof narrowing should work with template types
         testSame(
             "/** @template T\n" +
             " * @param {T} x */\n" +
             "function check(x) {\n" +
             "  if (x instanceof Array) {\n" +
             "    x.length;\n" +
             "  }\n" +
             "}\n" +
             "check([]);");
     }

     public void testCallbackParameterInference() {
         // The type of a callback argument should be inferred from the function parameter type
         testSame(
             "/** @template T\n" +
             " * @param {function(T): void} callback\n" +
             " * @param {T} value */\n" +
             "function forEach(callback, value) { callback(value); }\n" +
             "forEach(function(x) { x.toString(); }, 42);");
     }

     public void testCrossScopeNarrowedTemplate() {
         // Narrowed template type across scopes
         testSame(
             "/** @template T\n" +
             " * @param {T} obj */\n" +
             "function wrapper(obj) {\n" +
             "  function inner() {\n" +
             "    return obj;\n" +
             "  }\n" +
             "  return inner();\n" +
             "}\n" +
             "var /** number */ n = wrapper(5);");
     }
 }