```java
package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;

/**
 * Tests relational-expression evaluation through the public JXPath API.
 */
public class CoreOperationRelationalExpressionTest extends TestCase {

    public void testComplexOperationWithVariablesUsesTheComputedArithmeticValue() {
        JXPathContext context = newContext();
        context.getVariables().declareVariable("a", Integer.valueOf(1));
        context.getVariables().declareVariable("b", Integer.valueOf(2));
        context.getVariables().declareVariable("c", Integer.valueOf(3));

        assertEquals(Boolean.TRUE, context.getValue("$a + $b <= $c"));
    }

    public void testLessOrEqualHandlesLessEqualAndGreaterScalarValues() {
        JXPathContext context = newContext();
        context.getVariables().declareVariable("one", Integer.valueOf(1));
        context.getVariables().declareVariable("two", Integer.valueOf(2));
        context.getVariables().declareVariable("three", Integer.valueOf(3));

        assertEquals(Boolean.TRUE, context.getValue("$one <= $two"));
        assertEquals(Boolean.TRUE, context.getValue("$two <= $two"));
        assertEquals(Boolean.FALSE, context.getValue("$three <= $two"));
    }

    public void testCollectionOnLeftMatchesAnyElement() {
        JXPathContext context = newContext();
        List values = Arrays.asList(new Object[] {
            Integer.valueOf(3), Integer.valueOf(1)
        });
        context.getVariables().declareVariable("values", values);

        assertEquals(Boolean.TRUE, context.getValue("$values <= 2"));
    }

    public void testCollectionOnRightPreservesLeftToRightComparisonOrder() {
        JXPathContext context = newContext();
        context.getVariables().declareVariable(
                "values", Collections.singletonList(Integer.valueOf(1)));

        /*
         * XPath relational comparisons are directional: 2 <= 1 is false.
         * Reversing the operands while iterating the right-hand collection
         * would incorrectly evaluate 1 <= 2.
         */
        assertEquals(Boolean.FALSE, context.getValue("2 <= $values"));
    }

    public void testTwoCollectionsMatchOnlyWhenAnOrderedPairSatisfiesComparison() {
        JXPathContext matchingContext = newContext();
        matchingContext.getVariables().declareVariable(
                "left", Collections.singletonList(Integer.valueOf(2)));
        matchingContext.getVariables().declareVariable(
                "right", Arrays.asList(new Object[] {
                    Integer.valueOf(1), Integer.valueOf(3)
                }));

        assertEquals(Boolean.TRUE, matchingContext.getValue("$left <= $right"));

        JXPathContext nonMatchingContext = newContext();
        nonMatchingContext.getVariables().declareVariable(
                "left", Collections.singletonList(Integer.valueOf(2)));
        nonMatchingContext.getVariables().declareVariable(
                "right", Collections.singletonList(Integer.valueOf(1)));

        assertEquals(Boolean.FALSE, nonMatchingContext.getValue("$left <= $right"));
    }

    public void testNaNAndNullOperandsDoNotSatisfyRelationalComparison() {
        JXPathContext context = newContext();
        context.getVariables().declareVariable("notANumber", Double.valueOf(Double.NaN));
        context.getVariables().declareVariable("nullValue", null);

        assertEquals(Boolean.FALSE, context.getValue("$notANumber <= 1"));
        assertEquals(Boolean.FALSE, context.getValue("$nullValue <= 1"));
    }

    private JXPathContext newContext() {
        return JXPathContext.newContext(new Object());
    }
}
```

- `testComplexOperationWithVariablesUsesTheComputedArithmeticValue` is the JXPATH-149 regression case: the arithmetic result of `$a + $b` must be compared with `$c`.
- `testLessOrEqualHandlesLessEqualAndGreaterScalarValues` covers scalar comparison outcomes for negative, zero, and positive comparison results.
- `testCollectionOnLeftMatchesAnyElement` exercises iteration of a left-side collection, including continuing after a nonmatching value and finding a later match.
- `testCollectionOnRightPreservesLeftToRightComparisonOrder` targets the directional relational-comparison fault: iterating a right-side collection must not reverse operands.
- `testTwoCollectionsMatchOnlyWhenAnOrderedPairSatisfiesComparison` exercises the two-iterator matching path for both a matching and nonmatching pair of collections.
- `testNaNAndNullOperandsDoNotSatisfyRelationalComparison` covers operands that cannot produce a valid numeric relational comparison.