package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;

public class DisambiguatePropertiesPrototypeAccessTest extends TestCase {
  private DisambiguateProperties<?> processor;

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

  private void testSame(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.setCheckTypes(true);
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        options);

    processor = (DisambiguateProperties<?>) DisambiguateProperties.renameProperties(compiler);
    processor.process(compiler.getExternsRoot(), compiler.getJsRoot());
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
