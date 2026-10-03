package org.apache.commons.collections.functors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.commons.collections.Predicate;
import org.junit.Test;

public class EqualPredicateTest {

    @Test
    public void equalPredicateFactoryUsesEqualsForDistinctEqualObjects() {
        String stored = new String("value");
        Predicate<String> predicate = EqualPredicate.equalPredicate(stored);

        assertTrue(predicate.evaluate(new String("value")));
    }

    @Test
    public void equalPredicateFactoryRejectsNonEqualObject() {
        Predicate<String> predicate = EqualPredicate.equalPredicate("expected");

        assertFalse(predicate.evaluate("actual"));
    }

    @Test
    public void constructorEvaluatesUsingEqualsAndExposesStoredValue() {
        String stored = new String("stored");
        EqualPredicate<String> predicate = new EqualPredicate<String>(stored);

        assertTrue(predicate.evaluate(new String("stored")));
        assertSame(stored, predicate.getValue());
    }

    @Test
    public void equatorFactoryUsesProvidedEquatorForComparison() {
        Equator<String> caseInsensitiveEquator = new Equator<String>() {
            public boolean equate(String left, String right) {
                return left != null && left.equalsIgnoreCase(right);
            }

            public int hash(String object) {
                return object == null ? 0 : object.toLowerCase().hashCode();
            }
        };

        Predicate<String> predicate = EqualPredicate.equalPredicate("VALUE", caseInsensitiveEquator);

        assertTrue(predicate.evaluate("value"));
        assertFalse(predicate.evaluate("different"));
    }

    @Test
    public void equatorConstructorPassesStoredValueAsFirstArgument() {
        final String stored = "stored";
        Equator<String> orderCheckingEquator = new Equator<String>() {
            public boolean equate(String left, String right) {
                return stored.equals(left) && "input".equals(right);
            }

            public int hash(String object) {
                return object == null ? 0 : object.hashCode();
            }
        };
        EqualPredicate<String> predicate = new EqualPredicate<String>(stored, orderCheckingEquator);

        assertTrue(predicate.evaluate("input"));
        assertFalse(predicate.evaluate(stored));
        assertEquals(stored, predicate.getValue());
    }
}
