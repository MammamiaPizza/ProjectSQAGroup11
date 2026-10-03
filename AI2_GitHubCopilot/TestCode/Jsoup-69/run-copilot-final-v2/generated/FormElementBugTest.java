package org.jsoup.nodes;

import org.jsoup.Connection;
import org.jsoup.parser.Tag;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FormElementBugTest {

 private FormElement createForm() {
     return new FormElement(Tag.valueOf("form"), "http://example.com", new Attributes());
 }

 private Element createInput(String name, String value) {
     Attributes attrs = new Attributes();
     attrs.put("name", name);
     attrs.put("value", value);
     return new Element(Tag.valueOf("input"), "http://example.com", attrs);
 }

 @Test
 public void testRemoveElementDecrementsSize() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     Element e2 = createInput("b", "2");
     form.addElement(e1);
     form.addElement(e2);
     assertEquals(2, form.elements().size());
     e1.remove();
     assertEquals(1, form.elements().size());
 }

 @Test
 public void testRemoveFirstChildDecrementsSize() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     Element e2 = createInput("b", "2");
     Element e3 = createInput("c", "3");
     form.addElement(e1);
     form.addElement(e2);
     form.addElement(e3);
     e1.remove();
     assertEquals(2, form.elements().size());
     assertFalse(form.elements().contains(e1));
     assertTrue(form.elements().contains(e2));
     assertTrue(form.elements().contains(e3));
 }

 @Test
 public void testRemoveLastChildDecrementsSize() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     Element e2 = createInput("b", "2");
     form.addElement(e1);
     form.addElement(e2);
     e2.remove();
     assertEquals(1, form.elements().size());
 }

 @Test
 public void testRemoveMiddleChildDecrementsSize() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     Element e2 = createInput("b", "2");
     Element e3 = createInput("c", "3");
     form.addElement(e1);
     form.addElement(e2);
     form.addElement(e3);
     e2.remove();
     assertEquals(2, form.elements().size());
 }

 @Test
 public void testRemoveAllChildrenEmptyElements() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     Element e2 = createInput("b", "2");
     form.addElement(e1);
     form.addElement(e2);
     e1.remove();
     e2.remove();
     assertEquals(0, form.elements().size());
 }

 @Test
 public void testRemoveChildNotInElementsDoesNotAffectSize() {
     FormElement form = createForm();
     Element input = createInput("a", "1");
     Element div = new Element(Tag.valueOf("div"), "http://example.com");
     form.addElement(input);
     form.appendChild(div);
     assertEquals(1, form.elements().size());
     div.remove();
     assertEquals(1, form.elements().size());
 }

 @Test
 public void testRemoveViaParentRemoveChild() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     form.addElement(e1);
     form.removeChild(e1);
     assertEquals(0, form.elements().size());
 }

 @Test
 public void testFormDataAfterRemoval() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     Element e2 = createInput("b", "2");
     form.addElement(e1);
     form.addElement(e2);
     assertEquals(2, form.formData().size());
     e1.remove();
     List<Connection.KeyVal> data = form.formData();
     assertEquals(1, data.size());
     boolean foundRemoved = false;
     for (Connection.KeyVal kv : data) {
         if ("a".equals(kv.key())) {
             foundRemoved = true;
             break;
         }
     }
     assertFalse(foundRemoved);
 }

 @Test
 public void testFormDataAlRemoved() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     form.addElement(e1);
     e1.remove();
     assertEquals(0, form.formData().size());
 }

 @Test
 public void testElementsSizeAfterMltipleAddRemove() {
     FormElement form = createForm();
     Element e1 = createInput("a", "1");
     Element e2 = createInput("b", "2");
     form.addElement(e1);
     form.addElement(e2);
     e1.remove();
     assertEquals(1, form.elements().size());
     Element e3 = createInput("c", "3");
     form.addElement(e3);
     assertEquals(2, form.elements().size());
     e2.remove();
     assertEquals(1, form.elements().size());
 }

}
