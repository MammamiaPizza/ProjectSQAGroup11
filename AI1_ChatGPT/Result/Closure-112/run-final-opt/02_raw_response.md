package com.google.javascript.jscomp;

import junit.framework.TestCase;
import org.junit.Test;

public final class TypeInferenceRegressionTest extends CompilerTestCase {

  public TypeInferenceRegressionTest() {
    super();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new TypeCheck(
        compiler,
        compiler.getReverseAbstractInterpreter(),
        compiler.getTypeRegistry());
  }

  @Test
  public void testTemplatedConstructorInfersArgumentType() {
    testSame(
        "/** @constructor @template T @param {T} value */\n"
            + "function Box(value) {\n"
            + "  /** @type {T} */ this.value = value;\n"
            + "}\n"
            + "/** @type {Box<string>} */\n"
            + "var box = new Box('value');\n"
            + "/** @type {string} */\n"
            + "var value = box.value;");
  }

  @Test
  public void testTemplatedConstructorCanFlowToTemplatedParameter() {
    testSame(
        "/** @constructor @template T @param {T} value */\n"
            + "function Holder(value) {\n"
            + "  /** @type {T} */ this.value = value;\n"
            + "}\n"
            + "/** @param {Holder<number>} holder */\n"
            + "function useNumberHolder(holder) {\n"
            + "  /** @type {number} */ var n = holder.value;\n"
            + "}\n"
            + "useNumberHolder(new Holder(1));");
  }

  @Test
  public void testTemplatedCallUpdatesFunctionLiteralParameterType() {
    testSame(
        "/**\n"
            + " * @template T\n"
            + " * @param {T} value\n"
            + " * @param {function(T):void} callback\n"
            + " */\n"
            + "function consume(value, callback) {\n"
            + "  callback(value);\n"
            + "}\n"
            + "consume('text', function(value) {\n"
            + "  value.toUpperCase();\n"
            + "});");
  }

  @Test
  public void testTemplatedFunctionLiteralReturnTypeIsSubstituted() {
    testSame(
        "/**\n"
            + " * @template T\n"
            + " * @param {function(T):T} callback\n"
            + " * @param {T} value\n"
            + " * @return {T}\n"
            + " */\n"
            + "function transform(callback, value) {\n"
            + "  return callback(value);\n"
            + "}\n"
            + "/** @type {string} */\n"
            + "var result = transform(function(value) {\n"
            + "  return value;\n"
            + "}, 'text');");
  }
}