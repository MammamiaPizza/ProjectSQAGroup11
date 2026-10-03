package org.jsoup.nodes;

import org.junit.Test;
import static org.junit.Assert.*;

public class AttributeTest {
    @Test
    public void booleanAttributesAreEmptyStringValues() {
        Attribute attr = new Attribute("checked", null);
        assertEquals("", attr.getValue());
    }

 @Test
 public void booleanAttributeWithExplicitEmptyValue() {
     Attribute attr = new Attribute("checked", "");
     assertEquals("", attr.getValue());
 }

 @Test
 public void nonBooleanAttributeNullValueRemainsNull() {
     Attribute attr = new Attribute("class", null);
     assertNull(attr.getValue());
 }

 @Test
 public void isBooleanAttributeStaticReturnsTrue() {
     String[] boolKeys = {"checked", "disabled", "hidden", "readonly", "selected"};
     for (String k : boolKeys) {
         assertTrue(Attribute.isBooleanAttribute(k));
     }
 }

 @Test
 public void isBooleanAttributeStaticReturnsFalse() {
     assertFalse(Attribute.isBooleanAttribute("class"));
     assertFalse(Attribute.isBooleanAttribute("style"));
     assertFalse(Attribute.isBooleanAttribute("data-attr"));
 }

 @Test
 public void shouldCollapseBooleanWithEmptyOrEqualToKeyInHtmlSyntax() {
     Document.OutputSettings out = new Document("").outputSettings();
     assertTrue(Attribute.shouldCollapseAttribute("checked", "", out));
     assertTrue(Attribute.shouldCollapseAttribute("checked", "checked", out));
     assertTrue(Attribute.shouldCollapseAttribute("checked", "CHECKED", out));
     assertFalse(Attribute.shouldCollapseAttribute("checked", "yes", out));
 }

 @Test
 public void shouldNotCollapseBooleanInXmlSyntax() {
     Document.OutputSettings out = new Document("").outputSettings();
     out.syntax(Document.OutputSettings.Syntax.xml);
     assertFalse(Attribute.shouldCollapseAttribute("checked", "", out));
 }

 @Test
 public void shouldNotCollapseNonBooleanAttribute() {
     Document.OutputSettings out = new Document("").outputSettings();
     assertFalse(Attribute.shouldCollapseAttribute("class", "", out));
     assertFalse(Attribute.shouldCollapseAttribute("class", "class", out));
 }

 @Test
 public void htmlBooleanAttributeWithEmptyValueCollapses() {
     Attribute attr = new Attribute("checked", "");
     assertEquals("checked", attr.html());
 }

 @Test
 public void htmlNonBooleanAttributeWithNullValueOutputsEmptyString() {
     Attribute attr = new Attribute("id", null);
     assertEquals("id=\"\"", attr.html());
 }

 @Test
 public void cloneCreatesEqualIndependentAttribute() {
     Attribute attr = new Attribute("key", "val");
     Attribute clone = attr.clone();
     assertNotSame(attr, clone);
     assertEquals(attr.getKey(), clone.getKey());
     assertEquals(attr.getValue(), clone.getValue());
 }

 @Test
 public void equalsAndHashCode() {
     Attribute a1 = new Attribute("key", "val");
     Attribute a2 = new Attribute("key", "val");
     Attribute a3 = new Attribute("key", null);
     Attribute a4 = new Attribute("other", "val");
     assertEquals(a1, a2);
     assertEquals(a1.hashCode(), a2.hashCode());
     assertNotEquals(a1, a3);
     assertNotEquals(a1, a4);
 }

}
