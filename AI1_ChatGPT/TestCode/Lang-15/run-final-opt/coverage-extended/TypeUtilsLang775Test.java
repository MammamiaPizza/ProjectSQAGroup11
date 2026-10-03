package org.apache.commons.lang3.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import org.junit.Test;

public class TypeUtilsLang775Test {

    interface This<A, B> {
    }

    interface Alias<T> extends This<T, T> {
    }

    static class Thing implements Alias<String> {
    }

    static class GenericBase<A, B> implements This<A, B> {
    }

    static class SubThing extends GenericBase<String, Integer> {
    }

    static class Types {
        This<String, Integer> stringInteger;
        This<String, String> stringString;
        This<Integer, Integer> integerInteger;
        This<Integer, String> integerString;
    }

    private Type typeOf(final String fieldName) throws Exception {
        return Types.class.getDeclaredField(fieldName).getGenericType();
    }

    @Test
    public void getTypeArgumentsForDirectParameterizedTypeMapsBothVariables() throws Exception {
        final Type type = typeOf("stringInteger");
        final Map<TypeVariable<?>, Type> arguments = TypeUtils.getTypeArguments(type, This.class);
        final TypeVariable<?>[] variables = This.class.getTypeParameters();

        assertEquals(2, arguments.size());
        assertEquals(String.class, arguments.get(variables[0]));
        assertEquals(Integer.class, arguments.get(variables[1]));
    }

    @Test
    public void getTypeArgumentsResolvesRepeatedInterfaceVariableThroughInheritance() {
        final Map<TypeVariable<?>, Type> arguments = TypeUtils.getTypeArguments(Thing.class, This.class);
        final TypeVariable<?>[] variables = This.class.getTypeParameters();

        assertEquals(String.class, arguments.get(variables[0]));
        assertEquals(String.class, arguments.get(variables[1]));
    }

    @Test
    public void getTypeArgumentsResolvesSuperclassBindingsThroughInterface() {
        final Map<TypeVariable<?>, Type> arguments = TypeUtils.getTypeArguments(SubThing.class, This.class);
        final TypeVariable<?>[] variables = This.class.getTypeParameters();

        assertEquals(String.class, arguments.get(variables[0]));
        assertEquals(Integer.class, arguments.get(variables[1]));
    }

    @Test
    public void isAssignableRecognizesInheritedConcreteInterfaceArguments() throws Exception {
        assertTrue(TypeUtils.isAssignable(Thing.class, typeOf("stringString")));
    }

    @Test
    public void isAssignableRejectsDifferentInheritedConcreteInterfaceArguments() throws Exception {
        assertFalse(TypeUtils.isAssignable(Thing.class, typeOf("integerInteger")));
    }

    @Test
    public void isAssignableRecognizesSuperclassConcreteInterfaceArguments() throws Exception {
        assertTrue(TypeUtils.isAssignable(SubThing.class, typeOf("stringInteger")));
        assertFalse(TypeUtils.isAssignable(SubThing.class, typeOf("integerString")));
    }

    @Test
    public void isAssignableRequiresMatchingDirectParameterizedArguments() throws Exception {
        assertTrue(TypeUtils.isAssignable(typeOf("stringInteger"), typeOf("stringInteger")));
        assertFalse(TypeUtils.isAssignable(typeOf("stringInteger"), typeOf("integerString")));
    }

    @Test
    public void classTypesRemainAssignableToObjectAndNotToUnrelatedGenericType() throws Exception {
        assertTrue(TypeUtils.isAssignable(Thing.class, Object.class));
        assertFalse(TypeUtils.isAssignable(String.class, typeOf("stringString")));
    }

    @Test
    public void getTypeArgumentsReturnsNullForUnrelatedTypes() {
        assertNull(TypeUtils.getTypeArguments(String.class, This.class));
    }

    @Test
    public void getTypeArgumentsParameterizedTypeOverloadMapsOwnVariables() throws Exception {
        final ParameterizedType type = (ParameterizedType) typeOf("stringString");
        final Map<TypeVariable<?>, Type> arguments = TypeUtils.getTypeArguments(type);
        final TypeVariable<?>[] variables = This.class.getTypeParameters();

        assertEquals(2, arguments.size());
        assertEquals(String.class, arguments.get(variables[0]));
        assertEquals(String.class, arguments.get(variables[1]));
    }
}
