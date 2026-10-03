package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.JSDocInfo.Visibility;
import com.google.javascript.rhino.JSDocInfoBuilder;
import com.google.javascript.rhino.JSTypeExpression;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class JSDocInfoBuilderRegressionTest {

  private static JSTypeExpression stringType() {
    return new JSTypeExpression(new Node(Token.STRING, "string"), "test.js");
  }

  @Test
  public void testTypeAnnotationProducesUsableJSDocInfo() {
    JSDocInfoBuilder builder = new JSDocInfoBuilder(true);

    builder.markAnnotation("type", 1, 4);
    assertTrue(builder.recordType(stringType()));
    assertTrue(builder.isPopulated());

    JSDocInfo info = builder.build("test.js");
    assertNotNull(info);
    assertNull(builder.build("test.js"));
  }

  @Test
  public void testNoSideEffectsAnnotationIsRecordedAndOnlyOnce() {
    JSDocInfoBuilder builder = new JSDocInfoBuilder(false);

    assertTrue(builder.recordNoSideEffects());
    assertFalse(builder.recordNoSideEffects());

    JSDocInfo info = builder.build("externs.js");
    assertNotNull(info);
    assertTrue(info.isNoSideEffects());
    assertFalse(builder.isPopulated());
  }

  @Test
  public void testDocumentationDescriptionRequiresDocumentationParsing() {
    JSDocInfoBuilder withoutDocumentation = new JSDocInfoBuilder(false);
    assertTrue(withoutDocumentation.recordBlockDescription("ignored for population"));
    assertFalse(withoutDocumentation.isPopulated());
    assertNull(withoutDocumentation.build("test.js"));

    JSDocInfoBuilder withDocumentation = new JSDocInfoBuilder(true);
    assertTrue(withDocumentation.recordBlockDescription("A useful description."));
    assertTrue(withDocumentation.isDescriptionRecorded());

    JSDocInfo info = withDocumentation.build("test.js");
    assertNotNull(info);
    assertEquals("A useful description.", info.getDescription());
  }

  @Test
  public void testFileOverviewBuildsInfoAndResetsBuilder() {
    JSDocInfoBuilder builder = new JSDocInfoBuilder(true);

    assertTrue(builder.recordFileOverview("Overview."));
    assertTrue(builder.isPopulated());
    assertTrue(builder.isPopulatedWithFileOverview());

    JSDocInfo info = builder.build("overview.js");
    assertNotNull(info);
    assertTrue(info.hasFileOverview());
    assertFalse(builder.isPopulated());
    assertFalse(builder.isPopulatedWithFileOverview());
    assertNull(builder.build("overview.js"));
  }

  @Test
  public void testParameterTypeAndDescriptionAreBothRetainedAsPopulation() {
    JSDocInfoBuilder builder = new JSDocInfoBuilder(true);

    assertTrue(builder.recordParameter("value", stringType()));
    assertTrue(builder.hasParameter("value"));
    assertTrue(builder.recordParameterDescription("value", "input value"));
    assertTrue(builder.isPopulated());

    assertNotNull(builder.build("test.js"));
  }

  @Test
  public void testConstructorAndInterfaceFlagsAreIndependentlyRecorded() {
    JSDocInfoBuilder constructorBuilder = new JSDocInfoBuilder(false);
    assertTrue(constructorBuilder.recordConstructor());
    assertTrue(constructorBuilder.isConstructorRecorded());
    assertNotNull(constructorBuilder.build("test.js"));

    JSDocInfoBuilder interfaceBuilder = new JSDocInfoBuilder(false);
    assertTrue(interfaceBuilder.recordInterface());
    assertTrue(interfaceBuilder.isInterfaceRecorded());
    assertNotNull(interfaceBuilder.build("test.js"));
  }

  @Test
  public void testVisibilityAnnotationProducesPopulatedInfo() {
    JSDocInfoBuilder builder = new JSDocInfoBuilder(false);

    assertTrue(builder.recordVisibility(Visibility.PRIVATE));
    assertTrue(builder.isPopulated());
    assertNotNull(builder.build("test.js"));
  }

  @Test
  public void testMarkerOnlyTextDoesNotCreateStandaloneJSDocInfo() {
    JSDocInfoBuilder builder = new JSDocInfoBuilder(true);

    builder.markAnnotation("type", 1, 4);
    builder.markName("value", 1, 10);
    builder.markText("string", 1, 16, 1, 22);

    assertFalse(builder.isPopulated());
    assertNull(builder.build("test.js"));
  }
}
