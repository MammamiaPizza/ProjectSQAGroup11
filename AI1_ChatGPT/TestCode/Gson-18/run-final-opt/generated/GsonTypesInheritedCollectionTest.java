package com.google.gson.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;

public class GsonTypesInheritedCollectionTest {

  public static class SmallClass {
    String name;
  }

  public static class GenericList<T> extends ArrayList<T> {
  }

  public static class ConcreteSmallList extends GenericList<SmallClass> {
  }

  static class DeclaredTypes {
    Collection<SmallClass> smallClasses;
  }

  @Test
  public void getCollectionElementTypeResolvesConcreteTypeThroughGenericSuperclass() {
    Type elementType = $Gson$Types.getCollectionElementType(
        ConcreteSmallList.class, ConcreteSmallList.class);

    assertSame(SmallClass.class, elementType);
  }

  @Test
  public void resolveResolvesCollectionElementVariableThroughGenericSuperclass() {
    TypeVariable<?> collectionElement = Collection.class.getTypeParameters()[0];

    Type resolved = $Gson$Types.resolve(
        ConcreteSmallList.class, ConcreteSmallList.class, collectionElement);

    assertSame(SmallClass.class, resolved);
  }

  @Test
  public void resolveResolvesDirectGenericSuperclassVariable() {
    TypeVariable<?> elementVariable = GenericList.class.getTypeParameters()[0];

    Type resolved = $Gson$Types.resolve(
        ConcreteSmallList.class, ConcreteSmallList.class, elementVariable);

    assertSame(SmallClass.class, resolved);
  }

  @Test
  public void gsonDeserializesInheritedGenericCollectionElementsAsDeclaredClass() {
    ConcreteSmallList values = new Gson().fromJson(
        "[{\"name\":\"first\"}]", ConcreteSmallList.class);

    assertEquals(1, values.size());
    assertTrue(values.get(0) instanceof SmallClass);
    assertEquals("first", values.get(0).name);
  }

  @Test
  public void getCollectionElementTypeReturnsDirectDeclaredElementType() throws Exception {
    Field field = DeclaredTypes.class.getDeclaredField("smallClasses");

    Type elementType = $Gson$Types.getCollectionElementType(
        field.getGenericType(), Collection.class);

    assertSame(SmallClass.class, elementType);
  }

  @Test
  public void rawCollectionFallsBackToObjectElementType() {
    Type elementType = $Gson$Types.getCollectionElementType(
        Collection.class, Collection.class);

    assertSame(Object.class, elementType);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getCollectionElementTypeRejectsNonCollectionTypes() {
    $Gson$Types.getCollectionElementType(String.class, String.class);
  }
}
