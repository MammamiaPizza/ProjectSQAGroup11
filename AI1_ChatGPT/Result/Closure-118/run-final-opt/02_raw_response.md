package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

public class DisambiguatePropertiesPrototypeAccessTest extends CompilerTestCase {
  private DisambiguateProperties<?> processor;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    enableNormalize();
    enableTypeCheck();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    CompilerPass pass = DisambiguateProperties.renameProperties(compiler);
    processor = (DisambiguateProperties<?>) pass;
    return pass;
  }

  public void testPrototypePropertyAccessIsNotRecordedForOneConstructor() throws Exception {
    testSame(
        "/** @constructor */ function Foo() {}\n"
            + "Foo.prototype.a;\n");

    assertTrue(getRecordedRootTypes("a").isEmpty());
  }

  public void testPrototypePropertyAccessIsNotRecordedForDistinctConstructors() throws Exception {
    testSame(
        "/** @constructor */ function Foo() {}\n"
            + "/** @constructor */ function Bar() {}\n"
            + "Foo.prototype.a;\n"
            + "Bar.prototype.a;\n");

    assertTrue(getRecordedRootTypes("a").isEmpty());
  }

  public void testInstancePropertyAccessIsStillRecorded() throws Exception {
    testSame(
        "/** @constructor */ function Foo() {}\n"
            + "var foo = new Foo();\n"
            + "foo.a;\n");

    assertFalse(getRecordedRootTypes("a").isEmpty());
  }

  @SuppressWarnings("unchecked")
  private Map<Node, ?> getRecordedRootTypes(String propertyName) throws Exception {
    assertNotNull(processor);

    Field propertiesField = DisambiguateProperties.class.getDeclaredField("properties");
    propertiesField.setAccessible(true);
    Map<String, ?> properties = (Map<String, ?>) propertiesField.get(processor);

    Object property = properties.get(propertyName);
    if (property == null) {
      return Collections.emptyMap();
    }

    Field rootTypesField = property.getClass().getDeclaredField("rootTypes");
    rootTypesField.setAccessible(true);
    return (Map<Node, ?>) rootTypesField.get(property);
  }
}