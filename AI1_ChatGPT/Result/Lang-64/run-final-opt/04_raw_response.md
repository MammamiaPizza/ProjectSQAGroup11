@Test
public void getEnumFindsMatchingValueAndReturnsNullForUnknownValue() {
    assertSame(LookupValuedEnum.ONE, LookupValuedEnum.lookup(LookupValuedEnum.class, 1));
    assertNull(LookupValuedEnum.lookup(LookupValuedEnum.class, 2));
}

@Test
public void getEnumRejectsNullEnumClass() {
    try {
        LookupValuedEnum.lookup(null, 1);
        fail("A null enum class must be rejected");
    } catch (IllegalArgumentException expected) {
    }
}

@Test
public void getValueReturnsConstructorValueAndToStringIsCached() {
    assertEquals(1, LookupValuedEnum.ONE.getValue());

    String first = LookupValuedEnum.ONE.toString();
    String second = LookupValuedEnum.ONE.toString();
    String className = LookupValuedEnum.class.getName();
    String shortName = className.substring(className.lastIndexOf('.') + 1);

    assertEquals(shortName + "[ONE=1]", first);
    assertSame(first, second);
}

private static final class LookupValuedEnum extends org.apache.commons.lang.enums.ValuedEnum {
    private static final LookupValuedEnum ONE = new LookupValuedEnum("ONE", 1);

    private LookupValuedEnum(String name, int value) {
        super(name, value);
    }

    private static org.apache.commons.lang.enums.Enum lookup(Class enumClass, int value) {
        return getEnum(enumClass, value);
    }
}