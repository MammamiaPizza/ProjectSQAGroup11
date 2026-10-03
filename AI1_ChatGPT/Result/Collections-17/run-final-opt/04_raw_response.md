@org.junit.Test
public void equalPredicateFactoryWithNullMatchesOnlyNull() {
    org.junit.Assert.assertTrue(EqualPredicate.<Object>equalPredicate(null).evaluate(null));
    org.junit.Assert.assertFalse(EqualPredicate.<Object>equalPredicate(null).evaluate(new Object()));
}

@org.junit.Test
public void equalPredicateFactoryWithNullAndEquatorMatchesOnlyNull() {
    org.junit.Assert.assertTrue(EqualPredicate.<Object>equalPredicate(null, null).evaluate(null));
    org.junit.Assert.assertFalse(EqualPredicate.<Object>equalPredicate(null, null).evaluate(new Object()));
}