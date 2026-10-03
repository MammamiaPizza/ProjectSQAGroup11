package org.jsoup.nodes;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class AttributesTest {

 @Test
 public void booleanAttributeTrueNoEmptyValue() {
     Attributes attrs = new Attributes();
     attrs.put("src", "foo");
     attrs.put("noshade", true);
     String html = attrs.html();
     assertTrue("Should contain noshade", html.contains("noshade"));
     assertFalse("Should not contain noshade=\"\"", html.contains("noshade=\"\""));
 }

 @Test
 public void booleanAttributeFalseNotPresent() {
     Attributes attrs = new Attributes();
     attrs.put("checked", false);
     assertFalse(attrs.hasKey("checked"));
     assertEquals(0, attrs.size());
 }

 @Test
 public void getReturnsEmptyStringForBooleanTrue() {
     Attributes attrs = new Attributes();
     attrs.put("selected", true);
     assertEquals("", attrs.get("selected"));
 }

 @Test
 public void getReturnsEmptyStringForAbsentKey() {
     Attributes attrs = new Attributes();
     assertEquals("", attrs.get("nonexistent"));
 }

 @Test
 public void mixedAttributesOutputCorrect() {
     Attributes attrs = new Attributes();
     attrs.put("src", "foo");
     attrs.put("noshade", true);
     attrs.put("async", true);
     attrs.put("data-x", "val");
     String html = attrs.html();
     assertTrue(html.contains(" src=\"foo\""));
     assertTrue(html.contains(" noshade"));
     assertTrue(html.contains(" async"));
     assertTrue(html.contains(" data-x=\"val\""));
     assertFalse(html.contains("noshade=\"\""));
     assertFalse(html.contains("async=\"\""));
 }

 @Test
 public void booleanAttributeOverrideWithStringValue() {
     Attributes attrs = new Attributes();
     attrs.put("noshade", true);
     attrs.put("noshade", "bar");
     String html = attrs.html();
     assertTrue(html.contains(" noshade=\"bar\""));
     assertFalse(html.contains("noshade=\"\""));
 }

 @Test
 public void normalAttributeWithValue() {
     Attributes attrs = new Attributes();
     attrs.put("src", "foo");
     assertEquals(" src=\"foo\"", attrs.html());
 }

 @Test
 public void attributeWithEmptyStringValue() {
     Attributes attrs = new Attributes();
     attrs.put("alt", "");
     assertEquals(" alt=\"\"", attrs.html());
 }

 @Test
 public void sizeAfterBooleanPut() {
     Attributes attrs = new Attributes();
     attrs.put("a", true);
     assertEquals(1, attrs.size());
     attrs.put("b", true);
     assertEquals(2, attrs.size());
     attrs.put("c", false);
     assertEquals(2, attrs.size());
 }

 @Test
 public void removeBooleanAttribute() {
     Attributes attrs = new Attributes();
     attrs.put("noshade", true);
     attrs.remove("noshade");
     assertFalse(attrs.hasKey("noshade"));
     assertEquals(0, attrs.size());
 }

 @Test
 public void booleanAttributeIteration() {
     Attributes attrs = new Attributes();
     attrs.put("noshade", true);
     attrs.put("src", "foo");
     boolean foundBoolean = false;
     for (Attribute attr : attrs) {
         if (attr.getKey().equals("noshade")) {
             foundBoolean = true;
             assertTrue(attr instanceof BooleanAttribute);
             assertEquals("", attr.getValue());
         }
     }
     assertTrue(foundBoolean);
 }

 @Test
 public void dataAttributePreserved() {
     Attributes attrs = new Attributes();
     attrs.put("data-key", "value");
     assertEquals(" data-key=\"value\"", attrs.html());
     Map<String, String> dataset = attrs.dataset();
     assertEquals("value", dataset.get("key"));
 }

}
