The compilation failure was caused by `Constant` not providing a `Constant(Object)` constructor; it only accepts `Number` or `String`. Several valid tests require literal `null` and collection values, which cannot be represented by `Constant`.

The corrected test introduces a small `LiteralExpression` test helper that extends the existing `Expression` API and returns any supplied value from `compute(EvalContext)`. This preserves all original test scenarios, including scalar, `null`, and collection arguments.

```java
package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.JXPathInvalidSyntaxException;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.EvalContext;

/**
 * Tests for {@link CoreFunction}.
 */
public class CoreFunctionGeneratedTest extends TestCase {

    /**
     * An expression that can return arbitrary test values, including null and
     * collections. Constant only accepts Number and String constructor values.
     */
    private static class LiteralExpression extends Expression {
        private final Object value;

        LiteralExpression(Object value) {
            this.value = value;
        }

        public Object compute(EvalContext context) {
            return value;
        }

        public String toString() {
            return String.valueOf(value);
        }
    }

    private Expression constant(Object value) {
        return new LiteralExpression(value);
    }

    public void testRoundPreservesNaN() {
        CoreFunction function =
            new CoreFunction(
                Compiler.FUNCTION_ROUND,
                new Expression[] { constant("NaN") });

        Object result = function.computeValue(null);

        assertTrue(result instanceof Double);
        assertTrue(
            "round(NaN) must remain NaN",
            Double.isNaN(((Double) result).doubleValue()));
    }

    public void testFloorCeilingAndRoundForFiniteValues() {
        CoreFunction floor =
            new CoreFunction(
                Compiler.FUNCTION_FLOOR,
                new Expression[] { constant(new Double(3.8)) });
        CoreFunction ceiling =
            new CoreFunction(
                Compiler.FUNCTION_CEILING,
                new Expression[] { constant(new Double(-3.2)) });
        CoreFunction roundPositive =
            new CoreFunction(
                Compiler.FUNCTION_ROUND,
                new Expression[] { constant(new Double(2.5)) });
        CoreFunction roundNegative =
            new CoreFunction(
                Compiler.FUNCTION_ROUND,
                new Expression[] { constant(new Double(-2.5)) });

        assertEquals(new Double(3.0), floor.computeValue(null));
        assertEquals(new Double(-3.0), ceiling.computeValue(null));
        assertEquals(new Double(3.0), roundPositive.computeValue(null));
        assertEquals(new Double(-2.0), roundNegative.computeValue(null));
    }

    public void testCountHandlesCollectionsScalarsAndNull() {
        CoreFunction collectionCount =
            new CoreFunction(
                Compiler.FUNCTION_COUNT,
                new Expression[] {
                    constant(Arrays.asList(new Object[] { "a", "b", "c" }))
                });
        CoreFunction scalarCount =
            new CoreFunction(
                Compiler.FUNCTION_COUNT,
                new Expression[] { constant("value") });
        CoreFunction nullCount =
            new CoreFunction(
                Compiler.FUNCTION_COUNT,
                new Expression[] { constant(null) });

        assertEquals(new Double(3.0), collectionCount.computeValue(null));
        assertEquals(new Double(1.0), scalarCount.computeValue(null));
        assertEquals(new Double(0.0), nullCount.computeValue(null));
    }

    public void testStringFunctions() {
        CoreFunction concat =
            new CoreFunction(
                Compiler.FUNCTION_CONCAT,
                new Expression[] {
                    constant("one"),
                    constant("-"),
                    constant(new Integer(2))
                });
        CoreFunction startsWith =
            new CoreFunction(
                Compiler.FUNCTION_STARTS_WITH,
                new Expression[] {
                    constant("prefix-value"),
                    constant("prefix")
                });
        CoreFunction contains =
            new CoreFunction(
                Compiler.FUNCTION_CONTAINS,
                new Expression[] {
                    constant("prefix-value"),
                    constant("fix-v")
                });
        CoreFunction substringBefore =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING_BEFORE,
                new Expression[] {
                    constant("alpha-beta"),
                    constant("-")
                });
        CoreFunction substringAfter =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING_AFTER,
                new Expression[] {
                    constant("alpha-beta"),
                    constant("-")
                });
        CoreFunction missingDelimiter =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING_AFTER,
                new Expression[] {
                    constant("alpha"),
                    constant(":")
                });

        assertEquals("one-2", concat.computeValue(null));
        assertEquals(Boolean.TRUE, startsWith.computeValue(null));
        assertEquals(Boolean.TRUE, contains.computeValue(null));
        assertEquals("alpha", substringBefore.computeValue(null));
        assertEquals("beta", substringAfter.computeValue(null));
        assertEquals("", missingDelimiter.computeValue(null));
    }

    public void testSubstringBoundariesAndNaNStart() {
        CoreFunction twoArguments =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING,
                new Expression[] {
                    constant("12345"),
                    constant(new Double(2.4))
                });
        CoreFunction threeArguments =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING,
                new Expression[] {
                    constant("12345"),
                    constant(new Double(1.5)),
                    constant(new Double(2.6))
                });
        CoreFunction beforeBeginning =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING,
                new Expression[] {
                    constant("12345"),
                    constant(new Double(-5.0))
                });
        CoreFunction nanStart =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING,
                new Expression[] {
                    constant("12345"),
                    constant("NaN")
                });
        CoreFunction negativeLength =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING,
                new Expression[] {
                    constant("12345"),
                    constant(new Double(2.0)),
                    constant(new Double(-1.0))
                });

        assertEquals("2345", twoArguments.computeValue(null));
        assertEquals("234", threeArguments.computeValue(null));
        assertEquals("12345", beforeBeginning.computeValue(null));
        assertEquals("", nanStart.computeValue(null));
        assertEquals("", negativeLength.computeValue(null));
    }

    public void testNormalizeSpaceAndTranslate() {
        CoreFunction normalize =
            new CoreFunction(
                Compiler.FUNCTION_NORMALIZE_SPACE,
                new Expression[] {
                    constant(" \t alpha \n beta \r ")
                });
        CoreFunction translate =
            new CoreFunction(
                Compiler.FUNCTION_TRANSLATE,
                new Expression[] {
                    constant("abracadabra"),
                    constant("abc"),
                    constant("XY")
                });

        assertEquals("alpha beta", normalize.computeValue(null));
        assertEquals("XYrXXdXYrX", translate.computeValue(null));
    }

    public void testBooleanNotAndConstantFunctions() {
        CoreFunction booleanZero =
            new CoreFunction(
                Compiler.FUNCTION_BOOLEAN,
                new Expression[] { constant(new Double(0.0)) });
        CoreFunction booleanText =
            new CoreFunction(
                Compiler.FUNCTION_BOOLEAN,
                new Expression[] { constant("text") });
        CoreFunction notEmpty =
            new CoreFunction(
                Compiler.FUNCTION_NOT,
                new Expression[] { constant("") });
        CoreFunction trueFunction =
            new CoreFunction(
                Compiler.FUNCTION_TRUE,
                new Expression[0]);
        CoreFunction falseFunction =
            new CoreFunction(
                Compiler.FUNCTION_FALSE,
                new Expression[0]);
        CoreFunction nullFunction =
            new CoreFunction(
                Compiler.FUNCTION_NULL,
                new Expression[0]);

        assertEquals(Boolean.FALSE, booleanZero.computeValue(null));
        assertEquals(Boolean.TRUE, booleanText.computeValue(null));
        assertEquals(Boolean.TRUE, notEmpty.computeValue(null));
        assertEquals(Boolean.TRUE, trueFunction.compute(null));
        assertEquals(Boolean.FALSE, falseFunction.computeValue(null));
        assertNull(nullFunction.computeValue(null));
    }

    public void testNumberStringLengthAndSum() {
        CoreFunction number =
            new CoreFunction(
                Compiler.FUNCTION_NUMBER,
                new Expression[] { constant("12.5") });
        CoreFunction stringLength =
            new CoreFunction(
                Compiler.FUNCTION_STRING_LENGTH,
                new Expression[] { constant("abcde") });
        CoreFunction nullSum =
            new CoreFunction(
                Compiler.FUNCTION_SUM,
                new Expression[] { constant(null) });
        CoreFunction invalidSum =
            new CoreFunction(
                Compiler.FUNCTION_SUM,
                new Expression[] { constant("not-a-node-set") });

        assertEquals(new Double(12.5), number.computeValue(null));
        assertEquals(new Double(5.0), stringLength.computeValue(null));
        assertEquals(new Double(0.0), nullSum.computeValue(null));

        try {
            invalidSum.computeValue(null);
            fail("sum() must reject a non-node-set, non-null argument");
        }
        catch (JXPathException expected) {
            assertTrue(
                expected.getMessage().indexOf(
                    "Invalid argument type for 'sum'") >= 0);
        }
    }

    public void testArgumentValidationRejectsIncorrectArity() {
        CoreFunction concatWithOneArgument =
            new CoreFunction(
                Compiler.FUNCTION_CONCAT,
                new Expression[] { constant("only") });
        CoreFunction trueWithArgument =
            new CoreFunction(
                Compiler.FUNCTION_TRUE,
                new Expression[] { constant("unexpected") });
        CoreFunction substringWithOneArgument =
            new CoreFunction(
                Compiler.FUNCTION_SUBSTRING,
                new Expression[] { constant("text") });

        try {
            concatWithOneArgument.computeValue(null);
            fail("concat() requires at least two arguments");
        }
        catch (JXPathInvalidSyntaxException expected) {
            assertTrue(
                expected.getMessage().indexOf(
                    "Incorrect number of arguments") >= 0);
        }

        try {
            trueWithArgument.computeValue(null);
            fail("true() must not accept arguments");
        }
        catch (JXPathInvalidSyntaxException expected) {
            assertTrue(
                expected.getMessage().indexOf(
                    "Incorrect number of arguments") >= 0);
        }

        try {
            substringWithOneArgument.computeValue(null);
            fail("substring() requires two or three arguments");
        }
        catch (JXPathInvalidSyntaxException expected) {
            assertTrue(
                expected.getMessage().indexOf(
                    "Incorrect number of arguments") >= 0);
        }
    }

    public void testMetadataArgumentsAndContextDependency() {
        Expression first = constant("first");
        Expression second = constant("second");
        Expression third = constant("third");

        CoreFunction concat =
            new CoreFunction(
                Compiler.FUNCTION_CONCAT,
                new Expression[] { first, second, third });
        CoreFunction position =
            new CoreFunction(
                Compiler.FUNCTION_POSITION,
                null);
        CoreFunction stringWithoutArgument =
            new CoreFunction(
                Compiler.FUNCTION_STRING,
                null);
        CoreFunction stringWithArgument =
            new CoreFunction(
                Compiler.FUNCTION_STRING,
                new Expression[] { constant("value") });
        CoreFunction unknown =
            new CoreFunction(
                98765,
                new Expression[0]);

        assertEquals(Compiler.FUNCTION_CONCAT, concat.getFunctionCode());
        assertEquals(3, concat.getArgumentCount());
        assertSame(first, concat.getArg1());
        assertSame(second, concat.getArg2());
        assertSame(third, concat.getArg3());
        assertEquals("concat(first, second, third)", concat.toString());

        assertEquals(0, position.getArgumentCount());
        assertTrue(position.computeContextDependent());
        assertTrue(stringWithoutArgument.computeContextDependent());
        assertFalse(stringWithArgument.computeContextDependent());
        assertEquals("unknownFunction98765()", unknown.toString());
        assertNull(unknown.computeValue(null));
    }
}
```